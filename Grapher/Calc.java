import java.util.Scanner;

public class Calc {
    public static void main() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your two vectors:");
        double u1, u2, u3, v1, v2, v3;
        u1 = scanner.nextDouble();
        u2 = scanner.nextDouble();
        u3 = scanner.nextDouble();
        v1 = scanner.nextDouble();
        v2 = scanner.nextDouble();
        v3 = scanner.nextDouble();
        
        double w1, w2, w3;
        w1 = (u2 * v3) - (u3 * v2);
        w2 = (u3 * v1) - (u1 * v3);
        w3 = (u1 * v2) - (v1 * u2);
        System.out.println("Cross product: " + w1 + ", " + w2 + ", " + w3);
    }   
}
