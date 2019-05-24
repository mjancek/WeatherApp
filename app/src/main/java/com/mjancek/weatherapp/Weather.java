package com.mjancek.weatherapp;

public class Weather {

    private String main;
    private String description;
    private String icon;
    private int temperature;
    private int pressure;
    private int humidity;
    private float wind;
    private int clouds;
    private String city;

    Weather() {

    }

    public Weather(String main, String description, String icon, int temperature, int pressure, int humidity, float wind, int clouds, String city) {
        this.main = main;
        this.description = description;
        this.icon = icon;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.wind = wind;
        this.clouds = clouds;
        this.city = city;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public float getWind() {
        return wind;
    }

    public int getClouds() {
        return clouds;
    }

    public String getCity() { return city; }

    public void setMain(String main) {
        this.main = main;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setWind(float wind) {
        this.wind = wind;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public void setCity(String city) { this.city = city; }
}
