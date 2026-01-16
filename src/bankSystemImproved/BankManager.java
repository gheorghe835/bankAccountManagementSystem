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
    
}
































