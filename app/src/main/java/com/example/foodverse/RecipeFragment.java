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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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
public class RecipeFragment extends DialogFragment {
    private EditText rec_title;
    private EditText rec_comments;
    private EditText rec_servings;
    private EditText rec_preptime;
    private RecipeFragment.OnFragmentInteractionListener listener;
    private Recipe chosenRecipe;
    private Button addButton, deleteButton, choosePhotoButton, takePhotoButton,
                   deletePhoto;
    private ImageView recipePhoto;
    public Boolean edit_text = Boolean.FALSE;
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
    public RecipeFragment() {
        super();
        this.chosenRecipe = null;
    }

    /**
     * Constructor for {@link RecipeFragment} with a given {@link Recipe}.
     *
     * @param recipe A {@link Recipe} representing the recipe for
     * for which we want to see the details of.
     */
    public RecipeFragment(Recipe recipe) {
        super();
        this.chosenRecipe = recipe;
        // Set that we are in edit mode
        edit_text = Boolean.TRUE;
    }

    /**
     * Called when fragment is attached to the context.
     * @param context The context being attached to.
     */
    //the function is what keeps track of the state of the system and the variables
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else{
            throw new RuntimeException(context.toString()+ "must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Initializes the components when the fragment is created.
     * @param savedInstanceState
     * @return a dialog with details of recipe
     */
    //The dialog box is what takes in the values for the attributes and displays them when in edit mode
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        // The layoutInflater gives access to the XML widgets in the fragment_recipe file to
        // edit the values and display them on the screen through the RecipeList
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recipe, null);
        rec_title = view.findViewById(R.id.recipe_title_edit_text);
        rec_comments = view.findViewById(R.id.comments_edit_text);
        rec_servings = view.findViewById(R.id.serving_size_edit_text);
        rec_preptime = view.findViewById(R.id.prep_time_edit_text);
        addButton = view.findViewById(R.id.recipe_add_ingredient_button);
        deleteButton = view.findViewById(R.id.recipe_delete_ingredient_button);
        choosePhotoButton = view.findViewById(R.id.recipe_add_image_button);
        takePhotoButton = view.findViewById(R.id.recipe_take_photo_button);
        deletePhoto = view.findViewById(R.id.recipe_remove_photo_button);
        ingredientList = view.findViewById(R.id.ing_list);
        listViewAdapter = new IngredientAdapter(getActivity(), recIngredients);
        ingredientSpinner = view.findViewById(R.id.recipe_ingredient_spinner);
        categorySpinner = view.findViewById(R.id.recipe_category_spinner);
        recipePhoto = view.findViewById(R.id.recipe_picture);
        ingredientList.setAdapter(listViewAdapter);
        cam_uri = null;
        String recCategory = "";

        // if in edit mode, get the values of each attribute that stored for the recipe item entry
        // and populate them on the dialog box to allow the user to edit - this is done using the getters
        // and setters for the attributes
        if (edit_text == Boolean.TRUE) {
            // obtain access to the Bundle's information inputted when
            // editing or creating a Recipe entry
            Bundle recipeVal = getArguments();

            //Recipe RecipeObject = (Recipe) recipeVal.get("recipe"); //accessing the value of the attribute passed
            Recipe RecipeObject = chosenRecipe;
            rec_title.setText(RecipeObject.getTitle());
            rec_comments.setText(RecipeObject.getComments());
            recCategory = RecipeObject.getCategory();
            rec_servings.setText(Integer.toString(RecipeObject.getServings()));//switched w int refactor
            rec_preptime.setText(Integer.toString(RecipeObject.getPrepTime()));//switched w int refactor
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

        // The spinner is set up to connect with the list of ingredients
        ingAdapter.setDropDownViewResource(R.layout.ingredient_spinner);
        ingredientSpinner.setAdapter(ingAdapter);

        categoryAdapter.setDropDownViewResource(R.layout.ingredient_spinner);
        categorySpinner.setAdapter(categoryAdapter);

        if (edit_text == Boolean.TRUE) {
            categorySpinner.setSelection(categoryList.indexOf(recCategory));
        }

        // When the user clicks on the plus button to add an ingredient
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ingIndex;
                Log.d("RecFrag", "Adding ingredient");
                ingIndex = ingredientSpinner.getSelectedItemPosition();
                recIngredients.add(act.getDatabaseIngredients().get(ingIndex));

                listViewAdapter.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                        recIngredients.remove(pos);
                        listViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        /*
         * Both selecting and taking photo functionality made with reference to:
         * https://developer.android.com/training/camera/camera-intents (2022-11-22)
         * https://developer.android.com/reference/android/provider/MediaStore (2022-06-08)
         * https://stackoverflow.com/questions/67115099/how-do-i-use-registerforactivityresult-to-launch-camera
         * Answer by Alias (2021)
         * Both Accessed 2022-11-24
         */
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
                cam_uri = requireContext().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    Log.e("RecFrag", "Take Picture Activity not found.");
                }
            }
        });

        /*
         * Made with reference to the above and:
         * https://stackoverflow.com/questions/38352148/get-image-from-the-gallery-and-show-in-imageview
         * Answer by Atul Mavani (2016), edited by shagberg (2021).
         * https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
         * by adityamshidlyali
         * Accessed 2022-11-24.
         */
        choosePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Gallery");
                cam_uri = requireContext().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK);
                choosePictureIntent.setType("image/*");
                choosePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cam_uri);
                try {
                    startActivityForResult(choosePictureIntent, REQUEST_IMAGE_SELECTION);
                } catch (ActivityNotFoundException e) {
                    Log.e("RecFrag", "Choose Picture Activity not found.");
                }
            }
        });

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cam_uri = null;
                bitmap = null;
                recipePhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));
            }
        });


        //creates the dialog box with the populated info and action buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add/Edit Recipe")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String recipe_title = rec_title.getText().toString();
                        String recipe_comments = rec_comments.getText().toString();
                        // The following lines set default values to prevent the app from crashing if
                        // values for servings size and prepare time are not entered
                        Integer serving_size = 1;
                        Integer prepare_time = 10;

                        if (rec_servings.getText().toString() != "") {
                            serving_size = Integer.parseInt(
                                    rec_servings.getText().toString());
                        }
                        if (rec_preptime.getText().toString() != "") {
                            prepare_time = Integer.parseInt(
                                    rec_preptime.getText().toString());
                        }

                        String recipeCategory = "";
                        int categoryInd;
                        categoryInd = categorySpinner.getSelectedItemPosition();
                        recipeCategory = categoryList.get(categoryInd);

                        // Send new edited recipe back to the activity
                        if (edit_text == Boolean.TRUE){
                            Recipe edited = new Recipe(recipe_title,
                                    prepare_time, serving_size,
                                    recipeCategory, recipe_comments,
                                    recIngredients, bitmap);
                            listener.onOkEditPressed(edited);

                        }
                        // Context updated by creating a new recipe item entry
                        else {
                            listener.onOkPressed(new Recipe(recipe_title,
                                    prepare_time,serving_size,recipeCategory,
                                    recipe_comments,recIngredients, bitmap));
                        }


                    }
                }).create(); //creates the dialog box
    }


    public void deleteIngredient(View view) {
        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                recIngredients.remove(pos);
                listViewAdapter.notifyDataSetChanged();
            }
        });
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

