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

public class ShoppingList extends ArrayAdapter<Ingredient> {
    private ArrayList<Ingredient> ingredients;
    private Context context;

    public ShoppingList(Context context, ArrayList<Ingredient> ingredients) {
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
            view = LayoutInflater.from(context).inflate(R.layout.shopping_list_item,
                    parent,false);
        }
        Ingredient ingredient = ingredients.get(position);
        TextView ingredientDescription = view.findViewById(R.id.description_text);
        TextView ingredientAmount = view.findViewById(R.id.amount_value);
        TextView ingredientCost = view.findViewById(R.id.cost_value);
        TextView ingredientUnit = view.findViewById(R.id.unit_value);

        ingredientDescription.setText(ingredient.getDescription());
        ingredientAmount.setText("Amount:" + Integer.toString(ingredient.getCount()));
        ingredientCost.setText("Cost:$" + Integer.toString(ingredient.getUnitCost()));
//        ingredientUnit.setText
        return view;
    }
}
