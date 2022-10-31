package com.example.foodverse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FinalFragment extends DialogFragment {
    private EditText editText;

    private Meal meal;
    private EditText date;

    private FinalFragment.OnFragmentInteractionListener listener;

    public FinalFragment() {
        super();
        this.meal = null;
    }

    public FinalFragment(Meal meal) {
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
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener ");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.final_fragment, null);
        editText = view.findViewById(R.id.edit_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Add/Edit Meal")
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL", null)
                .create();
    }
}
