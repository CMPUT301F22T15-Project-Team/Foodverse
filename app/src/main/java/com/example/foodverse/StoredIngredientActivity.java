package com.example.foodverse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
 * StoredIngredientActivity
 * This class is used to run the app and is responsible for managing all of the
 * other components within it.
 *
 * @version 1.1
 *
 * 2022-09-24
 *
 */

public class StoredIngredientActivity extends AppCompatActivity
        implements StoredIngredientFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    // Declare the variables so that you will be able to reference it later.
    private ListView ingredientListView;
    private ArrayAdapter<StoredIngredient> ingredientAdapter;
    private ArrayList<StoredIngredient> ingredientArrayList;
    private int selectedIngredientIndex = -1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private final String TAG = "IngredientActivity";
    private CollectionReference collectionReference;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private CategoryList catListRec = new CategoryList("Recipe");
    private CategoryList catListIng = new CategoryList("Ingredient");
    private LocationList locList = new LocationList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_ingredient);

        ingredientListView = findViewById(R.id.ingredient_list);

        ingredientArrayList = new ArrayList<>();
        ingredientAdapter = new StoredIngredientList(this, ingredientArrayList);
        ingredientListView.setAdapter(ingredientAdapter);

        /*
         * https://www.geeksforgeeks.org/navigation-drawer-in-android/
         * by adityamshidlyali, 2020
         */
        drawerLayout = findViewById(R.id.stored_ingredient_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // Allow menu to be toggleable, always display.
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup listeners for the navigation view
        navView = findViewById(R.id.nav_menu_ingredients);
        navView.setNavigationItemSelectedListener(this);

        // Get our database
        auth = FirebaseAuth.getInstance();
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
                    String description = "", location = "", unit = "";
                    Long count = 0l, unitCost = 0l;
                    Date bestBefore = new Date();

                    if (doc.getData().get("Description") != null) {
                        description =
                                (String) doc.getData().get("Description");
                    }
                    /*
                     * https://stackoverflow.com/questions/54838634/timestamp-firebase-casting-error-to-date-util
                     * Answer by Niyas, February 23, 2019. Reference on casting
                     * from firebase.timestamp to java.date.
                     */
                    if (doc.getData().get("Best Before") != null) {
                        bestBefore = ((Timestamp) doc.getData().get("Best Before"))
                                .toDate();
                    }
                    if (doc.getData().get("Location") != null) {
                        location = (String) doc.getData().get("Location");
                    }
                    if (doc.getData().get("Unit") != null) {
                        unit = (String) doc.getData().get("Unit");
                    }
                    if (doc.getData().get("Count") != null) {
                        count = (Long) doc.getData().get("Count");
                    }
                    if (doc.getData().get("Cost") != null) {
                        unitCost = (Long) doc.getData().get("Cost");
                    }
                    ingredientArrayList.add(
                            new StoredIngredient(description, count.intValue(),
                                    bestBefore, location, unit, unitCost.intValue()));
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
                new StoredIngredientFragment().show(getSupportFragmentManager(),
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
                new StoredIngredientFragment(ingredient).show(
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
        data.put("Unit", ingredient.getUnit());
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        }

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
        data.put("Unit", ingredient.getUnit());
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        }

        // Delete old ingredient and set new since hashCode() will return different result
        ingredientDeleted();
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
     * Implemented to allow for the opening and closing of the navigation menu.
     *
     * Code from: https://www.geeksforgeeks.org/navigation-drawer-in-android/
     * By adityamshidlyali, posted 2020, accessed October 28, 2022.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Overridden from NavigationView.OnNavigationItemSelectedListener.
     * Navigate to the selected activity, if we are not already on it, otherwise
     * close the menu. Possible destinations are {@link StoredIngredientActivity},
     * {@link MealPlanActivity}, {@link RecipeActivity}, and
     * {@link ShoppingListActivity}.
     *
     * Code inspired by: https://stackoverflow.com/questions/42297381/onclick-event-in-navigation-drawer
     * Post by Grzegorz (2017) edited by ElOjcar (2019). Accessed Oct 28, 2022.
     *
     * @returns Always true, iff the selected item is the calling activity.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menu) {
        // Go to activity selected, based on title.
        String destination = (String) menu.getTitle();
        switch(destination) {
            case "Recipes": {
                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                break;
            }
            case "Meal Planner": {
                Intent intent = new Intent(this, MealPlanActivity.class);
                startActivity(intent);
                break;
            }
            case "Shopping List": {
                Intent intent = new Intent(this, ShoppingListActivity.class);
                startActivity(intent);
                break;
            }
            case "Manage Storage Locations": {
                new LocationCategoryManager("Location",
                        locList.getLocations())
                        .show(getSupportFragmentManager(), "LocMgr");
                break;
            }
            case "Manage Ingredient Categories": {
                new LocationCategoryManager("Ingredient Category",
                        catListIng.getCategories())
                        .show(getSupportFragmentManager(), "IngCatMgr");
                break;
            }
            case "Manage Recipe Categories": {
                new LocationCategoryManager("Recipe Category",
                        catListRec.getCategories())
                        .show(getSupportFragmentManager(), "RecCatMgr");
                break;
            }
            case "Logout": {
                Intent intent = new Intent(this, LoginActivity.class);
                auth.signOut();
                startActivity(intent);
                break;
            }
            default: break;
        }

        // Close navigation drawer if we selected the current activity.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * A getter method for use in the {@link StoredIngredientFragment} to get
     * access to all currently stored categories to create
     * {@link StoredIngredient} objects.
     *
     * @return An {@link ArrayList<String>} containing all categories
     *         known to the database.
     * @since 1.1
     */
    public ArrayList<String> getCategories() {
        return catListIng.getCategories();
    }


    /**
     * A getter method for use in the {@link StoredIngredientFragment} to get
     * access to all currently stored locations to create
     * {@link StoredIngredient} objects.
     *
     * @return An {@link ArrayList<String>} containing all locations
     *         known to the database.
     * @since 1.1
     */
    public ArrayList<String> getLocations() {
        return locList.getLocations();
    }
}