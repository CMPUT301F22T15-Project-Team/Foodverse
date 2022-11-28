package com.example.foodverse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

/**
 * RecipeIngredientFragment
 * Used to create an interface for adding a new {@link Ingredient} to a
 * {@link Recipe}, which does not already exist as a {@link StoredIngredient}.
 *
 * @version 1.0
 */
public class RecipeIngredientFragment extends DialogFragment {
    private Recipe rec;
    private RecipeIngredientFragment.OnFragmentInteractionListener listener;
    private EditText ingredientDescription;
    private EditText ingredientCount;
    private EditText ingredientUnit;
    private Spinner ingredientCategory;
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> catAdapter;

    /**
     * Default constructor for StoredIngredientFragment.
     */
    public RecipeIngredientFragment(Recipe recipe) {
        super();
        this.rec = recipe;
    }


    public interface OnFragmentInteractionListener{
        void ingAdded(Recipe rec, Ingredient ing);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeIngredientFragment.OnFragmentInteractionListener) {
            listener = (RecipeIngredientFragment.OnFragmentInteractionListener) context;
        } else{
            throw new RuntimeException(context.toString()+ "must implement OnFragmentInteractionListener");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.fragment_recipe_ingredient, null);

        // Initialize components
        ingredientDescription = view.findViewById(R.id.recing_description_edit_text);
        ingredientCount = view.findViewById(R.id.recing_count_edit_text);
        ingredientUnit = view.findViewById(R.id.recing_unit_edit_text);
        ingredientCategory = view.findViewById(R.id.recing_category_spinner);

        RecipeActivity act = (RecipeActivity) getActivity();

        // Get ingredient categories from firebase
        for (int i = 0; i < act.getIngCategories().size(); i++) {
            categoryList.add(act.getIngCategories().get(i));
        }
        catAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, categoryList);
        ingredientCategory.setAdapter(catAdapter);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Ingredient to Recipe")
                .setNeutralButton("Cancel", (dialog, which) -> {
                    listener.ingAdded(rec, null);
                })
                .setPositiveButton("Confirm",
                        (dialog, which) -> {
                            String descriptionStr = ingredientDescription
                                    .getText().toString();
                            String countStr = ingredientCount
                                    .getText().toString();
                            String categoryStr;
                            try {
                                categoryStr = categoryList
                                        .get(ingredientCategory.getSelectedItemPosition());
                            } catch (ArrayIndexOutOfBoundsException e) {
                                categoryStr = "";
                            }
                            String unitStr = ingredientUnit
                                    .getText().toString();

                            Ingredient newIngredient = new Ingredient(descriptionStr,
                                    Integer.parseInt(countStr), unitStr,
                                    categoryStr);
                            listener.ingAdded(rec, newIngredient);
                        }).create();
    }
}
