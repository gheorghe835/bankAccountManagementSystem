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
}
