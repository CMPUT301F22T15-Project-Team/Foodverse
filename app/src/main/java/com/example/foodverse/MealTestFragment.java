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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * MealTestFragment
 * Used to create a fragment for adding, editing or deleting meals.
 * Originally this was created as a test, but it has since supplanted
 * the original MealPlanFragment class.
 */

public class MealTestFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {
    private Meal meal;
    private EditText date;
    // Recipe-related elements still need to be implemented
    private ListView ingredientList;
    private EditText servings;

    private Button mealDate; // Date selector
    private Button addButton; // Plus button to add ingredient to meal
    private Date date2;
    private Spinner recipeSpinner; // Spinner for recipes
    private Spinner ingredientSpinner; // Spinner for ingredients
    private ArrayList<Ingredient> mealIngredients = new ArrayList<>(); //
    private ArrayList<String> ingredientStringList = new ArrayList<>();
    private ArrayList<Ingredient> addedIngredients = new ArrayList<>();
    private ArrayList<String> listedIngredients = new ArrayList<>();
    //private ArrayAdapter<Ingredient> ingAdapter;
    private ArrayAdapter<String> ingAdapter;
    private FirebaseFirestore db;
    private CollectionReference ingRef, storedRef;
    private HashSet<Ingredient> set = new HashSet<>();
    private ArrayAdapter<Ingredient> listViewAdapter;

    private MealTestFragment.OnFragmentInteractionListener listener;

    public MealTestFragment() {
        super();
        this.meal = null;
    }

    public MealTestFragment(Meal meal) {
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
        if (context instanceof MealTestFragment.OnFragmentInteractionListener) {
            listener = (MealTestFragment.OnFragmentInteractionListener) context;
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
        recipeSpinner = view.findViewById(R.id.recipe_spinner);
        ingredientSpinner = view.findViewById(R.id.meal_ingredient_spinner);
        addButton = view.findViewById(R.id.add_meal_ingredient_button);
        ingredientList = view.findViewById(R.id.meal_fragment_list);
        listViewAdapter = new IngredientAdapter(getActivity(), mealIngredients);
        ingredientList.setAdapter(listViewAdapter);

        ingredientSpinner.setOnItemSelectedListener(this);


        //ArrayAdapter<String> listedAdapter = new ArrayAdapter<String>(getActivity(), R.layout.content_stored_ingredient, listedIngredients);
        //ingredientList.setAdapter(listedAdapter);

        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                setNewExpiryDate(calendar);
            }
        };

        db = FirebaseFirestore.getInstance();
        db.enableNetwork();
        ingRef = db.collection("Ingredients");
        storedRef = db.collection("StoredIngredients");

        //ArrayList<Ingredient> arraySpinner = new ArrayList<>();
        ArrayList<Ingredient> arraySpinner = new ArrayList<>();
        //ingAdapter = new IngredientSpinnerAdapter(getActivity(), ingredientStringList);

        ingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, ingredientStringList);

        Ingredient test1 = new Ingredient("Test1", 1);
        Ingredient test2 = new Ingredient("Test6", 2);
        Ingredient test3 = new Ingredient("Test3", 3);


        arraySpinner.add(test1);
        arraySpinner.add(test2);
        arraySpinner.add(test3);

        for (int i = 0; i < arraySpinner.size(); i++) {
            ingredientStringList.add(arraySpinner.get(i).getDescription());
        }

        ingAdapter.setDropDownViewResource(R.layout.ingredient_spinner);

        ingredientSpinner.setAdapter(ingAdapter);



        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setNewExpiryDate(calendar);

        if (meal != null) {
            // Set the values for the various fields

            // Load the time information
            calendar.setTime(meal.getDate());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            setNewExpiryDate(calendar);

            //mealIngredients = meal.getIngredients();

            for (int i = 0; i < meal.getIngredients().size(); i++) {
                mealIngredients.add(meal.getIngredients().get(i));
            }
            listViewAdapter.notifyDataSetChanged();




        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                dateSetListener, year, month, day);

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
                //addedIngredients.add((Ingredient) ingredientSpinner.getSelectedItem());
                //listedIngredients.add((String) ingredientSpinner.getSelectedItem());
                //listedAdapter.notifyDataSetChanged();
                int ingIndex;
                ingIndex = ingredientSpinner.getSelectedItemPosition();
                mealIngredients.add(arraySpinner.get(ingIndex));
                //ingAdapter.notifyDataSetChanged();
                listViewAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add/Edit Meal")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.mealDeleted();
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //listener.onOkPressed(new City(city, province));
                        Meal newMeal = new Meal();
                        //LocalDate date = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        newMeal.setDate(date2);
                        // Add Recipe Stuff
                        // Add array of ingredients to meal
                        newMeal.setIngredients(mealIngredients);
                        if (meal == null) {
                            listener.mealAdded(newMeal);
                        } else {
                            listener.mealEdited(newMeal);
                        }
                        //listener.mealAdded(newMeal);

                    }
                }).create();
    }

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
        //this.locationInt = pos;
        //ingredientSpinner.setSelection(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
