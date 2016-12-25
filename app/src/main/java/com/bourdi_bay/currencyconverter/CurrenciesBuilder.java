package com.bourdi_bay.currencyconverter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

class CurrenciesBuilder {

    class CurrenciesBuilderException extends java.lang.Exception {
        CurrenciesBuilderException(String msg) {
            super(msg);
        }
    }

    Currencies fromXml(String xml) throws CurrenciesBuilderException {
        final Currencies currencies = new Currencies();
        // Add Euros into the list of currencies, since it is implicit.
        currencies.add(new Currency("EUR", 1.0));

        try {
            final XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            final XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("Cube")) {
                    final String time = xpp.getAttributeValue(null, "time");
                    if (time != null) {
                        currencies.setTime(time);
                    } else {
                        final String currency = xpp.getAttributeValue(null, "currency");
                        final String rate = xpp.getAttributeValue(null, "rate");
                        if (currency != null && rate != null) {
                            currencies.add(new Currency(currency, Double.parseDouble(rate)));
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            throw new CurrenciesBuilderException(e.getMessage());
        }
        return currencies;
    }
}
