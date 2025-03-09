package bankSystem;

public class BankAccount {
    private String accountNumber;
    private String password;
    private double balance;

    public BankAccount(String accountNumber, String password, double balance) {
        this.accountNumber = accountNumber;
        this.password = password;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    //verificarea parolei
    public boolean verifyPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }
    //metoda de depunere
    public void deposit(double amount){
        if(amount > 0){
            balance += amount;
            System.out.printf("%nDepunere reusita! Sold nou: %.2f MDL%n",balance);
        }
        else {
            System.out.printf("%nSuma intodusa este invalida.%n");
        }
    }

    //metoda de retragere
    public void withdraw(double amount){
        if (amount > 0 && amount <= balance){
            balance -= amount;
            System.out.printf("%nRetragere reusita! Sold nou: %.2f MDL",balance);
        }
        else {
            System.out.printf("%nFonduri insuficiente!%n");
        }
    }
}
