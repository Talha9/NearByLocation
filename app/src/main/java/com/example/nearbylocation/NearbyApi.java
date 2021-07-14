package com.example.nearbylocation;

import com.example.nearbylocation.nearByLocationModel.Category;
import com.example.nearbylocation.nearByLocationModel.Model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbyApi {

    @GET("/v2/venues/search")
    Call< Model > getFourSqurePlace(@Query("ll") String latlng,
                                    @Query("client_id") String client_id,
                                    @Query("client_secret") String client_secret,
                                    @Query("v") String date,
                                    @Query("limit") String radius,
                                    @Query("radius") String limit);
}
