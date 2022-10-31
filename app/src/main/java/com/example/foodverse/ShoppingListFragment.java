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
    private OnFragmentInteractionListener listener;

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


        // Load data from an existing Ingredient object
        if (ingredient != null) {
            ingredientDescription.setText(ingredient.getDescription());
            ingredientCount.setText(Integer.toString(ingredient.getCount()));
            ingredientUnit.setText(ingredient.getUnit());
            ingredientCategory.setText(ingredient.getCategory());
        }

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
                            ShoppingListIngredient newIngredient = new ShoppingListIngredient();
                            String descriptionStr = ingredientDescription
                                    .getText().toString();
                            String countStr = ingredientCount
                                    .getText().toString();
                            String unitStr = ingredientUnit
                                    .getText().toString();
                            String categoryStr = ingredientCategory
                                    .getText().toString();

                            // Load the data into the Ingredient object
                            newIngredient.setDescription(descriptionStr);
                            newIngredient.setCount(Integer.parseInt(countStr));
                            newIngredient.setUnit(unitStr);
                            newIngredient.setCategory(categoryStr);

                            /* Determine if a ingredient was added or edited
                            based on if there was a previous value. */
                            if (ingredient == null) {
                                listener.ingredientAdded(newIngredient);
                            } else {
                                listener.ingredientEdited(newIngredient);
                            }
                        }).create();
    }

}
