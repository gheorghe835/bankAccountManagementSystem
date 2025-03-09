package bankSystem;

import java.util.HashMap;
import java.util.Map;

public class BankAccount {
    private String accountNumber;
    private String password;
    private final Map<String,Double> balances;

    public BankAccount(String accountNumber, String password, double initialBalance) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.balances = new HashMap<>();
        this.balances.put("MDL",initialBalance);
        this.balances.put("EUR",0.0);
        this.balances.put("USD",0.0);
        this.balances.put("GBP",0.0);
    }

    public String getAccountNumber() {
        return accountNumber;
    }


    //verificarea parolei
    public boolean verifyPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }
    public double getBalance(String currency){
        return balances.getOrDefault(currency,0.0);
    }
    //metoda de depunere
    public void deposit(String currency,double amount){
        if(amount > 0){
            balances.put(currency,balances.getOrDefault(currency,0.0) + amount);
            System.out.printf("%nDepunere reusita! Sold nou %s: %.2f MDL%n",currency,balances.get(currency));
        }
        else {
            System.out.printf("%nSuma intodusa este invalida.%n");
        }
    }

    //metoda de retragere
    public void withdraw(String currency,double amount){
        if (amount > 0 && balances.getOrDefault(currency,0.0) >= amount){
            balances.put(currency,balances.get(currency) - amount);
            System.out.printf("%nRetragere reusita! Sold nou %s: %.2f MDL",currency,balances.get(currency));
        }
        else {
            System.out.printf("%nFonduri insuficiente!%n");
        }
    }
    public void addCurrency(String currency,double amount){
        if (amount > 0){
            balances.put(currency,balances.getOrDefault(currency,0.0) + amount);
        }
    }
    public void displayBalances(){
        System.out.println("\nSolduri actuale:");
        for (Map.Entry<String,Double> entry:
             balances.entrySet()) {
            System.out.printf("%s: %.2f%n",entry.getKey(),entry.getValue());
        }
    }
}
