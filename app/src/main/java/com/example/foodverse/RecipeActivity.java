package com.example.foodverse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * RecipeActivity
 * This class displays a list of recipes which is constructed based on the users
 * input of recipe title, comments, serving size, preparation time, and a list of ingredients.
 * This class also allows the user to push buttons to edit, add and delete recipe items.
 *
 * @version 1.1
 *
 */

public class RecipeActivity  extends AppCompatActivity implements
        RecipeFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {
    private ListView RecipeList;
    private ArrayAdapter<Recipe> RecAdapter;
    private ArrayList<Recipe> RecipeDataList;
    private View clickedElement;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();
    private int selectedRecipeIndex;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private final String TAG = "RecipeActivity";
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private CollectionReference storedRef;
    private HashSet<Ingredient> set = new HashSet<>();
    private ArrayList<Ingredient> databaseIngredients = new ArrayList<>();
    private CategoryList catListRec = new CategoryList("Recipe");
    private CategoryList catListIng = new CategoryList("Ingredient");
    private LocationList locList = new LocationList();


    /**
     * The startup function that is called when the activity is launched.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        RecipeList = findViewById(R.id.recipes_list);

        RecipeDataList = new ArrayList<>();
        RecAdapter = new RecipeList(this, RecipeDataList); //create the interface for the entries
        RecipeList.setAdapter(RecAdapter); //update the UI


        /*
         * https://www.geeksforgeeks.org/navigation-drawer-in-android/
         * by adityamshidlyali, 2020
         */

        drawerLayout = findViewById(R.id.recipe_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // Allow menu to be toggleable, always display.
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup listeners for the navigation view
        navView = findViewById(R.id.nav_menu_recipes);
        navView.setNavigationItemSelectedListener(this);

        // Connect to the database, grab the Recipes collection.
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
                                    DatabaseIngredient.stringToIngredient(ingString);
                            ingredients.add(ing);
                        }
                    }
                    /*
                     * Decoding and encoding of bitmap with reference to:
                     * https://www.learnhowtoprogram.com/android/gestures-animations-flexible-uis/using-the-camera-and-saving-images-to-firebase
                     * Accessed 2022-11-24
                     */
                    Bitmap bm = null;
                    if (doc.getData().get("Bitmap") != null) {
                        String bmEncoded = (String) doc.getData().get("Bitmap");
                        byte[] decodedByteArray = android.util.Base64.decode(
                                bmEncoded, Base64.DEFAULT);
                        bm = BitmapFactory.decodeByteArray(
                                decodedByteArray, 0,
                                decodedByteArray.length);
                    }
                    RecipeDataList.add(new Recipe(title, prep.intValue(),
                            servings.intValue(), category, comments,
                            ingredients, bm));
                }
                // Update with new cloud data
                RecAdapter.notifyDataSetChanged();
            }
        });

        storedRef = db.collection("StoredIngredients");

        storedRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * Updates the recipe list with all the documents on firebase everytime it is updated.
             * @param queryDocumentSnapshots Firebase documents
             * @param error Error message received when retrieving documents(if applicable)
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Add ingredients from the cloud
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String hashCode = doc.getId();
                    String description = "", unit = "";
                    Long count = 0l;
                    if (doc.getData().get("Description") != null) {
                        description = (String) doc.getData().get("Description");
                        Log.d("RECFRAG", description);
                    }
                    if (doc.getData().get("Count") != null) {
                        count = (Long) doc.getData().get("Count");
                    }
                    if (doc.getData().get("Unit") != null) {
                        unit = (String) doc.getData().get("Unit");
                    }
                    Ingredient ing = new Ingredient(description, count.intValue(), unit);
                    if (!set.contains(ing)) {
                        databaseIngredients.add(ing);
                        set.add(ing);
                        Log.d("RECFRAG", "Added ing");
                    }
                }
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
            /**
             * Launches a fragment when an item on the recipe list is clicked.
             * @param adapterView The parent of the view.
             * @param view The view that was clicked.
             * @param i The position of the view that was clicked.
             * @param l The id of the view that was clicked
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //changes the color of the item based on if it was clicked or not
                if (clickedElement != null) {
                    clickedElement.setBackgroundColor(Color.WHITE);
                }
                clickedElement = view;
                clickedElement.setBackgroundColor(Color.GRAY);
                selectedRecipeIndex = i;
            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            /**
             * Launches the fragment when the add recipe to storage is clicked.
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                if (clickedElement != null) {
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
                    new RecipeFragment(RecipeDataList.get(selectedRecipeIndex))
                            .show(getSupportFragmentManager(), "Edit_Recipe");
                    clickedElement.setBackgroundColor(Color.WHITE);
                    clickedElement = null;
                }
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

        // Can't store ingredient directly so use DatabaseIngredient methods
        ArrayList<String> ingStrings = new ArrayList<>();
        String ingString;
        for (int i = 0; i < newRecipe.getIngredients().size(); i++) {
            ingString = DatabaseIngredient.ingredientToString(
                    newRecipe.getIngredients().get(i));
            ingStrings.add(ingString);
        }

        // Put all data from the recipe into data
        data.put("Title", newRecipe.getTitle());
        data.put("Category", newRecipe.getCategory());
        data.put("Comments", newRecipe.getComments());
        data.put("Prep Time", newRecipe.getPrepTime());
        data.put("Servings", newRecipe.getServings());
        data.put("Ingredients", ingStrings);
        /*
         * Decoding and encoding of bitmap with reference to:
         * https://www.learnhowtoprogram.com/android/gestures-animations-flexible-uis/using-the-camera-and-saving-images-to-firebase
         * Accessed 2022-11-24
         */
        if (newRecipe.getPhotoBitmap() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            newRecipe.getPhotoBitmap().compress(
                    Bitmap.CompressFormat.JPEG, 50, baos);
            String imageEncoded = Base64.encodeToString(
                    baos.toByteArray(), Base64.DEFAULT);
            data.put("Bitmap", imageEncoded);
        }

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
     * @param newRecipe The edited {@link Recipe} object to be removed.
     */
    public void onOkEditPressed(Recipe newRecipe) {
        HashMap<String, Object> data = new HashMap<>();
        Recipe oldRecipe = RecipeDataList.get(selectedRecipeIndex);


        // Can't store ingredient directly so use DatabaseIngredient methods
        ArrayList<String> ingStrings = new ArrayList<>();
        String ingString;
        for (int i = 0; i < newRecipe.getIngredients().size(); i++) {
            ingString = DatabaseIngredient.ingredientToString(
                    newRecipe.getIngredients().get(i));
            ingStrings.add(ingString);
        }

        // Grab data from the updated recipe
        data.put("Title", newRecipe.getTitle());
        data.put("Category", newRecipe.getCategory());
        data.put("Comments", newRecipe.getComments());
        data.put("Prep Time", newRecipe.getPrepTime());
        data.put("Servings", newRecipe.getServings());
        data.put("Ingredients", ingStrings);
        /*
         * Decoding and encoding of bitmap with reference to:
         * https://www.learnhowtoprogram.com/android/gestures-animations-flexible-uis/using-the-camera-and-saving-images-to-firebase
         * Accessed 2022-11-24
         */
        if (newRecipe.getPhotoBitmap() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            newRecipe.getPhotoBitmap().compress(
                    Bitmap.CompressFormat.JPEG, 50, baos);
            String imageEncoded = Base64.encodeToString(
                    baos.toByteArray(), Base64.DEFAULT);
            data.put("Bitmap", imageEncoded);
        }

        Log.d(TAG, String.valueOf(selectedRecipeIndex));

        // Update hash code so we can continue referencing recipe
        onDeletePressed();
        collectionReference
                .document(String.valueOf(newRecipe.hashCode()))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Log success
                        Log.d(TAG+"NewData", String.valueOf(newRecipe.hashCode()));
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
            Log.d(TAG+"DelH", String.valueOf(oldRecipe.hashCode()));
            Log.d(TAG+"Del", String.valueOf(selectedRecipeIndex));
            // Remove ingredient from database
            collectionReference
                    .document(String.valueOf(oldRecipe.hashCode()))
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Log success
                            Log.d(TAG+"Del", "Data has been deleted!");
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
     * @return Always true, iff the selected item is the calling activity.
     * @since 1.0
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menu) {
        // Go to activity selected, based on title.
        String destination = (String) menu.getTitle();
        switch(destination) {
            case "Shopping List": {
                Intent intent = new Intent(this, ShoppingListActivity.class);
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
            default: break;
        }

        // Close navigation drawer if we selected the current activity.
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * A getter method for use in the RecipeFragment to get access to all
     * currently stored ingredients to create {@link Recipe} objects.
     *
     * @return An {@link ArrayList<Ingredient>} containing all ingredients
     *         known to the database.
     * @since 1.1
     */
    public ArrayList<Ingredient> getDatabaseIngredients() {
        return databaseIngredients;
    }


    /**
     * A getter method for use in the {@link RecipeFragment} to get access to
     * all currently stored categories to create {@link Recipe} objects.
     *
     * @return An {@link ArrayList<String>} containing all categories
     *         known to the database.
     * @since 1.1
     */
    public ArrayList<String> getCategories() {
        return catListRec.getCategories();
    }


    /**
     * A getter method for use in the {@link RecipeFragment} to get access to
     * all currently stored categories to create {@link Ingredient} objects.
     *
     * @return An {@link ArrayList<String>} containing all categories
     *         known to the database.
     * @since 1.1
     */
    public ArrayList<String> getIngCategories() {
        return catListIng.getCategories();
    }
}
