
package com.example.nearbylocation.nearByLocationModel;

import java.util.List;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Location {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("labeledLatLngs")
    @Expose
    private List< LabeledLatLng > labeledLatLngs = null;
    @SerializedName("distance")
    @Expose
    private int distance;
    @SerializedName("cc")
    @Expose
    private String cc;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("contextLine")
    @Expose
    private String contextLine;
    @SerializedName("contextGeoId")
    @Expose
    private int contextGeoId;
    @SerializedName("formattedAddress")
    @Expose
    private List<String> formattedAddress = null;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<LabeledLatLng> getLabeledLatLngs() {
        return labeledLatLngs;
    }

    public void setLabeledLatLngs(List<LabeledLatLng> labeledLatLngs) {
        this.labeledLatLngs = labeledLatLngs;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContextLine() {
        return contextLine;
    }

    public void setContextLine(String contextLine) {
        this.contextLine = contextLine;
    }

    public int getContextGeoId() {
        return contextGeoId;
    }

    public void setContextGeoId(int contextGeoId) {
        this.contextGeoId = contextGeoId;
    }

    public List<String> getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(List<String> formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
