package bankSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankManager {
    private final List<BankAccount> accounts = new ArrayList<>();

    //metoda de adaugare a conturilor
    public void addBankAccount(Scanner scanner){
        System.out.printf("%nIntroduceti numarul contului: ");
        String accountNumber = scanner.nextLine();

        System.out.printf("Introduceti parola contuluiu:");
        String password = scanner.nextLine();

        System.out.printf("Introduceti soldul actual:");
        double balance = scanner.nextDouble();

        accounts.add(new BankAccount(accountNumber,password,balance));
        System.out.printf("%nContul a fost adaugat cu succes!%n");
    }

    //metoda de afisare a tuturor conturilor
    public void displayAllAccounts(){
        if(accounts.isEmpty()){
            System.out.printf("%nNu exista conturi bancare inregistrate.%n");
            return;
        }
        System.out.printf("%nLista conturilor bancare:%n");
        for (BankAccount account:
             accounts) {
            System.out.printf("Numar cont: %s | Sold: %.2f MDL%n",account.getAccountNumber(),account.getBalance());
        }
    }

    //metoda de cautare a contului
    public void searchAccount(Scanner scanner){
        System.out.printf("%nIntroduceti numarul contului:");
        String accountNumber = scanner.nextLine();
        for (BankAccount account:
             accounts) {
            if(account.getAccountNumber().equals(accountNumber)){
                System.out.printf("%nCont gasit. Numar cont %s | Sold %.2f MDL%n",account.getAccountNumber(),account.getBalance());
                return;
            }
            System.out.printf("%nContul: %s  nu este inregistrat",account.getAccountNumber());
        }
    }

}
