package com.bourdi_bay.currencyconverter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class CurrencyDetailsAdapter extends ArrayAdapter<String> {

    private final Context m_context;
    private final Currencies m_eurosRefRates;
    private int m_positionToHide = -1;
    private double m_inputAmount = 1.0;

    CurrencyDetailsAdapter(Context context, Currencies eurosRefRates) {
        super(context, R.layout.currency_detail, eurosRefRates.getCurrencyNames());

        m_context = context;
        m_eurosRefRates = eurosRefRates;
    }

    @Override
    public int getCount() {
        return m_eurosRefRates.getCurrencies().size() - 1; // less the selected one in the spinner.
    }

    void hidePosition(int pos) {
        m_positionToHide = pos;
    }

    void setInputAmount(double inputAmount) {
        m_inputAmount = inputAmount;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) m_context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.currency_detail, parent, false);

        final Currency selectedCurrency = m_eurosRefRates.getCurrencies().get(m_positionToHide);
        final Currency targetCurrency = m_eurosRefRates.getCurrencies().get(m_positionToHide > position ? position : position + 1);

        final double newValue = Currencies.getCurrencyValue(m_inputAmount, selectedCurrency, targetCurrency);

        TextView nameCurrency = rowView.findViewById(R.id.name_currency_detail);
        nameCurrency.setText(targetCurrency.getName());
        TextView amountCurrency = rowView.findViewById(R.id.amount_currency_detail);
        amountCurrency.setText(String.format("%.04f", newValue));

        ImageView iconCurrency = rowView.findViewById(R.id.icon_currency_detail);
        iconCurrency.setImageResource(m_context.getResources().getIdentifier("_" + targetCurrency.getName().toLowerCase(), "drawable", m_context.getPackageName()));

        return rowView;
    }
}
