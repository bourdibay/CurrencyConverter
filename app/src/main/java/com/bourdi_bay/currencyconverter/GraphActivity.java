package com.bourdi_bay.currencyconverter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    static private final String latestRatesHistoryFilename = "latest_rates_history.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        syncLatestRates();
    }

    private void syncLatestRates() {
        final TextView lastUpdatedText = (TextView) findViewById(R.id.lastUpdatedGraph);
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.GET, "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "Response from url is " + response);

                        try {
                            final CurrenciesBuilder builder = new CurrenciesBuilder();
                            final List<Currencies> listCurrencies = builder.fromXml(response);
                            if (listCurrencies.size() <= 0) {
                                return;
                            }
                            setupWidgets(listCurrencies);

                            CurrenciesFile file = new CurrenciesFile(GraphActivity.this, latestRatesHistoryFilename);
                            file.save(response);
                            lastUpdatedText.setText(getString(R.string.lastUpdated, listCurrencies.get(0).getTime()));
                        } catch (CurrenciesBuilder.CurrenciesBuilderException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            final CurrenciesFile file = new CurrenciesFile(GraphActivity.this, latestRatesHistoryFilename);
                            final CurrenciesBuilder builder = new CurrenciesBuilder();
                            final List<Currencies> listCurrencies = builder.fromXml(file.load());
                            if (listCurrencies.size() <= 0) {
                                return;
                            }
                            setupWidgets(listCurrencies);
                            lastUpdatedText.setText(getString(R.string.lastUpdated, listCurrencies.get(0).getTime()));
                            Toast.makeText(GraphActivity.this, getString(R.string.cannotGetLatestRates_TakePrev, listCurrencies.get(0).getTime()), Toast.LENGTH_SHORT).show();
                        } catch (IOException | CurrenciesBuilder.CurrenciesBuilderException e) {
                            lastUpdatedText.setText(getString(R.string.lastUpdated, getString(R.string.initLastUpdatedTextNever)));
                            Toast.makeText(GraphActivity.this, getString(R.string.cannotGetLatestRates_NoInternet), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }));
    }

    private HashMap<String, ArrayList<Pair<String, Currency>>> createCurrenciesHistory(final List<Currencies> listCurrencies) {
        HashMap<String, ArrayList<Pair<String, Currency>>> currenciesHistory = new HashMap<>();
        for (Currencies currencies : listCurrencies) {
            for (Currency currency : currencies.getCurrencies()) {

                if (!currenciesHistory.containsKey(currency.getName())) {
                    currenciesHistory.put(currency.getName(), new ArrayList<Pair<String, Currency>>());
                }

                currenciesHistory.get(currency.getName()).add(new Pair<>(currencies.getTime(), currency));
            }
        }
        return currenciesHistory;
    }

    private void refreshGraph(final HashMap<String, ArrayList<Pair<String, Currency>>> currenciesHistory, String sourceCurrency, String targetCurrency) {
        final LineChart chart = (LineChart) findViewById(R.id.chart);

        final ArrayList<Pair<String, Currency>> sourceCurrencyHistory = currenciesHistory.get(sourceCurrency);
        final ArrayList<Pair<String, Currency>> targetCurrencyHistory = currenciesHistory.get(targetCurrency);

        final List<Entry> entries = new ArrayList<>();

        for (int i = 0; i < sourceCurrencyHistory.size(); ++i) {
            // Cannot use a reverse iterator because the chart library seems to crash when xaxis' data are not in ascending order.
            final int realIndex = sourceCurrencyHistory.size() - i - 1;

            final Pair<String, Currency> formerCurrency = sourceCurrencyHistory.get(realIndex);
            final double value = Currencies.getCurrencyValue(1.0, formerCurrency.second, targetCurrencyHistory.get(realIndex).second);
            entries.add(new Entry(i, (float) value));
        }

        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return sourceCurrencyHistory.get(sourceCurrencyHistory.size() - 1 - (int) value).first;
            }
        });

        final LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(0xFF0000);
        dataSet.setValueTextColor(0x00FF00);
        dataSet.setDrawFilled(true);
        final LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void setupWidgets(final List<Currencies> listCurrencies) {
        final Currencies eurosRefRates = listCurrencies.get(0);

        final Spinner sourceCurrencies = (Spinner) findViewById(R.id.choice_currency_source);
        sourceCurrencies.setAdapter(new CurrencySpinnerAdapter(GraphActivity.this, eurosRefRates));
        final Spinner targetCurrencies = (Spinner) findViewById(R.id.choice_currency_target);
        targetCurrencies.setAdapter(new CurrencySpinnerAdapter(GraphActivity.this, eurosRefRates));
        // So that we have 2 different currencies at the launch of the app.
        if (eurosRefRates.getCurrencies().size() > 1) {
            targetCurrencies.setSelection(1);
        }

        final LineChart chart = (LineChart) findViewById(R.id.chart);
        final Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setLabelRotationAngle(75.0f);

        sourceCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                final String sourceCurrencyName = (String) sourceCurrencies.getAdapter().getItem(sourceCurrencies.getSelectedItemPosition());
                final String targetCurrencyName = (String) targetCurrencies.getAdapter().getItem(targetCurrencies.getSelectedItemPosition());
                refreshGraph(createCurrenciesHistory(listCurrencies), sourceCurrencyName, targetCurrencyName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        targetCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                final String sourceCurrencyName = (String) sourceCurrencies.getAdapter().getItem(sourceCurrencies.getSelectedItemPosition());
                final String targetCurrencyName = (String) targetCurrencies.getAdapter().getItem(targetCurrencies.getSelectedItemPosition());
                refreshGraph(createCurrenciesHistory(listCurrencies), sourceCurrencyName, targetCurrencyName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final ImageButton switchCurrency = (ImageButton) findViewById(R.id.switch_currency_graph);
        switchCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int sourcePosition = sourceCurrencies.getSelectedItemPosition();
                final int targetPosition = targetCurrencies.getSelectedItemPosition();
                sourceCurrencies.setSelection(targetPosition);
                targetCurrencies.setSelection(sourcePosition);
            }
        });
    }
}
