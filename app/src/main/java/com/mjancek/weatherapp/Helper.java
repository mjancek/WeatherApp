package com.mjancek.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Helper {

    private static String API_KEY = "c8f689ae68579c9a4ecd94b2ce4dff44";
    private static String API_LINK = "http://api.openweathermap.org/data/2.5/weather?";


    /**
     * Build API link with all parameters
     * @param lat latitude of phone
     * @param lon longitude of phone
     * @return full link to API
     */
    static String getLink(double lat, double lon){

        return API_LINK + String.format("lat=%s&lon=%s&APPID=%s&units=metric", lat, lon, API_KEY);
    }


    /**
     * Build link to current weather image
     * @param imageID ID of current weather image
     * @return full link to current weather image
     */
    static String getImage(String imageID){

        return "http://openweathermap.org/img/w/" + imageID;
    }


    /**
     * Check internet connection
     * @param context current state of phone
     * @return boolean if phone is connected to internet
     */
    static boolean isConnected(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

}
