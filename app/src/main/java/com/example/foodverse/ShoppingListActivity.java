package com.example.foodverse;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ShoppingListActivity extends AppCompatActivity implements  ShoppingListFragment.OnFragmentInteractionListener{
    // Declare the variables so that you will be able to reference it later.
    ListView shoppingListView;
    ArrayAdapter<StoredIngredient> shoppingListAdapter;
    ArrayList<StoredIngredient> shoppingArrayList;
    int selectedIngredientIndex = -1;
    FirebaseFirestore db;
    final String TAG = "ShoppingListActivity";
    CollectionReference collectionReference;
    Button addButton;
    Spinner sortSpinner;
    String[] sortingMethods = {"Sort by Purchased", "Short by Description", "Sort by Category"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        shoppingListView = findViewById(R.id.shopping_list_view);
        addButton = findViewById(R.id.add_ingredient_to_storage_button);
        sortSpinner = findViewById(R.id.sort_Spinner);

        shoppingArrayList = new ArrayList<>();
        Calendar calendar = new GregorianCalendar(2019, 7, 7);
        StoredIngredient newIng1 = new StoredIngredient("Ingredient 1", 2, calendar.getTime(), "Pantry", "Boxes",3);

//        shoppingArrayList.add(new StoredIngredient("Ingredient 1", 2, calendar.getTime(), "Pantry", "Boxes",3));
//        shoppingArrayList.add(new StoredIngredient("Ingredient 2", 4, calendar.getTime(), "Fridge", "Cans",4));
        shoppingListAdapter = new ShoppingList(this, shoppingArrayList);
        shoppingListView.setAdapter(shoppingListAdapter);
        shoppingListAdapter.notifyDataSetChanged();

        // Get our database
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);

        collectionReference = db.collection("ShoppingList");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                shoppingArrayList.clear();
                // Add ingredients from the cloud
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getId()));
                    String hashCode = doc.getId();
                    String description = (String) doc.getData().get("Description");
                    /*
                     * https://stackoverflow.com/questions/54838634/timestamp-firebase-casting-error-to-date-util
                     * Answer by Niyas, February 23, 2019. Reference on casting
                     * from firebase.timestamp to java.date.
                     */
                    Date bestBefore = ((Timestamp)doc.getData().get("Best Before"))
                            .toDate();
                    String location = (String) doc.getData().get("Location");
                    Long count = (Long) doc.getData().get("Count");
//                    String unit = (String) doc.getData().get("Unit");
                    Long unitCost = (Long) doc.getData().get("Cost");
                    shoppingArrayList.add(
                            new StoredIngredient(description, count.intValue(),
                                    bestBefore, location,  unitCost.intValue()));
                }
                // Update with new cloud data
                shoppingListAdapter.notifyDataSetChanged();
            }
        });


        /*
         * Learned how to do this using the following link:
         * Author: AdamC
         * Title: How to update a spinner dynamically?
         * URL: https://stackoverflow.com/questions/3283337/how-to-update-a-spinner-dynamical
         * License: CC BY-SA 2.5
         * Date Posted: 2010-07-20
         * Date Retrieved: 2022-09-25
         */
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, sortingMethods);
        spinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);

        /* Inspiration for getting information on a selected listView item from
        https://www.flutter-code.com/2016/03/android-listview-item-selector
        -example.html. This code creates a listener for the ingredient list and
        alters the currently selected ingredient */
        shoppingListView.setOnItemClickListener(
                (adapterView, view, i, l) -> selectedIngredientIndex = i);


        shoppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StoredIngredient ingredient = shoppingListAdapter.getItem(position);
                selectedIngredientIndex = position;
                new ShoppingListFragment(ingredient).show(
                        getSupportFragmentManager(), "EDIT_INGREDIENT");
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new ShoppingListFragment().show(
                        getSupportFragmentManager(), "ADD_INGREDIENT");
            }
        });

//        sortSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Add code for sorting here
//            }
//        });
    }

    @Override
    public void ingredientAdded(StoredIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        // Grab data from the ingredient object
        data.put("Description", ingredient.getDescription());
        data.put("Best Before", ingredient.getBestBefore());
        data.put("Location", ingredient.getLocation());
        data.put("Unit", ingredient.getUnit());
        data.put("Count", ingredient.getCount());
        data.put("Cost", ingredient.getUnitCost());
        /*
         * Store all data under the hash code of the ingredient, so we can
         * store multiple similar ingredients.
         */
        collectionReference
                .document(String.valueOf(ingredient.hashCode()))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });
    }

    @Override
    public void ingredientEdited(StoredIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        StoredIngredient oldIngredient = shoppingArrayList.get(
                selectedIngredientIndex);

        // Grab data from the updated ingredient
        data.put("Description", ingredient.getDescription());
        data.put("Best Before", ingredient.getBestBefore());
        data.put("Location", ingredient.getLocation());
        data.put("Count", ingredient.getCount());
        data.put("Unit", ingredient.getCount());
        data.put("Cost", ingredient.getUnitCost());

        // Delete old ingredient and set new since hashCode() will return different result
        collectionReference.document(String.valueOf(oldIngredient.hashCode()))
                .delete();
        collectionReference
                .document(String.valueOf(ingredient.hashCode()))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log success
                        Log.d(TAG, "Data has been updated successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log any issues
                        Log.d(TAG, "Data could not be updated!" + e.toString());
                    }
                });
    }

    @Override
    public void ingredientDeleted() {
        if (selectedIngredientIndex != -1) {
            StoredIngredient oldIngredient = shoppingArrayList.get(
                    selectedIngredientIndex);
            // Remove ingredient from database
            collectionReference
                    .document(String.valueOf(oldIngredient.hashCode()))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Log success
                            Log.d(TAG, "Data has been deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Log any issues
                            Log.d(TAG, "Data could not be deleted!" + e.toString());
                        }
                    });

            // Change the index to be invalid
            shoppingArrayList.remove(selectedIngredientIndex);
            shoppingListAdapter.notifyDataSetChanged();
            selectedIngredientIndex = -1;
        }

    }
}
