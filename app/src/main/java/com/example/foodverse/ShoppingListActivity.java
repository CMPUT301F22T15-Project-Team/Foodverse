package com.example.foodverse;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * ShoppingListActivity
 * This class displays the shopping list which is constructed based on the users
 * meal plan and ingredient storage.
 *
 * @version 1.0
 *
 */
public class ShoppingListActivity extends AppCompatActivity implements
        ShoppingListFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    // Declare the variables so that you will be able to reference it later.
    private ListView shoppingListView;
    private ArrayAdapter<ShoppingListIngredient> shoppingListAdapter;
    private ArrayList<Ingredient> mealPlanArrayList;
    private ArrayList<Ingredient> summedMealPlanArrayList;
    private ArrayList<Ingredient> storedIngredientsArrayList;
    private ArrayList<Ingredient> summedStoredIngredientArrayList;
    private ArrayList<ShoppingListIngredient> shoppingArrayList;
    private int selectedIngredientIndex = -1;
    private FirebaseFirestore db;
    private final String TAG = "ShoppingListActivity";
    private CollectionReference shoppingListCollectionReference;
    private CollectionReference mealPlanCollectionReference;
    private CollectionReference storedIngredientsCollectionReference;
    private Button addButton;
    private Spinner sortSpinner;
    private String[] sortingMethods = {"Sort by Purchased", "Short by Description", "Sort by Category"};
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    /**
     * The startup function that is called when the activity is launched.
     *
     * @param savedInstanceState Any data that needs to be passed into the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieving the respective layouts.
        setContentView(R.layout.activity_shopping_list);
        shoppingListView = findViewById(R.id.shopping_list_view);
        addButton = findViewById(R.id.add_ingredient_to_storage_button);
        sortSpinner = findViewById(R.id.sort_Spinner);

        // Creating the array list and attaching it to the adapter.
        shoppingArrayList = new ArrayList<>();
        mealPlanArrayList = new ArrayList<>();
        summedMealPlanArrayList = new ArrayList<>();
        storedIngredientsArrayList = new ArrayList<>();
        summedStoredIngredientArrayList = new ArrayList<>();
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
        FirebaseFirestore.setLoggingEnabled(true);
        // From https://firebase.google.com/docs/firestore/manage-data/enable-offline#java_3
        db.enableNetwork()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Firebase online");
                    }
                });

        shoppingListCollectionReference = db.collection("ShoppingList");
        mealPlanCollectionReference = db.collection("MealPlan");
        storedIngredientsCollectionReference = db.collection("StoredIngredients");

        // Auto populate the shopping list by checking the meal plan and ingredient storage
        mealPlanCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            /**
             * Updates the local meal plan ingredient list everytime firebase is updated
             * @param queryDocumentSnapshots The meal plans stored in firebase
             * @param error The error message from firebase
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                mealPlanArrayList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getId()));
                    String hashCode = doc.getId();
                    ArrayList<String> ingStrings = (ArrayList<String>) doc.getData().get("Ingredients");
                    if (ingStrings != null) {
                        for (String s : ingStrings) {
                            mealPlanArrayList.add(DatabaseIngredient.stringToIngredient(s));
                        }
                    }
                }
                updateShoppingList();
            }
        });

        storedIngredientsCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {

            /**
             * Updates the local stored ingredient list everytime firebase is updated
             * @param queryDocumentSnapshots The stored ingredients stored in firebase
             * @param error The error message from firebase
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                storedIngredientsArrayList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Log.d(TAG, String.valueOf(doc.getId()));
                    String hashCode = doc.getId();
                    String description = "", unit = "";
                    Long count = 0l;
                    if (doc.getData().get("Description") != null) {
                        description = (String) doc.getData().get("Description");
                    }
                    if (doc.getData().get("Count") != null) {
                        count = (Long) doc.getData().get("Count");
                    }
                    if (doc.getData().get("Unit") != null) {
                        unit = (String) doc.getData().get("Unit");
                    }
                    storedIngredientsArrayList.add(new Ingredient(description,
                            count.intValue(), unit));
                }
                updateShoppingList();
            }
        });

        shoppingListCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * Updates the shopping list with all the documents on firebase everytime it is updated.
             * @param queryDocumentSnapshots Firebase documents
             * @param error Error message received when retrieving documents(if applicable)
             */
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
                    String unit = (String) doc.getData().get("Unit");
                    String category = (String) doc.getData().get("Category");
                    Boolean purchased = (Boolean) doc.getData().get("Purchased");
                    shoppingArrayList.add(
                            new ShoppingListIngredient(description, count.intValue(), unit, category, purchased));
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
            /**
             * Launches an edit fragment when an item on the shopping list is clicked.
             * @param parent The parent of the view.
             * @param view The view that was clicked.
             * @param position The position of the view that was clicked.
             * @param id The id of the view that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ShoppingListIngredient ingredient = shoppingListAdapter.getItem(position);
//                selectedIngredientIndex = position;
//                new ShoppingListFragment(ingredient).show(
//                        getFragmentManager(), "EDIT_INGREDIENT");
            }

        });

        addButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Launches the fragment when the add ingredient to storage is clicked.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                new ShoppingListFragment().show(
                        getFragmentManager(), "ADD_INGREDIENT");
            }
        });

    }

    /**
     * Called when the user clicks confirms a new {@link ShoppingListIngredient}
     * object in the shopping list. Adds the ingredient to Firebase, with a key
     * value using the {@link ShoppingListIngredient#hashCode()} method.
     *
     * @param ingredient The {@link ShoppingListIngredient} object that was edited.
     */
    @Override
    public void ingredientAdded(ShoppingListIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        // Grab data from the ingredient object
        data.put("Description", ingredient.getDescription());
        data.put("Count", ingredient.getCount());
        data.put("Unit", ingredient.getUnit());
        data.put("Category", ingredient.getCategory());
        data.put("Purchased", ingredient.isPurchased());

        /*
         * Store all data under the hash code of the ingredient, so we can
         * store multiple similar ingredients.
         */
        shoppingListCollectionReference
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
     * Called when the user chooses to delete an {@link ShoppingListIngredient} object from
     * the shopping list. Removes the associated object from Firebase.
     */
    @Override
    public void ingredientDeleted() {
        if (selectedIngredientIndex != -1) {
            ShoppingListIngredient oldIngredient = shoppingArrayList.get(selectedIngredientIndex);
            // Remove ingredient from database
            shoppingListCollectionReference
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
     * Called when the user edits an {@link ShoppingListIngredient} object in the shopping
     * list. Will first delete the old object from Firebase, then add a new
     * object so that the {@link ShoppingListIngredient#hashCode()} is updated.
     *
     * @param ingredient The {@link ShoppingListIngredient} object that was edited.
     */
    @Override
    public void ingredientEdited(ShoppingListIngredient ingredient) {
        HashMap<String, Object> data = new HashMap<>();
        ShoppingListIngredient oldIngredient = shoppingArrayList.get(
                selectedIngredientIndex);

        // Grab data from the updated ingredient
        data.put("Description", ingredient.getDescription());
        data.put("Count", ingredient.getCount());
        data.put("Unit", ingredient.getUnit());
        data.put("Category", ingredient.getCategory());
        data.put("Purchased", ingredient.isPurchased());

        // Delete old ingredient and set new since hashCode() will return different result
        shoppingListCollectionReference.document(String.valueOf(oldIngredient.hashCode()))
                .delete();
        shoppingListCollectionReference
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
        data.put("Unit", ingredient.getUnit());

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
        shoppingListCollectionReference
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
     * Updates the shopping list based on the ingredients needed in the meal plan
     * and the ingredients already contained in the storage.
     */
    public void updateShoppingList() {

        summedMealPlanArrayList.clear();
        // We sum up the ingredient counts from different meals
        for (Ingredient mealIngredient : mealPlanArrayList) {
            boolean addToList = true;
            for(Ingredient summedMealIngredient : summedMealPlanArrayList){
                if(summedMealIngredient.hashCode() == mealIngredient.hashCode()){
                    summedMealIngredient.setCount(summedMealIngredient.getCount() + mealIngredient.getCount());
                    addToList = false;
                    break;
                }
            }
            if(addToList){
                summedMealPlanArrayList.add(new Ingredient(mealIngredient.getDescription(), mealIngredient.getCount(),
                        mealIngredient.getUnit(), mealIngredient.getCategory()));
            }
        }

        summedStoredIngredientArrayList.clear();
        // We sum up the ingredient counts from different stored ingredients
        for (Ingredient storedIngredient : storedIngredientsArrayList) {
            boolean addToList = true;
            for(Ingredient summedStoredIngredient : summedStoredIngredientArrayList){
                if(summedStoredIngredient.hashCode() == storedIngredient.hashCode()){
                    summedStoredIngredient.setCount(summedStoredIngredient.getCount() + storedIngredient.getCount());
                    addToList = false;
                    break;
                }
            }
            if(addToList){
                summedStoredIngredientArrayList.add(new Ingredient(storedIngredient.getDescription(), storedIngredient.getCount(),
                        storedIngredient.getUnit(), storedIngredient.getCategory()));
            }
        }

        shoppingArrayList.clear();
        for (Ingredient mealIngredient : summedMealPlanArrayList) {
            boolean addToList = true;
            int count = mealIngredient.getCount();

            for (Ingredient storedIngredient : summedStoredIngredientArrayList) {
                // We check if a required ingredient already exists in storage
                if (mealIngredient.getDescription().equals(storedIngredient.getDescription()) &&
                        mealIngredient.getUnit().equals(storedIngredient.getUnit()) &&
                        mealIngredient.getCategory().equals(storedIngredient.getCategory())) {

                    // We check how many units are actually needed
                    if (mealIngredient.getCount() > storedIngredient.getCount()) {
                        count -= storedIngredient.getCount();
                        break;
                    }
                    addToList = false;
                    break;
                }
            }

            if(addToList){
                mealIngredient.setCount(count);

                ingredientAdded(new ShoppingListIngredient(mealIngredient.getDescription(),
                        mealIngredient.getCount(), mealIngredient.getUnit(), mealIngredient.getCategory(), false));
            } else {

                shoppingListCollectionReference
                        .document(String.valueOf(mealIngredient.hashCode()))
                        .delete();
            }
        }
        shoppingListAdapter.notifyDataSetChanged();
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