package com.example.foodverse;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RecipeActivity  extends AppCompatActivity implements RecipeFragment.OnFragmentInteractionListener{
    private ListView RecipeList;
    ArrayAdapter<Recipe> RecAdapter;
    ArrayList<Recipe> RecipeDataList;
    View clickedElement;
    private int selectedRecipeIndex;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "RecipeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        RecipeList = findViewById(R.id.recipes_list);

        RecipeDataList = new ArrayList<>();
        RecAdapter = new RecipeList(this, RecipeDataList); //create the interface for the entries
        RecipeList.setAdapter(RecAdapter); //update the UI

        // Connect to the database, grab the Recipes collection.
        db = FirebaseFirestore.getInstance();

        collectionReference = db.collection("Recipes");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                RecipeDataList.clear();
                // Add ingredients from the cloud
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getId()));
                    String hashCode = doc.getId();
                    String title = (String) doc.getData().get("Title");
                    String category = (String) doc.getData().get("Category");
                    String comments = (String) doc.getData().get("Comments");
                    Long prep = (Long) doc.getData().get("Prep Time");
                    Long servings = (Long) doc.getData().get("Servings");
                    RecipeDataList.add(new Recipe(title, prep.intValue(),
                            servings.intValue(), category, comments));
                }
                // Update with new cloud data
                RecAdapter.notifyDataSetChanged();
            }
        });

        // When the addButton is clicked, open a dialog box to enter the attributes for the entry
        final Button addRecButton = findViewById(R.id.id_add_recipe_button);
        addRecButton.setOnClickListener((v) -> {
            new RecipeFragment().show(getSupportFragmentManager(), "ADD_Recipe");
        });
        Button btn_del = findViewById(R.id.id_del_recipe_button);
        Button edit_btn = findViewById(R.id.id_edit_recipe_button);

        RecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (clickedElement != null) {
                    clickedElement.setBackgroundColor(Color.WHITE);
                }
                clickedElement = view;
                clickedElement.setBackgroundColor(Color.GRAY);
                RecAdapter.notifyDataSetChanged();
                btn_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickedElement != null) {
                            selectedRecipeIndex = i;
                            onDeletePressed();
                            clickedElement.setBackgroundColor(Color.WHITE);
                            clickedElement = null;
                        }
                    }
                });
                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickedElement != null) {
                            selectedRecipeIndex = i;
                            RecipeFragment.newInstance(RecipeDataList.get(i))
                                    .show(getSupportFragmentManager(), "Edit_Recipe");
                            clickedElement.setBackgroundColor(Color.WHITE);
                            clickedElement = null;
                        }
                    }
                });

            }
        });

    }


    /**
     * This method is called when the user confirms adding a new {@link Recipe}.
     * It will add the object to Firebase, using the {@link Recipe#hashCode()}
     * as a reference to the object.
     *
     * @param newRecipe The new {@link Recipe} object to add to Firebase
     */
    @Override
    public void onOkPressed(Recipe newRecipe) {
        HashMap<String, Object> data = new HashMap<>();

        // Put all data from the recipe into data
        data.put("Title", newRecipe.getTitle());
        data.put("Category", newRecipe.getCategory());
        data.put("Comments", newRecipe.getComments());
        data.put("Prep Time", newRecipe.getPrepTime());
        data.put("Servings", newRecipe.getServings());

        /*
         * Store all data under the hash code of the recipe, so we can
         * store multiple similar recipes.
         */
        collectionReference
                .document(String.valueOf(newRecipe.hashCode()))
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
     * This method if called when the user confirms the edit of an existing
     * {@link Recipe} object. It will remove the object in Firebase that is
     * referenced by an equal {@link Recipe#hashCode()} and add a new one for
     * the edited object, so the hash is updated.
     *
     * @param newRecipe The edited {@link Recipe} object to add to Firebase.
     */
    public void onOkEditPressed(Recipe newRecipe) {
        HashMap<String, Object> data = new HashMap<>();
        Recipe oldRecipe = RecipeDataList.get(selectedRecipeIndex);

        // Grab data from the updated recipe
        data.put("Title", newRecipe.getTitle());
        data.put("Category", newRecipe.getCategory());
        data.put("Comments", newRecipe.getComments());
        data.put("Prep Time", newRecipe.getPrepTime());
        data.put("Servings", newRecipe.getServings());

        // Update hash code so we can continue referencing recipe
        collectionReference.document(String.valueOf(oldRecipe.hashCode()))
                .delete();
        collectionReference
                .document(String.valueOf(newRecipe.hashCode()))
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
     * This method if called when the user deletes a {@link Recipe} object. It
     * will remove the object in Firebase that is referenced by an equal
     * {@link Recipe#hashCode()}.
     */
    public void onDeletePressed() {
        if (selectedRecipeIndex != -1) {
            Recipe oldRecipe = RecipeDataList.get(selectedRecipeIndex);
            // Remove ingredient from database
            collectionReference
                    .document(String.valueOf(oldRecipe.hashCode()))
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
            selectedRecipeIndex = -1;
        }
    }
}
