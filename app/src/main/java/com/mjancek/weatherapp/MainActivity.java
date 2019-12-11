package com.mjancek.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import static com.mjancek.weatherapp.Helper.getLink;
import static com.mjancek.weatherapp.Helper.isConnected;
import static com.mjancek.weatherapp.Helper.setWeatherIcon;


public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    protected static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 0;

    double latitude, longitude;

    TextView cityText;
    TextView temperatureText;
    TextView humidityText;
    TextView pressureText;
    ImageButton refreshBtn;
    TextView weatherIcon;
    TextView humidityIcon;
    TextView pressureIcon;
    TextView cloudsIcon;
    TextView windIcon;
    TextView cloudsText;
    TextView windText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find all UI items
        cityText = findViewById(R.id.cityTextView);
        temperatureText = findViewById(R.id.temperatureTextView);
        humidityText = findViewById(R.id.humidityTextView);
        pressureText = findViewById(R.id.pressureTextView);
        refreshBtn = findViewById(R.id.imageButton);
        weatherIcon = findViewById(R.id.weatherTextView);
        humidityIcon = findViewById(R.id.humidityCaption);
        pressureIcon = findViewById(R.id.pressureCaption);
        cloudsIcon = findViewById(R.id.cloudsCaption);
        cloudsText = findViewById(R.id.cloudsTextView);
        windIcon = findViewById(R.id.windCaption);
        windText = findViewById(R.id.windTextView);

        // Change font to weather font
        Typeface face = Typeface.createFromAsset(getAssets(), "font/weathericons_regular_webfont.ttf");
        weatherIcon.setTypeface(face);
        humidityIcon.setTypeface(face);
        pressureIcon.setTypeface(face);
        windIcon.setTypeface(face);
        cloudsIcon.setTypeface(face);



        // Get location
        // Through fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if app has permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissionAndLocation();
        }else{
            getLocation();
        }

        String url = getLink(latitude, longitude);
        // Need to create new instance of class on every call
        RequestAPI getData = new RequestAPI();
        getData.execute(url);

    }


    /**
     * Function to refresh weather after user press button
     * @param theBtn UI component button
     */
    public void refreshWeather(View theBtn){

        getLocation();
        // Need to create new instance of class on every call
        RequestAPI getData = new RequestAPI();
        getData.execute(getLink(latitude, longitude));

    }


    /*
     * Check if user want to give permission
     */
    public void getPermissionAndLocation(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            // TODO: Show dialog with reason to grand permission to access location (snackbar)
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            getLocation();      // FIXME: check this call
        }
    }



    // Invoked after getting result from requesting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION){
            if((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                getLocation();
            }else{
                // TODO: what to do when app doesn't have location?
                finishAndRemoveTask();
            }
        }
    }


    /**
     * Get location from fused location client
     */
    @SuppressLint("MissingPermission")
    public void getLocation(){

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }else{
                    Log.d("Location", "null");
                }
            }
        });
    }

    /**
     * Background task for requesting weather data from API.
     * Use AsyncTask to make background thread.
     */
    public class RequestAPI extends AsyncTask<String, Void, JSONObject> {

        Weather weatherStatus = new Weather();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // If there is no connection, inform user about it and cancel task
            if(!isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                this.cancel(true);
                refreshBtn.setVisibility(View.INVISIBLE);
                // TODO: disable all UI items and show information about no connection
            }
            refreshBtn.setEnabled(false);

        }

        @Override
        protected JSONObject doInBackground(String... APIurl) {

            HttpURLConnection urlConnection = null;

            try {
                // Request GET from API link
                URL url = new URL(APIurl[0]);
                Log.d("APIurl ", APIurl[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Get status of call
                int status = urlConnection.getResponseCode();
                if(status != HttpURLConnection.HTTP_OK){
                    Log.e("HTTP_NOT_OK", "HTTP code not OK");    // TODO: handle error from site
                }

                // Parse data from API
                StringBuilder response = new StringBuilder();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    response.append(current);
                    data = reader.read();
                }


                Log.d("Return", response.toString());
                return new JSONObject(response.toString());

            } catch (MalformedURLException e){
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(JSONObject APIData) {
            super.onPostExecute(APIData);

            // Parse JSON object to object variables
            try {
                JSONArray weatherData = APIData.getJSONArray("weather");
                JSONObject mainData = APIData.getJSONObject("main");
                JSONObject windData = APIData.getJSONObject("wind");
                JSONObject cloudData = APIData.getJSONObject("clouds");


                JSONObject jsonPart = weatherData.getJSONObject(0);
                weatherStatus.setID(jsonPart.getInt("id"));
                weatherStatus.setMain(jsonPart.getString("main"));
                weatherStatus.setDescription(jsonPart.getString("description"));
                weatherStatus.setIcon(jsonPart.getString("icon"));

                weatherStatus.setTemperature(mainData.getInt("temp"));
                weatherStatus.setPressure(mainData.getInt("pressure"));
                weatherStatus.setHumidity(mainData.getInt("humidity"));

                weatherStatus.setWind(windData.getInt("speed"));

                weatherStatus.setClouds(cloudData.getInt("all"));

                weatherStatus.setCity(APIData.getString("name"));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Main", weatherStatus.getMain());
            Log.d("City", weatherStatus.getCity());
            Log.d("Icon", weatherStatus.getIcon());
            showWeather(weatherStatus);

            refreshBtn.setEnabled(true);
        }
    }


    /**
     * Show weather information to user
     * @param weatherStatus weather data
     */
    void showWeather(Weather weatherStatus){

        cityText.setText(weatherStatus.getCity());
        temperatureText.setText(String.format(Locale.ENGLISH, "%d°C", weatherStatus.getTemperature()));
        humidityText.setText(String.format(Locale.ENGLISH, "%d%%", weatherStatus.getHumidity()));
        pressureText.setText(String.format(Locale.ENGLISH, "%d hPa", weatherStatus.getPressure()));
        weatherIcon.setText(setWeatherIcon(getBaseContext(), weatherStatus.getID()));
        cloudsText.setText(String.format(Locale.ENGLISH, "%d%%", weatherStatus.getClouds()));
        windText.setText(String.format(Locale.ENGLISH, "%.1fm/s", weatherStatus.getWind()));
    }
}
