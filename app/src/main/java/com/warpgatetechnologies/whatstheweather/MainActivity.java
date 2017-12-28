package com.warpgatetechnologies.whatstheweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button btnGetWeather;
    EditText edtEnterCity;
    TextView mianTextView, descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEnterCity = findViewById(R.id.editTextCityName);
        mianTextView = findViewById(R.id.mainTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);

    }

    public void displayWeather(View view){

        String city = edtEnterCity.getText().toString();

        new DownloadWeather().execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=cd2c7c5c37ff272780840b65270e2de3");

    }

    protected class DownloadWeather extends AsyncTask<String, Void, String>{

        String AUTH_TOKEN = "cd2c7c5c37ff272780840b65270e2de3";
        String rawJson = "";
        WeatherObjects.Weather[] weather;

        @Override
        protected String doInBackground(String... urls) {

            URL url = null;


            try {
                url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
               // connection.setRequestProperty("Authorization", AUTH_TOKEN);
                connection.connect();

                int status = connection.getResponseCode();
                switch (status){
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        rawJson = br.readLine();

                        Log.d("JSON", " "+ rawJson);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return rawJson;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);

            WeatherObjects.Weather weather;
            try {
                weather = jsonParse(jsonString);

                Log.d("MAIN", weather.main);

                mianTextView.setText(weather.main);
                descriptionTextView.setText(weather.description);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private WeatherObjects.Weather jsonParse(String result) throws JSONException {


        WeatherObjects.Weather[] weathers;
        Gson gson = new Gson();

        JsonObject object = new JsonParser().parse(result).getAsJsonObject();

        weathers = gson.fromJson(object.get("weather"), WeatherObjects.Weather[].class);

        return weathers[0];

    }
}
