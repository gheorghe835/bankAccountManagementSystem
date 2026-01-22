package bankSystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

/**
 * Clasa BankAccount reprezintă un cont bancar în sistemul bancar.
 * Această clasă gestionează toate operațiunile și informațiile asociate
 * unui cont bancar individual.
 *
 * <p>Funcționalități principale:
 * <ul>
 *   <li>Gestiunea soldurilor în multiple valute</li>
 *   <li>Operațiuni bancare (depuneri, retrageri, transferuri)</li>
 *   <li>Schimb valutar</li>
 *   <li>Istoric tranzacții</li>
 *   <li>Setări de securitate și limite</li>
 *   <li>Calcul dobânzi</li>
 * </ul>
 *
 * @author [Batiri Gheorghe]
 * @version 1.0
 * @since 2024
 */
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

    /**
     * Constructor principal pentru crearea unui cont bancar.
     *
     * @param accountNumber Numarul contului (16 cifre)
     * @param password Parola contului (minimum 6 caractere, litere și cifre)
     * @param initialBalance Soldul initial în MDL (nu poate fi negativ)
     * @param ownerName Numele proprietarului contului
     *
     * @throws IllegalArgumentException Dacă:
     * <ul>
     *   <li>Numărul contului nu are 16 caractere</li>
     *   <li>Parola nu respectă cerințele</li>
     *   <li>Soldul initial este negativ</li>
     * </ul>
     *
     * @see #validateAccountNumber(String)
     * @see #validatePassword(String)
     * @see #validateInitialBalance(double)
     */
    public BankAccount(String accountNumber,String password,double initialBalance,
                       String ownerName) {
        validateAccountNumber(accountNumber);
        validatePassword(password);
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

    /**
     * Constructor pentru crearea unui cont bancar fără sold initial.
     * Soldul initial va fi setat la 0.0 MDL.
     *
     * @param accountNumber Numarul contului (16 cifre)
     * @param password Parola contului (minimum 6 caractere, litere și cifre)
     * @param ownerName Numele proprietarului contului
     */
    public BankAccount(String accountNumber,String password,String ownerName){
        this(accountNumber,password,0.0,ownerName);
    }

    //getteri si setteri

    /**
     * Returnează numărul contului.
     *
     * @return Numarul contului ca String
     */
    public String getAccountNumber(){
        return accountNumber;
    }

    /**
     * Returnează numele proprietarului contului.
     *
     * @return Numele proprietarului
     */
    public String getOwnerName(){
        return ownerName;
    }

    /**
     * Actualizează numele proprietarului contului.
     * Numele trebuie să aibă minimum 2 caractere.
     *
     * @param ownerName Noul nume al proprietarului
     */
    public void setOwnerName(String ownerName){
        if (ownerName != null && ownerName.length() >= 2){
            this.ownerName = ownerName;
            System.out.println("Numele a fost actualizat cu succes.");
        }
        else {
            System.out.println("Numele trebuie sa aiba minimum 2 caractere.");
        }
    }

    /**
     * Schimbă parola contului.
     *
     * @param oldPassword Parola veche pentru verificare
     * @param newPassword Parola nouă (trebuie să respecte cerințele)
     * @return true dacă parola a fost schimbată cu succes, false altfel
     *
     * @see #validatePassword(String)
     */
    public boolean changePassword(String oldPassword,String newPassword){
        if (!verifyPassword(oldPassword)){
            System.out.println("Parola veche este incorecta.");
            return false;
        }
        try{
            validatePassword(newPassword);
            this.password = newPassword;
            addTransaction("PASSWORD_CHANGE",0,"MDL","Parola schimbata.");
            System.out.println("Parola a fost schimbata cu succes.");
            return true;
        }
        catch (IllegalArgumentException e){
            System.out.println("Parola noua invalida :: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returnează soldul pentru o anumită monedă.
     *
     * @param currency Codul monedei (MDL, EUR, USD, GBP, RON)
     * @return Soldul în moneda specificată, sau 0.0 dacă moneda nu există
     */
    public double getBalance(String currency){
        return balances.getOrDefault(currency,0.0);
    }

    /**
     * Returnează toate soldurile în toate valutele suportate.
     *
     * @return Mapă cu toate soldurile (cheie: cod monedă, valoare: sold)
     */
    public Map<String,Double> getAllBalances(){
        return new HashMap<>(balances);
        //returnam o copie pentru siguranta
    }

    /**
     * Returnează data creării contului.
     *
     * @return Data creării contului
     */
    public LocalDate getCreationDate(){
        return creationDate;
    }

    /**
     * Verifică dacă contul este activ.
     *
     * @return true dacă contul este activ, false dacă este inactiv
     */
    public boolean isActive(){
        return isActive;
    }

    /**
     * Dezactivează contul. Contul trebuie să fie activ pentru a fi dezactivat.
     */
    public void deactivateAccount(){
        if (isActive){
            isActive = false;
            addTransaction("ACCOUNT_DEACTIVATED",0,"MDL","Cont dezactivat");
            System.out.println("Contul a fost dezactivat.");
        }
    }

    /**
     * Reactivează contul. Contul trebuie să fie inactiv pentru a fi reactivat.
     */
    public void reactivateAccount(){
        if (!isActive){
            isActive = true;
            addTransaction("ACCOUNT_REACTIVATED",0,"MDL","Cont reactivat");
            System.out.println("Contul a fost reactivat.");
        }
    }

    /**
     * Returnează limita zilnică de retragere în MDL.
     *
     * @return Limita zilnică de retragere
     */
    public double getDailyWithdrawalLimit(){
        return dailyWithdrawalLimit;
    }

    /**
     * Setează o nouă limită zilnică de retragere.
     * Limita trebuie să fie minim 100 MDL.
     *
     * @param limit Noua limită zilnică
     */
    public void setDailyWithdrawalLimit(double limit){
        if (limit >= 100){
            dailyWithdrawalLimit = limit;
            System.out.printf("Limita zilnica setata la %.2f MDL %n",limit);
        }
        else {
            System.out.println("Limita trebuie sa fie minim de 100 MDL.");
        }
    }

    /**
     * Returnează suma utilizată din limita zilnică de retragere.
     * Această valoare este resetată automat la începutul fiecărei zile.
     *
     * @return Suma utilizată din limita zilnică
     *
     * @see #resetDailyLimitIfNeeded()
     */
    public double getDailyWithdrawalUsed(){
        resetDailyLimitIfNeeded();
        return dailyWithdrawalUsed;
    }

    /**
     * Returnează data și ora ultimei autentificări.
     *
     * @return Timestamp-ul ultimei autentificări, sau null dacă nu s-a autentificat niciodată
     */
    public LocalDateTime getLastLogin(){
        return lastLogin;
    }

    /**
     * Actualizează data și ora ultimei autentificări cu timpul curent.
     */
    public void updateLastLogin(){
        this.lastLogin = LocalDateTime.now();
    }

    //metode private de validare

    /**
     * Validează numărul contului.
     * Numărul contului trebuie să aibă exact 16 caractere și să conțină doar cifre.
     *
     * @param accountNumber Numărul contului de validat
     * @throws IllegalArgumentException Dacă numărul contului nu este valid
     */
    private void validateAccountNumber(String accountNumber){
        if (accountNumber == null || accountNumber.length() != 16){
            throw new IllegalArgumentException("Numarul contului trebuie sa aiba 16 caractere.");
        }

        if (!accountNumber.matches("\\d+")){
            throw new IllegalArgumentException("Numarul contului trebuie sa contina doar cifre.");
        }
    }

    /**
     * Validează parola.
     * Parola trebuie să aibă minimum 6 caractere și să conțină atât litere cât și cifre.
     *
     * @param password Parola de validat
     * @throws IllegalArgumentException Dacă parola nu este validă
     */
    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Parola trebuie sa aiba minimum 6 caractere.");
        }

        // Verifică dacă conține cifre
        boolean hasDigit = password.matches(".*\\d.*");
        // Verifică dacă conține litere
        boolean hasLetter = password.matches(".*[a-zA-Z].*");

        if (!hasDigit || !hasLetter) {
            throw new IllegalArgumentException("Parola trebuie sa contina atat litere cat si cifre.");
        }
    }

    /**
     * Validează soldul initial.
     * Soldul initial nu poate fi negativ.
     *
     * @param balance Soldul initial de validat
     * @throws IllegalArgumentException Dacă soldul este negativ
     */
    private void validateInitialBalance(double balance){
        if (balance < 0){
            throw new IllegalArgumentException("Soldul initial nu poate fi negativ.");
        }
    }

    /**
     * Inițializează soldurile pentru toate valutele suportate.
     * Soldul initial este setat doar pentru MDL, celelalte valute sunt setate la 0.0.
     *
     * @param initialBalance Soldul initial în MDL
     *
     * @see #SUPPORTED_CURRENCIES
     */
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

    /**
     * Resetează limita zilnică utilizată dacă s-a schimbat ziua.
     * Această metodă este apelată automat înainte de a utiliza limita zilnică.
     */
    private void resetDailyLimitIfNeeded(){
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetDate)){
            dailyWithdrawalUsed = 0.0;
            lastResetDate = today;
        }
    }

    //METODELE DE BAZA

    /**
     * Efectuează o depunere în cont.
     *
     * @param amount Suma de depus
     * @param currency Moneda în care se face depunerea (MDL, EUR, USD, GBP, RON)
     * @return true dacă depunerea a reușit, false dacă a eșuat
     *
     * @throws IllegalArgumentException Dacă:
     * <ul>
     *   <li>Contul este inactiv</li>
     *   <li>Moneda nu este suportată</li>
     *   <li>Suma este mai mică decât minimul permis</li>
     * </ul>
     *
     * @see #MINIMUM_DEPOSIT
     * @see #isValidCurrency(String)
     */
    public boolean deposit(double amount,String currency){
        if (!isActive){
            System.out.println("Contul este inactiv. Contactati banca.");
            return false;
        }

        if(!isValidCurrency(currency)){
            System.out.println("Moneda nesuportata :: " + currency);
            return false;
        }

        if (amount < MINIMUM_DEPOSIT){
            System.out.printf("Suma minima pentru depunere este %.2f %s%n",MINIMUM_DEPOSIT,currency);
            return false;
        }

        double newBalance = balances.get(currency) + amount;
        balances.put(currency,newBalance);

        addTransaction("DEPOSIT",amount,currency,String.format("Depunere in cont %s",accountNumber));

        System.out.printf("Depunere reusita! Sold %s actual :: %.2f%n",currency,newBalance);

        return true;
    }

    /**
     * Efectuează o retragere din cont.
     *
     * @param amount Suma de retras
     * @param currency Moneda în care se face retragerea
     * @return true dacă retragerea a reușit, false dacă a eșuat
     *
     * @throws IllegalArgumentException Dacă:
     * <ul>
     *   <li>Contul este inactiv</li>
     *   <li>Moneda nu este suportată</li>
     *   <li>Suma nu este pozitivă</li>
     *   <li>Se depășește limita zilnică</li>
     *   <li>Fonduri insuficiente</li>
     * </ul>
     *
     * @see #resetDailyLimitIfNeeded()
     * @see #convertToMDL(double, String)
     */
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
        balances.put(currency,newBalance);
        dailyWithdrawalUsed += amountInMDL;

        addTransaction("WITHDRAWAL",amount,currency,String.format("Retragere de la cont %s",accountNumber));

        System.out.printf("Retragere reusita! Sold %s actual :: %.2f%n",currency,newBalance);
        System.out.printf("Limita zilnica utilizata :: %.2f/%.2f MDL%n",dailyWithdrawalUsed,dailyWithdrawalLimit);
        return true;
    }

    /**
     * Transferă bani către un alt cont bancar.
     *
     * @param targetAccount Contul destinație al transferului
     * @param amount Suma de transferat
     * @param currency Moneda transferului
     * @param description Descrierea transferului
     * @return true dacă transferul a reușit, false dacă a eșuat
     *
     * @throws IllegalArgumentException Dacă:
     * <ul>
     *   <li>Unul dintre conturi este inactiv</li>
     *   <li>Se încearcă transfer către același cont</li>
     *   <li>Retragerea sau depunerea eșuează</li>
     * </ul>
     *
     * @see #withdraw(double, String)
     * @see BankAccount#deposit(double, String)
     */
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

    /**
     * Efectuează un schimb valutar între două monede.
     *
     * @param fromCurrency Moneda sursă pentru schimb
     * @param toCurrency Moneda destinație pentru schimb
     * @param amount Suma de schimbat
     * @param exchangesRates Mapă cu ratele de schimb curente
     * @return true dacă schimbul a reușit, false dacă a eșuat
     *
     * @throws IllegalArgumentException Dacă:
     * <ul>
     *   <li>Contul este inactiv</li>
     *   <li>Monedele nu sunt suportate</li>
     *   <li>Se încearcă schimb între aceeași monedă</li>
     *   <li>Fonduri insuficiente</li>
     * </ul>
     *
     * <p>Se aplică un comision de 0.5% la schimb.
     */
    public boolean exchangeCurrency(String fromCurrency,String toCurrency,double amount,
                                    Map<String,Double> exchangesRates){
        if (!isActive){
            System.out.println("Contul este inactiv. Contactati banca.");
            return false;
        }

        if (!isValidCurrency(fromCurrency) || !isValidCurrency(toCurrency)){
            System.out.println("Moneda nesuportata.");
            return false;
        }

        if (fromCurrency.equals(toCurrency)){
            System.out.println("Nu puteti schimba aceeasi moneda.");
            return false;
        }

        //verifica soldul sursa
        double fromBalance = balances.getOrDefault(fromCurrency,0.0);
        if (fromBalance < amount){
            System.out.printf("Fonduri insuficiente in %s. Disponibil :: %.2f%n",fromCurrency,fromBalance);
            return false;
        }

        //calculeaza suma schimbata
        double exchangeAmount;
        if (fromCurrency.equals("MDL")){
            exchangeAmount = amount / exchangesRates.get(toCurrency);
        }
        else if (toCurrency.equals("MDL")){
            exchangeAmount = amount * exchangesRates.get(fromCurrency);
        }
        else {
            //din valuta in valuta prin MDL
            double amountInMDL = amount * exchangesRates.get(fromCurrency);
            exchangeAmount = amountInMDL / exchangesRates.get(toCurrency);
        }

        //aplica comision 0.5%
        double commission = exchangeAmount * 0.005;
        exchangeAmount -= commission;

        //efectuiaza schimbul
        balances.put(fromCurrency,fromBalance - amount);
        balances.put(toCurrency,balances.getOrDefault(toCurrency,0.0) + exchangeAmount);

        addTransaction("EXCHANGE",amount,fromCurrency,String.format("Schimb %s->%s:: %.2f(comision :: %.4f",
                fromCurrency,toCurrency,amount,exchangeAmount,commission));

        System.out.printf("Schimb valutar reusit :: %n");
        System.out.printf("Ai dat :: %.2f %s%n", amount,fromCurrency);
        System.out.printf("Ai primit :: %.2f %s%n",exchangeAmount,toCurrency);
        System.out.printf("Comision aplicat :: %.4f %s%n",commission,toCurrency);

        return true;
    }

    //metodele de calcul

    /**
     * Calculează soldul total al contului în MDL.
     * Converteste toate soldurile în valute străine în MDL folosind ratele de schimb.
     *
     * @param exchangeRates Mapă cu ratele de schimb curente
     * @return Soldul total în MDL
     */
    public double getTotalBalanceInMDL(Map<String,Double> exchangeRates)
    {
        double total = 0.0;
        for (Map.Entry<String,Double> entry : balances.entrySet()){
            String currency = entry.getKey();
            double amount = entry.getValue();

            if (currency.equals("MDL")){
                total += amount;
            }
            else if (exchangeRates.containsKey(currency)){
                total += amount * exchangeRates.get(currency);
            }
        }
        return total;
    }

    /**
     * Calculează și aplică dobânda la toate soldurile contului.
     * Dobânda este calculată zilnic și aplicată proporțional.
     *
     * @param annualRate Rata anuală a dobânzii (în procente)
     *
     * <p>Exemplu: Pentru o rată anuală de 5%, se calculează rata zilnică ca 5/365/100.
     * Dobânda este aplicată doar pentru solduri pozitive și doar dacă este mai mare de 0.01.
     */
    public void calculateInterest(double annualRate){
        if (!isActive){
            return;
        }
        for (Map.Entry<String,Double> entry : balances.entrySet()){
            String currency = entry.getKey();
            double amount = entry.getValue();

            if (amount > 0){
                double dailyRate = annualRate / 365 / 100;
                double interest = amount * dailyRate;

                balances.put(currency,amount + interest);

                if (interest > 0.01){
                    //ignora dobinzi foarte mici
                    addTransaction("INTEREST",interest,currency,String.format("Dobinda la %.2f%% anual",annualRate));
                }
            }
        }
    }

    /**
     * Calculează vârsta contului în zile.
     *
     * @return Numărul de zile de la crearea contului
     *
     * @see Period#between(LocalDate, LocalDate)
     */
    public int getAccountAgeInDays(){
        return Period.between(creationDate,LocalDate.now()).getDays();
    }

    //metode de verificare

    /**
     * Verifică dacă o parolă introdusă corespunde parolei contului.
     *
     * @param inputPassword Parola de verificat
     * @return true dacă parolele corespund, false altfel
     */
    public boolean verifyPassword(String inputPassword){
        return this.password.equals(inputPassword);
    }

    /**
     * Verifică dacă contul are suficiente fonduri pentru o anumită sumă.
     *
     * @param amount Suma de verificat
     * @param currency Moneda în care se verifică suma
     * @return true dacă sunt suficiente fonduri, false altfel
     */
    public boolean hasEnoughFunds(double amount,String currency){
        return balances.getOrDefault(currency,0.0) >= amount;
    }

    /**
     * Verifică dacă o monedă este suportată de cont.
     *
     * @param currency Codul monedei de verificat
     * @return true dacă moneda este suportată, false altfel
     *
     * @see #SUPPORTED_CURRENCIES
     */
    public boolean isValidCurrency(String currency){
        return SUPPORTED_CURRENCIES.contains(currency);
    }

    //metode de afisare

    /**
     * Afișează informațiile detaliate ale contului.
     * Include număr cont, proprietar, data creării, stare, vârstă,
     * ultima autentificare și limitele de retragere.
     */
    public void displayAccountInfo(){
        System.out.println("\n" + "=".repeat(50));
        System.out.println("INFORMATII CONT BANCAR");
        System.out.println("=".repeat(50));
        System.out.printf("Numar cont :: %s%n",accountNumber);
        System.out.printf("Proprietar :: %s%n",ownerName);
        System.out.printf("Data crearii :: %s%n",creationDate);
        System.out.printf("Stare :: %s%n",isActive ? "ACTIV" : "INACTIV");
        System.out.printf("Virsta contului :: %d zile %n",getAccountAgeInDays());
        System.out.printf("Ultima autentificare :: %s%n",lastLogin != null ? lastLogin : "Niciodata");
        System.out.printf("Limita retragere zilnica :: %.2f MDL (utilizat :: %.2f MDL)%n",
                dailyWithdrawalLimit,getDailyWithdrawalUsed());
        System.out.println("=".repeat(50));
    }

    /**
     * Afișează soldurile contului pentru toate valutele.
     * Se afișează doar valutele cu sold pozitiv și MDL (chiar dacă este 0).
     */
    public void displayBalances(){
        System.out.println("\nSOLDURI CONT :: ");
        System.out.println("-".repeat(30));
        for (String currency : SUPPORTED_CURRENCIES){
            double balance = balances.getOrDefault(currency,0.0);
            if (balance > 0 || currency.equals("MDL")){
                System.out.printf("%-5s:: %12.2f%n",currency,balance);
            }
        }
        System.out.println("-".repeat(30));
    }

    /**
     * Afișează ultimele tranzacții ale contului.
     *
     * @param limit Numărul maxim de tranzacții de afișat
     */
    public void displayTransactionHistory(int limit){
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ISTORIC TRANZACTII ( ultimele " + limit + ").");
        System.out.println("=".repeat(50));

        if (transactionHistory.isEmpty()){
            System.out.println("Nu exista tranzactii inregistrate.");
            return;
        }

        int start = Math.max(0,transactionHistory.size() - limit);
        for (int i = start; i < transactionHistory.size(); i++){
            System.out.println(transactionHistory.get(i));
        }
    }

    /**
     * Generează un extras de cont pentru o perioadă specificată.
     *
     * @param from Data de început a perioadei
     * @param to Data de sfârșit a perioadei
     * @param exchangeRates Ratele de schimb pentru conversia valuteelor străine în MDL
     *
     * <p>Afișează:
     * <ul>
     *   <li>Toate tranzacțiile din perioada specificată</li>
     *   <li>Totalul intrărilor în MDL</li>
     *   <li>Totalul ieșirilor în MDL</li>
     *   <li>Soldul final în MDL</li>
     * </ul>
     */
    public void generateAccountStatement(LocalDate from, LocalDate to, Map<String, Double> exchangeRates) {
        System.out.printf("\nSTATEMENT CONT %s(%s - %s)%n", accountNumber, from, to);
        System.out.println("=".repeat(60));

        double totalIn = 0;
        double totalOut = 0;

        for (Transaction t : transactionHistory) {
            LocalDate transDate = t.getTimestamp().toLocalDate();
            if (!transDate.isBefore(from) && !transDate.isAfter(to)) {
                System.out.println(t);

                double amountInMDL = t.getAmount();

                // Converteste in MDL daca nu e MDL
                if (!t.getCurrency().equals("MDL") && exchangeRates != null) {
                    amountInMDL = t.getAmount() * exchangeRates.getOrDefault(t.getCurrency(), 1.0);
                }

                if (t.getType().contains("DEPOSIT") ||
                        t.getType().contains("TRANSFER_IN") ||
                        t.getType().contains("INTEREST")) {
                    totalIn += amountInMDL;
                } else if (t.getType().contains("WITHDRAWAL") ||
                        t.getType().contains("TRANSFER_OUT") ||
                        t.getType().contains("EXCHANGE")) {
                    totalOut += amountInMDL;
                }
            }
        }

        System.out.println("=".repeat(60));
        System.out.printf("TOTAL INTRARI :: %12.2f MDL%n", totalIn);
        System.out.printf("TOTAL IESIRI :: %12.2f MDL%n", totalOut);
        System.out.printf("SOLD FINAL :: %12.2f MDL\n", totalIn - totalOut);
    }

    //metode private utilitare

    /**
     * Adaugă o tranzacție în istoricul contului.
     * Istoricul este limitat la ultimele 1000 de tranzacții.
     *
     * @param type Tipul tranzacției
     * @param amount Suma tranzacției
     * @param currency Moneda tranzacției
     * @param description Descrierea tranzacției
     *
     * @see Transaction
     */
    private void addTransaction(String type,double amount,String currency,String description){
        Transaction transaction = new Transaction(type,amount,currency,description);
        transactionHistory.add(transaction);

        //limiteaza istoricul la ultimele 1000 de tranzactii
        if (transactionHistory.size() > 1000){
            transactionHistory.remove(0);
        }
    }

    /**
     * Converteste o sumă dintr-o monedă străină în MDL.
     * Folosește rate de schimb hardcodate pentru exemplificare.
     *
     * @param amount Suma de convertit
     * @param currency Moneda sursă
     * @return Suma echivalentă în MDL
     */
    private double convertToMDL(double amount,String currency){
        //exemplu
        Map<String,Double> rates = Map.of("EUR",19.45,
                "USD",17.80,
                "GBP",22.10,
                "RON",4.00);
        if (currency.equals("MDL")){
            return amount;
        }
        else if (rates.containsKey(currency)){
            return amount * rates.get(currency);
        }
        return amount;//fallback
    }

    /**
     * Clasă internă care reprezintă o tranzacție bancară.
     * Fiecare tranzacție are un ID unic, tip, sumă, monedă,
     * timestamp și descriere.
     */
    private static class Transaction{
        private final String id;
        private final String type;
        private final double amount;
        private final String currency;
        private final LocalDateTime timestamp;
        private final String description;

        /**
         * Constructor pentru crearea unei tranzacții.
         * Generează automat un ID unic și timestamp-ul curent.
         *
         * @param type Tipul tranzacției
         * @param amount Suma tranzacției
         * @param currency Moneda tranzacției
         * @param description Descrierea tranzacției
         */
        public Transaction(String type,double amount,String currency,String description){
            this.id = UUID.randomUUID().toString().substring(0,8);
            this.type = type;
            this.amount = amount;
            this.currency = currency;
            this.timestamp = LocalDateTime.now();
            this.description = description;
        }

        /**
         * @return Tipul tranzacției
         */
        public String getType(){return type;}

        /**
         * @return Suma tranzacției
         */
        public double getAmount(){return amount;}

        /**
         * @return Moneda tranzacției
         */
        public String getCurrency(){return currency;}

        /**
         * @return Timestamp-ul tranzacției
         */
        public LocalDateTime getTimestamp(){return timestamp;}

        /**
         * Returnează reprezentarea String a tranzacției.
         * Format: "Data | Tip | SumaMoneda | Descriere | ID"
         *
         * @return String formatat cu detaliile tranzacției
         */
        @Override
        public String toString(){
            return String.format("%s | %-20s | %10.2f%-4s | %-25s | %s",
                    timestamp.toLocalDate(),type,amount,currency,description,id);
        }
    }
}

