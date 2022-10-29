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

/**
 * IngredientList
 * A custom class to display the description, amount, and unit cost of a
 * ingredient entry.
 *
 * Version 1.0
 *
 * 2022-09-24
 */

public class StoredIngredientList extends ArrayAdapter<StoredIngredient> {
    private ArrayList<StoredIngredient> ingredients;
    private Context context;

    public StoredIngredientList(Context context, ArrayList<StoredIngredient> ingredients) {
        super(context, 0, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_stored_ingredient,
                    parent,false);
        }
        StoredIngredient ingredient = ingredients.get(position);
        TextView ingredientDescription = view.findViewById(
                R.id.description_text);
        TextView ingredientCount = view.findViewById(R.id.count_text);
        TextView ingredientCost = view.findViewById(R.id.cost_text);

        ingredientDescription.setText(ingredient.getDescription());
        ingredientCount.setText("x" + Integer.toString(ingredient.getCount()));
        ingredientCost.setText(
                "$" + Integer.toString(ingredient.getUnitCost()));
        return view;
    }
}

