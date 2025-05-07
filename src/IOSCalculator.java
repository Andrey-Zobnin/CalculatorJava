import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IOSCalculator extends JFrame {
    private JTextField display;
    private JTextField expressionDisplay;
    private JLabel angleModeLabel;
    private double currentNumber = 0;
    private double storedNumber = 0;
    private String currentOperation = "";
    private boolean newInput = true;
    private boolean isRadians = false;
    private DecimalFormat df = new DecimalFormat("#.##########");
    private double memory = 0;
    private String currentExpression = "";

    // Caches for optimization
    private Map<Double, Double> sinCache = new HashMap<>();
    private Map<Double, Double> cosCache = new HashMap<>();
    private Map<Double, Double> tanCache = new HashMap<>();
    private Map<String, Double> powerCache = new HashMap<>();

    public IOSCalculator() {
        createUI();
    }

    private void createUI() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Display panel
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.BLACK);

        expressionDisplay = new JTextField();
        expressionDisplay.setHorizontalAlignment(JTextField.RIGHT);
        expressionDisplay.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));
        expressionDisplay.setEditable(false);
        expressionDisplay.setBackground(Color.BLACK);
        expressionDisplay.setForeground(new Color(150, 150, 150));
        expressionDisplay.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        displayPanel.add(expressionDisplay, BorderLayout.NORTH);

        display = new JTextField("0");
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Helvetica Neue", Font.BOLD, 48));
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5));
        displayPanel.add(display, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.BLACK);
        angleModeLabel = new JLabel(isRadians ? "RAD" : "DEG");
        angleModeLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 14));
        angleModeLabel.setForeground(new Color(150, 150, 150));
        angleModeLabel.setHorizontalAlignment(JLabel.RIGHT);
        statusPanel.add(angleModeLabel, BorderLayout.EAST);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        displayPanel.add(statusPanel, BorderLayout.SOUTH);

        mainPanel.add(displayPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(7, 5, 10, 10));
        buttonPanel.setBackground(Color.BLACK);

        String[][] buttonLabels = {
                {"2nd", "Deg/Rad", "m+", "m-", "mr"},
                {"mc", "Rand", "(", ")", "÷"},
                {"x²", "x³", "10^x", "e^x", "sin"},
                {"7", "8", "9", "×", "cos"},
                {"4", "5", "6", "-", "tan"},
                {"1", "2", "3", "+", "√"},
                {"±", "0", ".", "=", "AC"}
        };

        for (String[] row : buttonLabels) {
            for (String label : row) {
                JButton button = createButton(label);
                button.addActionListener(new ButtonClickListener());

                if (label.equals("0")) {
                    JPanel zeroPanel = new JPanel(new BorderLayout());
                    zeroPanel.setBackground(Color.BLACK);
                    zeroPanel.add(button, BorderLayout.CENTER);
                    buttonPanel.add(zeroPanel);
                } else {
                    buttonPanel.add(button);
                }
            }
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
        pack();
        setSize(500, 700);
        setLocationRelativeTo(null);
    }

    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Helvetica Neue", Font.PLAIN, 20));

        // Create final copy for inner class
        final Color buttonBgColor = determineButtonColor(label);
        button.setBackground(buttonBgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(brightenColor(buttonBgColor));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(buttonBgColor);
            }
        });

        return button;
    }

    private Color determineButtonColor(String label) {
        if (label.matches("AC|mc|mr|m\\+|m\\-")) {
            return new Color(165, 165, 165);
        } else if (label.matches("÷|×|\\-|\\+|√|=")) {
            return new Color(255, 159, 11);
        } else if (label.matches("2nd|Deg/Rad|sin|cos|tan|x²|x³|10^x|e^x")) {
            return new Color(70, 70, 70);
        }
        return new Color(51, 51, 51);
    }

    private Color brightenColor(Color color) {
        int r = Math.min(color.getRed() + 30, 255);
        int g = Math.min(color.getGreen() + 30, 255);
        int b = Math.min(color.getBlue() + 30, 255);
        return new Color(r, g, b);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String command = source.getText();

            updateExpression(command);

            switch (command) {
                case "0": case "1": case "2": case "3": case "4":
                case "5": case "6": case "7": case "8": case "9":
                    handleNumber(command);
                    break;

                case ".":
                    handleDecimal();
                    break;

                case "+": case "-": case "×": case "÷":
                    handleOperator(command);
                    break;

                case "=":
                    handleEquals();
                    break;

                case "AC":
                    handleAllClear();
                    break;

                case "±":
                    handleSignChange();
                    break;

                case "√":
                    handleSquareRoot();
                    break;

                case "x²":
                    handlePower(2);
                    break;

                case "x³":
                    handlePower(3);
                    break;

                case "10^x":
                    handlePower(10);
                    break;

                case "e^x":
                    handleExponential();
                    break;

                case "sin": case "cos": case "tan":
                    handleTrigFunction(command);
                    break;

                case "Deg/Rad":
                    toggleAngleUnit();
                    break;

                case "Rand":
                    generateRandom();
                    break;

                case "m+": case "m-": case "mr": case "mc":
                    handleMemory(command);
                    break;
            }
        }

        private void updateExpression(String command) {
            if (command.matches("[0-9]|\\.")) {
                currentExpression += command;
            } else if (command.matches("[+\\-×÷]")) {
                currentExpression += " " + command + " ";
            } else if (command.equals("=")) {
                currentExpression += " = ";
            }
            expressionDisplay.setText(currentExpression);
        }

        private void handleNumber(String number) {
            if (newInput) {
                display.setText(number);
                newInput = false;
            } else {
                display.setText(display.getText() + number);
            }
            currentNumber = Double.parseDouble(display.getText());
        }

        private void handleDecimal() {
            if (!display.getText().contains(".")) {
                display.setText(display.getText() + ".");
                newInput = false;
            }
        }

        private void handleOperator(String op) {
            if (!currentOperation.isEmpty()) calculate();
            storedNumber = currentNumber;
            currentOperation = op;
            newInput = true;
        }

        private void handleEquals() {
            if (!currentOperation.isEmpty()) calculate();
            currentOperation = "";
            newInput = true;
        }

        private void handleAllClear() {
            display.setText("0");
            expressionDisplay.setText("");
            currentNumber = 0;
            storedNumber = 0;
            currentOperation = "";
            currentExpression = "";
            newInput = true;
        }

        private void handleSignChange() {
            currentNumber = -currentNumber;
            updateDisplay();
        }

        private void handleSquareRoot() {
            if (currentNumber >= 0) {
                currentNumber = Math.sqrt(currentNumber);
                updateDisplay();
            } else {
                showError();
            }
        }

        private void handlePower(int exponent) {
            currentNumber = cachedPower(currentNumber, exponent);
            updateDisplay();
        }

        private void handleExponential() {
            currentNumber = Math.exp(currentNumber);
            updateDisplay();
        }

        private void handleTrigFunction(String func) {
            double angle = isRadians ? currentNumber : Math.toRadians(currentNumber);
            switch (func) {
                case "sin": currentNumber = cachedSin(angle); break;
                case "cos": currentNumber = cachedCos(angle); break;
                case "tan": currentNumber = cachedTan(angle); break;
            }
            updateDisplay();
        }

        private void toggleAngleUnit() {
            isRadians = !isRadians;
            angleModeLabel.setText(isRadians ? "RAD" : "DEG");
        }

        private void generateRandom() {
            currentNumber = new Random().nextDouble();
            updateDisplay();
        }

        private void handleMemory(String operation) {
            switch (operation) {
                case "m+": memory += currentNumber; break;
                case "m-": memory -= currentNumber; break;
                case "mr": currentNumber = memory; updateDisplay(); break;
                case "mc": memory = 0; break;
            }
        }

        private void calculate() {
            try {
                switch (currentOperation) {
                    case "+": currentNumber = storedNumber + currentNumber; break;
                    case "-": currentNumber = storedNumber - currentNumber; break;
                    case "×": currentNumber = storedNumber * currentNumber; break;
                    case "÷":
                        if (currentNumber == 0) throw new ArithmeticException();
                        currentNumber = storedNumber / currentNumber;
                        break;
                }
                updateDisplay();
            } catch (ArithmeticException ex) {
                showError();
            }
        }

        private void updateDisplay() {
            display.setText(df.format(currentNumber));
            newInput = true;
        }

        private void showError() {
            display.setText("Error");
            newInput = true;
            currentNumber = 0;
            storedNumber = 0;
            currentOperation = "";
        }
    }

    // Cached methods
    private double cachedSin(double angle) {
        return sinCache.computeIfAbsent(angle, Math::sin);
    }

    private double cachedCos(double angle) {
        return cosCache.computeIfAbsent(angle, Math::cos);
    }

    private double cachedTan(double angle) {
        return tanCache.computeIfAbsent(angle, Math::tan);
    }

    private double cachedPower(double base, double exponent) {
        String key = base + "^" + exponent;
        return powerCache.computeIfAbsent(key, k -> Math.pow(base, exponent));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new IOSCalculator().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}