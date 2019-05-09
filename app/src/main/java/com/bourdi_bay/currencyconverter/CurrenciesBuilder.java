package com.bourdi_bay.currencyconverter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

class CurrenciesBuilder {

    List<Currencies> fromXml(String xml) throws CurrenciesBuilderException {
        final ArrayList<Currencies> listCurrencies = new ArrayList<>();

        try {
            final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            final XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));

            Currencies currentCurrencies = null;
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("Cube")) {
                    final String time = xpp.getAttributeValue(null, "time");
                    if (time != null) {
                        currentCurrencies = new Currencies();
                        listCurrencies.add(currentCurrencies);
                        // Add Euros into the list of currencies, since it is implicit.
                        currentCurrencies.add(new Currency("EUR", 1.0));
                        currentCurrencies.setTime(time);
                    } else {
                        final String currency = xpp.getAttributeValue(null, "currency");
                        final String rate = xpp.getAttributeValue(null, "rate");
                        if (currentCurrencies != null && currency != null && rate != null) {
                            currentCurrencies.add(new Currency(currency, Double.parseDouble(rate)));
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            throw new CurrenciesBuilderException(e.getMessage());
        }
        return listCurrencies;
    }

    class CurrenciesBuilderException extends java.lang.Exception {
        CurrenciesBuilderException(String msg) {
            super(msg);
        }
    }
}
