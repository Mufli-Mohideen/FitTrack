package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.recipeName.setText(recipe.getLabel());

        holder.recipeCalories.setText(String.format("Calories: %.2f", recipe.getCalories()));

        String ingredients = String.join(", ", recipe.getIngredientLines());
        holder.recipeIngredients.setText("Ingredients: " + ingredients);

        String dietLabels = recipe.getDietLabels().isEmpty() ? "None" : String.join(", ", recipe.getDietLabels());
        holder.recipeDietLabels.setText("Diet: " + dietLabels);

        String imageUrl = recipe.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.recipeImage);
        } else {
            holder.recipeImage.setImageResource(R.drawable.placeholder_image);
        }

        holder.viewRecipeLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(recipe.getUrl()));
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, recipeCalories, recipeIngredients, recipeDietLabels,viewRecipeLink;
        ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeCalories = itemView.findViewById(R.id.recipeCalories);
            recipeIngredients = itemView.findViewById(R.id.recipeIngredients);
            recipeDietLabels = itemView.findViewById(R.id.recipeDietLabels);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            viewRecipeLink = itemView.findViewById(R.id.viewRecipeLink);
        }
    }
}

