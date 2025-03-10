package bankSystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Map<String,Double> exchangeRates = new HashMap<>();
    private static final BankManager bankManager = new BankManager();
    private static final Map<String, Integer> failedAttempts = new HashMap<>();
    private static final Map<String, Long> blockedAccounts = new HashMap<>();
    // Simulăm cursul valutar
    static  {
        exchangeRates.put("EUR",19.45);
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
            System.out.printf("%n1. Introduceti datele:.%n");
            System.out.printf("2. Iesire din aplicatie.%n");
            //System.out.printf("3. Iesire%n");
            System.out.print("Alegeti o optiune.");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option){
                case 1:
                    authenticateClient(scanner);
                    break;
                case 2:
                    System.out.printf("%nMulțumim că ați vizitat Banca Comercială. O zi frumoasă!%n");
                    running = false;
                    break;
                case 9:
                    authenticateManager(scanner);
                    break;
                default:
                    System.out.printf("%nOpțiune invalidă! Vă rugăm să alegeți din nou.%n");
            }
        }
    }

    // Afișarea cursului valutar
    private static void displayExchangeRates() {
        for (Map.Entry<String, Double> entry : exchangeRates.entrySet()) {
            System.out.printf("   %s - %.2f MDL%n", entry.getKey(), entry.getValue());
        }
    }

    // Autentificarea managerului
    private static void authenticateManager(Scanner scanner) {
        final String MANAGER_USERNAME = "a";
        final String MANAGER_PASSWORD = "1";
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

    //autentificarea clientului
    private static void authenticateClient(Scanner scanner) {
        boolean validCard = false;
        String cardNumber = "";
        int attempts = 0;

        while (!validCard && attempts < 3) {
            System.out.printf("%nIntroduceți numărul cardului: ");
            cardNumber = scanner.nextLine();

            if (blockedAccounts.containsKey(cardNumber)) {
                long remainingTime = (blockedAccounts.get(cardNumber) - System.currentTimeMillis()) / 1000;
                if (remainingTime > 0) {
                    System.out.printf("%nContul este blocat. Încercați din nou în %d secunde.%n", remainingTime);
                    return;
                } else {
                    blockedAccounts.remove(cardNumber);
                    failedAttempts.put(cardNumber, 0);
                }
            }

            if (bankManager.findAccount(cardNumber) == null) {
                attempts++;
                failedAttempts.put(cardNumber, failedAttempts.getOrDefault(cardNumber, 0) + 1);
                System.out.printf("%nNumărul cardului este invalid! Încercare %d din 3.%n", attempts);

                if (failedAttempts.get(cardNumber) >= 3) {
                    blockedAccounts.put(cardNumber, System.currentTimeMillis() + 30000);
                    System.out.printf("%nContul a fost blocat timp de 30 secunde.%n");
                    return;
                }
            } else {
                validCard = true;
                failedAttempts.put(cardNumber, 0);
            }
        }

        if (!validCard) {
            System.out.printf("%nAutentificare eșuată! Ați depășit numărul maxim de încercări.%n");
            return;
        }

        System.out.printf("Introduceți parola: ");
        String password = scanner.nextLine();
        BankAccount account = bankManager.findAccount(cardNumber);

        if (account != null && account.verifyPassword(password)) {
            System.out.printf("%nAutentificare reușită! Ultima autentificare: %s%n",
                    account.getLastLogin() == null ? "Prima autentificare" : account.getLastLogin());
            account.updateLastLogin();
            openClientMenu(scanner, account);
        } else {
            System.out.printf("%nAutentificare eșuată! Introduceți datele corecte.%n");
        }
    }

    //meniul clientului
    private static void openClientMenu(Scanner scanner,BankAccount bankAccount){
        boolean running = true;

        while (running){
            System.out.printf("%n======= Meniu Client =======%n");
            System.out.printf("1. Afisare sold.%n");
            System.out.printf("2. Depunere bani.%n");
            System.out.printf("3. Retragere bani.%n");
            System.out.printf("4. Schimb valutar.%n");
            System.out.printf("5. EXIT.%n");
            System.out.print("Alegeti o optiune:");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option){
                case 1:
                    System.out.printf("%nSoldul actual: %.2f MDL%n",bankAccount.getBalance("MDL"));
                    bankAccount.displayBalances();
                    break;
                case 2:
                    System.out.printf("Introduceti suma si moneda de a depune: ");
                    double depositAmount = scanner.nextDouble();
                    String depositCurrency = scanner.next().toUpperCase();
                    scanner.nextLine();
                    bankAccount.deposit(depositCurrency,depositAmount);
                    break;
                case 3:
                    System.out.printf("Introduceti suma si moneda de a retrage: ");
                    double withdrawAmount = scanner.nextDouble();
                    String withdrawCurrency = scanner.next().toUpperCase();
                    scanner.nextLine();
                    bankAccount.withdraw(withdrawCurrency,withdrawAmount);
                    break;
                case 4:
                    currencyMenu(scanner,bankAccount);
                    break;
                case 5:
                    System.out.printf("%nDeconectare reusita!%n");
                    running = false;
                    break;
                default:
                    System.out.printf("%nOptiune invalida! Incercati din nou!");
            }
        }
    }

    private static void currencyMenu(Scanner scanner, BankAccount bankAccount) {
        boolean running = true;
        while (running) {
            System.out.printf("%n======= Meniu Schimb Valutar =======%n");
            System.out.printf("1. Schimb valutar%n");
            System.out.printf("2. Cumpărare valută%n");
            System.out.printf("3. Înapoi la meniul client%n");
            System.out.print("Alegeți o opțiune: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    performCurrencyExchange(scanner, bankAccount);
                    break;
                case 2:
                    buyCurrency(scanner, bankAccount);
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.printf("%nOpțiune invalidă! Vă rugăm să alegeți din nou.%n");
            }
        }
    }

    //schimbul valutar
    private static void buyCurrency(Scanner scanner, BankAccount bankAccount) {
        System.out.printf("%nIntroduceți moneda pe care doriți să o cumpărați: ");
        String currency = scanner.next().trim().toUpperCase();

        if (!exchangeRates.containsKey(currency)) {
            System.out.printf("%nMoneda introdusă nu este suportată.%n");
            return;
        }

        System.out.printf("%nIntroduceți suma de %s pe care doriți să o cumpărați: ", currency);
        double amount = scanner.nextDouble();
        scanner.nextLine();

        double requiredMDL = amount * exchangeRates.get(currency);

        if (bankAccount.getBalance("MDL") >= requiredMDL) {
            System.out.printf("%nConfirmati cumpărarea: %.2f MDL -> %.2f %s (Da/Nu): ", requiredMDL, amount, currency);
            String confirmation = scanner.nextLine().trim().toUpperCase();

            if (confirmation.equals("DA")) {
                bankAccount.withdraw("MDL", requiredMDL);
                bankAccount.addCurrency(currency, amount);
                System.out.printf("%nCumpărare reușită. Ați primit %.2f %s.%n", amount, currency);
                bankAccount.displayBalances();
            } else {
                System.out.printf("%nCumpărarea a fost anulată.%n");
            }
        } else {
            System.out.printf("%nFonduri insuficiente. Aveți nevoie de %.2f MDL.%n", requiredMDL);
        }
    }

    private static void performCurrencyExchange(Scanner scanner, BankAccount bankAccount) {
        System.out.printf("%nCursul valutar actual: %n");
        displayExchangeRates();

        System.out.printf("%nIntroduceți suma și moneda pe care doriți să o schimbați (ex: 34 EUR): ");
        double amount = scanner.nextDouble();
        String fromCurrency = scanner.next().trim().toUpperCase();

        if (!exchangeRates.containsKey(fromCurrency) && !fromCurrency.equals("MDL")) {
            System.out.printf("%nMoneda introdusă nu este suportată.%n");
            return;
        }

        System.out.printf("%nIntroduceți moneda în care doriți să schimbați: ");
        String toCurrency = scanner.next().trim().toUpperCase();

        if (!exchangeRates.containsKey(toCurrency) && !toCurrency.equals("MDL")) {
            System.out.printf("%nMoneda introdusă nu este suportată.%n");
            return;
        }

        double exchangeAmount;
        if (fromCurrency.equals("MDL")) {
            exchangeAmount = amount / exchangeRates.get(toCurrency);
        } else if (toCurrency.equals("MDL")) {
            exchangeAmount = amount * exchangeRates.get(fromCurrency);
        } else {
            exchangeAmount = (amount * exchangeRates.get(fromCurrency)) / exchangeRates.get(toCurrency);
        }

        System.out.printf("%nConfirmati schimbul: %.2f %s -> %.2f %s (Da/Nu): ", amount, fromCurrency, exchangeAmount, toCurrency);
        scanner.nextLine();
        String confirmation = scanner.nextLine().trim().toUpperCase();

        if (confirmation.equals("DA")) {
            bankAccount.withdraw(fromCurrency, amount);
            bankAccount.addCurrency(toCurrency, exchangeAmount);
            System.out.printf("%nSchimb valutar reușit. Ați primit %.2f %s.%n", exchangeAmount, toCurrency);
            bankAccount.displayBalances();
        } else {
            System.out.printf("%nSchimbul a fost anulat.%n");
        }
    }
}








