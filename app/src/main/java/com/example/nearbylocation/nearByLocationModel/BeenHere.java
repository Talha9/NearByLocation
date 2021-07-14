
package com.example.nearbylocation.nearByLocationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BeenHere {

    @SerializedName("lastCheckinExpiredAt")
    @Expose
    private int lastCheckinExpiredAt;

    public int getLastCheckinExpiredAt() {
        return lastCheckinExpiredAt;
    }

    public void setLastCheckinExpiredAt(int lastCheckinExpiredAt) {
        this.lastCheckinExpiredAt = lastCheckinExpiredAt;
    }

}
