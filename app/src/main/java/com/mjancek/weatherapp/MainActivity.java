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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.mjancek.weatherapp.Helper.getLink;
import static com.mjancek.weatherapp.Helper.isConnected;


public class MainActivity extends AppCompatActivity {


    private FusedLocationProviderClient fusedLocationClient;
    protected static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 0;

    double latitude, longitude;

    Weather weatherStatus = new Weather();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        RequestAPI getData = new RequestAPI();
        getData.execute(url);

    }


    /**
     * Check if user want to give permission
     */
    public void getPermissionAndLocation(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            // TODO: Show dialog with reason to grand permission to access location (snackbar)
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
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
                }
            }
        });
    }


    /**
     * Background task for requesting weather data from API.
     * Use AsyncTask to make background thread.
     */
    public class RequestAPI extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... APIurl) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                // Request GET from API link
                url = new URL(APIurl[0]);
                Log.d("APIurl ", APIurl[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");

                // Get status of call
                int status = urlConnection.getResponseCode();
                if(status != HttpURLConnection.HTTP_OK){
                    Log.e("HTTP_NOT_OK", "HTTP code not OK");    // TODO: handle error from site
                }

                // Process response from API
//                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                String inLine;
//                Log.i("API Output", in.toString());
//                StringBuilder response = new StringBuilder();
//                while((inLine = in.readLine()) != null){
//                    response.append(inLine);
//                }
//                in.close();

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
                return response;

            } catch (MalformedURLException e){
                return null;
            } catch (IOException e) {
                return null;
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject APIdata = new JSONObject(s);
                JSONArray weatherData = new JSONArray(APIdata.getJSONArray("weather"));
                JSONArray mainData = new JSONArray(APIdata.getJSONObject("main"));
                JSONArray windData = new JSONArray(APIdata.getJSONObject("wind"));
                JSONArray cloudData = new JSONArray(APIdata.getJSONObject("clouds"));


                JSONObject jsonPart = weatherData.getJSONObject(0);
                weatherStatus.setMain(jsonPart.getString("main"));
                weatherStatus.setDescription(jsonPart.getString("description"));
                weatherStatus.setIcon(jsonPart.getString("icon"));

                weatherStatus.setTemperature(mainData.getInt(0));
                weatherStatus.setPressure(mainData.getInt(1));
                weatherStatus.setHumidity(mainData.getInt(2));

                weatherStatus.setWind(windData.getInt(0));

                weatherStatus.setClouds(cloudData.getInt(0));

                weatherStatus.setCity(APIdata.getString("name"));
                }
            catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }



}
