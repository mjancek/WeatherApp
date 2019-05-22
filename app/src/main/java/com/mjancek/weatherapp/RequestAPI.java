package com.mjancek.weatherapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Background task for requesting weather data from API.
 * Use AsyncTask to make background thread.
 */
public class RequestAPI extends AsyncTask<String, Void, String> {

    public Weather weatherStatus = new Weather();

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