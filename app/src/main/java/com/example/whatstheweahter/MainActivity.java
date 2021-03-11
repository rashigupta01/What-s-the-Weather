package com.example.whatstheweahter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText city;
    TextView weatherInfoTextView;

    public class DownloadJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                Log.i("Check", "doInBackground: Started reading data");
                int data = reader.read();
                String result="";
                int count=0;
                while(data != -1) {
                    count++;
                    if(count%10==0) Log.i(Integer.toString(count), "doInBackground: in progress");
                    char temp = (char) data;
                    result += temp;
                    data = reader.read();
                }
                Log.i("Check", "doInBackground: Finished reading data");
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Issue", "doInBackground: Could not find weather");
//                Toast.makeText(getApplicationContext(), "Could not find weather.", Toast.LENGTH_SHORT).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray arr = new JSONArray(jsonObject.getString("weather"));
                    String weatherInfo = "";
                    for (int i=0; i<arr.length(); i++) {
                        JSONObject jsonPart = arr.getJSONObject(i);
                        weatherInfo += jsonPart.getString("main") + " : " + jsonPart.getString("description") + "\n";
                    }
                    weatherInfoTextView.setText(weatherInfo);
                } catch (Exception e) {
                    weatherInfoTextView.setText("");
                    Toast.makeText(getApplicationContext(), "Could not find weather :/", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                weatherInfoTextView.setText("");
                Toast.makeText(getApplicationContext(), "Could not find weather :/", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkWeather (View view) {
        weatherInfoTextView.setText("Featching data.....");
        String cityName = city.getText().toString();
        DownloadJSON downloadJSON = new DownloadJSON();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName +"&appid=d568b21f1ccc63098739ea4d41cf0e34";
        try {
            downloadJSON.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.cityName);
        weatherInfoTextView = findViewById(R.id.weatherInfoTextView);
    }
}