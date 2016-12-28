package com.bourdi_bay.currencyconverter;

import android.content.Context;
import android.os.Bundle;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static private final String lastRatesFilename = "last_rates.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        final TextView lastUpdatedText = (TextView) findViewById(R.id.lastUpdated);
        lastUpdatedText.setText(getString(R.string.lastUpdated, getString(R.string.initLastUpdatedText)));

        syncLatestRates();
    }

    private void syncLatestRates() {
        final TextView lastUpdatedText = (TextView) findViewById(R.id.lastUpdated);
        final RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(new StringRequest(Request.Method.GET, "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", "Response from url is " + response);
                        try {
                            final String lastTime = readXmlResponse(response);
                            FileOutputStream outputStream;
                            outputStream = openFileOutput(lastRatesFilename, Context.MODE_PRIVATE);
                            outputStream.write(response.getBytes());
                            outputStream.close();

                            lastUpdatedText.setText(getString(R.string.lastUpdated, lastTime));
                        } catch (CurrenciesBuilder.CurrenciesBuilderException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            FileInputStream inputStream = openFileInput(lastRatesFilename);
                            StringBuilder fileContent = new StringBuilder("");
                            byte[] buffer = new byte[1024];
                            int n;
                            while ((n = inputStream.read(buffer)) != -1) {
                                fileContent.append(new String(buffer, 0, n));
                            }

                            final String lastTime = readXmlResponse(fileContent.toString());

                            lastUpdatedText.setText(getString(R.string.lastUpdated, lastTime));
                            Toast.makeText(MainActivity.this, getString(R.string.cannotGetLatestRates_TakePrev, lastTime), Toast.LENGTH_SHORT).show();
                        } catch (IOException | CurrenciesBuilder.CurrenciesBuilderException e) {
                            lastUpdatedText.setText(getString(R.string.lastUpdated, getString(R.string.initLastUpdatedTextNever)));
                            Toast.makeText(MainActivity.this, getString(R.string.cannotGetLatestRates_NoInternet), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }));
    }

    private void updateAmountInAdapter(CurrencyDetailsAdapter listCurrenciesAdapter, EditText amountInput) {
        try {
            listCurrenciesAdapter.setInputAmount(Double.parseDouble(amountInput.getText().toString()));
        } catch (Exception e) {
            listCurrenciesAdapter.setInputAmount(0.0);
        }
    }

    private String readXmlResponse(String response) throws CurrenciesBuilder.CurrenciesBuilderException {
        final ListView listCurrencies = (ListView) findViewById(R.id.list_currencies);
        final Spinner choiceCurrencies = (Spinner) findViewById(R.id.choice_currency);
        final EditText amountInput = (EditText) findViewById(R.id.input_amount);

        final CurrenciesBuilder builder = new CurrenciesBuilder();
        final Currencies eurosRefRates = builder.fromXml(response);

        final CurrencySpinnerAdapter choiceCurrenciesAdapter = new CurrencySpinnerAdapter(MainActivity.this, eurosRefRates);
        choiceCurrencies.setAdapter(choiceCurrenciesAdapter);

        final CurrencyDetailsAdapter listCurrenciesAdapter = new CurrencyDetailsAdapter(MainActivity.this, eurosRefRates);
        listCurrenciesAdapter.hidePosition(choiceCurrencies.getSelectedItemPosition());
        updateAmountInAdapter(listCurrenciesAdapter, amountInput);
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

        return eurosRefRates.getTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            syncLatestRates();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
