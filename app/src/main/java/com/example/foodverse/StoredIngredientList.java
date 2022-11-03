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
import java.util.Date;

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
        TextView description = view.findViewById(R.id.description_text);
        TextView count = view.findViewById(R.id.count_text);
        TextView cost = view.findViewById(R.id.cost_text);
        TextView unit = view.findViewById(R.id.unit_text);
        TextView location = view.findViewById(R.id.location_text);
        TextView bestBefore = view.findViewById(R.id.best_before_text);

        description.setText(ingredient.getDescription());
        count.setText(Integer.toString(ingredient.getCount()));
        unit.setText(ingredient.getUnit() + " stored in:");
        location.setText(ingredient.getLocation());
        cost.setText("$" + Integer.toString(ingredient.getUnitCost()));
        Date date = ingredient.getBestBefore();

        /*
         * Date class measures year-1900, month-1, so fix here for display
         * https://docs.oracle.com/javase/8/docs/api/java/util/Date.html
         */
        String displayText = "Best Before: " + (date.getYear()+1900)
                + "-" + (date.getMonth()+1) + "-" + date.getDate();
        bestBefore.setText(displayText);
        return view;
    }
}

