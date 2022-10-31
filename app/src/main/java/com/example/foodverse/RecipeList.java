package com.example.foodverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class RecipeList extends ArrayAdapter<Recipe> {
    private ArrayList<Recipe> recipes;
    private Context context;

    public RecipeList(Context context,ArrayList<Recipe> recipe) {
        super(context, 0, recipe);
        this.recipes = recipe;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.recipe_layout,parent,false);
        }
        //get the recipe entry and the widgets' associated with each attribute
        Recipe recipe_obj = recipes.get(position);
        TextView recipeTitle = view.findViewById(R.id.title);
        TextView recipeCategory = view.findViewById(R.id.category);
        TextView recipeServings = view.findViewById(R.id.serving_size);
        TextView recipeComments = view.findViewById(R.id.comments);
        TextView recipePrep = view.findViewById(R.id.prep_time);


        //set the value of the attribute based on the information that was stored in the field when creating the food item entry
        recipeTitle.setText(recipe_obj.getTitle());
        recipeCategory.setText(recipe_obj.getCategory());
        recipeServings.setText(recipe_obj.getServings().toString());
        recipeComments.setText(recipe_obj.getComments());
        recipePrep.setText(recipe_obj.getPrepTime().toString());


        return view;
    }
}
