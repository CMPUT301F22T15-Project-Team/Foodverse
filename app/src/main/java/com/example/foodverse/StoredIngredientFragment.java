package com.example.foodverse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * IngredientFragment
 * Used to create an interface for adding/editing/deleting a ingredient item.
 *
 * Version 1.0
 *
 * 2022-09-24
 */

public class StoredIngredientFragment extends DialogFragment {
    private StoredIngredient ingredient;
    private EditText ingredientDescription;
    private EditText ingredientCount;
    private EditText ingredientUnit;
    private EditText ingredientCost;
    private Spinner ingredientLocation;
    private Button ingredientExpiry;
    private OnFragmentInteractionListener listener;
    private Date expiryDate;
    private ArrayList<String> locationList = new ArrayList<>();
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> ingAdapter, catAdapter;

    /**
     * Default constructor for StoredIngredientFragment.
     */
    public StoredIngredientFragment() {
        super();
        this.ingredient = null;
    }

    /**
     * Constructor for StoredIngredientFragment with a given ingredient.
     *
     * @param ingredient A StoredIngredient representing the ingredient for
     * for which we want to see the details of.
     */
    public StoredIngredientFragment(StoredIngredient ingredient) {
        super();
        this.ingredient = ingredient;
    }

    /**
     * Interface for interacting with ingredient entries in the list.
     */
    public interface OnFragmentInteractionListener {
        void ingredientAdded(StoredIngredient ingredient);
        void ingredientEdited(StoredIngredient ingredient);
        void ingredientDeleted();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.stored_ingredient_fragment, null);

        // Initialize components
        ingredientDescription = view.findViewById(R.id.description_edit_text);
        ingredientCount = view.findViewById(R.id.count_edit_text);
        ingredientCost = view.findViewById(R.id.cost_edit_text);
        ingredientUnit = view.findViewById(R.id.unit_edit_text);
        ingredientLocation = view.findViewById(R.id.location_spinner);
        ingredientExpiry = view.findViewById(R.id.expiry_button);

        ingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, locationList);
        catAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, categoryList);

        StoredIngredientActivity act = (StoredIngredientActivity) getActivity();
        // The ingredients from the database are added to the spinner
        for (int i = 0; i < act.getLocations().size(); i++) {
            locationList.add(act.getLocations().get(i));
        }

        for (int i = 0; i < act.getCategories().size(); i++) {
            categoryList.add(act.getCategories().get(i));
        }

        /* Code for creating a spinner-style date picker inspired off of "Pop Up
        Date Picker Android Studio Tutorial" by Code With Cal on December 19th,
        2020 (https://www.youtube.com/watch?v=qCoidM98zNk). Retrieved September
        24th, 2022 */
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

        // Set the start date to the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        setNewExpiryDate(calendar);

        // Load data from an existing Ingredient object
        if (ingredient != null) {
            ingredientDescription.setText(ingredient.getDescription());
            ingredientCount.setText(Integer.toString(ingredient.getCount()));
            ingredientCost.setText(Integer.toString(ingredient.getUnitCost()));
            ingredientUnit.setText(ingredient.getUnit());

            // Load the time information
            calendar.setTime(ingredient.getBestBefore());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            setNewExpiryDate(calendar);

            // Locations stored in firebase, set correct selection.
            ingredientLocation.setSelection(locationList
                    .indexOf(ingredient.getLocation()));
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                dateSetListener, year, month, day);

        // Connect the DatePickerDialog to the expiry_button
        ingredientExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Ingredient Editor")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Delete", (dialog, which) -> {
                    listener.ingredientDeleted();
                })
                .setPositiveButton("Confirm",
                        (dialog, which) -> {
                            // Create a Ingredient object with the new values
                            StoredIngredient newIngredient = new StoredIngredient();
                            String descriptionStr = ingredientDescription
                                    .getText().toString();
                            String countStr = ingredientCount
                                    .getText().toString();
                            String costStr = ingredientCost
                                    .getText().toString();
                            String locationStr = locationList
                                    .get(ingredientLocation.getSelectedItemPosition());
                            String unitStr = ingredientUnit
                                    .getText().toString();

                            // Load the data into the Ingredient object
                            newIngredient.setDescription(descriptionStr);
                            newIngredient.setCount(Integer.parseInt(countStr));
                            float roundedCost = Float.parseFloat(costStr);
                            newIngredient.setUnitCost(
                                    (int) Math.ceil(roundedCost));
                            newIngredient.setLocation(locationStr);
                            newIngredient.setBestBefore(expiryDate);
                            newIngredient.setUnit(unitStr);

                            /* Determine if a ingredient was added or edited
                            based on if there was a previous value. */
                            if (ingredient == null) {
                                listener.ingredientAdded(newIngredient);
                            } else {
                                listener.ingredientEdited(newIngredient);
                            }
                        }).create();
    }

    // This code was copied from https://stackoverflow.com/questions/15421271/custom-fragmentdialog-with-round-corners-and-not-100-screen-width
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stored_ingredient_fragment, null);
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return view;
    }

    /**
     * This function changes the text displayed on the "expiry_button" to
     * match the time represented by calendar and updates the expiryDate global
     * variable to match.
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
        ingredientExpiry.setText(date);
        expiryDate = calendar.getTime();
    }
}
