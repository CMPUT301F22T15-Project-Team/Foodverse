package com.example.foodverse;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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

public class ShoppingListActivity extends AppCompatActivity {
    // Declare the variables so that you will be able to reference it later.
    private ListView shoppingListView;
    private ArrayAdapter<Ingredient> shoppingListAdapter;
    private ArrayList<Ingredient> ingredientArrayList;
    private int selectedIngredientIndex = -1;
    private FirebaseFirestore db;
    private final String TAG = "ShoppingListActivity";
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        shoppingListView = findViewById(R.id.shopping_list);

        ingredientArrayList = new ArrayList<>();
        shoppingListAdapter = new ShoppingList(this, ingredientArrayList);
        shoppingListView.setAdapter(shoppingListAdapter);

        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection("ShoppingList");

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
                    Long count = (Long) doc.getData().get("Count");
                    ingredientArrayList.add(
                            new Ingredient(description, count.intValue()));
                }
                // Update with new cloud data
                shoppingListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Called when the user clicks confirms a new {@link Ingredient}
     * object in the shopping list. Adds the ingredient to Firebase, with a key
     * value using the {@link Ingredient#hashCode()} method.
     *
     * @param ingredient The {@link Ingredient} object that was edited.
     */
    //@Override
    public void ingredientAdded(Ingredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        // Grab data from the ingredient object
        data.put("Description", ingredient.getDescription());
        data.put("Count", ingredient.getCount());
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
     * Called when the user chooses to delete an {@link Ingredient} object from
     * the shopping list. Removes the associated object from Firebase.
     */
    //@Override
    public void ingredientDeleted() {
        if (selectedIngredientIndex != -1) {
            Ingredient oldIngredient = ingredientArrayList.get(
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
     * Called when the user edits an {@link Ingredient} object in the shopping
     * list. Will first delete the old object from Firebase, then add a new
     * object so that the {@link Ingredient#hashCode()} is updated.
     *
     * @param ingredient The {@link Ingredient} object that was edited.
     */
    //@Override
    public void ingredientEdited(Ingredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        Ingredient oldIngredient = ingredientArrayList.get(
                selectedIngredientIndex);

        // Grab data from the updated ingredient
        data.put("Description", ingredient.getDescription());
        data.put("Count", ingredient.getCount());

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

    /**
     * Called when the user requests adding an {@link Ingredient} object to
     * storage. The object will be stored in the collection used to store other
     * {@link StoredIngredient} objects, with a key created using the
     * {@link StoredIngredient#hashCode()} method. Additionally, will remove
     * the {@link Ingredient} object from the shopping list.
     *
     * @param ingredient The {@link StoredIngredient} object that should be
     *                   added to storage. It is expected to have the same
     *                   description as the {@link Ingredient} object it is
     *                   generated from.
     */
    public void addToStorage(StoredIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("Description", ingredient.getDescription());
        data.put("Best Before", ingredient.getBestBefore());
        data.put("Location", ingredient.getLocation());
        data.put("Count", ingredient.getCount());
        data.put("Cost", ingredient.getUnitCost());

        // Need to store ingredient in the stored ingredients collection
        CollectionReference storedReference =
                db.collection("StoredIngredients");

        storedReference
                .document(String.valueOf(ingredient.hashCode()))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // These are a method which gets executed when the task is succeeded
                        Log.d(TAG, "Data added to StoredIngredients successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // These are a method which gets executed if there’s any problem
                        Log.d(TAG, "Data could not be added to StoredIngredients!" + e.toString());
                    }
                });
        Ingredient toRemove =
                new Ingredient(ingredient.getDescription(), ingredient.getCount());
        collectionReference
                .document(String.valueOf(toRemove.hashCode()))
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
    }
}
