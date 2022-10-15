package com.example.foodverse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * IngredientFragment
 * Used to create an interface for adding/editing/deleting a ingredient item.
 *
 * Version 1.0
 *
 * 2022-09-24
 *
 * No copyright
 */

public class IngredientFragment extends DialogFragment {
    private Ingredient ingredient;
    private EditText ingredientDescription;
    private EditText ingredientCount;
    private EditText ingredientCost;
    private Spinner ingredientLocation;
    private Button ingredientExpiry;
    private OnFragmentInteractionListener listener;
    private Date expiryDate;

    public IngredientFragment() {
        super();
        this.ingredient = null;
    }

    public IngredientFragment(Ingredient ingredient) {
        super();
        this.ingredient = ingredient;
    }

    /**
     * Interface for interacting with ingredient entries in the list.
     */
    public interface OnFragmentInteractionListener {
        void ingredientAdded(Ingredient ingredient);
        void ingredientEdited(Ingredient ingredient);
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
                .inflate(R.layout.fragment_edit_text, null);

        // Initialize components
        ingredientDescription = view.findViewById(R.id.description_edit_text);
        ingredientCount = view.findViewById(R.id.count_edit_text);
        ingredientCost = view.findViewById(R.id.cost_edit_text);
        ingredientLocation = view.findViewById(R.id.location_spinner);
        ingredientExpiry = view.findViewById(R.id.expiry_button);

        /* Code for using a spinner inspired off of "Setting spinners in
        fragment" by Nick on September 2nd, 2012 under the CC BY-SA 4.0 license
        (https://stackoverflow.com/questions/12238155/
        setting-spinners-in-fragment). Code was retrieved on September 24th,
        2022 and modified. */
        String []locations = {"Pantry", "Freezer", "Fridge"};
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                this.getActivity(), android.R.layout.simple_spinner_item,
                locations);

        locationAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        ingredientLocation.setAdapter(locationAdapter);

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

            // Load the time information
            calendar.setTime(ingredient.getBestBefore());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            setNewExpiryDate(calendar);

            // The enum is stored as {PANTRY, FREEZER, FRIDGE}
            Location location = ingredient.getLocation();
            if (location == Location.PANTRY) {
                ingredientLocation.setSelection(0);
            } else if (location == Location.FREEZER) {
                ingredientLocation.setSelection(1);
            } else {
                ingredientLocation.setSelection(2);
            }
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
                            Ingredient newIngredient = new Ingredient();
                            String descriptionStr = ingredientDescription
                                    .getText().toString();
                            String countStr = ingredientCount
                                    .getText().toString();
                            String costStr = ingredientCost
                                    .getText().toString();
                            int locationPosition = ingredientLocation
                                    .getSelectedItemPosition();

                            // Load the data into the Ingredient object
                            newIngredient.setDescription(descriptionStr);
                            newIngredient.setCount(Integer.parseInt(countStr));
                            float roundedCost = Float.parseFloat(costStr);
                            newIngredient.setUnitCost(
                                    (int) Math.ceil(roundedCost));
                            newIngredient.setLocation(
                                    Location.values()[locationPosition]);
                            newIngredient.setBestBefore(expiryDate);

                            /* Determine if a ingredient was added or edited
                            based on if there was a previous value. */
                            if (ingredient == null) {
                                listener.ingredientAdded(newIngredient);
                            } else {
                                listener.ingredientEdited(newIngredient);
                            }
                        }).create();
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
