package com.example.foodverse;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.Date;
import java.util.HashMap;

/**
 * IngredientActivity
 * This class is used to run the app and is responsible for managing all of the
 * other components within it.
 *
 * @Version 1.0
 *
 * 2022-09-24
 *
 */

public class IngredientActivity extends AppCompatActivity
        implements IngredientFragment.OnFragmentInteractionListener {

    // Declare the variables so that you will be able to reference it later.
    private ListView ingredientListView;
    private ArrayAdapter<StoredIngredient> ingredientAdapter;
    private ArrayList<StoredIngredient> ingredientArrayList;
    private int selectedIngredientIndex = -1;
    private FirebaseFirestore db;
    private final String TAG = "IngredientActivity";
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        ingredientListView = findViewById(R.id.ingredient_list);

        ingredientArrayList = new ArrayList<>();
        ingredientAdapter = new IngredientList(this, ingredientArrayList);
        ingredientListView.setAdapter(ingredientAdapter);

        // Get our database
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);

        collectionReference = db.collection("StoredIngredients");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                ingredientArrayList.clear();
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
                    Long unitCost = (Long) doc.getData().get("Cost");
                    ingredientArrayList.add(
                            new StoredIngredient(description, count.intValue(),
                                    bestBefore, location, unitCost.intValue()));
                }
                // Update with new cloud data
                ingredientAdapter.notifyDataSetChanged();
            }
        });

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
                StoredIngredient ingredient = ingredientAdapter.getItem(position);
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
    public void ingredientAdded(StoredIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        // Grab data from the ingredient object
        data.put("Description", ingredient.getDescription());
        data.put("Best Before", ingredient.getBestBefore());
        data.put("Location", ingredient.getLocation());
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

    /**
     * Called when the user clicks the "Delete" button in the
     * IngredientFragment. This function deletes the ingredient and updates the
     * total cost of the ingredient list.
     */
    @Override
    public void ingredientDeleted() {
        if (selectedIngredientIndex != -1) {
            StoredIngredient oldIngredient = ingredientArrayList.get(
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
    public void ingredientEdited(StoredIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        StoredIngredient oldIngredient = ingredientArrayList.get(
                selectedIngredientIndex);

        // Grab data from the updated ingredient
        data.put("Description", ingredient.getDescription());
        data.put("Best Before", ingredient.getBestBefore());
        data.put("Location", ingredient.getLocation());
        data.put("Count", ingredient.getCount());
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
}