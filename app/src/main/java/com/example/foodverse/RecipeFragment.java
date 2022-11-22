package com.example.foodverse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

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
    private Button addButton; // Button to add ingredient to meal
    private Button deleteButton;
    public Boolean edit_text = Boolean.FALSE;
    private ArrayAdapter<Ingredient> listViewAdapter;
    private ArrayList<Ingredient> recIngredients = new ArrayList<>();
    private ArrayList<String> ingredientStringList = new ArrayList<>();
    private ArrayAdapter<String> ingAdapter;
    private Spinner ingredientSpinner; // Spinner for ingredients
    private ListView ingredientList;
    private RecipeActivity act;


    /**
     * Interface for interacting with recipe entries in the list.
     */
    public interface OnFragmentInteractionListener{
        void onOkPressed(Recipe newRec);
        void onOkEditPressed(Recipe newRec);
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
        ingredientList = view.findViewById(R.id.ing_list);
        listViewAdapter = new IngredientAdapter(getActivity(), recIngredients);
        ingredientSpinner = view.findViewById(R.id.recipe_ingredient_spinner);
        ingredientList.setAdapter(listViewAdapter);
        String recipeCategory = "tmp";


        // if in edit mode, get the values of each attribute that stored for the recipe item entry
        // and populate them on the dialog box to allow the user to edit - this is done using the getters
        // and setters for the attributes
        if (edit_text == Boolean.TRUE){
            // obtain access to the Bundle's information inputted when
            // editing or creating a Recipe entry
            Bundle recipeVal = getArguments();

            Recipe RecipeObject = (Recipe) recipeVal.get("recipe"); //accessing the value of the attribute passed
            rec_title.setText(RecipeObject.getTitle());
            rec_comments.setText(RecipeObject.getComments());
            String StrCat = RecipeObject.getCategory();
            rec_servings.setText(Integer.toString(RecipeObject.getServings()));//switched w int refactor
            rec_preptime.setText(Integer.toString(RecipeObject.getPrepTime()));//switched w int refactor
            chosenRecipe = RecipeObject; //update on the object
            // Get all the ingredients from the recipe and add them to
            // an array list to be displayed on a listview
            for (int i = 0; i < RecipeObject.getIngredients().size(); i++) {
                recIngredients.add(RecipeObject.getIngredients().get(i));
            }
            listViewAdapter.notifyDataSetChanged();
        }

        ingAdapter = new ArrayAdapter<String>(getActivity(), R.layout.ingredient_spinner, ingredientStringList);

        act = (RecipeActivity) getActivity();
        // The ingredients from the database are added to the spinner
        for (int i = 0; i < act.getDatabaseIngredients().size(); i++) {
            ingredientStringList.add(
                    act.getDatabaseIngredients().get(i).getDescription());
        }

        // The spinner is set up to connect with the list of ingredients
        ingAdapter.setDropDownViewResource(R.layout.ingredient_spinner);
        ingredientSpinner.setAdapter(ingAdapter);

        // When the user clicks on the plus button to add an ingredient
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ingIndex;
                Log.d("MealFrag", "Adding ingredient");
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

                        // Send new edited recipe back to the activity
                        if (edit_text == Boolean.TRUE){
                            Recipe edited = new Recipe(recipe_title,
                                    prepare_time, serving_size,
                                    recipeCategory, recipe_comments,
                                    recIngredients);
                            listener.onOkEditPressed(edited);

                        }
                        // Context updated by creating a new recipe item entry
                        else {
                            listener.onOkPressed(new Recipe(recipe_title,
                                    prepare_time,serving_size,recipeCategory,
                                    recipe_comments,recIngredients));
                        }


                    }
                }).create(); //creates the dialog box
    }


    // Bundles are utilized to pass data attributes into recipeObject
    static RecipeFragment newInstance(Recipe recipe){
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);

        RecipeFragment fragment = new RecipeFragment();
        fragment.edit_text = Boolean.TRUE;
        fragment.setArguments(args);
        return fragment;
    }


    public void deleteIngredient(View view) {
        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                recIngredients.remove(pos);
                listViewAdapter.notifyDataSetChanged();
            }
        });
    }

}

