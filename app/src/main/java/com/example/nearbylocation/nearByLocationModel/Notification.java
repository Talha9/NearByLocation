
package com.example.nearbylocation.nearByLocationModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Notification {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("item")
    @Expose
    private Item item;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
