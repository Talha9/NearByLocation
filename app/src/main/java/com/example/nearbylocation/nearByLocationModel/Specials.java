
package com.example.nearbylocation.nearByLocationModel;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Specials {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("items")
    @Expose
    private List<Object> items = null;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Object> getItems() {
        return items;
    }

    public void setItems(List<Object> items) {
        this.items = items;
    }

}
