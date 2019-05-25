package com.mjancek.weatherapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;


class Helper {

    private static String API_KEY = "c8f689ae68579c9a4ecd94b2ce4dff44";
    private static String API_LINK = "http://api.openweathermap.org/data/2.5/weather?";

//    private static String[] resourceID[] = {["i01d.png", ]};



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

    /**
     * Set weather icon from weather font
     * @param context actual context of app
     * @param id weather id
     * @return string saved as string reference to font icon
     */
    public static String setWeatherIcon(Context context, int id) {
        id /= 100;
        String icon = "";
        if (id * 100 == 800) {
            int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = context.getString(R.string.weather_sunny);
            }
            else {
                icon = context.getString(R.string.weather_clear_night);
            }
        }
        else {
            switch (id) {
                case 2:
                    icon = context.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = context.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = context.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = context.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = context.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

}
