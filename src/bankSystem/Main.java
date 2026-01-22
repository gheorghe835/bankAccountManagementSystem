package bankSystem;

import java.time.LocalDate;
import java.util.*;

/**
 * Clasa principală a sistemului bancar. Această clasă reprezintă punctul de intrare
 * în aplicația bancară și gestionează interacțiunea cu utilizatorul prin intermediul
 * unui meniu consolă.
 *
 * <p>Sistemul bancar oferă funcționalități pentru:
 * <ul>
 *   <li>Autentificare clienți și manageri</li>
 *   <li>Operațiuni bancare (depuneri, retrageri, transferuri)</li>
 *   <li>Schimb valutar</li>
 *   <li>Gestionare conturi (creare, ștergere, căutare)</li>
 *   <li>Rapoarte și statistici</li>
 * </ul>
 *
 * @author [Batiri Gheorghe]
 * @version 1.0
 * @since 2024
 *
 * @see BankAccount
 * @see BankManager
 */
public class Main {

    /**
     * Mapă care stochează cursurile valutare curente.
     * Cheile sunt codurile valutelor (EUR, USD, GBP, RON),
     * iar valorile sunt ratele de schimb față de MDL.
     */
    private static final Map<String, Double> exchangeRates = new HashMap<>();

    /**
     * Managerul bancar care gestionează toate conturile.
     * Acesta este responsabil pentru operațiunile administrative.
     */
    private static final BankManager bankManager = new BankManager();

    /**
     * Mapă care ține evidența încercărilor eșuate de autentificare.
     * Cheile sunt numerele de cont, iar valorile sunt numărul de încercări eșuate.
     */
    private static final Map<String, Integer> failedAttemps = new HashMap<>();

    /**
     * Mapă care stochează conturile blocate temporar.
     * Cheile sunt numerele de cont, iar valorile sunt timestamp-urile
     * până la care conturile rămân blocate.
     */
    private static final Map<String, Long> blockedAccounts = new HashMap<>();

    /**
     * Bloc static de inițializare care configurează cursurile valutare inițiale.
     * Acesta rulează o singură dată când clasa este încărcată.
     */
    static {
        exchangeRates.put("EUR", 19.45);
        exchangeRates.put("USD", 17.55);
        exchangeRates.put("GBP", 22.10);
        exchangeRates.put("RON", 4.0);
    }

    /**
     * Metoda principală care rulează aplicația bancară.
     * Aceasta afișează meniul principal și procesează alegerile utilizatorului.
     *
     * @param args Argumentele liniei de comandă (nefolosite în această aplicație)
     *
     * @throws IllegalStateException Dacă apar probleme la inițializarea conturilor
     * @see #initializeAccounts()
     * @see #displayWelcomeScreen()
     * @see #authenticateClient(Scanner)
     * @see #authenticateManager(Scanner)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        initializeAccounts();

        while (running) {
            displayWelcomeScreen();

            System.out.print("\nAlegeti o optiune :: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        authenticateClient(scanner);
                        break;

                    case 2:
                        authenticateManager(scanner);
                        break;

                    case 3:
                        displayExchangeRates();
                        break;

                    case 4:
                        System.out.println("\n📞 Contact: 022 123 456 | email: info@bancacomerciala.md");
                        break;

                    case 5:
                        System.out.println("\n🙏 Multumim ca ati vizitat Banca Comerciala. O zi frumoasa!");
                        running = false;
                        break;

                    default:
                        System.out.println("\n❌ Optiune invalida! Va rugam sa alegeti din nou.");
                }
            }
            catch (InputMismatchException e){
                System.out.println("Va rugam sa introduceti un numar valid.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    /**
     * Afișează ecranul de bun venit al băncii.
     * Această metodă afișează antetul băncii, data și ora curentă,
     * și meniul principal cu opțiuni disponibile.
     *
     * <p>Formatul afișat:
     * <pre>
     * ==================================================
     *    🏦 BANCA COMERCIALĂ
     * ==================================================
     *   Data :: [data curentă]
     *   Ora  :: [ora curentă]
     * ==================================================
     *
     * 📋 MENIU PRINCIPAL:
     *    1. 👤 Autentificare Client
     *    2. 👔 Autentificare Manager
     *    3. 💱 Curs Valutar
     *    4. 📞 Contact
     *    5. 🚪 Iesire
     * </pre>
     *
     * @see LocalDate#now()
     */
    public static void displayWelcomeScreen() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   🏦 BANCA COMERCIALĂ");
        System.out.println("\n" + "=".repeat(50));
        System.out.printf("  Data :: %s%n", LocalDate.now());
        System.out.printf("  Ora  :: %s%n", new Date().toString().split(" ")[3]);
        System.out.println("\n" + "=".repeat(50));

        System.out.println("\n📋 MENIU PRINCIPAL:");
        System.out.println("   1. 👤 Autentificare Client");
        System.out.println("   2. 👔 Autentificare Manager");
        System.out.println("   3. 💱 Curs Valutar");
        System.out.println("   4. 📞 Contact");
        System.out.println("   5. 🚪 Iesire");
    }

    /**
     * Afișează cursurile valutare curente în format tabelar.
     * Monedele sunt sortate alfabetic și afișate împreună cu cursul lor față de MDL.
     * De asemenea, se afișează un exemplu de conversie pentru EUR.
     *
     * <p>Exemplu de afișare:
     * <pre>
     * ────────────────────────────────────────
     *    💱 CURS VALUTAR
     * ────────────────────────────────────────
     *    Moneda  │  Curs (MDL)
     *   ─────────┼──────────
     *   EUR      |     19.45
     *   GBP      |     22.10
     *   RON      |      4.00
     *   USD      |     17.55
     * ────────────────────────────────────────
     *    💡 Exemplu: 100 EUR = 1945.0 MDL
     * </pre>
     *
     * @see #exchangeRates
     */
    private static void displayExchangeRates() {
        System.out.println("\n" + "─".repeat(40));
        System.out.println("   💱 CURS VALUTAR");
        System.out.println("─".repeat(40));
        System.out.println("   Moneda  │  Curs (MDL)");
        System.out.println("  ─────────┼──────────");

        List<Map.Entry<String, Double>> sortedRates = new ArrayList<>(exchangeRates.entrySet());
        sortedRates.sort(Map.Entry.comparingByKey());

        for (Map.Entry<String, Double> entry : sortedRates) {
            System.out.printf("  %-7s | %10.2f%n", entry.getKey(), entry.getValue());
        }

        System.out.println("-".repeat(40));
        System.out.println("   💡 Exemplu: 100 EUR = " + (100 * exchangeRates.get("EUR")) + " MDL");
    }

    /**
     * Autentifică un manager bancar. Managerul trebuie să introducă
     * numele de utilizator și parola corecte pentru a accesa meniul de administrare.
     *
     * <p>După 3 încercări eșuate, sistemul blochează accesul temporar.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @see #openManagerMenu(Scanner)
     *
     * @throws InterruptedException Dacă firul de execuție este întrerupt în timpul așteptării
     */
    private static void authenticateManager(Scanner scanner){
        final String MANAGER_USERNAME = "admin";
        final String MANAGER_PASSWORD = "Admin1234";
        int attempts = 0;

        System.out.println("\n" + "=".repeat(40));
        System.out.println("   👔 AUTENTIFICARE MANAGER");
        System.out.println("\n" + "=".repeat(40));

        while (attempts < 3){
            System.out.print("\n🔑 Utilizator: ");
            String username = scanner.nextLine();
            System.out.print("🔒 Parola: ");
            String password = scanner.nextLine();

            if (username.equals(MANAGER_USERNAME) && password.equals(MANAGER_PASSWORD)){
                System.out.println("\n✅ Autentificare reusita!");
                openManagerMenu(scanner);
                return;
            }
            else {
                attempts++;
                System.out.printf("\n❌ Autentificare esuata! Incercare %d/3%n", attempts);

                if (attempts >= 3){
                    System.out.println("\n🚫 Cont blocat temporar. Contactati administratorul.");

                    try {
                        Thread.sleep(3000);//asteapta 3 secunde
                    }
                    catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                    }

                }
            }
        }
    }

    /**
     * Deschide meniul de administrare pentru manageri autentificați.
     * Managerii pot efectua operațiuni administrative precum crearea de conturi,
     * afișarea tuturor conturilor, generarea de rapoarte etc.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     *
     * @see BankManager#addBankAccount(Scanner)
     * @see BankManager#displayAllAccounts()
     * @see BankManager#searchAccount(Scanner)
     * @see BankManager#deleteAccount(Scanner)
     * @see #updateExchangeRates(Scanner)
     * @see BankManager#generateBalanceReport(Map)
     * @see BankManager#applyInterestToAllAccounts(double)
     * @see BankManager#displayInactiveAccounts()
     */
    private static void openManagerMenu(Scanner scanner){
        boolean running = true;

        while (running){
            System.out.println("\n" + "═".repeat(40));
            System.out.println("   📊 MENIU MANAGER");
            System.out.println("═".repeat(40));
            System.out.println("   1. ➕ Adaugare cont nou");
            System.out.println("   2. 👁️  Afisare toate conturile");
            System.out.println("   3. 🔍 Cautare cont");
            System.out.println("   4. 🗑️  Stergere cont");
            System.out.println("   5. 💱 Actualizare curs valutar");
            System.out.println("   6. 📈 Raport solduri totale");
            System.out.println("   7. 🏦 Aplicare dobinda");
            System.out.println("   8. 🔴 Conturi inactive");
            System.out.println("   9. 🚪 Deconectare");
            System.out.println("═".repeat(40));
            System.out.print("\n👉 Alegeti optiunea: ");

            try {
                int option = scanner.nextInt();scanner.nextLine();

                switch (option){
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
                        bankManager.generateBalanceReport(exchangeRates);
                        break;

                    case 7:
                        System.out.print("Introduceti procentul dobinzii anuale :: ");
                        double rate = scanner.nextDouble();scanner.nextLine();

                        bankManager.applyInterestToAllAccounts(rate);
                        break;

                    case 8:
                        bankManager.displayInactiveAccounts();
                        break;

                    case 9:
                        System.out.println("\n👋 Deconectare reușită!");
                        running = false;
                        break;

                    default:
                        System.out.println("\nOptiune invalida!");
                }
                //pauza pentru citire
                if (running && (option == 2 || option == 6)){
                    System.out.print("\n↵ Apasați Enter pentru a continua...");scanner.nextLine();
                }
            }
            catch (InputMismatchException e){
                System.out.println("va rugam sa introduceti un numar valid.");scanner.nextLine();
            }
        }
    }

    /**
     * Actualizează cursurile valutare. Managerul poate modifica cursul
     * pentru fiecare valută suportată.
     *
     * @param scanner Scanner pentru citirea noilor cursuri valutare
     * @see #exchangeRates
     * @see #displayExchangeRates()
     */
    private static void updateExchangeRates(Scanner scanner){
        System.out.println("\n" + "═".repeat(40));
        System.out.println("   💱 ACTUALIZARE CURS VALUTAR");
        System.out.println("═".repeat(40));

        for (String currency : exchangeRates.keySet()){
            System.out.printf("\nCurs actual %s:: %.2f MDL%n",currency,exchangeRates.get(currency));
            System.out.printf("Noul curs pentru %s:: ",currency);
            double newRate = scanner.nextDouble();
            exchangeRates.put(currency,newRate);
        }

        System.out.println("\n✅ Cursul valutar a fost actualizat!");

        displayExchangeRates();
    }

    /**
     * Autentifică un client bancar. Clientul trebuie să introducă
     * numărul de cont și parola corecte.
     *
     * <p>Sistemul implementează măsuri de securitate:
     * <ul>
     *   <li>Blochează contul după 3 încercări eșuate pentru 30 de secunde</li>
     *   <li>Ține evidența încercărilor eșuate</li>
     *   <li>Actualizează data ultimei autentificări</li>
     * </ul>
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @see BankManager#findAccount(String)
     * @see BankAccount#verifyPassword(String)
     * @see BankAccount#updateLastLogin()
     * @see #openClientMenu(Scanner, BankAccount)
     */
    private static void authenticateClient(Scanner scanner){
        System.out.println("\n" + "═".repeat(40));
        System.out.println("   👤 AUTENTIFICARE CLIENT");
        System.out.println("═".repeat(40));

        System.out.print("\n🔢 Numar card/cont: ");
        String accountNumber = scanner.nextLine();

        //verificare cont blocat
        if (blockedAccounts.containsKey(accountNumber)){
            long remainingTime = (blockedAccounts.get(accountNumber) - System.currentTimeMillis() / 1000);
            if (remainingTime > 0){
                System.out.printf("\n🚫 Cont blocat! Incercați din nou în %d secunde.%n", remainingTime);
                return;
            }
            else {
                blockedAccounts.remove(accountNumber);
                failedAttemps.remove(accountNumber);
            }
        }

        BankAccount account = bankManager.findAccount(accountNumber);
        if (account == null){
            failedAttemps.merge(accountNumber,1,Integer::sum);
            int attempts = failedAttemps.get(accountNumber);

            System.out.printf("\nCont inexistent! Incercare %d/3%n",attempts);

            if (attempts >= 3){
                blockedAccounts.put(accountNumber,System.currentTimeMillis() + 30000);
                System.out.println("\nCont blocat pentru 30 de secunde.");
            }
            return;
        }
        System.out.print("🔒 Parola: ");
        String password = scanner.nextLine();

        if (!account.verifyPassword(password)){
            failedAttemps.merge(accountNumber,1,Integer::sum);
            int attempts = failedAttemps.get(accountNumber);

            System.out.printf("\nParola incorecta! Inceercare %d/3%n",attempts);

            if (attempts >= 3){
                blockedAccounts.put(accountNumber,System.currentTimeMillis() + 30000);
                System.out.println("Cont blocat pentru 30 de secunde.");
            }
            return;
        }

        //autentificare reusita
        failedAttemps.remove(accountNumber);
        account.updateLastLogin();

        System.out.println("\n✅ Autentificare reușită!");
        System.out.printf("👋 Bun venit, %s!%n", account.getOwnerName());

        if (account.getLastLogin() != null){
            System.out.printf("Ultima autentificare :: %s%n",account.getLastLogin());
        }
        openClientMenu(scanner,account);
    }

    /**
     * Deschide meniul principal pentru clienți autentificați.
     * Clienții pot efectua operațiuni bancare și pot gestiona conturile lor.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param account Contul bancar al clientului autentificat
     *
     * @see BankAccount#displayAccountInfo()
     * @see #displayBalanceMenu(Scanner, BankAccount)
     * @see #performDeposit(Scanner, BankAccount)
     * @see #performWithdrawal(Scanner, BankAccount)
     * @see #performTransfer(Scanner, BankAccount)
     * @see #performCurrencyExchange(Scanner, BankAccount)
     * @see #openAccountSettings(Scanner, BankAccount)
     */
    private static void openClientMenu(Scanner scanner,BankAccount account) {
        boolean running = true;

        while (running) {
            System.out.println("\n" + "═".repeat(40));
            System.out.println("   🏦 MENIU CLIENT");
            System.out.printf("   👤 %s%n", account.getOwnerName());
            System.out.printf("   💳 %s%n", account.getAccountNumber());
            System.out.println("═".repeat(40));

            System.out.println("   1. 👁️  Informatii cont");
            System.out.println("   2. 💰 Solduri si tranzactii");
            System.out.println("   3. 📥 Depunere");
            System.out.println("   4. 📤 Retragere");
            System.out.println("   5. 🔄 Transfer");
            System.out.println("   6. 💱 Schimb valutar");
            System.out.println("   7. ⚙️  Setari cont");
            System.out.println("   8. 🚪 Deconectare");
            System.out.println("═".repeat(40));

            System.out.print("\n👉 Alegeti optiunea: ");

            try {
                int option = scanner.nextInt();scanner.nextLine();
                switch (option) {
                    case 1:
                        account.displayAccountInfo();
                        break;

                    case 2:
                        displayBalanceMenu(scanner, account);
                        break;

                    case 3:
                        performDeposit(scanner, account);
                        break;

                    case 4:
                        performWithdrawal(scanner, account);
                        break;

                    case 5:
                        performTransfer(scanner, account);
                        break;

                    case 6:
                        performCurrencyExchange(scanner, account);
                        break;

                    case 7:
                        openAccountSettings(scanner, account);
                        break;

                    case 8:
                        System.out.println("\n👋 Deconectare reusita! Vă asteptam din nou!");
                        running = false;
                        break;

                    default:
                        System.out.println("Optiune invalida.");
                }
            }
            catch (InputMismatchException e){
                System.out.println("Va rugam sa introduceti un numar valid.");scanner.nextLine();
            }
        }
    }

    /**
     * Afișează meniul pentru vizualizarea soldurilor și tranzacțiilor.
     * Clienții pot vedea soldurile curente, istoricul tranzacțiilor,
     * extrase de cont și totalul în MDL.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param account Contul bancar al clientului
     *
     * @see BankAccount#displayBalances()
     * @see BankAccount#displayTransactionHistory(int)
     * @see BankAccount#generateAccountStatement(LocalDate, LocalDate, Map)
     * @see BankAccount#getTotalBalanceInMDL(Map)
     */
    private static void displayBalanceMenu(Scanner scanner,BankAccount account){
        boolean viewing = true;

        while (viewing){
            System.out.println("\n" + "─".repeat(40));
            System.out.println("   💰 SOLDURI SI TRANZACTII");
            System.out.println("─".repeat(40));
            System.out.println("   1. 👁️  Afisare solduri");
            System.out.println("   2. 📋 Istoric tranzactii");
            System.out.println("   3. 📊 Extras de cont");
            System.out.println("   4. 💱 Total în MDL");
            System.out.println("   5. ↩️  Inapoi");
            System.out.println("─".repeat(40));

            System.out.print("\n👉 Alegeti optiunea :: ");

            try {
                int choice = scanner.nextInt();scanner.nextLine();

                switch (choice){
                    case 1:
                        account.displayBalances();
                        break;

                    case 2:
                        System.out.print("Numar tranzactii de afisat :: ");
                        int limit = scanner.nextInt();scanner.nextLine();
                        account.displayTransactionHistory(limit);
                        break;

                    case 3:
                        System.out.print("Data de inceput(AAAA-LL-ZZ) :: ");
                        LocalDate from = LocalDate.parse(scanner.nextLine());
                        System.out.print("Data de sfirsit(AAAA-LL-ZZ) :: ");
                        LocalDate to = LocalDate.parse(scanner.nextLine());
                        account.generateAccountStatement(from,to,exchangeRates);
                        break;

                    case 4:
                        double total = account.getTotalBalanceInMDL(exchangeRates);
                        System.out.printf("\n💰 Total în MDL: %.2f MDL%n", total);
                        break;

                    case 5:
                        viewing = false;
                        break;

                    default:
                        System.out.printf("Optiune invalida.");
                }

                if (viewing && choice != 5){
                    System.out.print("\n↵ Apasati Enter pentru a continua...");scanner.nextLine();
                }
            }
            catch (InputMismatchException e){
                System.out.println("Va rugam sa introduceti un numar valid.");scanner.nextLine();
            }
        }
    }

    /**
     * Efectuează o operațiune de depunere în cont.
     * Clientul specifică moneda și suma de depus.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param account Contul în care se face depunerea
     *
     * @see BankAccount#deposit(double, String)
     *
     * @throws InputMismatchException Dacă suma introdusă nu este un număr valid
     */
    private static void performDeposit(Scanner scanner,BankAccount account){
        System.out.println("\n" + "─".repeat(40));
        System.out.println("   📥 DEPUNERE");
        System.out.println("─".repeat(40));

        System.out.print("Moneda (MDL/EUR/USD/GBP/RON): ");
        String currency = scanner.next().toUpperCase();
        System.out.print("Suma :: ");
        double amount = scanner.nextDouble();scanner.nextLine();
        if (account.deposit(amount,currency)){
            System.out.println("✅ Depunere finalizata cu succes.");
        }
    }

    /**
     * Efectuează o operațiune de retragere din cont.
     * Clientul specifică moneda și suma de retras.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param account Contul din care se face retragerea
     *
     * @see BankAccount#withdraw(double, String)
     *
     * @throws InputMismatchException Dacă suma introdusă nu este un număr valid
     */
    private static void performWithdrawal(Scanner scanner,BankAccount account){
        System.out.println("\n" + "─".repeat(40));
        System.out.println("   📤 RETRAGERE");
        System.out.println("─".repeat(40));

        System.out.print("Moneda (MDL/EUR/USD/GBP/RON): ");
        String currency = scanner.next().toUpperCase();
        System.out.print("Suma :: ");
        double amount = scanner.nextDouble();scanner.nextLine();

        if (account.withdraw(amount,currency)){
            System.out.println("✅ Retragere finalizata cu succes.");
        }
    }

    /**
     * Efectuează un transfer bancar către un alt cont.
     * Clientul specifică contul destinație, moneda, suma și o descriere.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param sourceAccount Contul sursă al transferului
     *
     * @see BankManager#findAccount(String)
     * @see BankAccount#transferTo(BankAccount, double, String, String)
     */
    private static void performTransfer(Scanner scanner,BankAccount sourceAccount){
        System.out.println("\n" + "─".repeat(40));
        System.out.println("   🔄 TRANSFER");
        System.out.println("─".repeat(40));

        System.out.print("Catre contul :: ");
        String targetAccountNumber = scanner.nextLine();

        BankAccount targetAccount = bankManager.findAccount(targetAccountNumber);
        if (targetAccount == null){
            System.out.println("Contul destinatie nu exista.");
            return;
        }

        System.out.print("Moneda :: ");
        String currency = scanner.next().toUpperCase();

        System.out.print("Suma :: ");
        double amount = scanner.nextDouble();scanner.nextLine();

        System.out.print("Descriere :: ");
        String description = scanner.nextLine();

        if (sourceAccount.transferTo(targetAccount,amount,currency,description)){
            System.out.println("✅ Transfer finaizat cu succes.");
        }
    }

    /**
     * Efectuează o operațiune de schimb valutar.
     * Clientul poate schimba bani dintr-o valută în alta.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param account Contul în care se efectuează schimbul valutar
     *
     * @see #displayExchangeRates()
     * @see BankAccount#exchangeCurrency(String, String, double, Map)
     */
    private static void performCurrencyExchange(Scanner scanner,BankAccount account){
        System.out.println("\n" + "─".repeat(40));
        System.out.println("   💱 SCHIMB VALUTAR");
        System.out.println("─".repeat(40));

        displayExchangeRates();

        System.out.print("\nDin moneda :: ");
        String from = scanner.next().toUpperCase();

        System.out.print("In moneda :: ");
        String to = scanner.next().toUpperCase();

        System.out.print("Suma de schimbat :: ");
        double amount = scanner.nextDouble();scanner.nextLine();

        if (account.exchangeCurrency(from,to,amount,exchangeRates)){
            System.out.println("✅ Schimb valutar finalizat.");
        }
    }

    /**
     * Deschide meniul de setări al contului.
     * Clientul poate modifica parola, numele, limita de retragere,
     * sau poate (de)activa contul.
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @param account Contul ale cărui setări se modifică
     *
     * @see BankAccount#changePassword(String, String)
     * @see BankAccount#setOwnerName(String)
     * @see BankAccount#setDailyWithdrawalLimit(double)
     * @see BankAccount#deactivateAccount()
     * @see BankAccount#reactivateAccount()
     */
    private static void openAccountSettings(Scanner scanner,BankAccount account){
        boolean configuring = true;

        while (configuring){
            System.out.println("\n" + "-".repeat(40));
            System.out.println("   ⚙️  SETARI CONT");
            System.out.println("─".repeat(40));
            System.out.println("   1. 🔐 Schimbare parola");
            System.out.println("   2. 👤 Schimbare nume");
            System.out.println("   3. ⚖️  Setare limita retragere");
            System.out.println("   4. 🔴 Dezactivare cont");
            System.out.println("   5. 🟢 Reactivare cont");
            System.out.println("   6. ↩️  Inapoi");
            System.out.println("─".repeat(40));

            System.out.print("\n👉 Alegeti optiunea :: ");

            try {
                int choice = scanner.nextInt();scanner.nextLine();

                switch (choice){
                    case 1:
                        System.out.print("Parola actuala :: ");
                        String oldPass = scanner.nextLine();
                        System.out.print("Parola noua :: ");
                        String newPass = scanner.nextLine();
                        account.changePassword(oldPass,newPass);
                        break;

                    case 2:
                        System.out.print("Noul nume :: ");
                        String newName = scanner.nextLine();
                        account.setOwnerName(newName);
                        break;

                    case 3:
                        System.out.print("Noua limita (MDL) :: ");
                        double newLimit = scanner.nextDouble();scanner.nextLine();
                        account.setDailyWithdrawalLimit(newLimit);
                        break;

                    case 4:
                        System.out.print("Sigur doriti sa dezactivati contul? (DA/NU) :: ");
                        if (scanner.nextLine().equalsIgnoreCase("DA")){
                            account.deactivateAccount();
                        }
                        break;

                    case 5:
                        account.reactivateAccount();
                        break;

                    case 6:
                        configuring = false;
                        break;

                    default:
                        System.out.println("Optiune invalida.");
                }
            }
            catch (InputMismatchException e) {
                System.out.println("\n Va rugam sa introduceti un numar valid!");
                scanner.nextLine();
            }
        }
    }

    /**
     * Inițializează conturile de test pentru aplicație.
     * Această metodă creează 4 conturi de test cu solduri inițiale
     * și efectuează câteva depuneri pentru a popula sistemul cu date.
     *
     * <p>Conturile create:
     * <ol>
     *   <li>Egor Batiri - 50000 MDL + 500 EUR + 1000 MDL</li>
     *   <li>Iulia Batiri - 50000 MDL + 1000 USD + 500 MDL</li>
     *   <li>Oxana Batiri - 50000 MDL + 200 GBP + 7000 MDL</li>
     *   <li>Gheorghe Batiri - 50000 MDL + 800 RON + 20000 MDL</li>
     * </ol>
     *
     * @see BankAccount
     * @see BankManager#addAccount(BankAccount)
     *
     * @throws Exception Dacă apar erori la crearea sau popularea conturilor
     */
    private static void initializeAccounts(){
        try {
            BankAccount bankAccount1 = new BankAccount("1234567890123456","Parola1234",50000.0,"Egor Batiri");
            BankAccount bankAccount2 = new BankAccount("2345678901234567","Parola5678",50000.00,"Iulia Batiri");
            BankAccount bankAccount3 = new BankAccount("3456789012345678","Parola4321",50000.00,"Oxana Batiri");
            BankAccount bankAccount4 = new BankAccount("4567890123456789","Parola9876",50000.00,"Gheorghe Batiri");

            bankManager.addAccount(bankAccount1);
            bankManager.addAccount(bankAccount2);
            bankManager.addAccount(bankAccount3);
            bankManager.addAccount(bankAccount4);

            // adaugam valute straine
            bankAccount1.deposit(500,"EUR");
            bankAccount2.deposit(1000,"USD");
            bankAccount3.deposit(200,"GBP");
            bankAccount4.deposit(800,"RON");

            //adaugam tranzactii
            bankAccount1.deposit(1000,"MDL");
            bankAccount2.deposit(500,"MDL");
            bankAccount3.deposit(7000,"MDL");
            bankAccount4.deposit(20000,"MDL");

            System.out.println("\nConturi incarcate pentru testare.");
        }catch (Exception e){
            System.out.println("Nu s-au putut incarca conturi pentru testare.");
        }
    }
}

