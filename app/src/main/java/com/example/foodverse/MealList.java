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
 * An array adapter to allow for meals to be displayed in a listview.
 * Most of this code was adapted from an earlier version of the StoredIngredientList
 * class.
 *
 * @version 1.0
 */
public class MealList extends ArrayAdapter<Meal> {
    private ArrayList<Meal> meals;
    private Context context;

    /**
     * A constructor to make an array adapter for meals.
     * @param context
     * @param meals The {@link ArrayList<Meal>} of meals to be linked to the listview
     */
    public MealList(Context context, ArrayList<Meal> meals) {
        super(context, 0, meals);
        this.meals = meals;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_meal, parent, false);
        }

        Meal meal = meals.get(position);

        TextView mealDate = view.findViewById(R.id.meal_date);
        //TextView mealName = view.findViewById(R.id.meal_name);
        TextView mealServings = view.findViewById(R.id.meal_servings);


        mealDate.setText(meal.getDate().toString());
        //mealName.setText(meal.get);
        //mealServings.setText((int) meal.getNumberOfServings());

        return view;
    }
}
