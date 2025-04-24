import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class IOSCalculator extends JFrame {
    private JTextField display;
    private double currentNumber = 0;
    private double storedNumber = 0;
    private String currentOperation = "";
    private boolean newInput = true;
    private DecimalFormat df = new DecimalFormat("#.##########");

    public IOSCalculator() {
        createUI();
    }

    private void createUI() {
        setTitle("iOS Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));

        // Главная панель
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // Дисплей
        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Helvetica Neue", Font.PLAIN, 72));
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(display, BorderLayout.NORTH);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        buttonPanel.setBackground(Color.BLACK);

        // Кнопки в порядке iOS калькулятора
        String[][] buttonLabels = {
                {"C", "+/-", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"0", "", ".", "="}
        };

        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                String label = buttonLabels[row][col];
                if (label.isEmpty()) continue;

                JButton button = createIOSButton(label);
                button.addActionListener(new ButtonClickListener());
                buttonPanel.add(button);

                if (label.equals("0")) {
                    buttonPanel.remove(button);
                    JPanel zeroPanel = new JPanel(new BorderLayout());
                    zeroPanel.setBackground(Color.BLACK);
                    zeroPanel.add(button, BorderLayout.CENTER);
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridwidth = 2;
                    gbc.fill = GridBagConstraints.BOTH;
                    buttonPanel.add(zeroPanel, gbc);
                }
            }
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        pack();
        setSize(350, 550);
        setLocationRelativeTo(null);
    }

    private JButton createIOSButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Helvetica Neue", Font.PLAIN, 28));

        // Цвета кнопок как в iOS
        if (isDigitButton(label)) {
            button.setBackground(new Color(51, 51, 51));
            button.setForeground(Color.WHITE);
        } else if (isFunctionButton(label)) {
            button.setBackground(new Color(165, 165, 165));
            button.setForeground(Color.BLACK);
        } else {
            button.setBackground(new Color(255, 159, 11));
            button.setForeground(Color.WHITE);
        }

        // Стиль при наведении
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                Color bg = button.getBackground();
                button.setBackground(new Color(
                        Math.min(bg.getRed() + 30, 255),
                        Math.min(bg.getGreen() + 30, 255),
                        Math.min(bg.getBlue() + 30, 255)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
                button.setOpaque(true);
                if (isDigitButton(label)) {
                    button.setBackground(new Color(51, 51, 51));
                } else if (isFunctionButton(label)) {
                    button.setBackground(new Color(165, 165, 165));
                } else {
                    button.setBackground(new Color(255, 159, 11));
                }
            }
        });

        // Круглая форма для кнопок
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setPreferredSize(new Dimension(70, 70));

        return button;
    }

    private boolean isDigitButton(String label) {
        return label.matches("[0-9.]");
    }

    private boolean isFunctionButton(String label) {
        return label.equals("C") || label.equals("+/-") || label.equals("%");
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = ((JButton)e.getSource()).getText();

            if (command.matches("[0-9]")) {
                handleNumberInput(command);
            } else if (command.equals(".")) {
                handleDecimalPoint();
            } else if (command.matches("[÷×+\\-]")) {
                handleOperation(command);
            } else if (command.equals("=")) {
                handleEquals();
            } else if (command.equals("C")) {
                handleClear();
            } else if (command.equals("+/-")) {
                handleSignChange();
            } else if (command.equals("%")) {
                handlePercentage();
            }
        }

        private void handleNumberInput(String number) {
            if (newInput) {
                display.setText(number);
                newInput = false;
            } else {
                display.setText(display.getText() + number);
            }
            currentNumber = Double.parseDouble(display.getText());
        }

        private void handleDecimalPoint() {
            if (newInput) {
                display.setText("0.");
                newInput = false;
            } else if (!display.getText().contains(".")) {
                display.setText(display.getText() + ".");
            }
        }

        private void handleOperation(String op) {
            if (!currentOperation.isEmpty()) {
                calculate();
            }
            storedNumber = currentNumber;
            currentOperation = op;
            newInput = true;
        }

        private void handleEquals() {
            if (!currentOperation.isEmpty()) {
                calculate();
                currentOperation = "";
            }
            newInput = true;
        }

        private void handleClear() {
            display.setText("0");
            currentNumber = 0;
            storedNumber = 0;
            currentOperation = "";
            newInput = true;
        }

        private void handleSignChange() {
            currentNumber = -currentNumber;
            display.setText(df.format(currentNumber));
        }

        private void handlePercentage() {
            currentNumber = currentNumber / 100;
            display.setText(df.format(currentNumber));
        }

        private void calculate() {
            switch (currentOperation) {
                case "+":
                    currentNumber = storedNumber + currentNumber;
                    break;
                case "-":
                    currentNumber = storedNumber - currentNumber;
                    break;
                case "×":
                    currentNumber = storedNumber * currentNumber;
                    break;
                case "÷":
                    if (currentNumber != 0) {
                        currentNumber = storedNumber / currentNumber;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }
            display.setText(df.format(currentNumber));
            newInput = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                IOSCalculator calculator = new IOSCalculator();
                calculator.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}