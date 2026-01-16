package bankSystemImproved;

import java.util.*;
import java.util.stream.Collectors;

public class BankManager {
    private final Map<String,BankAccount> accounts;
    private int totalTransactions;
    private double totalDeposits;
    private double totalWithdrawals;

    public BankManager(){
        this.accounts = new HashMap<>();
        this.totalTransactions = 0;
        this.totalDeposits = 0;
        this.totalWithdrawals = 0;
    }

    //adaugare cont cu validare
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
            System.out.printf("Proprietar :: ",ownerName);
            System.out.printf("Sold initial :: %.2f MDL %n",initialBalance);

            return true;
        }
        catch (Exception e){
            System.out.println("Eroare la crearea contului :: " + e.getMessage());
            return false;
        }
    }

    //afisare toate conyurile cu optiuni de filtrare
    public void displayAllAccounts(){
        if (accounts.isEmpty()){
            System.out.println("\n\uD83D\uDCED Nu exista conturi bancare inregistrate.");
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
        System.out.println("\n\uD83D\uDCCA STATISTICI");
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

    //cautare avansata
    public void searchAccount(Scanner scanner){
        System.out.println("\n🔍 CAUTARE CONT:");
        System.out.println("1. Dupa numar cont.");
        System.out.println("2. Dupa nume proprietar.");
        System.out.println("3. Dupa sold minim.");
        System.out.print("Alegeti optiunea :: ");

        int option = scanner.nextInt();scanner.nextLine();

        switch (option){
            case 1:
                System.out.print("Introduceti numarul contului :: ");
                String accNumber = scanner.nextLine();
                BankAccount account = findAccount(accNumber);
                if (account != null){
                    account.displayAccountInfo();
                    account.displayBalances();
                }
                else {
                    System.out.println("❌ Contul nu a fost gasit");
                }
                break;

            case 2:
                System.out.print("Introduceti numele proprietarului(sau parte din nume) :: ");
                String namePart = scanner.nextLine();
                List<BankAccount> foundByName = accounts.values().stream()
                        .filter(acc->acc.getOwnerName().toLowerCase().contains(namePart))
                        .collect(Collectors.toList());

                if (foundByName.isEmpty()){
                    System.out.println("❌ Nu s-au gasit conturi pentru acest nume.");
                }
                else {
                    System.out.printf("n🔍 S-au gasit %d conturi :: %n",foundByName.size());
                    for (BankAccount acc : foundByName){
                        System.out.printf("  %s - %s(%.2f MDL)%n",
                                acc.getAccountNumber(),
                                acc.getCreationDate(),
                                acc.getBalance("MDL"));
                    }
                }
                break;

            case 3:
                System.out.print("Introduceti soldul minim (MDL) :: ");
                double minBalance = scanner.nextDouble();scanner.nextLine();

                List<BankAccount> foundByBalance = accounts.values().stream()
                        .filter(acc->acc.getBalance("MDL") >= minBalance)
                        .sorted((a,b)->Double.compare(b.getBalance("MDL"),a.getBalance("MDL")))
                        .collect(Collectors.toList());

                if (foundByBalance.isEmpty()){
                    System.out.println("❌ Nu exista conturi cu acest minim.");
                }
                else {
                    System.out.printf("\n\uD83D\uDCB0 Conturi cu sold >= %.2f MDL ::%n",minBalance);
                    for (BankAccount acc : foundByBalance){
                        System.out.printf("  %s - %s:: %.2f MDL%n",
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

    //stergere cu confirmare
    public boolean deleteAccount(Scanner scanner){
        System.out.print("\n\uD83D\uDDD1️ Introduceti numarul contului de sters :: ");
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

        if (confirmation.equals("Da")){
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

    //gasire cont
    public BankAccount findAccount(String accountNumber){
        return accounts.get(accountNumber);
    }

    //listare conturi inactive
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
            System.out.printf("\t%s - %s(creat la %s)%n",
                    acc.getAccountNumber(),
                    acc.getOwnerName(),
                    acc.getCreationDate());
        }
    }

    //raport solduri totale
}
































