package com.example.foodverse;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {
    // Declare the variables so that you will be able to reference it later.
    private ListView shoppingListView;
    private ArrayAdapter<StoredIngredient> shoppingListAdapter;
    private ArrayList<StoredIngredient> ingredientArrayList;
    private int selectedIngredientIndex = -1;
    private FirebaseFirestore db;
    private final String TAG = "ShoppingListActivity";
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        shoppingListView = findViewById(R.id.shopping_list);

        ingredientArrayList = new ArrayList<>();
        shoppingListAdapter = new ShoppingList(this, ingredientArrayList);
        shoppingListView.setAdapter(shoppingListAdapter);
    }
}
