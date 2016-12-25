package com.bourdi_bay.currencyconverter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class CurrencySpinnerAdapter extends ArrayAdapter<String> {

    private final Context m_context;
    private final Currencies m_eurosRefRates;

    CurrencySpinnerAdapter(Context context, Currencies eurosRefRates) {
        super(context, R.layout.currency_choice, eurosRefRates.getCurrencyNames());

        m_context = context;
        m_eurosRefRates = eurosRefRates;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) m_context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.currency_choice, parent, false);

        Currency currency = m_eurosRefRates.getCurrencies().get(position);

        TextView label = (TextView) rowView.findViewById(R.id.name_currency_choice);
        label.setText(currency.getName());

        ImageView iconCurrency = (ImageView) rowView.findViewById(R.id.icon_currency_choice);
        iconCurrency.setImageResource(m_context.getResources().getIdentifier("_" + currency.getName().toLowerCase(), "drawable", m_context.getPackageName()));

        return rowView;
    }
}