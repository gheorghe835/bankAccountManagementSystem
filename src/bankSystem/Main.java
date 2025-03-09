package bankSystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Map<String,Double> exchangeRates = new HashMap<>();
    private static final BankManager bankManager = new BankManager();
    // Simulăm cursul valutar
    static  {
        exchangeRates.put("EURO",19.45);
        exchangeRates.put("USD",18.5);
        exchangeRates.put("GBP",22.1);
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running){
            //Afisarea mesajului de salutare
            System.out.println("\n====================================");
            System.out.printf("   Va saluta BANCA COMERCIALA %n");
            System.out.printf("   Azi: %s%n", LocalDate.now());
            System.out.println("   CURS VALUTAR ");
            displayExchangeRates();
            System.out.println("\n====================================");
            //meniul principal
            System.out.printf("%n1. ClientAccount.%n");
            System.out.printf("2. Manager.%n");
            System.out.printf("3. Iesire%n");
            System.out.print("Alegeti o optiune.");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option){
                case 1:
                    System.out.printf("%nAutentificare");
                    break;
                case 2:
                    authenticateManager(scanner);
                    break;
                case 3:
                    System.out.printf("%nMulțumim că ați vizitat Banca Comercială. O zi frumoasă!%n");
                    running = false;
                    break;
                default:
                    System.out.printf("%nOpțiune invalidă! Vă rugăm să alegeți din nou.%n");
            }
        }
    }

    // Afișarea cursului valutar
    private static void displayExchangeRates() {
        for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
            System.out.printf("   %s - %.2f RON%n", entry.getKey(), entry.getValue());
        }
    }

    // Autentificarea managerului
    private static void authenticateManager(Scanner scanner) {
        final String MANAGER_USERNAME = "admin";
        final String MANAGER_PASSWORD = "1234";
        int attempts = 0;

        while (attempts < 3) {
            System.out.printf("%nIntroduceți numele managerului: ");
            String username = scanner.nextLine();

            System.out.printf("Introduceți parola: ");
            String password = scanner.nextLine();

            if (username.equals(MANAGER_USERNAME) && password.equals(MANAGER_PASSWORD)) {
                System.out.printf("%nAutentificare reușită! Se deschide meniul managerului...%n");
                openManagerMenu(scanner);
                return;
            } else {
                attempts++;
                System.out.printf("%nAutentificare eșuată! Încercare %d din 3.%n", attempts);
            }
        }
        System.out.printf("%nContul managerului a fost blocat temporar. Ieșire din sistem...%n");
    }

    // Meniul managerului
    private static void openManagerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.printf("%n======= Meniu Manager =======%n");
            System.out.printf("1. Adăugare cont bancar nou%n");
            System.out.printf("2. Afișare toate conturile%n");
            System.out.printf("3. Căutare cont bancar%n");
            System.out.printf("4. Ștergere cont bancar%n");
            System.out.printf("5. Actualizare curs valutar%n");
            System.out.printf("6. Deconectare%n");
            System.out.print("Alegeți o opțiune: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1:
                    bankManager.addBankAccount(scanner);
                    break;
                case 2:
                    bankManager.displayAllAccounts();
                    break;
                case 3:
                    bankManager.searchAccount(scanner);
                    break;
                case 4:
                    bankManager.deleteAccount(scanner);
                    break;
                case 5:
                    updateExchangeRates(scanner);
                    break;
                case 6:
                    System.out.printf("%nDeconectare reușită!%n");
                    running = false;
                    break;
                default:
                    System.out.printf("%nOpțiune invalidă! Vă rugăm să alegeți din nou.%n");
            }
        }
    }

    // Actualizarea cursului valutar
    private static void updateExchangeRates(Scanner scanner) {
        System.out.printf("%nIntroduceți noul curs valutar:%n");
        for (String currency : exchangeRates.keySet()) {
            System.out.printf("%s: ", currency);
            double newRate = scanner.nextDouble();
            exchangeRates.put(currency, newRate);
        }
        System.out.printf("%nCursul valutar a fost actualizat cu succes!%n");
    }

}
