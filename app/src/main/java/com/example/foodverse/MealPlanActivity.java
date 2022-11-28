package com.example.foodverse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * MealPlanActivity
 * This activity allows the user to build a meal plan by
 * adding, editing, or deleting meals.
 */
public class MealPlanActivity extends AppCompatActivity implements
        MealViewFragment.OnFragmentInteractionListener,
        MealPlanFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private ListView mealListView; // The list that displays the meals
    private ArrayAdapter<Meal> mealAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private final String TAG = "MealPlanActivity";
    private CollectionReference collectionReference;
    private Query mealQuery, recQuery, ingQuery;
    private ArrayList<Meal> mealArrayList; // The array list that stores the meals
    private ArrayList<Ingredient> databaseIngredients = new ArrayList<>();
    private HashSet<Ingredient> set = new HashSet<>();
    private int selectedMealIndex;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ArrayList<Integer> recipeHashCodes = new ArrayList<Integer>();
    private ArrayList<String> recipeTitleList = new ArrayList<String>();
    private ArrayList<Integer> recipeServingSizes = new ArrayList<Integer>();
    private CategoryList catListRec = new CategoryList("Recipe");
    private CategoryList catListIng = new CategoryList("Ingredient");
    private LocationList locList = new LocationList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        selectedMealIndex = -1;
        mealListView = findViewById(R.id.meal_list);

        // Initialize attributes
        mealArrayList = new ArrayList<>();
        mealAdapter = new MealList(this, mealArrayList);
        mealListView.setAdapter(mealAdapter);

        /*
         * https://www.geeksforgeeks.org/navigation-drawer-in-android/
         * by adityamshidlyali, 2020
         */
        drawerLayout = findViewById(R.id.meal_plan_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // Allow menu to be toggleable, always display.
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup listeners for the navigation view
        navView = findViewById(R.id.nav_menu_meals);
        navView.setNavigationItemSelectedListener(this);

        // Get db, the MealPlan collection
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

        collectionReference = db.collection("MealPlan");
        /*
         * Query made with reference to the following to stop permissions errors
         * https://stackoverflow.com/questions/46590155/firestore-permission-denied-missing-or-insufficient-permissions
         * answer by rwozniak (2019) edited by Elia Weiss (2020).
         * Accessed 2022-11-27
         */
        try {
            mealQuery = db.collection("MealPlan")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
            Log.e(TAG, auth.getCurrentUser().getUid());
        } catch (NullPointerException e) {
            mealQuery = db.collection("MealPlan")
                    .whereEqualTo("OwnerUID", "");
        }
        mealQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                } else {
                    // Clear the old list
                    mealArrayList.clear();
                    // Add ingredients from the cloud
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d(TAG, String.valueOf(doc.getId()));
                        String hashCode = doc.getId();
                        Date date = new Date();
                        String recipeName = "No Recipe";
                        int recipeCode = 0;
                        int numServings = 0;
                        int scale = 1;
                        String name = "";
                        ArrayList<String> ingStrings =
                                (ArrayList<String>) doc.getData().get("Ingredients");
                        if (doc.getData().get("Date") != null) {
                            date = ((Timestamp) doc.getData().get("Date")).toDate();
                        }
                        if (doc.getData().get("Recipe") != null) {
                            recipeName = (String) doc.getData().get("Recipe");
                        }
                        if (doc.getData().get("Recipe Code") != null) {
                            recipeCode = ((Long) doc.getData().get("Recipe Code")).intValue();
                        }
                        if (doc.getData().get("Servings") != null) {
                            numServings = ((Long) doc.getData().get("Servings")).intValue();
                        }
                        if (doc.getData().get("Scaling") != null) {
                            scale = ((Long) doc.getData().get("Scaling")).intValue();
                        }
                        if (doc.getData().get("Name") != null) {
                            name = (String) doc.getData().get("Name");
                        }
                        // Reconstruct ArrayList
                        ArrayList<Ingredient> ingredients = new ArrayList<>();
                        if (ingStrings != null) {
                            for (String ingString : ingStrings) {
                                Ingredient ing =
                                        DatabaseIngredient.stringToIngredient(ingString);
                                ingredients.add(ing);
                            }
                        }
                        Meal newMeal = new Meal(ingredients, date);
                        newMeal.addRecipe(recipeCode, recipeName);
                        newMeal.setServings(numServings);
                        newMeal.setServingScaling(scale);
                        newMeal.setName(name);
                        mealArrayList.add(newMeal);
                        //mealArrayList.add(new Meal(ingredients, date));
                    }

                    // Update with new cloud data
                    Collections.sort(mealArrayList);
                    mealAdapter.notifyDataSetChanged();
                }
            }
        });
        try {
            recQuery = db.collection("Recipes")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
        } catch (NullPointerException e) {
            recQuery = db.collection("Recipes")
                    .whereEqualTo("OwnerUID", "");
        }
        recQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                } else {
                    recipeHashCodes.clear();
                    recipeTitleList.clear();
                    recipeServingSizes.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        Log.d(TAG, String.valueOf(doc.getId()));
                        String hashCode = doc.getId();
                        String name = "";
                        Integer code = 0;
                        int recipeServings = 0;
                        ArrayList<String> recStrings =
                                (ArrayList<String>) doc.getData().get("Recipes");
                        if (doc.getData().get("Title") != null) {
                            name = (String) doc.getData().get("Title");
                            code = Integer.parseInt(hashCode);
                        }
                        if (hashCode != null) {
                            code = Integer.valueOf(hashCode);
                        }
                        if (doc.getData().get("Servings") != null) {
                            recipeServings = ((Long) doc.getData().get("Servings")).intValue();
                        }
                        recipeHashCodes.add(code);
                        recipeTitleList.add(name);
                        recipeServingSizes.add(recipeServings);
                    }
                }
            }
        });

        try {
            ingQuery = db.collection("StoredIngredients")
                    .whereEqualTo("OwnerUID", auth.getCurrentUser().getUid());
        } catch (NullPointerException e) {
            ingQuery = db.collection("StoredIngredients")
                    .whereEqualTo("OwnerUID", "");
        }
        ingQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                } else {
                    databaseIngredients.clear();
                    // Add ingredients from the cloud
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String hashCode = doc.getId();
                        String description = "", unit = "", category = "";
                        Long count = 0l;
                        if (doc.getData().get("Description") != null) {
                            description =
                                    (String) doc.getData().get("Description");
                        }
                        Log.d("MEALFRAG", description);
                        if (doc.getData().get("Count") != null) {
                            count = (Long) doc.getData().get("Count");
                        }
                        if (doc.getData().get("Unit") != null) {
                            unit = (String) doc.getData().get("Unit");
                        }
                        if (doc.getData().get("Category") != null) {
                            category = (String) doc.getData().get("Category");
                        }
                        Ingredient ing = new Ingredient(description,
                                count.intValue(), unit, category);
                        databaseIngredients.add(ing);
                        set.add(ing);
                        Log.d("MEALFRAG", "Added ing");
                    }
                }
            }
        });

        mealListView.setOnItemClickListener((adapterView, view, i, l) -> selectedMealIndex = i);

        final Button addMealButton = findViewById(R.id.add_meal_button);

        addMealButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MealPlanFragment().show(getSupportFragmentManager(), "TEST");
            }
        });

        mealListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view,
                                            int position, long id) {
                        // When a meal is selected from the list,
                        // its index is taken and the meal is passed
                        // to the fragment.
                        Meal meal = mealAdapter.getItem(position);
                        selectedMealIndex = position;
                        new MealViewFragment(meal).show(
                                getSupportFragmentManager(), "EDIT_MEAL");
                    }
                });


    }

    /**
     * Called when the user chooses to add a meal to their list.
     * The function adds the meal using the {@link Meal#hashCode()} as the key
     * for the {@link Meal} object for the database.
     *
     * @param meal The {@link Meal} object to add to the database.
     */
    public void mealAdded(Meal meal) {
        HashMap<String, Object> data = new HashMap<>();
        // Grab data from the ingredient object
        // Can't store ingredient directly so use DatabaseIngredient methods
        ArrayList<String> ingStrings = new ArrayList<>();
        String ingString;
        for (int i = 0; i < meal.getIngredients().size(); i++) {
            ingString = DatabaseIngredient.ingredientToString(
                    meal.getIngredients().get(i));
            ingStrings.add(ingString);
        }

        data.put("Ingredients", ingStrings);
        data.put("Date", meal.getDate());
        data.put("Recipe", meal.getRecipeTitle());
        data.put("Recipe Code", meal.getRecipeHashCode());
        data.put("Servings", meal.getServings());
        data.put("Scaling", meal.getServingScaling());
        data.put("Name", meal.getName());
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        } else {
            data.put("OwnerUID", "");
        }
        
        /*
         * Store all data under the hash code of the meal, so we can
         * store multiple similar meals.
         */
        collectionReference
                .document(String.valueOf(meal.hashCode()))
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
     * Called when the user chooses to delete a meal from their list.
     * The function removes the meal with equal {@link Meal#hashCode()} from
     * the Firestore database.
     */
    public void mealDeleted() {
        if (selectedMealIndex != -1) {
            Meal oldMeal = mealArrayList.get(selectedMealIndex);
            // Remove ingredient from database
            collectionReference
                    .document(String.valueOf(oldMeal.hashCode()))
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
            selectedMealIndex = -1;
        }
    }

    /**
     * Called when the user confirms new edits in a meal. This function removes
     * the old {@link Meal} from Firestore and adds a new one to the database
     * so that {@link Meal#hashCode()} references remain up to date.
     *
     * @param meal The {@link Meal} object that was edited.
     */
    public void mealEdited(Meal meal) {
        HashMap<String, Object> data = new HashMap<>();
        Meal oldMeal = mealArrayList.get(selectedMealIndex);

        // Grab data from the updated ingredient
        // Can't store ingredient directly so use DatabaseIngredient methods
        ArrayList<String> ingStrings = new ArrayList<>();
        for (Ingredient ingredient : meal.getIngredients()) {
            String ingString = DatabaseIngredient.ingredientToString(ingredient);
            ingStrings.add(ingString);
        }
        data.put("Ingredients", ingStrings);
        data.put("Date", meal.getDate());
        data.put("Recipe", meal.getRecipeTitle());
        data.put("Recipe Code", meal.getRecipeHashCode());
        data.put("Servings", meal.getServings());
        data.put("Scaling", meal.getServingScaling());
        data.put("Name", meal.getName());
        if (auth.getCurrentUser() != null) {
            data.put("OwnerUID", auth.getCurrentUser().getUid());
        } else {
            data.put("OwnerUID", "");
        }

        // Delete old ingredient and set new since hashCode() will return different result
        collectionReference.document(String.valueOf(oldMeal.hashCode()))
                .delete();
        collectionReference
                .document(String.valueOf(meal.hashCode()))
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
     * Implemented to allow the list of database ingredients to be passed directly
     * to MealPlanFragment.
     * @return An {@link ArrayList<Ingredient>} that contains all the ingredients stored in the database
     */
    public ArrayList<Ingredient> getDatabaseIngredients() {
        return databaseIngredients;
    }


    // Add it here
    public ArrayList<String> getRecipeTitleList() {
        return recipeTitleList;
    }

    public ArrayList<Integer> getRecipeHashCodes() {
        return recipeHashCodes;
    }


    public ArrayList<Integer> getRecipeServingSizes() {
        return recipeServingSizes;
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
}