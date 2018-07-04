package com.denweisenseel.scotlandsaarexperimental.dialogFragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.denweisenseel.scotlandsaarexperimental.R;

/**
 * Created by RasSv 20.09.2017
 */
public class QuitGameFragment extends DialogFragment {

    private static final String TAG = "Quitting Game?";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static QuitGameFragment newInstance() {
        QuitGameFragment fragment = new QuitGameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.quit_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button yesButton = view.findViewById(R.id.quitGameYesButton);
        final Button noButton = view.findViewById(R.id.quitGameNoButton);

        //When the user clicks on YES
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getActivity().finish();
            }
        });

        //When the user clicks on NO
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }


    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttach(activity);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
