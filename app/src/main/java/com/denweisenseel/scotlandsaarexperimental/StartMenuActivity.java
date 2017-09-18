package com.denweisenseel.scotlandsaarexperimental;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denweisenseel.scotlandsaarexperimental.data.VolleyRequestQueue;
import com.denweisenseel.scotlandsaarexperimental.dialogFragments.UsernameInputFragment;

import org.json.JSONObject;

public class StartMenuActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startGame = (Button) findViewById(R.id.STARTMENU_StartGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StartMenuActivity.this, LobbyActivity.class);
                i.putExtra(getString(R.string.host), true);

                startGame();
                //startActivity(i);





            }
        });

    }

    private void startGame() {

        Log.v("Requesting", "This");

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, "http://10.0.2.2:8080/_ah/api/scotlandSaarAPI/v1/createGame/123/123/123",null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("T", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("T", error.toString());
            }
        });

        VolleyRequestQueue.getInstance(this).addToRequestQueue(gameRequest);

    }

    @Override
    protected void onStart() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        if(sharedPref.contains(getString(R.string.username))) {

        } else {
            FragmentManager fm = getFragmentManager();
            UsernameInputFragment editNameDialogFragment = UsernameInputFragment.newInstance();
            editNameDialogFragment.show(fm, "Dialog");

        }

        super.onStart();
    }
}
