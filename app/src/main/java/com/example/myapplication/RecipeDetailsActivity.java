package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailsActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipeAdapter;
    private EditText foodInputEditText;
    private Button fetchRecipesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_suggest);

        foodInputEditText = findViewById(R.id.foodInputEditText);
        fetchRecipesButton = findViewById(R.id.fetchRecipesButton);
        recipesRecyclerView = findViewById(R.id.recipesRecyclerView);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchRecipesButton.setOnClickListener(view -> {
            String query = foodInputEditText.getText().toString();
            fetchRecipes(query);
        });
    }

    private void fetchRecipes(String query) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            return;
        }

        String appId = "120f028d";
        String appKey = "df9be9e244d84576f94dd62944104b34";

        EdamamApi api = RetrofitClient.getClient().create(EdamamApi.class);
        Call<RecipeResponse> call = api.getRecipes(query, "public", appId, appKey);

        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Recipe> recipes = new ArrayList<>();
                    for (RecipeResponse.Hit hit : response.body().getHits()) {
                        recipes.add(hit.getRecipe());
                    }

                    if (recipes.isEmpty()) {
                        Toast.makeText(RecipeDetailsActivity.this, "No recipes found", Toast.LENGTH_SHORT).show();
                    } else {
                        recipeAdapter = new RecipeAdapter(recipes);
                        recipesRecyclerView.setAdapter(recipeAdapter);
                    }
                } else {
                    Toast.makeText(RecipeDetailsActivity.this, "Error fetching recipes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage(), t);
                Toast.makeText(RecipeDetailsActivity.this, "Failure in fetching recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
