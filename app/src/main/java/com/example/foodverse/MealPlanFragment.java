package com.example.foodverse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Build;
import android.widget.TextView;

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
 * Users can create a meal from either a recipe or miscellaneous ingredients.
 *
 * @version 1.0
 *
 */
public class MealPlanFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Meal meal;
    private EditText date;
    private EditText mealName;
    private TextView scale;
    // Recipe-related elements still need to be implemented
    private ListView ingredientList;
    //private TextView servings;

    private Button mealDate; // Date selector
    private Button addButton; // Button to add ingredient to meal
    private Button deleteButton;
    private Button positiveButton;
    private Button negativeButton;
    private Date date2; // The date which is added to a new or edited meal
    //private Spinner recipeSpinner;
    private Spinner ingredientSpinner; // Spinner for ingredients
    private Spinner recipeSpinner;

    private ArrayList<Ingredient> mealIngredients = new ArrayList<>();
    private ArrayList<String> ingredientStringList = new ArrayList<>();
    private ArrayList<String> recipeStringList = new ArrayList<>();
    private ArrayList<Integer> hashCodeList = new ArrayList<>();
    private ArrayList<Integer> servingList = new ArrayList<>();
    private ArrayAdapter<String> ingAdapter;
    private ArrayAdapter<String> recAdapter;
    private ArrayAdapter<Ingredient> listViewAdapter;
    private MealPlanActivity act;
    private int recipeServings = 0; // Initialized to a default value of 0
    private int recipeScaling  = 1; // Initialized to a default value of 1

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

    /**
     * To handle any firebase changes in the meal planner.
     */
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
        TextView servings;
        mealName = view.findViewById(R.id.meal_edit_name);
        mealDate = view.findViewById(R.id.date_button);
        scale = view.findViewById(R.id.scaling_servings);
        servings = view.findViewById(R.id.fragment_servings);
        ingredientSpinner = view.findViewById(R.id.meal_ingredient_spinner);
        recipeSpinner = view.findViewById(R.id.recipe_spinner);
        addButton = view.findViewById(R.id.add_meal_ingredient_button);
        deleteButton = view.findViewById(R.id.meal_ingredient_button);
        positiveButton = view.findViewById(R.id.positive_button);
        negativeButton = view.findViewById(R.id.negative_button);
        ingredientList = view.findViewById(R.id.meal_fragment_list);
        listViewAdapter = new IngredientAdapter(getActivity(), mealIngredients);
        ingredientList.setAdapter(listViewAdapter);
        ingredientSpinner.setOnItemSelectedListener(this);
        recipeSpinner.setOnItemSelectedListener(this);

        recipeStringList.add("No Recipe");
        hashCodeList.add(0);
        servingList.add(1);



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
        recAdapter = new ArrayAdapter<String>(getActivity(), R.layout.recipe_spinner, recipeStringList);

        act = (MealPlanActivity) getActivity();
        // The ingredients from the database are added to the spinner
        for (int i = 0; i < act.getDatabaseIngredients().size(); i++) {
            ingredientStringList.add(
                    act.getDatabaseIngredients().get(i).getDescription());
        }

        // Add it here
        for (int i = 0; i < act.getRecipeHashCodes().size(); i++) {
            hashCodeList.add(act.getRecipeHashCodes().get(i));
        }

        for (int i = 0; i < act.getRecipeTitleList().size(); i++) {
            recipeStringList.add(act.getRecipeTitleList().get(i));
        }

        for (int i = 0; i < act.getRecipeServingSizes().size(); i++) {
            servingList.add(act.getRecipeServingSizes().get(i));
        }

        // The spinner is set up to connect with the list of ingredients
        ingAdapter.setDropDownViewResource(R.layout.ingredient_spinner);
        ingredientSpinner.setAdapter(ingAdapter);
        recAdapter.setDropDownViewResource(R.layout.recipe_spinner);
        recipeSpinner.setAdapter(recAdapter);


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
            recipeSpinner.setSelection(hashCodeList.indexOf(meal.getRecipeHashCode()));
            Log.d("MEALFRAG", String.valueOf(hashCodeList.indexOf(meal.getRecipeHashCode())));
            Log.d("MEALFRAG", String.valueOf(meal.getRecipeHashCode()));
            mealName.setText(meal.getName());

            // Get all the ingredients from the meal and add them to
            // an array list to be displayed on a listview
            for (int i = 0; i < meal.getIngredients().size(); i++) {
                mealIngredients.add(meal.getIngredients().get(i));
            }
            listViewAdapter.notifyDataSetChanged();
            recipeServings = meal.getServings();
            recipeScaling = meal.getServingScaling();
        }

        // The following code to update the serving size was taken from the following link
        // https://stackoverflow.com/questions/14295150/how-to-update-a-textview-in-an-activity-constantly-in-an-infinite-loop
        // This handler updates the number of servings and the scaling number every 500 ms

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String selectedRecipeString = (String) recipeSpinner.getSelectedItem();
                int itemPosition = recipeStringList.indexOf(selectedRecipeString);
                if (itemPosition == -1) {
                    servings.setText("RECIPE NOT FOUND");
                } else if (itemPosition == 0) {
                    servings.setText("No Recipe");
                } else {
                    int calculatedServings = servingList.get(itemPosition) * recipeScaling;
                    servings.setText("Servings: " + calculatedServings);
                    scale.setText("x" + recipeScaling);
                }
                handler.postDelayed(this, 500);
            }
        });



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

        // When the user clicks on the green button to scale the recipe up
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeScaling = recipeScaling + 1;
            }
        });

        // When the user clicks on the red button to scale the recipe down
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recipeScaling > 1) {
                    recipeScaling = recipeScaling - 1;
                }
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
                        String newMealTitle = (String) recipeSpinner.getSelectedItem();
                        int newHash = hashCodeList.get(recipeSpinner.getSelectedItemPosition());
                        newMeal.addRecipe(newHash, newMealTitle);
                        int newMealServings = servingList.get(recipeSpinner.getSelectedItemPosition());
                        newMeal.setServingScaling(recipeScaling);
                        newMeal.setServings(newMealServings);
                        newMeal.setName(mealName.getText().toString());
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
