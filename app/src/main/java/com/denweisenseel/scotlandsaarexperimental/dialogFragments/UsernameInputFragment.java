package com.denweisenseel.scotlandsaarexperimental.dialogFragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denweisenseel.scotlandsaarexperimental.R;

import java.util.logging.Logger;

/**
 * Created by denwe on 17.09.2017.
 */

public class UsernameInputFragment extends DialogFragment {

    public static UsernameInputFragment newInstance() {
        UsernameInputFragment f = new UsernameInputFragment();
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.username_input, container, false);

        // Watch for button clicks.
        final EditText usernameInput = v.findViewById(R.id.usernameInput_edittext_input);

        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused)
            {
                if (focused)
                {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        usernameInput.setFocusable(true);
        usernameInput.requestFocus();
        usernameInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_DONE:
                        if(!usernameInput.getText().toString().matches("[a-zA-Z]+")){
                            Toast.makeText(getActivity(), "Username darf nur aus Buchstaben bestehen.", Toast.LENGTH_LONG).show();
                            Log.v("TEST","TEST");
                        } else {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).edit();
                            editor.putString(getString(R.string.username), usernameInput.getText().toString());
                            editor.commit();
                            Toast.makeText(getActivity(), "Username gespeichert!", Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                        break;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
