package com.example.foodverse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;


public class RecipeFragment extends DialogFragment {
    private EditText rec_title;
    private EditText rec_comments;
    private EditText rec_servings;
    private EditText rec_preptime;
    private RadioButton amercian_op;
    private RadioButton italian_op;
    private RadioButton mexican_op;
    private RadioGroup recCategory;
    private RecipeFragment.OnFragmentInteractionListener listener;
    private Recipe chosenRecipe;
    public Boolean edit_text = Boolean.FALSE;
    private ArrayAdapter<Ingredient> listViewAdapter;
    private ArrayList<Ingredient> recIngredients = new ArrayList<>();
    private ListView ingredientList;



    public interface OnFragmentInteractionListener{
        void onOkPressed(Recipe newRec);
        void onOkEditPressed(Recipe newRec);
    }

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
    //The dialog box is what takes in the values for the attributes and displays them when in edit mode
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        // The layoutInflater gives access to the XML widgets in the fragment_recipe file to
        // edit the values and display them on the screen through the RecipeList
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recipe, null);
        rec_title = view.findViewById(R.id.recipe_title_edit_text);
        rec_comments = view.findViewById(R.id.comment_edit_text);
        recCategory = view.findViewById(R.id.radioGroup);
        amercian_op = view.findViewById(R.id.american_op);
        italian_op = view.findViewById(R.id.italian_op);
        mexican_op = view.findViewById(R.id.mexican_op);
        rec_servings = view.findViewById(R.id.serving_size_edit_text);
        rec_preptime = view.findViewById(R.id.prep_time_edit_text);
        ingredientList = view.findViewById(R.id.ing_list);
        listViewAdapter = new IngredientAdapter(getActivity(), recIngredients);
        ingredientList.setAdapter(listViewAdapter);


        //if in edit mode, get the values of each attribute that stored for the recipe item entry
        // and populate them on the dialog box to allow the user to edit - this is done using the getters
        // and setters for the attributes
        if (edit_text == Boolean.TRUE){
            //obtain access to the Bundle's information inputted when
            // editing or creating a Recipe entry
            Bundle recipeVal = getArguments();

            Recipe RecipeObject = (Recipe) recipeVal.get("recipe"); //accessing the value of the attribute passed
            rec_title.setText(RecipeObject.getTitle());
            rec_comments.setText(RecipeObject.getComments());
            String StrCat = RecipeObject.getCategory();
            //since the radio button requires an element id to know which button to check, use an if statement to know which button is checked
            if (StrCat == "American"){
                recCategory.check(R.id.american_op);
            }
            if (StrCat == "Italian"){
                recCategory.check(R.id.italian_op);
            }
            if (StrCat == "Mexican"){
                recCategory.check(R.id.mexican_op);
            }

            rec_servings.setText(Integer.toString(RecipeObject.getServings()));//switched w int refactor
            rec_preptime.setText(Integer.toString(RecipeObject.getPrepTime()));//switched w int refactor
            chosenRecipe = RecipeObject; //update on the object
        }


        //creates the dialog box with the populated info and action buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Recipe")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String recipe_title = rec_title.getText().toString();
                        String recipe_comments = rec_comments.getText().toString();
                        int recipe_Cat = recCategory.getCheckedRadioButtonId();

                        //set the category in the dialog box depending on the id returned by the recipe element
                        String recipeCategory = " ";

                        if (recipe_Cat == R.id.american_op){
                            recipeCategory = "American";
                        }
                        if (recipe_Cat == R.id.italian_op){
                            recipeCategory = "Italian";
                        }
                        if (recipe_Cat == R.id.mexican_op){
                            recipeCategory = "Mexican";
                        }
                        //The following lines set default values to prevent the app from crashing if
                        // values for servings size and prepare time are not entered
                        Integer serving_size = 1;
                        Integer prepare_time = 10;

                        if (serving_size != null){
                            serving_size = Integer.parseInt(rec_servings.getText().toString());
                        }
                        if (prepare_time != null){
                            prepare_time = Integer.parseInt(rec_preptime.getText().toString());
                        }

                        //update the values of each attribute by getting and setting the values of the food item
                        // have the context be updated when the Ok button is pressed
                        if (edit_text == Boolean.TRUE){
                            Recipe edited = new Recipe(recipe_title,
                                    prepare_time, serving_size,
                                    recipeCategory, recipe_comments,recIngredients );
                            listener.onOkEditPressed(edited);

                        }
                        //context updated by creating a new recipe item entry
                        else{
                            listener.onOkPressed(new Recipe(recipe_title,
                                    prepare_time,serving_size,recipeCategory,
                                    recipe_comments,recIngredients));
                        }


                    }
                }).create(); //creates the dialog box
    }

    //Bundles are utilized to pass data attributes into foodObjects
    static RecipeFragment newInstance(Recipe recipe){
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);

        RecipeFragment fragment = new RecipeFragment();
        fragment.edit_text = Boolean.TRUE;
        fragment.setArguments(args);
        return fragment;
    }

}

