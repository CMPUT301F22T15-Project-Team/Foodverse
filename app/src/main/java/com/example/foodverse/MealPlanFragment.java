package com.example.foodverse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * MealPlanFragment
 * The fragment responsible for allowing the user to add or edit meals.
 * Currently, the meal plan fragment does not support adding a recipe to
 * a meal or deleting an ingredient from a meal once it is added.
 *
 * @version 1.0
 *
 */

public class MealPlanFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Meal meal;
    private EditText date;
    // Recipe-related elements still need to be implemented
    private ListView ingredientList;
    private EditText servings;

    private Button mealDate; // Date selector
    private Button addButton; // Button to add ingredient to meal
    private Button deleteButton;
    private Date date2; // The date which is added to a new or edited meal
    //private Spinner recipeSpinner;
    private Spinner ingredientSpinner; // Spinner for ingredients
    private ArrayList<Ingredient> mealIngredients = new ArrayList<>();
    private ArrayList<String> ingredientStringList = new ArrayList<>();
    private ArrayAdapter<String> ingAdapter;
    private FirebaseFirestore db;
    private ArrayAdapter<Ingredient> listViewAdapter;
    private MealPlanActivity act;

    private MealPlanFragment.OnFragmentInteractionListener listener;

    /**
     * A constructor for the fragment when a new meal is being created
     */
    public MealPlanFragment() {
        super();
        this.meal = null;
    }

    /**
     * A constructor for the fragment when a meal is being edited
     * @param meal A {@link Meal} that is being edited
     */
    public MealPlanFragment(Meal meal) {
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
        if (context instanceof MealPlanFragment.OnFragmentInteractionListener) {
            listener = (MealPlanFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.meal_plan_fragment, null);

        // Initialize Components
        mealDate = view.findViewById(R.id.date_button);
        //recipeSpinner = view.findViewById(R.id.recipe_spinner);
        ingredientSpinner = view.findViewById(R.id.meal_ingredient_spinner);
        addButton = view.findViewById(R.id.add_meal_ingredient_button);
        deleteButton = view.findViewById(R.id.meal_ingredient_button);
        ingredientList = view.findViewById(R.id.meal_fragment_list);
        listViewAdapter = new IngredientAdapter(getActivity(), mealIngredients);
        ingredientList.setAdapter(listViewAdapter);
        ingredientSpinner.setOnItemSelectedListener(this);


        DatePickerDialog.OnDateSetListener dateSetListener;

        // Connect the DatePickerDialog with the date button
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                setNewExpiryDate(calendar);
            }
        };

        ingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, ingredientStringList);
        db = FirebaseFirestore.getInstance();
        db.enableNetwork();

        act = (MealPlanActivity) getActivity();
        // The ingredients from the database are added to the spinner
        for (int i = 0; i < act.getDatabaseIngredients().size(); i++) {
            ingredientStringList.add(
                    act.getDatabaseIngredients().get(i).getDescription());
        }

        // The spinner is set up to connect with the list of ingredients
        ingAdapter.setDropDownViewResource(R.layout.ingredient_spinner);
        ingredientSpinner.setAdapter(ingAdapter);


        // Set the start date to the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setNewExpiryDate(calendar);

        // If a meal is being edited and not added, meal won't be null
        if (meal != null) {
            // Set the values for the various fields

            // Load the time information
            calendar.setTime(meal.getDate());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            setNewExpiryDate(calendar);

            // Get all the ingredients from the meal and add them to
            // an array list to be displayed on a listview
            for (int i = 0; i < meal.getIngredients().size(); i++) {
                mealIngredients.add(meal.getIngredients().get(i));
            }
            listViewAdapter.notifyDataSetChanged();
        }

        // The DatePickerDialog allows for the user to select a date for
        // the meal.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                dateSetListener, year, month, day);

        // When the user clicks on the button to set the date of a meal
        mealDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });



        // When the user clicks on the plus button to add an ingredient
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ingIndex;
                Log.d("MealFrag", "Adding ingredient");
                ingIndex = ingredientSpinner.getSelectedItemPosition();
                mealIngredients.add(act.getDatabaseIngredients().get(ingIndex));

                listViewAdapter.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                        mealIngredients.remove(pos);
                        listViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add/Edit Meal")
                // Nothing happens when the user selects 'Cancel'
                .setNeutralButton("Cancel", null)
                // The meal currently being viewed is deleted when 'Delete' is selected
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.mealDeleted();
                })
                // The meal is added or edited when 'Confirm' is selected
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Meal newMeal = new Meal();
                        newMeal.setDate(date2);
                        // Add array of ingredients to meal
                        newMeal.setIngredients(mealIngredients);
                        if (meal == null) {
                            listener.mealAdded(newMeal);
                        } else {
                            listener.mealEdited(newMeal);
                        }

                    }
                }).create();
    }

    /**
     * This function changes the text displayed on the "expiry_button" to
     * match the time represented by calendar and updates the date2 global
     * variable to match.
     *
     * While it is called setNewExpiryDate, it does not refer to an expiry
     * date here. Here, it is used to get a date for a meal.
     *
     * @param calendar A calendar object representing the new expiry time.
     */
    private void setNewExpiryDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // The month is an index, so we need to add one to make it accurate
        month++;

        // Format the month and day strings
        String monthStr = Integer.toString(month);
        String dayStr = Integer.toString(day);
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        if (day < 10) {
            dayStr = "0" + dayStr;
        }

        // Update the spots that track/display the best before date
        String date = Integer.toString(year) + "-" + monthStr + "-" + dayStr;
        mealDate.setText(date);
        date2 = calendar.getTime();
    }

    public void deleteIngredient(View view) {
        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                mealIngredients.remove(pos);
                listViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
