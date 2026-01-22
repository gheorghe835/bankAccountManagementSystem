package bankSystem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Clasa BankManager gestionează operațiunile administrative și de management
 * pentru toate conturile bancare din sistem. Această clasă este responsabilă
 * pentru crearea, căutarea, ștergerea și generarea de rapoarte pentru conturi.
 *
 * <p>Funcționalități principale:
 * <ul>
 *   <li>Gestiunea tuturor conturilor bancare</li>
 *   <li>Crearea de conturi noi cu validare</li>
 *   <li>Căutare avansată a conturilor</li>
 *   <li>Ștergerea conturilor cu confirmare</li>
 *   <li>Generarea de rapoarte și statistici</li>
 *   <li>Aplicarea dobânzilor la toate conturile</li>
 * </ul>
 *
 * @author [Batiri Gheorghe]
 * @version 1.0
 * @since 2024
 *
 * @see BankAccount
 */
public class BankManager {

    /**
     * Mapă care stochează toate conturile bancare din sistem.
     * Cheile sunt numerele de cont (String), iar valorile sunt obiectele BankAccount.
     */
    private final Map<String,BankAccount> accounts;

    /**
     * Numărul total de tranzacții procesate prin manager.
     */
    private int totalTransactions;

    /**
     * Suma totală a depunerilor procesate.
     */
    private double totalDeposits;

    /**
     * Suma totală a retragerilor procesate.
     */
    private double totalWithdrawals;

    /**
     * Constructor pentru clasa BankManager.
     * Inițializează structurile de date pentru stocarea conturilor
     * și setează contoarele la zero.
     */
    public BankManager(){
        this.accounts = new HashMap<>();
        this.totalTransactions = 0;
        this.totalDeposits = 0;
        this.totalWithdrawals = 0;
    }

    /**
     * Adaugă un cont bancar existent în sistemul de management.
     * Această metodă este folosită pentru încărcarea conturilor existente.
     *
     * @param account Contul bancar de adăugat
     *
     * @see BankAccount#getAccountNumber()
     */
    public void addAccount(BankAccount account){
        accounts.put(account.getAccountNumber(),account);
    }

    /**
     * Creează și adaugă un nou cont bancar în sistem prin interacțiunea cu utilizatorul.
     * Metoda solicită datele necesare de la utilizator și validează input-ul.
     *
     * <p>Datele solicitate:
     * <ol>
     *   <li>Număr cont (16 cifre)</li>
     *   <li>Nume proprietar</li>
     *   <li>Parolă (minim 6 caractere, litere și cifre)</li>
     *   <li>Sold inițial în MDL</li>
     * </ol>
     *
     * @param scanner Scanner pentru citirea input-ului de la utilizator
     * @return true dacă contul a fost creat cu succes, false dacă a eșuat
     *
     * @throws Exception Dacă apar erori la crearea contului (e.g., date invalide)
     *
     * @see BankAccount#BankAccount(String, String, double, String)
     */
    public boolean addBankAccount(Scanner scanner){
        try {
            System.out.print("\nIntroduceti numarul contului (16 cifre) :: ");
            String accountNumber = scanner.nextLine();

            if (accounts.containsKey(accountNumber)){
                System.out.println("Exista deja un cont cu acest numar");
                return false;
            }

            System.out.print("Introduceti numele proprietarului :: ");
            String ownerName = scanner.nextLine();

            System.out.print("Introduceti parola (minim 6 caractere, litere si cifre) :: ");
            String password = scanner.nextLine();

            System.out.print("Introduceti soldul initial (MDL) :: ");
            double initialBalance = scanner.nextDouble();scanner.nextLine();

            BankAccount newAccount = new BankAccount(accountNumber,password,initialBalance,ownerName);
            accounts.put(accountNumber,newAccount);

            System.out.printf("\n✅ Cont creat cu succes!\n");
            System.out.printf("Numar :: %s%n",accountNumber);
            System.out.printf("Proprietar :: %s%n",ownerName);
            System.out.printf("Sold initial :: %.2f MDL %n",initialBalance);

            return true;
        }
        catch (Exception e){
            System.out.println("Eroare la crearea contului :: " + e.getMessage());
            return false;
        }
    }

    /**
     * Afișează toate conturile bancare din sistem în format tabelar.
     * Conturile sunt sortate după data creării și se afișează statistici generale.
     *
     * <p>Informații afișate pentru fiecare cont:
     * <ul>
     *   <li>Număr cont</li>
     *   <li>Proprietar</li>
     *   <li>Data creării</li>
     *   <li>Stare (ACTIV/INACTIV)</li>
     *   <li>Sold în MDL</li>
     * </ul>
     *
     * <p>Statistici afișate:
     * <ul>
     *   <li>Numărul de conturi active vs. total</li>
     *   <li>Soldul total MDL în toate conturile</li>
     * </ul>
     */
    public void displayAllAccounts(){
        if (accounts.isEmpty()){
            System.out.println("\n📭 Nu exista conturi bancare inregistrate.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("LISTA CONTURI BANCARE (" + accounts.size() + " conturi)");
        System.out.println("=".repeat(60));

        List<BankAccount> sortedAccounts = accounts.values().stream()
                .sorted(Comparator.comparing(BankAccount::getCreationDate))
                .collect(Collectors.toList());

        for (BankAccount account : sortedAccounts){
            System.out.printf("┌ %-20s ────────────────────────┐%n",account.getAccountNumber());
            System.out.printf("| Proprietar: %-48s |%n",account.getOwnerName());
            System.out.printf("| Creat la: %-50s |%n",account.getCreationDate());
            System.out.printf("| Stare: %-52s |%n",account.isActive() ? "🟢 ACTIV" : "🔴 INACTIV");
            System.out.printf("| Sold MDL: %-49.2f |%n",account.getBalance("MDL"));
            System.out.printf("└────────────────────────────────────────────────────────────────┘%n");
        }

        //statistici
        System.out.println("\n📊 STATISTICI");
        long activeAccounts = accounts.values().stream()
                .filter(BankAccount::isActive)
                .count();
        System.out.printf("\tConturi active :: %d/%d (%.1f%%)%n",
                activeAccounts,accounts.size(),
                (activeAccounts * 100.0 / accounts.size()));

        double totalBalance = accounts.values().stream()
                .mapToDouble(acc->acc.getBalance("MDL"))
                .sum();
        System.out.printf("\tSold total MDL :: %.2f%n",totalBalance);
    }

    /**
     * Permite căutarea avansată a conturilor bancare după diferite criterii.
     *
     * <p>Opțiuni de căutare:
     * <ol>
     *   <li>După număr cont (căutare exactă)</li>
     *   <li>După nume proprietar (căutare parțială, case-insensitive)</li>
     *   <li>După sold minim în MDL</li>
     * </ol>
     *
     * @param scanner Scanner pentru citirea criteriilor de căutare
     *
     * @throws InputMismatchException Dacă se introduce un input nevalid
     *
     * @see #findAccount(String)
     */
    public void searchAccount(Scanner scanner){
        System.out.println("\n🔍 CAUTARE CONT:");
        System.out.println("1. Dupa numar cont.");
        System.out.println("2. Dupa nume proprietar.");
        System.out.println("3. Dupa sold minim.");
        System.out.print("Alegeti optiunea :: ");

        try {

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.print("Introduceti numarul contului :: ");
                    String accNumber = scanner.nextLine();
                    BankAccount account = findAccount(accNumber);
                    if (account != null) {
                        account.displayAccountInfo();
                        account.displayBalances();
                    } else {
                        System.out.println("❌ Contul nu a fost gasit");
                    }
                    break;

                case 2:
                    System.out.print("Introduceti numele proprietarului(sau parte din nume) :: ");
                    String namePart = scanner.nextLine();
                    List<BankAccount> foundByName = accounts.values().stream()
                            .filter(acc -> acc.getOwnerName().toLowerCase().contains(namePart.toLowerCase()))
                            .collect(Collectors.toList());

                    if (foundByName.isEmpty()) {
                        System.out.println("❌ Nu s-au gasit conturi pentru acest nume.");
                    } else {
                        System.out.printf("\n🔍 S-au gasit %d conturi :: %n", foundByName.size());
                        for (BankAccount acc : foundByName) {
                            System.out.printf("  %s - %s (%.2f MDL)%n",
                                    acc.getAccountNumber(),
                                    acc.getOwnerName(),
                                    acc.getBalance("MDL"));
                        }
                    }
                    break;

                case 3:
                    System.out.print("Introduceti soldul minim (MDL) :: ");
                    double minBalance = scanner.nextDouble();
                    scanner.nextLine();

                    List<BankAccount> foundByBalance = accounts.values().stream()
                            .filter(acc -> acc.getBalance("MDL") >= minBalance)
                            .sorted((a, b) -> Double.compare(b.getBalance("MDL"), a.getBalance("MDL")))
                            .collect(Collectors.toList());

                    if (foundByBalance.isEmpty()) {
                        System.out.println("❌ Nu exista conturi cu acest minim.");
                    } else {
                        System.out.printf("\n💰 Conturi cu sold >= %.2f MDL ::%n", minBalance);
                        for (BankAccount acc : foundByBalance) {
                            System.out.printf("  %s - %s :: %.2f MDL%n",
                                    acc.getAccountNumber(),
                                    acc.getOwnerName(),
                                    acc.getBalance("MDL"));
                        }
                    }
                    break;

                default:
                    System.out.println("❌ Optiune invalida.");
            }
        }
        catch (InputMismatchException e){
            System.out.println("Va rugam introduceti un numar valid");scanner.nextLine();
        }
    }

    /**
     * Șterge un cont bancar din sistem cu confirmare explicită de la utilizator.
     * Această operațiune este ireversibilă.
     *
     * <p>Procesul de ștergere:
     * <ol>
     *   <li>Caută contul după număr</li>
     *   <li>Afișează informații despre cont</li>
     *   <li>Solicită confirmare explicită</li>
     *   <li>Șterge contul (cu avertizare dacă are sold)</li>
     * </ol>
     *
     * @param scanner Scanner pentru citirea confirmării
     * @return true dacă contul a fost șters, false dacă operațiunea a fost anulată
     *
     * @see #findAccount(String)
     * @see BankAccount#displayAccountInfo()
     * @see BankAccount#getBalance(String)
     */
    public boolean deleteAccount(Scanner scanner){
        System.out.print("\n🗑️ Introduceti numarul contului de sters :: ");
        String accountNumber = scanner.nextLine();

        BankAccount account = findAccount(accountNumber);
        if (account == null){
            System.out.println("❌ Contul nu exista.");
            return false;
        }

        System.out.println("\n⚠️ ATENTIE! Aceasta actiune este ireversibila!");
        account.displayAccountInfo();

        System.out.print("\nConfirmati stergerea? (Da/Nu) :: ");
        String confirmation = scanner.nextLine().toUpperCase();

        if (confirmation.equals("DA")){
            //transfera soldul catre un cont de rezerva inainte de stergere
            if(account.getBalance("MDL") > 0){
                System.out.printf(" ⚠️ Contul are sold %.2f MDL. Transferati inainte de stergere?%n",
                        account.getBalance("MDL"));
            }
            accounts.remove(accountNumber);
            System.out.println("✅ Contul sters cu succes.");
            return true;
        }
        else {
            System.out.println("❌ Stergere anulata.");
            return false;
        }
    }

    /**
     * Caută și returnează un cont bancar după numărul său.
     *
     * @param accountNumber Numărul contului de căutat
     * @return Contul bancar corespunzător, sau null dacă nu este găsit
     */
    public BankAccount findAccount(String accountNumber){
        return accounts.get(accountNumber);
    }

    /**
     * Afișează toate conturile bancare inactive din sistem.
     * Dacă nu există conturi inactive, afișează un mesaj de confirmare.
     */
    public void displayInactiveAccounts(){
        List<BankAccount> inactive = accounts.values().stream()
                .filter(acc->!acc.isActive())
                .collect(Collectors.toList());

        if (inactive.isEmpty()){
            System.out.println("\n✅ Toate conturile sunt active.");
            return;
        }

        System.out.println("\n🔴 CONTURI INACTIVE");
        for (BankAccount acc : inactive){
            System.out.printf("\t%s - %s (creat la %s)%n",
                    acc.getAccountNumber(),
                    acc.getOwnerName(),
                    acc.getCreationDate());
        }
    }

    /**
     * Generează un raport detaliat cu soldurile totale din sistem.
     *
     * <p>Raportul include:
     * <ul>
     *   <li>Total MDL în toate conturile</li>
     *   <li>Total pentru fiecare valută străină (convertit în MDL)</li>
     *   <li>Statistici generale (număr conturi, sold mediu)</li>
     *   <li>Contul cu cel mai mare sold</li>
     * </ul>
     *
     * @param exchangeRates Mapă cu ratele de schimb curente pentru conversia valutelor străine
     *
     * @see BankAccount#getAllBalances()
     * @see BankAccount#getBalance(String)
     */
    public void generateBalanceReport(Map<String,Double> exchangeRates){
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RAPORT SOLDURI TOTALE");
        System.out.println("=".repeat(60));

        double totalMDL = 0;
        Map<String,Double> currencyTotals = new HashMap<>();

        for (BankAccount account : accounts.values()){
            totalMDL += account.getBalance("MDL");

            Map<String,Double> balances = account.getAllBalances();
            for (Map.Entry<String,Double> entry : balances.entrySet()){
                if (!entry.getKey().equals("MDL")){
                    currencyTotals.merge(entry.getKey(),entry.getValue(),Double::sum);
                }
            }
        }

        System.out.printf("Total MDL in toate conturile :: %.2f MDL%n",totalMDL);
        System.out.println("\nTotal valute :: ");
        for (Map.Entry<String,Double> entry : currencyTotals.entrySet()){
            System.out.printf("  %-5s: %12.2f (≈%.2f MDL)%n",
                    entry.getKey(),
                    entry.getValue(),
                    entry.getValue() * exchangeRates.getOrDefault(entry.getKey(),1.0));
        }

        System.out.printf("\n📈 STATISTICI CONTURI\n");
        System.out.printf("  Numar total conturi :: %d%n",accounts.size());
        System.out.printf("  Sold mediu per cont :: %.2f MDL%n",
                totalMDL / Math.max(1,accounts.size()));

        //contul cu cel mai mare sold
        accounts.values().stream()
                .max(Comparator.comparingDouble(acc->acc.getBalance("MDL")))
                .ifPresent(richest->{
                    System.out.printf("  Contul cu cel mai mare sold :: %s (%.2f MDL)%n",
                            richest.getAccountNumber(),
                            richest.getBalance("MDL"));
                });
    }

    /**
     * Aplică dobândă la toate conturile active din sistem.
     * Dobânda este calculată și aplicată proporțional pentru fiecare cont activ.
     *
     * <p>Procesul:
     * <ol>
     *   <li>Pentru fiecare cont activ</li>
     *   <li>Calculează dobânda folosind rata anuală specificată</li>
     *   <li>Adaugă dobânda la soldul contului</li>
     *   <li>Înregistrează tranzacția de dobândă</li>
     * </ol>
     *
     * @param annualRate Rata anuală a dobânzii (în procente)
     *
     * @see BankAccount#calculateInterest(double)
     * @see BankAccount#getBalance(String)
     */
    public void applyInterestToAllAccounts(double annualRate){
        System.out.printf("\n📈 Aplicare dobinda %.2f%% anual la toate conturile...%n", annualRate);

        int affectedAccounts = 0;
        double totalInterest = 0;

        for (BankAccount account : accounts.values()){
            if (account.isActive()){
                double before = account.getBalance("MDL");
                account.calculateInterest(annualRate);
                double after = account.getBalance("MDL");
                double interest = after - before;

                if (interest > 0.01){
                    affectedAccounts++;
                    totalInterest += interest;
                }
            }
        }

        System.out.printf("✅ Dobinda aplicata la %d conturi%n",affectedAccounts);
        System.out.printf("💰 Total dobinzi distribuite :: %.2f MDL%n", totalInterest);
    }
}
