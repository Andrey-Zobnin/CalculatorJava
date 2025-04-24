import java.lang.*;
import java.util.Scanner;

public class CalculatroOnCase {
    public static void main(String[] args) {
        double num1 = 0, num2 = 0;
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter nums:");
        num1 = scan.nextDouble();
        num2 = scan.nextDouble();
        System.out.println("Enter the operator (+,-,*,/):");

        char operation = scan.next().charAt(0);
        double currentOperation = 0;

        switch (operation) {
            case '+':
                currentOperation = num1 + num2;
                break;

            case '-':
                currentOperation = num1 - num2;
                break;

            case '*':
                currentOperation = num1 * num2;
                break;

            case '/':
                currentOperation = num1 / num2;
                break;

            default:
                System.out.println("Wrong.. 1");
                System.out.println("Stop process");
                break;
        }

        System.out.println("Result: ");
        System.out.println();
        System.out.println(num1 + " " + operation + " " + num2 + " = " + currentOperation);
    }
}
