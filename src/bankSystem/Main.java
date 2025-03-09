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
                    authenticateClient(scanner);
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
            System.out.printf("   %s - %.2f MDL%n", entry.getKey(), entry.getValue());
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

    //autentificarea clientului
    private static void authenticateClient(Scanner scanner){
        boolean validCard = false;
        String cardNumber = "";
        int attemps = 0;

        while (!validCard){
            System.out.printf("%nIntroduceti numarul cardului: ");
            cardNumber = scanner.nextLine();
            if (bankManager.findAccount(cardNumber) == null){
                System.out.printf("%nNumarul cardului este invalid! Introduceti corect datele!%n");
            }
            else {
                validCard = true;
            }
        }

        BankAccount account = bankManager.findAccount(cardNumber);

        while (attemps < 3){
            System.out.printf("Introduceti parola:");
            String password = scanner.nextLine();
            if( account.verifyPassword(password)){
                System.out.printf("%nAutentificare reusita! %n");
                openClientMenu(scanner,account);
                return;
            }
            else {
                attemps++;
                System.out.printf("%nParola incorecta! Incercare %d din 3.%n",attemps);
            }
        }

            System.out.printf("%nAutentificare esuata! Introduceti datele corecte.%n");

    }

    //meniul clientului
    private static void openClientMenu(Scanner scanner,BankAccount bankAccount){
        boolean running = true;

        while (running){
            System.out.printf("%n======= Meniu Client =======");
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
                    performCurrencyExchange(scanner,bankAccount);
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

    //schimbul valutar
    private static void performCurrencyExchange(Scanner scanner, BankAccount bankAccount) {
        System.out.printf("%nCursul valutar actual: %n");
        displayExchangeRates();

        System.out.printf("%nIntroduceți suma și moneda pe care doriți să o schimbați (ex: 34 EUR): ");

        // Citire corectă a sumei și monedei
        double amount = scanner.nextDouble();
        String fromCurrency = scanner.next().toUpperCase();

        // Verificăm dacă moneda introdusă este validă
        if (!exchangeRates.containsKey(fromCurrency) && !fromCurrency.equals("MDL")) {
            System.out.printf("%nMoneda introdusă nu este suportată.%n");
            return;
        }

        System.out.printf("%nIntroduceți moneda în care doriți să schimbați: ");
        String toCurrency = scanner.next().toUpperCase();

        if (!exchangeRates.containsKey(toCurrency)) {
            System.out.printf("%nMoneda introdusă nu este suportată.%n");
            return;
        }

        // Verificare fonduri și conversie
        if (bankAccount.getBalance(fromCurrency) >= amount) {
            // Conversie normală dacă există fonduri suficiente în moneda inițială
            double exchangeAmount = (fromCurrency.equals("MDL")) ?
                    amount / exchangeRates.get(toCurrency) :
                    (amount * exchangeRates.get(fromCurrency)) / exchangeRates.get(toCurrency);

            System.out.printf("%nConfirmati schimbul: %.2f %s -> %.2f %s (Da/Nu): ", amount, fromCurrency, exchangeAmount, toCurrency);
            scanner.nextLine();
            String confirmation = scanner.nextLine().toUpperCase();

            if (confirmation.equals("DA")) {
                bankAccount.withdraw(fromCurrency, amount);
                bankAccount.addCurrency(toCurrency, exchangeAmount);
                System.out.printf("%nSchimb valutar reușit. Ați primit %.2f %s.%n", exchangeAmount, toCurrency);
                bankAccount.displayBalances();
            } else {
                System.out.printf("%nSchimbul a fost anulat.%n");
            }
        } else if (fromCurrency.equals("MDL")) {
            // Dacă utilizatorul vrea să cumpere o altă monedă cu MDL
            double requiredMDL = amount * exchangeRates.get(toCurrency);
            if (bankAccount.getBalance("MDL") >= requiredMDL) {
                System.out.printf("%nConfirmati schimbul: %.2f MDL -> %.2f %s (Da/Nu): ", requiredMDL, amount, toCurrency);
                scanner.nextLine();
                String confirmation = scanner.nextLine().toUpperCase();

                if (confirmation.equals("DA")) {
                    bankAccount.withdraw("MDL", requiredMDL);
                    bankAccount.addCurrency(toCurrency, amount);
                    System.out.printf("%nSchimb valutar reușit. Ați primit %.2f %s.%n", amount, toCurrency);
                    bankAccount.displayBalances();
                } else {
                    System.out.printf("%nSchimbul a fost anulat.%n");
                }
            } else {
                System.out.printf("%nFonduri insuficiente. Aveți nevoie de %.2f MDL.%n", requiredMDL);
            }
        } else {
            System.out.printf("%nFonduri insuficiente.%n");
        }
    }
        }




