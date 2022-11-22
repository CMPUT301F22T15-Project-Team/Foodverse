package com.example.foodverse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * A {@link DialogFragment} subclass. Used to update Firebase with new
 * categories and locations.
 */
public class LocationCategoryManager extends DialogFragment {

    private String type;
    private Button addButton, deleteButton;
    private Spinner options;
    private EditText newField;
    private CategoryList catList;
    private LocationList locList;
    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayAdapter<String> itemAdapter;


    /**
     * A constructor for the fragment that takes in what type should be created.
     *
     * @param type A {@link String} for the type of the fragment, either
     *             "Ingredient Category", "Recipe Category" or "Location".
     */
    public LocationCategoryManager(String type, ArrayList<String> items) {
        this.type = type;
        this.itemList = items;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_location_category_manager, null);

        // Initialize components
        addButton = view.findViewById(R.id.manager_add_button);
        deleteButton = view.findViewById(R.id.manager_remove_button);
        options = view.findViewById(R.id.dropdown_spinner);
        newField = view.findViewById(R.id.manager_new_text);
        newField.setHint("New " + this.type);
        locList = null;
        catList = null;

        itemAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.ingredient_spinner, itemList);

        switch (this.type) {
            case "Location": {
                locList = new LocationList();
                addButton.setText("Add " + this.type);
                break;
            }
            case "Ingredient Category": {
                catList = new CategoryList("Ingredient");
                addButton.setText("Add Category");
                break;
            }
            case "Recipe Category": {
                catList = new CategoryList("Recipe");
                addButton.setText("Add Category");
                break;
            } default: {
                throw new IllegalArgumentException("Type is none of Location," +
                        "Ingredient Category or Recipe Category.");
            }
        }

        itemAdapter.setDropDownViewResource(R.layout.ingredient_spinner);
        options.setAdapter(itemAdapter);

        // When the user clicks on the plus button to add an ingredient
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newItem;
                if (!newField.getText().toString().equals("")) {
                    newItem = newField.getText().toString();
                    if (locList != null) {
                        locList.addLocation(newItem);
                    } else if (catList != null) {
                        catList.addCategory(newItem);
                    }
                    updateItemList();
                }
            }
        });

        deleteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ind;
                Log.d("LocCatMgr", "Removing item");
                ind = options.getSelectedItemPosition();
                if (locList != null) {
                    locList.deleteLocation(itemList.get(ind));
                } else if (catList != null) {
                    catList.deleteCategory(itemList.get(ind));
                }
                updateItemList();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle(this.type + " Manager")
                // The fragment is closed when this is selected
                .setPositiveButton("Close", null).create();
    }


    public void updateItemList() {
        itemList.clear();
        if (locList != null) {
            for (int i = 0; i < locList.getLocations().size(); i++) {
                itemList.add(locList.getLocations().get(i));
            }
        } else if (catList != null) {
            // Disregard warning, not correct. catList can be an object here.
            for (int i = 0; i < catList.getCategories().size(); i++) {
                itemList.add(catList.getCategories().get(i));
            }
        }
    }
}