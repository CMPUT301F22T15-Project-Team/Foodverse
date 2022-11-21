package com.example.foodverse;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A class to handle the storage and all Firebase operations for locations
 * used by {@link Ingredient} and {@link Recipe} objects. All locations are
 * stored as {@link String} objects in an {@link ArrayList}. Handles separate
 * Firebase collections for both {@link Ingredient} categories and
 * {@link Recipe} categories.
 *
 * @author Tyler
 * @version 1.0
 */
public class CategoryList {
    private ArrayList<String> categories;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "CategoryList";


    /**
     * The constructor for the {@link CategoryList} class. Sets up Firebase
     * and an {@link ArrayList} to store categories. Should be constructed with
     * a {@link String} argument having value either "Ingredient" or "Recipe",
     * which affects what collection is used, so that {@link Ingredient} and
     * {@link Recipe} categories are kept separate.
     *
     * @param caller a {@link String}, expected to be one of "Ingredient" or
     *               "Recipe". Set based on what activity this is used in.
     * @since 1.0
     */
    public CategoryList(String caller) {
        this.categories = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
        // From https://firebase.google.com/docs/firestore/manage-data/enable-offline#java_3
        db.enableNetwork()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Firebase online");
                    }
                });
        String collPath = caller+"Categories";
        collectionReference = db.collection(collPath);
        update();
    }


    /**
     * A method to setup the asynchronous updating of data from Firebase.
     * Called by the {@link CategoryList#CategoryList(String)} constructor.
     *
     * @since 1.0
     */
    private void update() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                // Clear the old list
                categories.clear();
                // Add categories from the cloud
                try {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String category = doc.getId();
                        categories.add(category);
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "NullPointerException");
                }
            }
        });
    }


    /**
     * A method to add a category, represented by a {@link String} to the
     * {@link CategoryList} and Firebase.
     *
     * @param category A {@link String} to represent the category to add.
     * @since 1.0
     */
    public void addCategory(String category) {
        HashMap<String, Object> data = new HashMap<>();
        collectionReference
                .document(category)
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
     * A method to delete a category, represented by a {@link String} from the
     * {@link CategoryList} and Firebase.
     *
     * @param category A {@link String} to represent the location to delete.
     * @since 1.0
     */
    public void deleteCategory(String category) {
        collectionReference
                .document(category)
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


    /**
     * The getter for the categories {@link ArrayList} stored by the
     * {@link CategoryList}.
     *
     * @return An {@link ArrayList<String>} object containing the categories.
     * @since 1.0
     */
    public ArrayList<String> getCategories() {
        return categories;
    }
}
