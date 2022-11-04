package com.example.foodverse;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Custom shopping list adapter to hold shopping list ingredients.
 */
public class ShoppingList extends ArrayAdapter<ShoppingListIngredient> {
    private ArrayList<ShoppingListIngredient> ingredients;
    private Context context;

    /**
     * Shopping List constructor
     * @param context
     * @param ingredients The ingredients array list.
     */
    public ShoppingList(Context context, ArrayList<ShoppingListIngredient> ingredients) {
        super(context, 0, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    /**
     * Returns the view of the {@link ShoppingList} after updating it.
     * @param position Position of the item within the data set.
     * @param convertView
     * @param parent
     * @return view of the {@link ShoppingList}
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.shopping_list_item,
                    parent,false);
        }

        // Retrieving the text views from the layout.
        ShoppingListIngredient ingredient = ingredients.get(position);
        TextView ingredientDescription = view.findViewById(R.id.description_text);
        TextView ingredientAmount = view.findViewById(R.id.amount_value);
        TextView ingredientUnit = view.findViewById(R.id.unit_value);
        TextView ingredientCategory = view.findViewById(R.id.category_value);
//        CheckBox ingredientCheckbox = view.findViewById(R.id.ingredient_checkbox);

        // Updating the values in the layout.
        ingredientDescription.setText(ingredient.getDescription());
        ingredientAmount.setText(Integer.toString(ingredient.getCount()));
        ingredientUnit.setText(ingredient.getUnit());
        ingredientCategory.setText(ingredient.getCategory());

//        ingredientCheckbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                view.setBackgroundColor(Color.GRAY);
//            }
//        });

        return view;
    }
}
