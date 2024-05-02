package com.example.zensmartbikes.Map.ServerHelper;

import com.google.gson.annotations.SerializedName;

public class LocationData {
    @SerializedName("id")
    private String id;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lng")
    private String longitude;

    @SerializedName("temp")
    private String temperature;

    @SerializedName("hum")
    private String humidity;

    @SerializedName("pre")
    private String pressure;

    @SerializedName("alt")
    private String altitude;

    @SerializedName("created_date")
    private String createdDate;

    // Constructor, getters, and setters
    public LocationData() {
    }

    // Constructor
    public LocationData(String id, String latitude, String longitude, String temperature, String humidity, String pressure, String altitude, String createdDate) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.altitude = altitude;
        this.createdDate = createdDate;
    }

    // Getters and setters for each field
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
