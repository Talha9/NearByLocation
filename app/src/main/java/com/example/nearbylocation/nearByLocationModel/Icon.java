
package com.example.nearbylocation.nearByLocationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Icon {

    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("mapPrefix")
    @Expose
    private String mapPrefix;
    @SerializedName("suffix")
    @Expose
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMapPrefix() {
        return mapPrefix;
    }

    public void setMapPrefix(String mapPrefix) {
        this.mapPrefix = mapPrefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
