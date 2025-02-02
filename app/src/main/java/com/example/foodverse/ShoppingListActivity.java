package com.example.foodverse;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ShoppingListActivity
 * This class displays the shopping list which is constructed based on the users
 * meal plan and ingredient storage.
 *
 * @version 1.1
 *
 */
public class ShoppingListActivity extends AppCompatActivity implements
        ShoppingListFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    // Declare the variables so that you will be able to reference it later.
    private ListView shoppingListView;
    private ArrayAdapter<ShoppingListIngredient> shoppingListAdapter;
    private ArrayList<Ingredient> mealPlanArrayList;
    private ArrayList<Recipe> recipeArrayList;
    private ArrayList<Ingredient> recipeIngredientsArrayList;
    private HashMap<Integer, Integer> mealPlanRecipeHash;
    private ArrayList<Ingredient> summedMealPlanArrayList;
    private ArrayList<Ingredient> storedIngredientsArrayList;
    private ArrayList<Ingredient> summedStoredIngredientArrayList;
    private ArrayList<ShoppingListIngredient> shoppingArrayList;
    private int selectedIngredientIndex = -1;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private final String TAG = "ShoppingListActivity";
    private CollectionReference shoppingListCollectionReference;
    private Query shoppingQuery, storedIngQuery, mealQuery, recipeQuery;
    private Button addButton;
    private Spinner sortSpinner;
    private String[] sortingMethods = {"Sort by Purchased", "Sort by Description", "Sort by Category"};
    private String sorting = "Sort by Purchased";
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private CategoryList catListRec = new CategoryList("Recipe");
    private CategoryList catListIng = new CategoryList("Ingredient");
    private LocationList locList = new LocationList();

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
        recipeArrayList = new ArrayList<>();
        recipeIngredientsArrayList = new ArrayList<>();
        mealPlanRecipeHash = new HashMap<>();
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

        shoppingListCollectionReference = db.collection("ShoppingList");
        /*
         * Query made with reference to the following to stop permissions errors
         * https://stackoverflow.com/questions/46590155/firestore-permission-denied-missing-or-insufficient-permissions
         * answer by rwozniak (2019) edited by Elia Weiss (2020).
         * Accessed 2022-11-27
         */
        if (auth.getCurrentUser() != null) {
            shoppingQuery = db.collection("ShoppingList")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
            mealQuery = db.collection("MealPlan")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
            storedIngQuery = db.collection("StoredIngredients")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
            recipeQuery = db.collection("Recipes")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
        } else {
            shoppingQuery = db.collection("ShoppingList")
                    .whereEqualTo("OwnerUID", "");
            mealQuery = db.collection("MealPlan")
                    .whereEqualTo("OwnerUID", "");
            storedIngQuery = db.collection("StoredIngredients")
                    .whereEqualTo("OwnerUID", "");
            recipeQuery = db.collection("Recipes")
                    .whereEqualTo("OwnerUID", "");
        }
        setSnapshotListener("Purchased");

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sorting = (String) sortSpinner.getSelectedItem();
                if (sorting == "Sort by Description") {
                    setSnapshotListener("Description");
                } else if (sorting == "Sort by Category") {
                    setSnapshotListener("Category");
                } else {
                    setSnapshotListener("Purchased");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No sorting change");
            }
        });

        recipeQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                recipeArrayList.clear();
                if(error != null){
                    Log.e(TAG, error.getMessage());
                } else {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getId()));
                        String hashCode = doc.getId();
                        String title = "", category = "", comments = "";
                        Long prep = 0l, servings = 0l;
                        if (doc.getData().get("Title") != null) {
                            title = (String) doc.getData().get("Title");
                        }
                        if (doc.getData().get("Category") != null) {
                            category = (String) doc.getData().get("Category");
                        }
                        if (doc.getData().get("Comments") != null) {
                            comments = (String) doc.getData().get("Comments");
                        }
                        if (doc.getData().get("Prep Time") != null) {
                            prep = (Long) doc.getData().get("Prep Time");
                        }
                        if (doc.getData().get("Servings") != null) {
                            servings = (Long) doc.getData().get("Servings");
                        }
                        ArrayList<String> ingStrings =
                                (ArrayList<String>) doc.getData().get("Ingredients");
                        ArrayList<Ingredient> ingredients = new ArrayList<>();
                        if (ingStrings != null) {
                            for (String ingString : ingStrings) {
                                Ingredient ing =
                                        DatabaseIngredient
                                                .stringToIngredient(ingString);
                                ingredients.add(ing);
                            }
                            recipeArrayList.add(new Recipe(title, prep.intValue(),
                                    servings.intValue(), category, comments,
                                    ingredients, null));
                        }
                    }
                }
            }
        });

        // Auto populate the shopping list by checking the meal plan and ingredient storage
        mealQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * Updates the local meal plan ingredient list everytime firebase is updated
             * @param queryDocumentSnapshots The meal plans stored in firebase
             * @param error The error message from firebase
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                mealPlanArrayList.clear();
                mealPlanRecipeHash.clear();
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                } else {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getId()));
                        String hashCode = doc.getId();
                        int recipeCode = 0;
                        int scale = 1;

                        if (doc.getData().get("Recipe Code") != null) {
                            recipeCode = ((Long) doc.getData().get("Recipe Code")).intValue();
                            if (doc.getData().get("Scaling") != null) {
                                scale = ((Long) doc.getData().get("Scaling")).intValue();
                                mealPlanRecipeHash.put(recipeCode, scale);
                            }
                        }


                        ArrayList<String> ingStrings = (ArrayList<String>) doc.getData().get("Ingredients");
                        if (ingStrings != null) {
                            for (String s : ingStrings) {
                                mealPlanArrayList.add(DatabaseIngredient.stringToIngredient(s));
                            }
                        }
                    }
                    updateShoppingList();
                }
            }
        });

        storedIngQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * Updates the local stored ingredient list everytime firebase is updated
             * @param queryDocumentSnapshots The stored ingredients stored in firebase
             * @param error The error message from firebase
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                } else {
                    storedIngredientsArrayList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getId()));
                        String hashCode = doc.getId();
                        String description = "", unit = "", category = "";
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
                        if(doc.getData().get("Category") != null){
                            category = (String) doc.getData().get("Category");
                        }
                        storedIngredientsArrayList.add(new Ingredient(description,
                                count.intValue(), unit, category));
                    }
                    updateShoppingList();
                }
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
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        }

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
    public void ingredientDeleted(ShoppingListIngredient ingredient) {
        if (ingredient != null) {
            // Remove ingredient from database
            shoppingListCollectionReference
                    .document(String.valueOf(ingredient.hashCode()))
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
        } else if (selectedIngredientIndex != -1) {
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
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        } else {
            data.put("OwnerUID", "");
        }

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
        data.put("Category", ingredient.getCategory());
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        } else {
            data.put("OwnerUID", "");
        }

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
        updateShoppingList();
    }

    /**
     * Updates the shopping list based on the ingredients needed in the meal plan
     * and the ingredients already contained in the storage.
     */
    public void updateShoppingList() {

        // We add the recipe ingredients to the meal ingredient list
        recipeIngredientsArrayList.clear();
        for(Recipe recipe: recipeArrayList){
            if(mealPlanRecipeHash.containsKey(recipe.hashCode())){
                int scale = mealPlanRecipeHash.get(recipe.hashCode());

                for(Ingredient ingredient: recipe.getIngredients()){
                    recipeIngredientsArrayList.add(new Ingredient(ingredient.getDescription(), ingredient.getCount()*scale,
                            ingredient.getUnit(), ingredient.getCategory()));
                }
            }
        }

        // We sum up the ingredient counts from different meals
        summedMealPlanArrayList.clear();
        recipeIngredientsArrayList.addAll(mealPlanArrayList);
        for (Ingredient mealIngredient : recipeIngredientsArrayList) {
            boolean addToList = true;
            for(Ingredient summedMealIngredient : summedMealPlanArrayList){
                if(mealIngredient.getDescription().equals(summedMealIngredient.getDescription()) &&
                        mealIngredient.getUnit().equals(summedMealIngredient.getUnit())){
                    // Since the ingredient has already been added to the list, we update the count
                    summedMealIngredient.setCount(summedMealIngredient.getCount() + mealIngredient.getCount());
                    addToList = false;
                    break;
                }
            }

            // We add the ingredient with the summed up count to the list
            if(addToList){
                summedMealPlanArrayList.add(new Ingredient(mealIngredient.getDescription(), mealIngredient.getCount(),
                        mealIngredient.getUnit(), mealIngredient.getCategory()));
            }
        }

        // We sum up the ingredient counts from different stored ingredients
        summedStoredIngredientArrayList.clear();
        for (Ingredient storedIngredient : storedIngredientsArrayList) {
            boolean addToList = true;
            for(Ingredient summedStoredIngredient : summedStoredIngredientArrayList){
                if(storedIngredient.getDescription().equals(summedStoredIngredient.getDescription()) &&
                        storedIngredient.getUnit().equals(summedStoredIngredient.getUnit())){
                    // Since the ingredient has already been added to the list, we update the count
                    summedStoredIngredient.setCount(summedStoredIngredient.getCount() + storedIngredient.getCount());
                    addToList = false;
                    break;
                }
            }

            // We add the ingredient with the summed up count to the list
            if(addToList){
                summedStoredIngredientArrayList.add(new Ingredient(storedIngredient.getDescription(), storedIngredient.getCount(),
                        storedIngredient.getUnit(), storedIngredient.getCategory()));
            }
        }

        // We populate the shopping list by counting how much of each ingredient is needed.
        for (Ingredient mealIngredient : summedMealPlanArrayList) {
            boolean addToList = true;
            int count = mealIngredient.getCount();

            for (Ingredient storedIngredient : summedStoredIngredientArrayList) {
                // We check if a required ingredient already exists in storage
                if (mealIngredient.getDescription().equals(storedIngredient.getDescription()) &&
                        mealIngredient.getUnit().equals(storedIngredient.getUnit())) {

                    // Since it does exist in storage, we check how many units are actually needed
                    if (mealIngredient.getCount() > storedIngredient.getCount()) {
                        count -= storedIngredient.getCount();
                        mealIngredient.setCategory(storedIngredient.getCategory());
                    } else {
                        // If we already have a sufficient amount of the ingredient, we do not need to add it
                        addToList = false;
                    }
                    break;
                }
            }

            if (addToList){
                boolean purchased = false;

                // Check if ingredient already exists in shopping list
                for(ShoppingListIngredient shoppingIngredient: shoppingArrayList){
                    if(mealIngredient.getDescription().equals(shoppingIngredient.getDescription()) &&
                            mealIngredient.getUnit().equals(shoppingIngredient.getUnit())){
                        // Retrieve the purchased status from the ingredient
                        purchased = shoppingIngredient.isPurchased();
                    }
                }

                // Add the ingredient to the shopping list
                ingredientAdded(new ShoppingListIngredient(mealIngredient.getDescription(),
                        count, mealIngredient.getUnit(), mealIngredient.getCategory(), purchased));
            } else {
                // We remove the ingredient from firebase since it is not needed anymore
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
                intent.putExtra("logout", true);
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
     * A getter method for use in the {@link ShoppingListFragment} to get
     * access to all currently stored categories to create
     * {@link Ingredient} objects.
     *
     * @return An {@link ArrayList<String>} containing all categories
     *         known to the database.
     * @since 1.1
     */
    public ArrayList<String> getCategories() {
        return catListIng.getCategories();
    }


    /**
     * A getter method for use in the {@link ShoppingListFragment} to get
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


    /**
     * A method to setup the snapshot listener for the main query of this
     * activity. Must be given a {@link String} for ordering of results.
     * Here, order must be one of "Description", "Purchased", or "Category"
     * any other value will cause the query to fail.
     *
     * @param order A {@link String} to set as the parameter in the
     *              {@link Query#orderBy(String)} method, to sort results.
     * @since 1.1
     */
    private void setSnapshotListener(String order) {
        shoppingQuery.orderBy(order).addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * Updates the shopping list with all the documents on firebase everytime it is updated.
             * @param queryDocumentSnapshots Firebase documents
             * @param error Error message received when retrieving documents(if applicable)
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                } else {
                    // Clear the old list
                    shoppingArrayList.clear();
                    Log.d(TAG, "Order by: " + order);
                    // Add ingredients from the cloud
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getId()));
                        String hashCode = doc.getId();

                        String description = "";
                        Long count = 0L;
                        String unit = "";
                        String category = "";
                        Boolean purchased = false;
                        if (doc.getData().get("Description") != null) {
                            description = (String) doc.getData().get("Description");
                        }
                        if (doc.getData().get("Count") != null) {
                            count = (Long) doc.getData().get("Count");
                        }
                        if (doc.getData().get("Unit") != null) {
                            unit = (String) doc.getData().get("Unit");
                        }
                        if (doc.getData().get("Category") != null) {
                            category = (String) doc.getData().get("Category");
                        }
                        if (doc.getData().get("Purchased") != null) {
                            purchased = (Boolean) doc.getData().get("Purchased");
                        }
                        shoppingArrayList.add(
                                new ShoppingListIngredient(description, count.intValue(), unit, category, purchased));
                    }
                    // Update with new cloud data
                    shoppingListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}