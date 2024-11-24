package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EdamamApi {
    @GET("api/recipes/v2")
    Call<RecipeResponse> getRecipes(@Query("q") String query,
                                    @Query("type") String type,
                                    @Query("app_id") String appId,
                                    @Query("app_key") String appKey);
}


