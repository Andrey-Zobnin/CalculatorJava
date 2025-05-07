import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class CalculatorWithLiteGui extends JFrame {
    private JTextField display;
    private double firstNumber = 0;
    private String operation = "";
    private boolean startNewNumber = true;
    private DecimalFormat df = new DecimalFormat("#.##########");

    public CalculatorWithLiteGui() {
        createUI();
    }

    private void createUI() {
        setTitle("Classic Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Главная панель с BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Дисплей калькулятора
        display = new JTextField("0", 12);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        mainPanel.add(display, BorderLayout.NORTH);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new GridLayout(5, 3, 5, 5));

        // Массив кнопок в порядке как на изображении
        String[] buttonLabels = {
                "AC", "%", "/",
                "7", "8", "9",
                "4", "5", "6",
                "1", "2", "3",
                "+", "0", "="
        };

        // Создаем кнопки и добавляем обработчики
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setFocusPainted(false);

            // Разные цвета для разных типов кнопок
            if (label.matches("[0-9]")) {
                button.setBackground(new Color(240, 240, 240));
            } else if (label.equals("AC")) {
                button.setBackground(new Color(255, 150, 150));
            } else {
                button.setBackground(new Color(200, 200, 255));
            }

            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.matches("[0-9]")) {
                // Обработка цифр
                if (startNewNumber) {
                    display.setText(command);
                    startNewNumber = false;
                } else {
                    display.setText(display.getText() + command);
                }
            } else if (command.equals(".")) {
                // Обработка десятичной точки
                if (startNewNumber) {
                    display.setText("0.");
                    startNewNumber = false;
                } else if (!display.getText().contains(".")) {
                    display.setText(display.getText() + ".");
                }
            } else if (command.matches("[+\\-*/%]")) {
                // Обработка операций
                if (!operation.isEmpty()) {
                    calculateResult();
                }
                firstNumber = Double.parseDouble(display.getText());
                operation = command;
                startNewNumber = true;
            } else if (command.equals("=")) {
                // Вычисление результата
                if (!operation.isEmpty()) {
                    calculateResult();
                    operation = "";
                }
            } else if (command.equals("AC")) {
                // Сброс калькулятора
                display.setText("0");
                firstNumber = 0;
                operation = "";
                startNewNumber = true;
            } else if (command.equals("%")) {
                // Процент
                double num = Double.parseDouble(display.getText());
                display.setText(df.format(num / 100));
            }
        }

        private void calculateResult() {
            double secondNumber = Double.parseDouble(display.getText());
            double result = 0;

            switch (operation) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "*":
                    result = firstNumber * secondNumber;
                    break;
                case "/":
                    if (secondNumber != 0) {
                        result = firstNumber / secondNumber;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }

            display.setText(df.format(result));
            startNewNumber = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                CalculatorWithLiteGui calculator = new CalculatorWithLiteGui();
                calculator.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}