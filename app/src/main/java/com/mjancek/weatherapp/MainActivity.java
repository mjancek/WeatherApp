package com.mjancek.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import static com.mjancek.weatherapp.Helper.getLink;
import static com.mjancek.weatherapp.Helper.isConnected;


public class MainActivity extends AppCompatActivity {

    private RequestAPI getData = new RequestAPI();

    private FusedLocationProviderClient fusedLocationClient;
    protected static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 0;

    double latitude, longitude;

    public TextView cityText;
    public TextView temperatureText;
    public TextView humidityText;
    public TextView pressureText;
    public ImageView weatherImage;
    public ImageButton refreshBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityText = (TextView) findViewById(R.id.cityTextView);
        temperatureText = (TextView) findViewById(R.id.temperatureTextView);
        humidityText = (TextView) findViewById(R.id.humidityTextView);
        pressureText = (TextView) findViewById(R.id.pressureTextView);
        weatherImage = (ImageView) findViewById(R.id.weatherImageView);
        refreshBtn = (ImageButton) findViewById(R.id.imageButton);


        if(isConnected(this)){           // check if 'this' as context is right
            finishAndRemoveTask();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissionAndLocation();
        }else{
            getLocation();
        }

        String url = getLink(latitude, longitude);


        getData.execute(url);

    }


    public void refreshWeather(View theBtn){

        getLocation();

        getData.execute(getLink(latitude, longitude));

    }


    /**
     * Check if user want to give permission
     */
    public void getPermissionAndLocation(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            // TODO: Show dialog with reason to grand permission to access location (snackbar)
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            getLocation();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION){
            if((grantResults.length == 1) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                getLocation();
            }else{
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

        private Weather weatherStatus = new Weather();

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

                String response = "";

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char current = (char) data;
                    response += current;
                    data = reader.read();
                }


                Log.d("Return", response);
                return new JSONObject(response);

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

            try {
                JSONArray weatherData = APIData.getJSONArray("weather");
                JSONObject mainData = APIData.getJSONObject("main");
                JSONObject windData = APIData.getJSONObject("wind");
                JSONObject cloudData = APIData.getJSONObject("clouds");


                JSONObject jsonPart = weatherData.getJSONObject(0);
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

            cityText.setText(weatherStatus.getCity());
            temperatureText.setText(String.valueOf(weatherStatus.getTemperature()));
            humidityText.setText(String.valueOf(weatherStatus.getHumidity()));
            pressureText.setText(String.valueOf(weatherStatus.getPressure()));

        }
    }
}
