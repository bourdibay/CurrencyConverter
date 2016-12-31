package com.bourdi_bay.currencyconverter;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class CurrenciesConverterTest {

    private Currencies getCurrencies() {
        Currencies currencies = new Currencies();
        currencies.add(new Currency("EUR",1.0));
        currencies.add(new Currency("USD",1.0446));
        currencies.add(new Currency("GBP",0.85278));
        return currencies;
    }

    @Test
    public void convertEurToUSD()
    {
        Currencies currencies = getCurrencies();
        assertEquals(1.0446, Currencies.getCurrencyValue(1.0, currencies.get("EUR"), currencies.get("USD")), 0.0001);
    }
    @Test
    public void convertUSDToEur()
    {
        Currencies currencies = getCurrencies();
        assertEquals(0.9573, Currencies.getCurrencyValue(1.0, currencies.get("USD"), currencies.get("EUR")), 0.0001);
    }
    @Test
    public void convert4USDToEur()
    {
        Currencies currencies = getCurrencies();
        assertEquals(3.8292, Currencies.getCurrencyValue(4.0, currencies.get("USD"), currencies.get("EUR")), 0.0001);
    }
    @Test
    public void convertUSDToGBP()
    {
        Currencies currencies = getCurrencies();
        assertEquals(12.5720, Currencies.getCurrencyValue(15.4, currencies.get("USD"), currencies.get("GBP")), 0.0001);
    }
}
