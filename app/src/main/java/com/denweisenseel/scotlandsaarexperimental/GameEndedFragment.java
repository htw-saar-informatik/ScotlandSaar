package com.denweisenseel.scotlandsaarexperimental;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by kluhan on 25.09.2017.
 */

public class GameEndedFragment extends DialogFragment{
    private String message;


    public void setMessage(String messsage){
        this.message = messsage;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
