
package com.example.nearbylocation.nearByLocationModel;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Response {

    @SerializedName("venues")
    @Expose
    private List<Venue> venues = null;
    @SerializedName("confident")
    @Expose
    private boolean confident;

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public boolean isConfident() {
        return confident;
    }

    public void setConfident(boolean confident) {
        this.confident = confident;
    }

}
