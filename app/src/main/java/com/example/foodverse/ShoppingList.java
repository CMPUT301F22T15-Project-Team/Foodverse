package com.example.foodverse;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Custom shopping list adapter to hold shopping list ingredients.
 */
public class ShoppingList extends ArrayAdapter<ShoppingListIngredient> {
    private ArrayList<ShoppingListIngredient> ingredients;
    private FirebaseFirestore db;
    private CollectionReference shoppingListCollectionReference;
    private Context context;

    /**
     * Shopping List constructor
     * @param context
     * @param ingredients The ingredients array list.
     */
    public ShoppingList(Context context, ArrayList<ShoppingListIngredient> ingredients) {
        super(context, 0, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    /**
     * Returns the view of the {@link ShoppingList} after updating it.
     * @param position Position of the item within the data set.
     * @param convertView
     * @param parent
     * @return view of the {@link ShoppingList}
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.shopping_list_item,
                    parent,false);
        }

        // Retrieving the text views from the layout.
        ShoppingListIngredient ingredient = ingredients.get(position);
        TextView ingredientDescription = view.findViewById(R.id.description_text);
        TextView ingredientAmount = view.findViewById(R.id.amount_value);
        TextView ingredientUnit = view.findViewById(R.id.unit_value);
        TextView ingredientCategory = view.findViewById(R.id.category_value);
        CheckBox ingredientCheckbox = view.findViewById(R.id.ingredient_checkbox);

        // Updating the values in the layout.
        ingredientDescription.setText(ingredient.getDescription());
        ingredientAmount.setText(Integer.toString(ingredient.getCount()));
        ingredientUnit.setText(ingredient.getUnit());
        ingredientCategory.setText(ingredient.getCategory());

        // Get our database
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
        // From https://firebase.google.com/docs/firestore/manage-data/enable-offline#java_3
        db.enableNetwork()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("ShoppingArrayList", "Firebase online");
                    }
                });

        shoppingListCollectionReference = db.collection("ShoppingList");

        View finalView = view;
        ingredientCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ingredient.isPurchased()){
                    finalView.setBackgroundColor(Color.WHITE);
                    ingredient.setPurchased(false);
                    shoppingListCollectionReference.document(String.valueOf(ingredient.hashCode()))
                            .update("Purchased", false);
                } else {
                    finalView.setBackgroundColor(Color.GRAY);
                    ingredient.setPurchased(true);
                    Activity activity = (Activity) context;
                    new ShoppingListFragment(ingredient).show(activity.getFragmentManager(), "EDIT");
                    shoppingListCollectionReference.document(String.valueOf(ingredient.hashCode()))
                            .update("Purchased", true);
                }
            }
        });

        if(ingredient.isPurchased()){
            finalView.setBackgroundColor(Color.GRAY);
            ingredientCheckbox.setChecked(true);
        } else {
            finalView.setBackgroundColor(Color.WHITE);
            ingredientCheckbox.setChecked(false);
        }

        return view;
    }
}