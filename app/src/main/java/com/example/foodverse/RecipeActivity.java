package com.example.foodverse;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

public class RecipeActivity  extends AppCompatActivity implements RecipeFragment.OnFragmentInteractionListener{
    private ListView RecipeList;
    ArrayAdapter<Recipe> RecAdapter;
    ArrayList<Recipe> RecipeDataList;
    View clickedElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        RecipeList = findViewById(R.id.recipes_list);

        RecipeDataList = new ArrayList<>();

        //create arrays to hold the values for each of the attributes for created recipe
        String[] rec_title = {};
        String[] rec_category = {};
        String[] rec_comments = {};
        Integer[] rec_servings = {};
        Integer[] rec_prep_time = {};
        //ArrayList<Ingredient> list_of_ingredients2 = new ArrayList<>();

        for (int i = 0; i < rec_title.length; i++) {
            RecipeDataList.add((new Recipe(rec_title[i], rec_prep_time[i], rec_servings[i], rec_category[i], rec_comments[i])));
        }
        RecAdapter = new RecipeList(this, RecipeDataList); //create the interface for the entries
        RecipeList.setAdapter(RecAdapter); //update the UI

        //when the addButton is clicked, open a dialog box to enter the attributes for the entry
        final Button addRecButton = findViewById(R.id.id_add_recipe_button);
        addRecButton.setOnClickListener((v) -> {
            new RecipeFragment().show(getSupportFragmentManager(), "ADD_Recipe");
        });
        Button btn_del = findViewById(R.id.id_del_recipe_button);
        Button edit_btn = findViewById(R.id.id_edit_recipe_button);

        RecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (clickedElement != null) {
                    clickedElement.setBackgroundColor(Color.WHITE);
                }
                clickedElement = view;
                clickedElement.setBackgroundColor(Color.GRAY);
                RecAdapter.notifyDataSetChanged();
                btn_del.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (clickedElement != null) {
                            RecipeDataList.remove(i);
                            RecAdapter.notifyDataSetChanged();
                            clickedElement.setBackgroundColor(Color.WHITE);
                            clickedElement = null;
//                            calcTotals();
                        }
                    }
                });
                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickedElement != null) {
                            RecipeFragment.newInstance(RecipeDataList.get(i)).show(getSupportFragmentManager(), "Edit_Recipe");
                            clickedElement.setBackgroundColor(Color.WHITE);
                            clickedElement = null;
                        }
                    }
                });

            }
        });

    }

    //the function is ran when the Ok button is pressed on the dialog box when creating a recipe entry
    @Override
    public void onOkPressed(Recipe newRecipe) {
        RecAdapter.add(newRecipe);
//        calcTotals();
    }
    //the function is ran when the Ok button is pressed on the dialog box when creating a food entry
    public void onOkEditPressed(Recipe newRecipe) {
        RecAdapter.notifyDataSetChanged();
//        calcTotals();
    }
}
