import java.util.Scanner;

public class CalculatroOnCase {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    private static final char ADD = '+';
    private static final char SUBTRACT = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVIDE = '/';

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        try {
            System.out.println(YELLOW + "Enter nums:" + RESET);
            double num1 = scan.nextDouble();
            double num2 = scan.nextDouble();

            System.out.println(YELLOW + "Enter the operator (+,-,*,/):" + RESET);
            char operation = scan.next().charAt(0);

            double result = calculate(num1, num2, operation);
            displayResult(num1, num2, operation, result);

        } catch (Exception e) {
            System.out.println(RED + "Invalid input! Please enter numbers only." + RESET);
        } finally {
            scan.close();
        }
    }

    private static double calculate(double num1, double num2, char operation) {
        switch (operation) {
            case ADD:
                return num1 + num2;
            case SUBTRACT:
                return num1 - num2;
            case MULTIPLY:
                return num1 * num2;
            case DIVIDE:
                if (num2 == 0) {
                    throw new ArithmeticException("Division by zero!");
                }
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Invalid operator!");
        }
    }

    private static void displayResult(double num1, double num2, char operation, double result) {
        System.out.println(GREEN + "Result: " + RESET);
        System.out.println(BLUE + num1 + " " + operation + " " + num2 + " = " + result + RESET);
    }
}