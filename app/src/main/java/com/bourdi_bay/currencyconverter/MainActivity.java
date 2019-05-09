package com.bourdi_bay.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static private final String latestRatesFilename = "latest_rates.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ((TextView) findViewById(R.id.lastUpdated)).setText(getString(R.string.lastUpdated, getString(R.string.initLastUpdatedText)));

        syncLatestRates();
    }

    private void syncLatestRates() {
        final TextView lastUpdatedText = findViewById(R.id.lastUpdated);
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.GET, "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "Response from url is " + response);
                        try {
                            final Currencies currencies = getCurrencies(response);
                            if (currencies == null) {
                                return;
                            }
                            setupWidgets(currencies);

                            final CurrenciesFile file = new CurrenciesFile(MainActivity.this, latestRatesFilename);
                            file.save(response);
                            lastUpdatedText.setText(getString(R.string.lastUpdated, currencies.getTime()));
                        } catch (CurrenciesBuilder.CurrenciesBuilderException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            final CurrenciesFile file = new CurrenciesFile(MainActivity.this, latestRatesFilename);
                            final Currencies currencies = getCurrencies(file.load());
                            if (currencies == null) {
                                return;
                            }

                            lastUpdatedText.setText(getString(R.string.lastUpdated, currencies.getTime()));
                            Toast.makeText(MainActivity.this, getString(R.string.cannotGetLatestRates_TakePrev, currencies.getTime()), Toast.LENGTH_SHORT).show();
                        } catch (IOException | CurrenciesBuilder.CurrenciesBuilderException e) {
                            lastUpdatedText.setText(getString(R.string.lastUpdated, getString(R.string.initLastUpdatedTextNever)));
                            Toast.makeText(MainActivity.this, getString(R.string.cannotGetLatestRates_NoInternet), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }));
    }

    private Currencies getCurrencies(String xml) throws CurrenciesBuilder.CurrenciesBuilderException {
        final CurrenciesBuilder builder = new CurrenciesBuilder();
        final List<Currencies> listEurosRefRates = builder.fromXml(xml);
        if (listEurosRefRates.size() <= 0) {
            return null;
        }
        return listEurosRefRates.get(0);
    }

    private void updateAmountInAdapter(CurrencyDetailsAdapter listCurrenciesAdapter, EditText amountInput) {
        try {
            listCurrenciesAdapter.setInputAmount(Double.parseDouble(amountInput.getText().toString()));
        } catch (Exception e) {
            listCurrenciesAdapter.setInputAmount(0.0);
        }
    }

    private void setupWidgets(Currencies eurosRefRates) {
        final Spinner choiceCurrencies = findViewById(R.id.choice_currency);
        choiceCurrencies.setAdapter(new CurrencySpinnerAdapter(MainActivity.this, eurosRefRates));

        final CurrencyDetailsAdapter listCurrenciesAdapter = new CurrencyDetailsAdapter(MainActivity.this, eurosRefRates);
        listCurrenciesAdapter.hidePosition(choiceCurrencies.getSelectedItemPosition());
        final EditText amountInput = findViewById(R.id.input_amount);
        updateAmountInAdapter(listCurrenciesAdapter, amountInput);
        final ListView listCurrencies = findViewById(R.id.list_currencies);
        listCurrencies.setAdapter(listCurrenciesAdapter);

        choiceCurrencies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                listCurrenciesAdapter.hidePosition(choiceCurrencies.getSelectedItemPosition());
                listCurrenciesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateAmountInAdapter(listCurrenciesAdapter, amountInput);
                listCurrenciesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                syncLatestRates();
                return true;
            case R.id.action_graph:
                Intent act = new Intent(this, GraphActivity.class);
                TaskStackBuilder.create(this).addNextIntentWithParentStack(act).startActivities();
                return true;
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
