package com.londonappbrewery.bitcointicker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
    private final String BASE_URL = "https://apiv2.bitcoinaverage" +
            ".com/indices/global/ticker/BTC";

    // Member Variables:
    TextView mPriceTextView;
    Button mRefreshButton;
    String mSelectedCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        mRefreshButton = (Button) findViewById(R.id.refresh_button);
        Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCurrency = parent.getItemAtPosition(position).toString();
                Log.d("Bitcoin", "Selected Currency: " + mSelectedCurrency);

                letsDoSomeNetworking(BASE_URL + mSelectedCurrency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Bitcoin", "User selected nothing new"); // doesn't get executed
            }
        });
        spinner.setSelection(adapter.getPosition("EUR")); // EUR is default

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letsDoSomeNetworking(BASE_URL + mSelectedCurrency);
            }
        });

    }

    // TODO: complete the letsDoSomeNetworking() method
    private void letsDoSomeNetworking(String url) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d("Bitcoin", "JSON: " + response.toString());
                updateUI(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("Bitcoin", "Request fail! Status code: " + statusCode);
                Log.d("Bitcoin", "Fail response: " + response);
                Log.e("ERROR", e.toString());
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //format so that every string always has 2 fractional-numbers TODO
    private void updateUI(JSONObject response) {
        try {
            String newPrice = response.getString("last");
            mPriceTextView.setText(newPrice);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Bitcoin", "Couldn't find specified String in passed json");
        }
    }


}
