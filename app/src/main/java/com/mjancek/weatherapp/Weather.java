package com.mjancek.weatherapp;

public class Weather {

    private String main;
    private String description;
    private String icon;
    private int ID;
    private int temperature;
    private int pressure;
    private int humidity;
    private float wind;
    private int clouds;
    private String city;

    Weather() {

    }

    public Weather(String main, String description, String icon, int temperature, int pressure, int humidity, float wind, int clouds, String city, int ID) {
        this.main = main;
        this.description = description;
        this.icon = icon;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind = wind;
        this.clouds = clouds;
        this.city = city;
        this.ID = ID;
    }

    String getMain() {
        return main;
    }

    String getDescription() {
        return description;
    }

    String getIcon() {
        return icon;
    }

    int getTemperature() {
        return temperature;
    }

    int getPressure() {
        return pressure;
    }

    int getHumidity() {
        return humidity;
    }

    float getWind() {
        return wind;
    }

    int getClouds() {
        return clouds;
    }

    String getCity() { return city; }

    int getID() { return ID; }

    void setMain(String main) {
        this.main = main;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setIcon(String icon) {
        this.icon = icon;
    }

    void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    void setPressure(int pressure) {
        this.pressure = pressure;
    }

    void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    void setWind(float wind) {
        this.wind = wind;
    }

    void setClouds(int clouds) {
        this.clouds = clouds;
    }

    void setCity(String city) { this.city = city; }

    void setID(int ID) { this.ID = ID; }
}
