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
 * used by {@link StoredIngredient} objects. All locations are stored as
 * {@link String} objects in an {@link ArrayList}.
 *
 * @author Tyler
 * @version 1.0
 */
public class LocationList {
    private ArrayList<String> locations;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "LocationList";


    /**
     * The constructor for the {@link LocationList}. Sets up an
     * {@link ArrayList} to store the locations and connects to Firebase.
     *
     * @since 1.0
     */
    public LocationList() {
        this.locations = new ArrayList<>();
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
        collectionReference = db.collection("Locations");
        update();
    }


    /**
     * A method to setup the asynchronous updating of data from Firebase.
     * Called by the {@link LocationList#LocationList()} constructor.
     *
     * @since 1.0
     */
    private void update() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                // Clear the old list
                locations.clear();
                // Add locations from the cloud
                try {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String category = doc.getId();
                        locations.add(category);
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "NullPointerException");
                }
            }
        });
    }


    /**
     * A method to add a location, represented by a {@link String} to the
     * {@link LocationList} and Firebase.
     *
     * @param location A {@link String} to represent the location to add.
     * @since 1.0
     */
    public void addCategory(String location) {
        HashMap<String, Object> data = new HashMap<>();
        collectionReference
                .document(location)
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
     * A method to delete a location, represented by a {@link String} from the
     * {@link LocationList} and Firebase.
     *
     * @param location The {@link String} representing the location to delete.
     * @since 1.0
     */
    public void deleteCategory(String location) {
        collectionReference
                .document(location)
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
     * The getter for the locations {@link ArrayList} stored by the
     * {@link LocationList}.
     *
     * @return An {@link ArrayList<String>} object containing the locations.
     * @since 1.0
     */
    public ArrayList<String> getLocations() {
        return locations;
    }
}
