package com.bourdi_bay.currencyconverter;

import java.util.ArrayList;
import java.util.List;

class Currencies {
    private final ArrayList<Currency> m_currencies = new ArrayList<>();
    private String m_time = "";

    String getTime() {
        return m_time;
    }

    void setTime(String time) {
        m_time = time;
    }

    ArrayList<Currency> getCurrencies() {
        return m_currencies;
    }

    boolean contains(String name) {
        return get(name) != null;
    }

    void add(Currency currency) {
        m_currencies.add(currency);
    }

    Currency get(String name) {
        for (Currency currency : m_currencies) {
            if (currency.getName().equals(name)) {
                return currency;
            }
        }
        return null;
    }

    List<String> getCurrencyNames() {
        List<String> currencyNames = new ArrayList<>();
        for (Currency curr : m_currencies) {
            currencyNames.add(curr.getName());
        }
        return currencyNames;
    }

    static double getCurrencyValue(double amount, Currency currentCurrency, Currency targetCurrency) {
        double initialToEuros = 1.0;
        if (!currentCurrency.getName().equals("EUR")) {
            initialToEuros = 1.0 / currentCurrency.getValue();
        }
        initialToEuros = initialToEuros * amount;
        return targetCurrency.getValue() * initialToEuros;
    }
}
