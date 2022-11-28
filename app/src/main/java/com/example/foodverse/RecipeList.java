package com.example.foodverse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * An array adapter to allow for recipe to be displayed in a listview.
 * Most of this code was adapted from assignment 1 code.
 *
 * @version 1.0
 */
public class RecipeList extends ArrayAdapter<Recipe> {
    private ArrayList<Recipe> recipes;
    private Context context;


    /**
     * A constructor to make an array adapter for meals.
     * @param context
     * @param recipe The {@link ArrayList<Recipe>} of recipe to be linked to the listview
     * */
    public RecipeList(Context context,ArrayList<Recipe> recipe) {
        super(context, 0, recipe);
        this.recipes = recipe;
        this.context = context;
    }



    /**
     * Returns the view of the {@link RecipeList} after updating it.
     * @param position Position of the item within the data set.
     * @param convertView
     * @param parent
     * @return view of the {@link RecipeList}
     */
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
        ImageView recipePhoto = view.findViewById(R.id.food_img);


        //set the value of the attribute based on the information that was stored in the field when creating the food item entry
        recipeTitle.setText(recipe_obj.getTitle());
        if (recipe_obj.getPhotoBitmap() != null) {
            try {
                recipePhoto.setImageBitmap(recipe_obj.getPhotoBitmap());
            } catch (SecurityException e) {
                Log.e("ImageActivity", e.getMessage());
                recipe_obj.setPhotoBitmap(null);
            }
        } else {
            recipePhoto.setImageBitmap(null);
        }

        return view;
    }
}
