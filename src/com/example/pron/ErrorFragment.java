package com.example.pron;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class ErrorFragment extends DialogFragment{
	
	AlertDialog.Builder builder;
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_error, null);
        builder.setView(view);
        builder.setTitle("Error: Unable to connect");
        builder.setMessage("Please connect to the internet or try again later.");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ((MainActivity) getActivity()).doPositiveClick();
            }
        }
    );
		// Create the AlertDialog object and return it
        return builder.create();
    }
	
}