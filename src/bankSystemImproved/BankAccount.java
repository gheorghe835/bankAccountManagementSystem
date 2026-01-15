package bankSystemImproved;

import java.lang.ref.PhantomReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BankAccount {

    //atribbute private
    private  String accountNumber;
    private String password;
    private String ownerName;
    private final Map<String,Double> balances;
    private LocalDateTime lastLogin;
    private LocalDate creationDate;
    private boolean isActive;
    private double dailyWithdrawalLimit;
    private  double dailyWithdrawalUsed;
    private LocalDate lastResetDate;
    private List<Transaction> transactionHistory;

    //valute suportate
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("MDL","EUR","USD","GBP","RON");

    //constante
    private static final double MINIMUM_BALANCE = 10.0;
    private static final double MINIMUM_DEPOSIT = 1.00;
    private static final double DEFAULT_DAILY_LIMIT = 5000.00;

    //constructor principal

    public BankAccount(String accountNumber,String password,double initialBalance,
                       String ownerName) {
        validateAccountNumber(accountNumber);
        validatePassword(accountNumber);
        validateInitialBalance(initialBalance);

        this.accountNumber = accountNumber;
        this.password = password;
        this.ownerName = ownerName;
        this.balances = new HashMap<>();
        this.creationDate = LocalDate.now();
        this.isActive = true;
        this.dailyWithdrawalLimit = DEFAULT_DAILY_LIMIT;
        this.dailyWithdrawalUsed = 0.0;
        this.lastResetDate = LocalDate.now();
        this.transactionHistory = new ArrayList<>();

        //initializeaza soldurile pentru toate valutele
        initializeBalances(initialBalance);

        //adaugarea tranzactiei initiale
        addTransaction("ACCOUNT_CREATION",initialBalance,"MDL","Cont creat cu sold initial.");
    }

    //constructor fara sold initial
    public BankAccount(String accountNumber,String password,String ownerName){
        this(accountNumber,password,0.0,ownerName);
    }

    //metode private de validare
    private void validateAccountNumber(String accountNumber){
        if (accountNumber == null || accountNumber.length() != 16){
            throw new IllegalArgumentException("Numarul contului trebuie sa aiba 16 caractere.");
        }

        if (!accountNumber.matches("\\d+")){
            throw new IllegalArgumentException("Numarul contului trebuie sa contina doar cifre.");
        }
    }

    private void validatePassword(String password){
        if (password == null || password.length() < 6){
            throw new IllegalArgumentException("Parola trebuie sa aiba minimum 6 caractere.")
        }

        if (password.matches(".*\\d.*")  || !password.matches(".*[a-zA-Z].*")){
            throw new IllegalArgumentException("Parola trebuie sa contina litere si cifre.");
        }
    }

    private void validateInitialBalance(double balance){
        if (balance < 0){
            throw new IllegalArgumentException("Soldul initial nu poate fi negativ.");
        }
    }

    private void initializeBalances(double initialBalance){
        for (String currency : SUPPORTED_CURRENCIES){
            if (currency.equals("MDL")){
                balances.put(currency,initialBalance);
            }
            else {
                balances.put(currency,0.0);
            }
        }
    }

    private void resetDailyLimitIfNeeded(){
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetDate)){
            dailyWithdrawalUsed = 0.0;
            lastResetDate = today;
        }
    }

    //METODELE DE BAZA

    //Deunere

    public boolean deposit(double amount,String currency){
        if (isActive){
            System.out.println("Contul este inactiv. Contactati banca.");
            return false;
        }

        if(!isValdCurrency(currency)){
            System.out.println("Moneda nesuportata :: " + currency);
            return false;
        }

        if (amount < MINIMUM_DEPOSIT){
            System.out.printf("Suma minima pentru depunere este %.2f %s%n",MINIMUM_DEPOSIT,currency);
            return false;
        }

        double newBalance = balances.get(currency) + amount;
        balances.put(currency,newBalance);

        addTransaction("Deposit",amount,currency,String.format("Depunere in cont %s",accountNumber));

        System.out.printf("Depunere reusita! Sold %s actual :: %.2f%n",currency,newBalance);

        return true;
    }

    //Retragere

    public boolean withdraw(double amount,String currency){
        if (!isActive){
            System.out.println("Contul este inactiv. Contactati banca.");
            return false;
        }

        if (!isValidCurrency(currency)){
            System.out.println("Moneda nesuportata :: " + currency);
            return false;
        }

        if (amount <= 0){
            System.out.println("Suma trebuie sa fie pozitiva.");
            return false;
        }

        resetDailyLimitIfNeeded();

        //verifica limita zilnica
        double amountInMDL = convertToMDL(amount,currency);
        if (dailyWithdrawalUsed + amountInMDL > dailyWithdrawalLimit){
            System.out.printf("Limita zilnica de retragere depasita. " + "Mai aveti disponibil :: " +
                    "%.2f MDL%n",dailyWithdrawalLimit - dailyWithdrawalUsed);
            return false;
        }

        //verifica soldul
        double currentBalance = balances.getOrDefault(currency,0.0);
        if (currentBalance < amount){
            System.out.printf("Fonduri insuficiente. " + "Sold disponibil %s:: %.2f%n",currency,currentBalance);
            return false;
        }

        //retragerea
        double newBalance = currentBalance - amount;
        balances.put(currency,currentBalance);
        dailyWithdrawalUsed += amountInMDL;

        addTransaction("WITHDRAWAL",amount,currency,String.format("Retragere de la cont %s",accountNumber));

        System.out.printf("Retragere reusita! Sold %s actual :: %.2f%n",currency,newBalance);
        System.out.printf("Limita zilnica utilizata :: %.2f/%.2f MDL%n",dailyWithdrawalUsed,dailyWithdrawalLimit);
        return true;
    }

    //transfer catre alt cont
    public boolean transferTo(BankAccount targetAccount,double amount,String currency,String description){
        if (!isActive || !targetAccount.isActive()){
            System.out.println("Unul dintre conturi este inactiv.");
            return false;
        }

        if (targetAccount == this){
            System.out.println("Nu puteti transfera catre acest cont.");
            return false;
        }

        //retrage din contul sursa
        if (!withdraw(amount,currency)){
            return false;
        }

        //depune in contul tinta
        if (!targetAccount.deposit(amount,currency)){
            //daca depunerea esuiaza, readuce banii
            deposit(amount,currency);
            System.out.println("Transfer esuat. Suma a fost returnata.");
            return false;
        }

        addTransaction("TRANSFER_OUT",amount,currency,String.format("Transfer catre %s :: %s",targetAccount.getAccountNumber(),description));
        targetAccount.addTransaction("TRANSFER_IN",amount,currency,String.format("Transfer de la %s :: %s",accountNumber,description));

        System.out.printf("Transfer reusit catre contul %s!%n",targetAccount.getAccountNumber());
        return true;
    }

    

}
