package com.example.foodverse;

import android.annotation.SuppressLint;
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
import android.app.DialogFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * ShoppingListFragment
 * Used to create an interface for adding/editing/deleting a ShoppingListIngredient item.
 *
 * Version 1.0
 *
 * 2022-09-24
 */
public class ShoppingListFragment extends DialogFragment {
    // Declare the variables so that you will be able to reference it later.
    private ShoppingListIngredient ingredient;
    private EditText ingredientDescription;
    private EditText ingredientCount;
    private EditText ingredientUnit;
    private EditText ingredientCategory;
    private Spinner ingredientLocation;
    private Button ingredientExpiry;
    private OnFragmentInteractionListener listener;
    private Date expiryDate;

    /**
     * Constructor used when a new ingredient is being added to the list.
     */
    public ShoppingListFragment() {
        super();
        this.ingredient = null;
    }

    /**
     * Constructor used when an existing ingredient is being edited.
     * @param ingredient The ingredient being edited.
     */
    @SuppressLint("ValidFragment")
    public ShoppingListFragment(ShoppingListIngredient ingredient) {
        super();
        this.ingredient = ingredient;
    }

    /**
     * Interface for interacting with ingredient entries in the list.
     */
    public interface OnFragmentInteractionListener {
        void ingredientAdded(ShoppingListIngredient ingredient);
        void ingredientEdited(ShoppingListIngredient ingredient);
        void ingredientDeleted();
        void addToStorage(StoredIngredient ingredient);
    }

    /**
     * Called when fragment is attached to the context.
     * @param context The context being attached to.
     */
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

    /**
     * Initializes the components when the fragment is created.
     * @param savedInstanceState
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.shopping_list_fragment, null);

        // Initialize components
        ingredientDescription = view.findViewById(R.id.description_edit_text);
        ingredientCount = view.findViewById(R.id.count_edit_text);
        ingredientUnit = view.findViewById(R.id.unit_edit_text);
        ingredientCategory = view.findViewById(R.id.category_edit_text);
        ingredientLocation = view.findViewById(R.id.location_spinner);
        ingredientExpiry = view.findViewById(R.id.expiry_button);

        // Load data from an existing Ingredient object
        if (ingredient != null) {
            ingredientDescription.setText(ingredient.getDescription());
            ingredientCount.setText(Integer.toString(ingredient.getCount()));
            ingredientCategory.setText(ingredient.getCategory());
            ingredientUnit.setText(ingredient.getUnit());
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
                .setTitle("Add To Storage")
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
                            String unitStr = ingredientUnit
                                    .getText().toString();
                            String categoryStr = ingredientCategory
                                    .getText().toString();
                            String locationStr = ingredientLocation
                                    .getSelectedItem().toString();

                            // Load the data into the Ingredient object
                            newIngredient.setDescription(descriptionStr);
                            newIngredient.setCount(Integer.parseInt(countStr));
                            newIngredient.setUnit(unitStr);
                            newIngredient.setCategory(categoryStr);
                            newIngredient.setLocation(locationStr);
                            newIngredient.setBestBefore(expiryDate);

                            /* Determine if a ingredient was added or edited
                            based on if there was a previous value. */
                            if (ingredient == null) {
                                listener.addToStorage(newIngredient);
                            } else {
                                listener.addToStorage(newIngredient);
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