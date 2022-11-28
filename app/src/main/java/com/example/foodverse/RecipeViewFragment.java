package com.example.foodverse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import kotlin.text.UStringsKt;

/**
 * RecipeFragment
 * The fragment responsible for allowing the user to add or edit recipes.
 * Currently, the recipe fragment does not support adding or deleting ingredients to
 * the recipe. It also does not provide the functionality of changing/uploading a recipe
 * image.
 *
 * @version 1.0
 *
 */
public class RecipeViewFragment extends DialogFragment {
    private TextView rec_title_v;
    private TextView rec_comments_v;
    private TextView rec_servings_v;
    private TextView rec_preptime_v;
    private String recCategory_v;
    private TextView rec_Category_v;
    private RecipeViewFragment.OnFragmentInteractionListener listener;
    private Recipe chosenRecipe;
    private Button addButton, deleteButton, choosePhotoButton, takePhotoButton,
            deletePhoto;
    private ImageView recipePhoto;
    public Boolean edit_text = Boolean.FALSE;
    public Boolean view_text = Boolean.TRUE;
    private ArrayAdapter<Ingredient> listViewAdapter;
    private ArrayList<Ingredient> recIngredients = new ArrayList<>();
    private ArrayList<String> ingredientStringList = new ArrayList<>();
    private ArrayList<String> categoryList = new ArrayList<>();
    private ArrayAdapter<String> ingAdapter, categoryAdapter;
    private Spinner ingredientSpinner, categorySpinner;
    private ListView ingredientList;
    private RecipeActivity act;
    private Uri cam_uri;
    private Bitmap bitmap;

    private final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_IMAGE_SELECTION = 2;


    /**
     * Interface for interacting with recipe entries in the list.
     */
    public interface OnFragmentInteractionListener{
        void onOkPressed(Recipe newRec);
        void onOkEditPressed(Recipe newRec);
    }

    /**
     * Default constructor for {@link RecipeFragment}. Sets the chosen recipe
     * to null.
     */
    public RecipeViewFragment() {
        super();
        this.chosenRecipe = null;
    }

    /**
     * Constructor for {@link RecipeFragment} with a given {@link Recipe}.
     *
     * @param recipe A {@link Recipe} representing the recipe for
     * for which we want to see the details of.
     */
    public RecipeViewFragment(Recipe recipe) {
        super();
        this.chosenRecipe = recipe;
        // Set that we are in edit mode
        edit_text = Boolean.TRUE;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else{
            throw new RuntimeException(context.toString()+ "must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        // The layoutInflater gives access to the XML widgets in the fragment_recipe file to
        // edit the values and display them on the screen through the RecipeList
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_recipe_fragment, null);
        rec_title_v = view.findViewById(R.id.recipe_title_view_text);
        rec_comments_v = view.findViewById(R.id.comments_view_text);
        rec_servings_v = view.findViewById(R.id.serving_size_view_text);
        rec_preptime_v = view.findViewById(R.id.prep_time_view_text);
        addButton = view.findViewById(R.id.recipe_add_ingredient_button); //remove
        deleteButton = view.findViewById(R.id.recipe_delete_ingredient_button); //remove
        choosePhotoButton = view.findViewById(R.id.recipe_add_image_button);//remove
        takePhotoButton = view.findViewById(R.id.recipe_take_photo_button);//remove
        deletePhoto = view.findViewById(R.id.recipe_remove_photo_button);//remove
        ingredientList = view.findViewById(R.id.ing_list);
        listViewAdapter = new IngredientAdapter(getActivity(), recIngredients);
        ingredientSpinner = view.findViewById(R.id.recipe_ingredient_spinner);//remove
        categorySpinner = view.findViewById(R.id.recipe_category_spinner);
        rec_Category_v = view.findViewById(R.id.category_view_text);
        recipePhoto = view.findViewById(R.id.recipe_picture);
        ingredientList.setAdapter(listViewAdapter);
        cam_uri = null;
        String recCategory = "";

        // if in edit mode, get the values of each attribute that stored for the recipe item entry
        // and populate them on the dialog box to allow the user to edit - this is done using the getters
        // and setters for the attributes
        if (view_text == Boolean.TRUE) {
            // obtain access to the Bundle's information inputted when
            // editing or creating a Recipe entry
            Bundle recipeVal = getArguments();

            Recipe RecipeObject = chosenRecipe;
            rec_title_v.setText(RecipeObject.getTitle());
            rec_comments_v.setText(RecipeObject.getComments());
            recCategory_v = RecipeObject.getCategory();
            rec_servings_v.setText(Integer.toString(RecipeObject.getServings()));//switched w int refactor
            rec_preptime_v.setText(Integer.toString(RecipeObject.getPrepTime()));//switched w int refactor
            chosenRecipe = RecipeObject; //update on the object
            // Get all the ingredients from the recipe and add them to
            // an array list to be displayed on a listview
            for (int i = 0; i < RecipeObject.getIngredients().size(); i++) {
                recIngredients.add(RecipeObject.getIngredients().get(i));
            }
            listViewAdapter.notifyDataSetChanged();

            if (RecipeObject.getPhotoBitmap() != null) {
                Log.e("ImageActivity", "Setting bitmap");
                try {
                    recipePhoto.setImageBitmap(RecipeObject.getPhotoBitmap());
                    bitmap = RecipeObject.getPhotoBitmap();
                } catch (SecurityException e) {
                    Log.e("ImageActivity", e.getMessage());
                    RecipeObject.setPhotoBitmap(null);
                }
            }

        }

        /*
         * Ensure list view shows all parameters. Reference:
         * https://stackoverflow.com/questions/40861136/set-listview-height-programmatically
         * Answer by: Rushi Ayyappa (2016)
         * https://stackoverflow.com/questions/5255184/android-and-setting-width-and-height-programmatically-in-dp-units
         * Answer by: Robby Pond (2011)
         * Accessed: 2022-11-28
         */
        float dpscale = getContext().getResources().getDisplayMetrics().density;
        ViewGroup.LayoutParams params = ingredientList.getLayoutParams();
        params.height = (int) Math.max(Math.ceil(50*recIngredients.size() *
                dpscale + 0.5f), Math.ceil(50 * dpscale + 0.5f));
        ingredientList.setLayoutParams(params);
        ingredientList.requestLayout();

        ingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, ingredientStringList);
        categoryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, categoryList);

        act = (RecipeActivity) getActivity();
        // The ingredients from the database are added to the spinner
        for (int i = 0; i < act.getDatabaseIngredients().size(); i++) {
            ingredientStringList.add(
                    act.getDatabaseIngredients().get(i).getDescription());
        }

        for (int i = 0; i < act.getCategories().size(); i++) {
            categoryList.add(act.getCategories().get(i));
        }
        String recipeCategory = "";
        String recipeCategory_value = chosenRecipe.getCategory();
        Log.d("Debuggin View", recipeCategory_value);
        rec_Category_v.setText(recipeCategory_value);



        //creates the dialog box with the populated info and action buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setNeutralButton("Cancel",null)
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new RecipeFragment(chosenRecipe).show(getFragmentManager(), "Edit_Recipe");
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String recipe_title = rec_title_v.getText().toString();
                        String recipe_comments = rec_comments_v.getText().toString();
                        // The following lines set default values to prevent the app from crashing if
                        // values for servings size and prepare time are not entered
                        Integer serving_size = 1;
                        Integer prepare_time = 10;

                        if (rec_servings_v.getText().toString() != "") {
                            serving_size = Integer.parseInt(
                                    rec_servings_v.getText().toString());
                        }
                        if (rec_preptime_v.getText().toString() != "") {
                            prepare_time = Integer.parseInt(
                                    rec_preptime_v.getText().toString());
                        }

                        // Send new edited recipe back to the activity
                        if (view_text == Boolean.TRUE){
                            Recipe edited = new Recipe(recipe_title,
                                    prepare_time, serving_size,
                                    recipeCategory_value, recipe_comments,
                                    recIngredients, bitmap);
                            listener.onOkEditPressed(edited);

                        }
                         //Context updated by creating a new recipe item entry
                        else {
                            listener.onOkPressed(new Recipe(recipe_title,
                                    prepare_time,serving_size,recipeCategory_value,
                                    recipe_comments,recIngredients, bitmap));
                        }


                    }
                }).create(); //creates the dialog box
    }



    /**
     * For displaying an image after the camera captures it, or the user selects
     * it from their photos. Made with reference to:
     * https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
     * Answer by jengelsma (2011) edited by emilekm (2019)
     * https://stackoverflow.com/questions/29803924/android-how-to-set-the-photo-selected-from-gallery-to-a-bitmap
     * Answer by Akash (2015)
     *
     * @param requestCode The integer code sent as a request with the activity.
     * @param resultCode The integer code sent back from Android, giving the
     *                   exit status of the activity.
     * @param data The intent from the closed activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d("ImageActivity", cam_uri.toString());
            //recipePhoto.setImageURI(cam_uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(act.getContentResolver(), cam_uri);
                recipePhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == Activity.RESULT_OK) {
            Log.d("ImageActivity", cam_uri.toString());
            cam_uri = data.getData();
            //recipePhoto.setImageURI(cam_uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(act.getContentResolver(), cam_uri);
                recipePhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

