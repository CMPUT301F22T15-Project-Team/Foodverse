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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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

public class ShoppingListActivity extends AppCompatActivity implements
        ShoppingListFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    // Declare the variables so that you will be able to reference it later.
    ListView shoppingListView;
    ArrayAdapter<Ingredient> shoppingListAdapter;
    ArrayList<Ingredient> shoppingArrayList;
    int selectedIngredientIndex = -1;
    FirebaseFirestore db;
    final String TAG = "ShoppingListActivity";
    CollectionReference collectionReference;
    Button addButton;
    Spinner sortSpinner;
    String[] sortingMethods = {"Sort by Purchased", "Short by Description", "Sort by Category"};
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

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

        //shoppingArrayList.add(new Ingredient("Ingredient 1", 2));
        //shoppingArrayList.add(new Ingredient("Ingredient 2", 4));

        shoppingListAdapter = new ShoppingList(this, shoppingArrayList);
        shoppingListView.setAdapter(shoppingListAdapter);
        shoppingListAdapter.notifyDataSetChanged();

        /*
         * https://www.geeksforgeeks.org/navigation-drawer-in-android/
         * by adityamshidlyali, 2020
         */
        drawerLayout = findViewById(R.id.shopping_list_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // Allow menu to be toggleable, always display.
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup listeners for the navigation view
        navView = findViewById(R.id.nav_menu_shopping);
        navView.setNavigationItemSelectedListener(this);

        // Get our database
        db = FirebaseFirestore.getInstance();

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
                    Long count = (Long) doc.getData().get("Count");
                    shoppingArrayList.add(
                            new Ingredient(description, count.intValue()));
                }
                // Update with new cloud data
                shoppingListAdapter.notifyDataSetChanged();
            }
        });


        //ingredientAdded(new Ingredient("Ingredient 1", 2));
        //ingredientAdded(new Ingredient("Ingredient 2", 4));

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
                Ingredient ingredient = shoppingListAdapter.getItem(position);
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


    /**
     * Called when the user clicks confirms a new {@link Ingredient}
     * object in the shopping list. Adds the ingredient to Firebase, with a key
     * value using the {@link Ingredient#hashCode()} method.
     *
     * @param ingredient The {@link Ingredient} object that was edited.
     */
    @Override
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
    @Override
    public void ingredientDeleted() {
        if (selectedIngredientIndex != -1) {
            Ingredient oldIngredient = shoppingArrayList.get(
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
    @Override
    public void ingredientEdited(Ingredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        Ingredient oldIngredient = shoppingArrayList.get(
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
    @Override
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
     * {@link MealPlanActivity}, and {@link ShoppingListActivity}.
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
            /*case "Recipes": {

            }*/
            case "Ingredients": {
                Intent intent = new Intent(this, StoredIngredientActivity.class);
                startActivity(intent);
                break;
            }
            case "Meal Planner": {
                Intent intent = new Intent(this, MealPlanActivity.class);
                startActivity(intent);
                break;
            }
            default: break;
        }

        // Close navigation drawer if we selected the current activity.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}