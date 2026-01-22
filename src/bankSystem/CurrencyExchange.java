package bankSystem;

import javax.swing.*;
import java.util.*;

/**
 * Clasa CurrencyExchange gestionează operațiunile de schimb valutar și ratele de schimb.
 * Această clasă permite convertirea între diferite valute, actualizarea ratelor de schimb,
 * și oferă istoric al modificărilor ratelor.
 *
 * <p>Funcționalități principale:
 * <ul>
 *   <li>Gestiunea ratelor de schimb curente</li>
 *   <li>Conversia sumelor între diferite valute</li>
 *   <li>Actualizarea ratelor de schimb</li>
 *   <li>Păstrarea istoricului modificărilor ratelor</li>
 *   <li>Afișarea ratelor în format tabelar</li>
 * </ul>
 *
 * @author [Batiri Gheorhge]
 * @version 1.0
 * @since 2024
 */
public class CurrencyExchange {

    /**
     * Mapă care stochează ratele de schimb curente.
     * Cheile sunt codurile valutelor (EUR, USD, GBP, etc.),
     * iar valorile sunt ratele de schimb față de MDL.
     */
    private final Map<String,Double> rates;

    /**
     * Listă care păstrează istoricul modificărilor ratelor de schimb.
     * Fiecare modificare este înregistrată cu timestamp-ul său.
     */
    private final List<ExchangeRateHistory> history;

    /**
     * Constructor pentru clasa CurrencyExchange.
     * Inițializează structurile de date și setează ratele de schimb implicite.
     *
     * @see #initializeDefaultRates()
     */
    public CurrencyExchange(){
        rates = new HashMap<>();
        history = new ArrayList<>();
        initializeDefaultRates();
    }

    /**
     * Inițializează ratele de schimb implicite pentru principalele valute.
     * Ratele sunt exprimate în MDL pentru fiecare unitate de valută străină.
     *
     * <p>Ratele implicite sunt:
     * <ul>
     *   <li>EUR: 19.45 MDL</li>
     *   <li>USD: 17.50 MDL</li>
     *   <li>GBP: 22.10 MDL</li>
     *   <li>RON: 4.00 MDL</li>
     *   <li>CHF: 20.35 MDL</li>
     *   <li>CAD: 13.50 MDL</li>
     * </ul>
     *
     * <p>Fiecare rată este adăugată și în istoricul modificărilor.
     */
    public void initializeDefaultRates(){
        rates.put("EUR",19.45);
        rates.put("USD",17.50);
        rates.put("GBP",22.10);
        rates.put("RON",4.00);
        rates.put("CHF",20.35);
        rates.put("CAD",13.50);

        //adauga istoric initial
        for (Map.Entry<String,Double> entry : rates.entrySet()){
            history.add(new ExchangeRateHistory(entry.getKey(),entry.getValue()));
        }
    }

    /**
     * Returnează rata de schimb curentă pentru o anumită monedă.
     *
     * @param currency Codul monedei pentru care se cere rata (e.g., "EUR", "USD")
     * @return Rata de schimb față de MDL, sau 0.0 dacă moneda nu este găsită
     */
    public double getRate(String currency){
        return rates.getOrDefault(currency,0.0);
    }

    /**
     * Actualizează rata de schimb pentru o anumită monedă.
     * Rata nouă trebuie să fie pozitivă.
     * Modificarea este înregistrată în istoric.
     *
     * @param currency Codul monedei de actualizat
     * @param newRate Noua rată de schimb (trebuie să fie > 0)
     * @return true dacă actualizarea a reușit, false dacă rata nouă nu este pozitivă
     *
     * @see ExchangeRateHistory
     */
    public boolean updateRate(String currency,double newRate){
        if (newRate <= 0){
            return false;
        }

        double oldRate = rates.getOrDefault(currency,0.0);
        rates.put(currency,newRate);
        history.add(new ExchangeRateHistory(currency,newRate));

        System.out.printf("Curs %s actualizat :: %.2f → %.2f MDL (Δ %.2f)%n",
                currency,oldRate,newRate,newRate - oldRate);
        return true;
    }

    /**
     * Convertește o sumă dintr-o monedă în alta.
     * Conversia se face prin MDL ca monedă intermediară.
     *
     * <p>Exemple:
     * <ul>
     *   <li>EUR → USD: EUR → MDL → USD</li>
     *   <li>MDL → EUR: MDL → EUR (direct)</li>
     *   <li>EUR → MDL: EUR → MDL (direct)</li>
     * </ul>
     *
     * @param amount Suma de convertit
     * @param fromCurrency Moneda sursă
     * @param toCurrency Moneda destinație
     * @return Suma convertită în moneda destinație
     *
     * <p>Dacă monedele sunt identice, returnează suma originală.
     */
    public double convert(double amount,String fromCurrency,String toCurrency){
        if (fromCurrency.equals(toCurrency)){
            return amount;
        }

        double amountInMDL;
        if (fromCurrency.equals("MDL")){
            amountInMDL = amount;
        }
        else {
            amountInMDL = amount * getRate(fromCurrency);
        }

        if (toCurrency.equals("MDL")){
            return amountInMDL;
        }
        else {
            return amountInMDL / getRate(toCurrency);
        }
    }

    /**
     * Afișează ratele de schimb curente în format tabelar.
     * Tabelul include și variația procentuală zilnică simulată.
     *
     * <p>Formatul afișării:
     * <pre>
     * ──────────────────────────────────────────────────
     *    💱 CURS VALUTAR BNR
     * ──────────────────────────────────────────────────
     * Moneda    Curs         Schimb(%)
     * ──────────────────────────────────────────────────
     * EUR        19.4500      ↗ 0.1234%
     * USD        17.5000      ↘ 0.2345%
     * ──────────────────────────────────────────────────
     * </pre>
     *
     * @see #calculateDailyChange(String)
     * @see #getSortedCurrencies()
     */
    public void displayRates(){
        System.out.println("\n" + "─".repeat(50));
        System.out.println("   💱 CURS VALUTAR BNR");
        System.out.println("─".repeat(50));
        System.out.printf("%-8s %-12s %-15s%n", "Moneda", "Curs", "Schimb(%)");
        System.out.println("─".repeat(50));

        for (String currency : getSortedCurrencies()){
            double rate = rates.get(currency);
            double change = calculateDailyChange(currency);
            String changeSymbol = change >= 0 ? "↗" : "↘";
            System.out.printf("%-8s %12.4f %s %-8.4f%%%n",
                    currency,rate,changeSymbol,Math.abs(change));
        }
        System.out.println("─".repeat(50));
    }

    /**
     * Returnează o listă sortată alfabetic a codurilor valutelor disponibile.
     *
     * @return Listă sortată de coduri valutare
     */
    private List<String> getSortedCurrencies(){
        List<String> sorted = new ArrayList<>(rates.keySet());
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Simulează o variație procentuală zilnică pentru o monedă.
     * Variația este generată aleatoriu între -0.25% și +0.25%.
     *
     * <p>Această metodă este folosită doar pentru demonstrație.
     * Într-un sistem real, variațiile ar fi preluate dintr-o sursă externă.
     *
     * @param currency Codul monedei (nefolosit în simulare, menținut pentru consistență)
     * @return Variația procentuală zilnică simulată
     */
    private double calculateDailyChange(String currency){
        //simulam o schimbare zilnica
        Random random = new Random();
        return (random.nextDouble() - 0.5) * 0.5;//-0.25% pina la +0.25%
    }

    /**
     * Returnează o copie a mapelor cu ratele de schimb curente.
     * Copia este returnată pentru a preveni modificări accidentale.
     *
     * @return Mapă cu ratele de schimb curente
     */
    public Map<String,Double> getRates(){
        return  new HashMap<>(rates);
    }

    /**
     * Clasă internă care reprezintă o intrare în istoricul ratelor de schimb.
     * Fiecare intrare conține moneda, rata și timestamp-ul modificării.
     */
    private static class ExchangeRateHistory{
        /**
         * Codul monedei pentru care s-a modificat rata.
         */
        private final String currency;

        /**
         * Rata de schimb setată la momentul modificării.
         */
        private final double rate;

        /**
         * Data și ora exactă a modificării ratei.
         */
        private final Date timestamp;

        /**
         * Constructor pentru o intrare în istoricul ratelor.
         *
         * @param currency Codul monedei
         * @param rate Rata de schimb setată
         */
        public ExchangeRateHistory(String currency,double rate){
            this.currency = currency;
            this.rate = rate;
            this.timestamp = new Date();
        }

        /**
         * Returnează reprezentarea String a intrării în istoric.
         * Format: "Moneda - Rata MDL la Timestamp"
         *
         * @return String formatat cu detaliile intrării
         */
        @Override
        public String toString(){
            return String.format("%s - %.4f MDL la %s",currency,rate,timestamp);
        }
    }
}
