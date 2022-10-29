package com.example.foodverse;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MealPlanFragment extends DialogFragment  {
    private Meal meal;
    private EditText date;
    // Recipe-related elements still need to be implemented
    private ListView ingredientList;
    private EditText servings;

    private Button mealDate;
    private Button addButton;
    private Date date2;
    private Spinner recipeSpinner;
    private Spinner ingredientSpinner;
    private ArrayList<Ingredient> mealIngredients = new ArrayList<>();
    private ArrayList<Ingredient> addedIngredients = new ArrayList<>();
    private ArrayList<String> listedIngredients = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference ingRef, storedRef;
    private HashSet<Ingredient> set = new HashSet<>();

    private MealPlanFragment.OnFragmentInteractionListener listener;

    public MealPlanFragment() {
        super();
        this.meal = null;
    }

    public MealPlanFragment(Meal meal) {
        super();
        this.meal = meal;
    }

    public interface OnFragmentInteractionListener {
        void mealAdded(Meal meal);
        void mealEdited(Meal meal);
        void mealDeleted();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MealPlanFragment.OnFragmentInteractionListener) {
            listener = (MealPlanFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.meal_plan_fragment, null);

        // Initialize Components
        mealDate = view.findViewById(R.id.date_button);
        recipeSpinner = view.findViewById(R.id.recipe_spinner);
        ingredientSpinner = view.findViewById(R.id.meal_ingredient_spinner);
        addButton = view.findViewById(R.id.add_meal_ingredient_button);
        ingredientList = view.findViewById(R.id.meal_list);

        ArrayAdapter<String> listedAdapter = new ArrayAdapter<String>(getActivity(), R.layout.content_stored_ingredient, listedIngredients);
        //ingredientList.setAdapter(listedAdapter);

        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month,
                                  int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                setNewExpiryDate(calendar);
            }
        };

        db = FirebaseFirestore.getInstance();
        db.enableNetwork();
        ingRef = db.collection("Ingredients");
        storedRef = db.collection("StoredIngredients");

        ingRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                mealIngredients.clear();
                // Add ingredients from the cloud
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String hashCode = doc.getId();
                    String description = (String) doc.getData().get("Description");
                    Long count = (Long) doc.getData().get("Count");
                    Ingredient ing = new Ingredient(description, count.intValue());
                    if (!set.contains(ing)) {
                        mealIngredients.add(ing);
                        set.add(ing);
                    }
                }
            }
        });

        storedRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {
                // Clear the old list
                mealIngredients.clear();
                // Add ingredients from the cloud
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    String hashCode = doc.getId();
                    String description = (String) doc.getData().get("Description");
                    Long count = (Long) doc.getData().get("Count");
                    Ingredient ing = new Ingredient(description, count.intValue());
                    if (!set.contains(ing)) {
                        mealIngredients.add(ing);
                        set.add(ing);
                    }
                }
            }
        });

        ArrayList<String> arraySpinner = new ArrayList<>();

        for (Ingredient ingredient : mealIngredients) {
            arraySpinner.add(ingredient.getDescription());
        }

        arraySpinner.add("Add New Ingredient");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, arraySpinner);

        ingredientSpinner.setAdapter(adapter);



        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //setNewExpiryDate(calendar);

        if (meal != null) {
            // Set the values for the various fields
            //ingredientDescription.setText(ingredient.getDescription());
            //ingredientCount.setText(Integer.toString(ingredient.getCount()));
            //ingredientCost.setText(Integer.toString(ingredient.getUnitCost()));

            // Load the time information
            //calendar.setTime(ingredient.getBestBefore());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            setNewExpiryDate(calendar);

        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                dateSetListener, year, month, day);


        //ingredientExpiry.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        datePickerDialog.show();
        //    }
        //});

        mealDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        //ArrayList<String> listedIngredients = new ArrayList<>();



        // When the user clicks on the plus button to add an ingredient
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addedIngredients.add((Ingredient) ingredientSpinner.getSelectedItem());
                listedIngredients.add((String) ingredientSpinner.getSelectedItem());
                listedAdapter.notifyDataSetChanged();
            }
        });

        //IngredientAdapter addedAdapter = new IngredientAdapter(getActivity(), addedIngredients);
        //ingredientList.setAdapter(listedAdapter);

        //ArrayList<String> listedIngredients = new ArrayList<String>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add/Edit Meal")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Delete", null)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String city = cityName.getText().toString();
                        //String province = provinceName.getText().toString();
                        //listener.onOkPressed(new City(city, province));
                        Meal meal = new Meal();
                        LocalDate date = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        meal.setDate(date2);
                        // Add Recipe Stuff
                        // Add array of ingredients to meal
                        meal.setIngredients(mealIngredients);
                    }
                }).create();
    }

    private void setNewExpiryDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // The month is an index, so we need to add one to make it accurate
        month++;

        // Format the month and day strings
        String monthStr = Integer.toString(month);
        String dayStr = Integer.toString(day);
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        if (day < 10) {
            dayStr = "0" + dayStr;
        }

        // Update the spots that track/display the best before date
        String date = Integer.toString(year) + "-" + monthStr + "-" + dayStr;
        mealDate.setText(date);
        date2 = calendar.getTime();
    }


}
