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

public class IngredientSpinnerAdapter extends ArrayAdapter<String> {
    private ArrayList<String> ingredients;
    private Context context;

    public IngredientSpinnerAdapter(Context context, ArrayList<String> ingredients) {
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
            view = LayoutInflater.from(context).inflate(R.layout.ingredient_spinner,
                    parent,false);
        }
        //Ingredient ingredient = ingredients.get(position);
        TextView ingredientDescription = view.findViewById(
                R.id.spinner_description_text);

        //ingredientDescription.setText(ingredient.getDescription());
        //ingredientDescription.setText("Hello");
        return ingredientDescription;
    }
}
