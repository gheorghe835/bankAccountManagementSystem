package bankSystemImproved;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
}
