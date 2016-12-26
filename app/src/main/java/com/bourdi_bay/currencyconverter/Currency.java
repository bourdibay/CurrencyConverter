package com.bourdi_bay.currencyconverter;

class Currency {
    private final String m_name;
    private final double m_value;

    Currency(String name, Double value) {
        m_name = name;
        m_value = value;
    }

    double getValue() {
        return m_value;
    }

    public String getName() {
        return m_name;
    }
}
