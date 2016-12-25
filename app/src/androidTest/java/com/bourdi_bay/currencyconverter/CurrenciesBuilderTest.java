package com.bourdi_bay.currencyconverter;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class CurrenciesBuilderTest {

    private final String m_xmlContent = "<gesmes:Envelope xmlns:gesmes=\"http://www.gesmes.org/xml/2002-08-01\" xmlns=\"http://www.ecb.int/vocabulary/2002-08-01/eurofxref\">\n" +
            "<gesmes:subject>Reference rates</gesmes:subject>\n" +
            "<gesmes:Sender>\n" +
            "<gesmes:name>European Central Bank</gesmes:name>\n" +
            "</gesmes:Sender>\n" +
            "<Cube>\n" +
            "<Cube time=\"2016-12-21\">\n" +
            "<Cube currency_detail=\"USD\" rate=\"1.0421\"/>\n" +
            "<Cube currency_detail=\"JPY\" rate=\"122.31\"/>\n" +
            "<Cube currency_detail=\"BGN\" rate=\"1.9558\"/>\n" +
            "<Cube currency_detail=\"CZK\" rate=\"27.021\"/>\n" +
            "<Cube currency_detail=\"DKK\" rate=\"7.4342\"/>\n" +
            "<Cube currency_detail=\"GBP\" rate=\"0.84240\"/>\n" +
            "<Cube currency_detail=\"HUF\" rate=\"310.28\"/>\n" +
            "<Cube currency_detail=\"PLN\" rate=\"4.4082\"/>\n" +
            "<Cube currency_detail=\"RON\" rate=\"4.5194\"/>\n" +
            "<Cube currency_detail=\"SEK\" rate=\"9.6385\"/>\n" +
            "<Cube currency_detail=\"CHF\" rate=\"1.0689\"/>\n" +
            "<Cube currency_detail=\"NOK\" rate=\"9.0260\"/>\n" +
            "<Cube currency_detail=\"HRK\" rate=\"7.5303\"/>\n" +
            "<Cube currency_detail=\"RUB\" rate=\"63.6682\"/>\n" +
            "<Cube currency_detail=\"TRY\" rate=\"3.6607\"/>\n" +
            "<Cube currency_detail=\"AUD\" rate=\"1.4342\"/>\n" +
            "<Cube currency_detail=\"BRL\" rate=\"3.4729\"/>\n" +
            "<Cube currency_detail=\"CAD\" rate=\"1.3932\"/>\n" +
            "<Cube currency_detail=\"CNY\" rate=\"7.2369\"/>\n" +
            "<Cube currency_detail=\"HKD\" rate=\"8.0887\"/>\n" +
            "<Cube currency_detail=\"IDR\" rate=\"14000.60\"/>\n" +
            "<Cube currency_detail=\"ILS\" rate=\"3.9925\"/>\n" +
            "<Cube currency_detail=\"INR\" rate=\"70.7380\"/>\n" +
            "<Cube currency_detail=\"KRW\" rate=\"1243.87\"/>\n" +
            "<Cube currency_detail=\"MXN\" rate=\"21.3048\"/>\n" +
            "<Cube currency_detail=\"MYR\" rate=\"4.6681\"/>\n" +
            "<Cube currency_detail=\"NZD\" rate=\"1.5052\"/>\n" +
            "<Cube currency_detail=\"PHP\" rate=\"52.002\"/>\n" +
            "<Cube currency_detail=\"SGD\" rate=\"1.5036\"/>\n" +
            "<Cube currency_detail=\"THB\" rate=\"37.510\"/>\n" +
            "<Cube currency_detail=\"ZAR\" rate=\"14.5366\"/>\n" +
            "</Cube>\n" +
            "</Cube>\n" +
            "</gesmes:Envelope>";
    private CurrenciesBuilder m_currenciesBuilder = new CurrenciesBuilder();

    private void testCurrency(Currencies currencies, String expectedName, Double expectedRate) {
        assertTrue(currencies.contains(expectedName));
        assertEquals(expectedRate, currencies.get(expectedName).getValue(), 0.0001);
    }

    @Test
    public void buildFromXml_retrieveAllCurrencies() {
        try {
            Currencies currencies = m_currenciesBuilder.fromXml(m_xmlContent);

            assertEquals("2016-12-21", currencies.getTime());

            testCurrency(currencies, "USD", 1.0421);
            testCurrency(currencies, "JPY", 122.31);
            testCurrency(currencies, "BGN", 1.9558);
            testCurrency(currencies, "CZK", 27.021);
            testCurrency(currencies, "DKK", 7.4342);
            testCurrency(currencies, "GBP", 0.84240);
            testCurrency(currencies, "HUF", 310.28);
            testCurrency(currencies, "PLN", 4.4082);
            testCurrency(currencies, "RON", 4.5194);
            testCurrency(currencies, "SEK", 9.6385);
            testCurrency(currencies, "CHF", 1.0689);
            testCurrency(currencies, "NOK", 9.0260);
            testCurrency(currencies, "HRK", 7.5303);
            testCurrency(currencies, "RUB", 63.6682);
            testCurrency(currencies, "TRY", 3.6607);
            testCurrency(currencies, "AUD", 1.4342);
            testCurrency(currencies, "BRL", 3.4729);
            testCurrency(currencies, "CAD", 1.3932);
            testCurrency(currencies, "CNY", 7.2369);
            testCurrency(currencies, "HKD", 8.0887);
            testCurrency(currencies, "IDR", 14000.60);
            testCurrency(currencies, "ILS", 3.9925);
            testCurrency(currencies, "INR", 70.7380);
            testCurrency(currencies, "KRW", 1243.87);
            testCurrency(currencies, "MXN", 21.3048);
            testCurrency(currencies, "MYR", 4.6681);
            testCurrency(currencies, "NZD", 1.5052);
            testCurrency(currencies, "PHP", 52.002);
            testCurrency(currencies, "SGD", 1.5036);
            testCurrency(currencies, "THB", 37.510);
            testCurrency(currencies, "ZAR", 14.5366);
        } catch (CurrenciesBuilder.CurrenciesBuilderException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
