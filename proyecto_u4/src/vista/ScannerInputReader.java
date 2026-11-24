package vista;

import java.util.Objects;
import java.util.Scanner;

public class ScannerInputReader implements ConsoleInputReader {

    private final Scanner scanner;

    public ScannerInputReader(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "El Scanner no puede ser nulo.");
    }

    @Override
    public String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    @Override
    public int leerEntero(String mensaje) {
        while (true) {
            String entrada = leerLinea(mensaje).trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor ingrese un número entero.");
            }
        }
    }
}
