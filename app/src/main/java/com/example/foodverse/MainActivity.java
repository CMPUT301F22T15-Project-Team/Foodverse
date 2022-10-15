package com.example.foodverse;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * MainActivity
 * This class is used to run the app and is responsible for managing all of the
 * other components within it.
 *
 * Version 1.0
 *
 * 2022-09-24
 *
 * No copyright
 */

public class MainActivity extends AppCompatActivity
        implements IngredientFragment.OnFragmentInteractionListener {

    // Declare the variables so that you will be able to reference it later.
    private ListView ingredientListView;
    private ArrayAdapter<Ingredient> ingredientAdapter;
    private ArrayList<Ingredient> ingredientArrayList;
    private int selectedIngredientIndex = -1;
    private int totalCost = 0;
    private TextView totalCostTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ingredientListView = findViewById(R.id.ingredient_list);
        totalCostTextView = findViewById(R.id.total_text_view);

        ingredientArrayList = new ArrayList<>();
        ingredientAdapter = new CustomList(this, ingredientArrayList);
        ingredientListView.setAdapter(ingredientAdapter);

        /* Inspiration for getting information on a selected listView item from
        https://www.flutter-code.com/2016/03/android-listview-item-selector
        -example.html. This code creates a listener for the ingredient list and
        alters the currently selected ingredient */
        ingredientListView.setOnItemClickListener(
                (adapterView, view, i, l) -> selectedIngredientIndex = i);

        final Button addIngredientButton = findViewById(
                R.id.add_ingredient_button);
        addIngredientButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IngredientFragment().show(getSupportFragmentManager(),
                        "ADD_INGREDIENT");
            }
        });

        /* Usage for using setOnItemClickListener found on
        https://stackoverflow.com/questions/49502070/how-do-i-add-click-
        listener-to-listview-items */
        ingredientListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                Ingredient ingredient = ingredientAdapter.getItem(position);
                selectedIngredientIndex = position;
                new IngredientFragment(ingredient).show(
                        getSupportFragmentManager(), "EDIT_INGREDIENT");
            }
        });
    }

    /**
     * Called when the user clicks the "Confirm" button in the
     * IngredientFragment and is creating a new ingredient. This function adds
     * the ingredient and updates the total cost of the ingredient list.
     *
     * @param ingredient The ingredient object that was edited.
     */
    @Override
    public void ingredientAdded(Ingredient ingredient) {
        totalCost += ingredient.getCount() * ingredient.getUnitCost();
        totalCostTextView.setText("$" + Integer.toString(totalCost));

        ingredientArrayList.add(ingredient);
        ingredientAdapter.notifyDataSetChanged();
    }

    /**
     * Called when the user clicks the "Delete" button in the
     * IngredientFragment. This function deletes the ingredient and updates the
     * total cost of the ingredient list.
     */
    @Override
    public void ingredientDeleted() {
        if (selectedIngredientIndex != -1) {
            Ingredient oldIngredient = ingredientArrayList.get(
                    selectedIngredientIndex);
            totalCost -= oldIngredient.getCount() * oldIngredient.getUnitCost();
            totalCostTextView.setText("$" + Integer.toString(totalCost));

            ingredientArrayList.remove(selectedIngredientIndex);
            ingredientAdapter.notifyDataSetChanged();

            // Change the index to be invalid
            selectedIngredientIndex = -1;
        }
    }

    /**
     * Called when the user clicks the "Confirm" button in the
     * IngredientFragment and is editing a ingredient entry. This function edits
     * an entry in the ingredient list and updates the total cost of the
     * ingredient list.
     *
     * @param ingredient The ingredient object that was edited.
     */
    @Override
    public void ingredientEdited(Ingredient ingredient) {
        Ingredient oldIngredient = ingredientArrayList.get(
                selectedIngredientIndex);
        totalCost -= oldIngredient.getCount() * oldIngredient.getUnitCost();
        totalCost += ingredient.getCount() * ingredient.getUnitCost();
        totalCostTextView.setText("$" + Integer.toString(totalCost));

        ingredientArrayList.set(selectedIngredientIndex, ingredient);
        ingredientAdapter.notifyDataSetChanged();
    }
}