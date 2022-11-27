package com.example.foodverse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * MealViewFragment
 * The fragment responsible for allowing the user to view a meal.
 * It is opened when a meal is selected from the main meal plan.
 *
 * @version 1.0
 *
 */
public class MealViewFragment extends DialogFragment {
    private Meal meal;
    private ListView ingredientList;
    private TextView viewName, viewServings, viewDate, viewRecipe;


    private ArrayList<Ingredient> mealIngredients = new ArrayList<>();
    private ArrayAdapter<Ingredient> listViewAdapter;

    private MealViewFragment.OnFragmentInteractionListener listener;

    /**
     * A constructor for the fragment
     */
    public MealViewFragment() {
        super();
        this.meal = null;
    }

    /**
     * A constructor for the fragment
     * @param meal A {@link Meal} that is being viewed.
     */
    public MealViewFragment(Meal meal) {
        super();
        this.meal = meal;
    }


    public interface OnFragmentInteractionListener {
        void mealAdded(Meal meal);
        void mealEdited(Meal meal);
        void mealDeleted();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MealViewFragment.OnFragmentInteractionListener) {
            listener = (MealViewFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.meal_view_fragment, null);

        // Initialize Components
        viewName = view.findViewById(R.id.meal_name_text_view);
        viewDate = view.findViewById(R.id.meal_date_text_view);
        viewRecipe = view.findViewById(R.id.meal_recipe_text_view);
        viewServings = view.findViewById(R.id.meal_servings_text_view);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(meal.getDate());
        String dayString = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.CANADA);
        String monthString = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CANADA);
        int dateInt = calendar.get(Calendar.DAY_OF_MONTH);
        Integer toStringInt = new Integer(dateInt);
        viewDate.setText(dayString + " " + monthString + " " + toStringInt.toString());

        viewName.setText(meal.getName());
        viewRecipe.setText(meal.getRecipeTitle());
        viewServings.setText("Servings: " + meal.getServingScaling() * meal.getServings());

        ingredientList = view.findViewById(R.id.meal_view_fragment_list);
        listViewAdapter = new IngredientAdapter(getActivity(), mealIngredients);
        ingredientList.setAdapter(listViewAdapter);

        for (int i = 0; i < meal.getIngredients().size(); i++) {
            mealIngredients.add(meal.getIngredients().get(i));
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("View Meal")
                // Nothing happens when the user selects 'Cancel'
                .setNeutralButton("OK", null)
                // The meal currently being viewed is deleted when 'Delete' is selected
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.mealDeleted();
                })
                // The edit fragment is opened when 'Edit' is selected
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MealPlanFragment(meal).show(
                                getFragmentManager(), "EDIT_MEAL");
                    }
                }).create();
    }
}
