package com.example.foodverse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        TextView mealRecipe = view.findViewById(R.id.meal_recipe);
        TextView mealServings = view.findViewById(R.id.meal_servings);

        mealRecipe.setText(meal.getRecipeTitle());


        //mealDate.setText(meal.getDate().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(meal.getDate());
        //calendar.set(Calendar.HOUR_OF_DAY, 0);
        //calendar.set(Calendar.MINUTE, 0);
        //calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.MILLISECOND, 0);
        String dayString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CANADA);
        String monthString = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CANADA);
        //String numString = calendar.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.CANADA);
        //String numString = Integer(calendar.get(Calendar.DAY_OF_YEAR)).toString();
        int dateInt = calendar.get(Calendar.DAY_OF_MONTH);
        Integer toStringInt = new Integer(dateInt);
        mealDate.setText(dayString + " " + monthString + " " + toStringInt.toString());


        //mealDate.setText(calendar.getTime().toString());
        String dateString = meal.getDate().toString();

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date cleanDate = dateFormat.parse(dateFormat.format(meal.getDate()));
        //mealDate.setText(calendar.toString());
        //mealName.setText(meal.get);
        //mealServings.setText((int) meal.getNumberOfServings());

        return view;
    }
}
