package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodSuggest extends AppCompatActivity {

    private EditText foodInputEditText;
    private Button fetchRecipesButton;
    private RecyclerView recipesRecyclerView;
    private final String APP_ID = "your_app_id";
    private final String APP_KEY = "your_app_key";

}
