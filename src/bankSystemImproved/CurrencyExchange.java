package bankSystemImproved;

import javax.swing.*;
import java.util.*;

public class CurrencyExchange {
    private final Map<String,Double> rates;
    private final List<ExchangeRateHistory> history;

    public CurrencyExchange(){
        rates = new HashMap<>();
        history = new ArrayList<>();
        initializeDefaultRates();
    }

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

    public double getRate(String currency){
        return rates.getOrDefault(currency,0.0);
    }

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
            System.out.printf("% -8s % 12.4f%s%-8.4f%%%n",
                    currency,rate,changeSymbol,Math.abs(change));
        }
        System.out.println("─".repeat(50));
    }

    private List<String> getSortedCurrencies(){
        List<String> sorted = new ArrayList<>(rates.keySet());
        Collections.sort(sorted);
        return sorted;
    }

    private double calculateDailyChange(String currency){
        //simulam o schimbare zilnica
        Random random = new Random();
        return (random.nextDouble() - 0.5) * 0.5;//-0.25% pina la +0.25%
    }

    public Map<String,Double> getRates(){
        return  new HashMap<>(rates);
    }

    //clasa interna pentru istoric
    private static class ExchangeRateHistory{
        private final String currency;
        private final double rate;
        private final Date timestamp;

        public ExchangeRateHistory(String currency,double rate){
            this.currency = currency;
            this.rate = rate;
            this.timestamp = new Date();
        }

        @Override
        public String toString(){
            return String.format("%s - %.4f MDL la %s",currency,rate,timestamp);
        }
    }

}
