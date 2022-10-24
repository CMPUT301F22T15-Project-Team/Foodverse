package com.example.foodverse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;

public class MealPlanActivity extends AppCompatActivity {

    private ArrayList<Pair<LocalDate, ArrayList<Meal>>> days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);
    }
}