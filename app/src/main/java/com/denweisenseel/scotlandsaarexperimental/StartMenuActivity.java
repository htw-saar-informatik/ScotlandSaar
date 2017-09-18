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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denweisenseel.scotlandsaarexperimental.api.RequestBuilder;
import com.denweisenseel.scotlandsaarexperimental.data.VolleyRequestQueue;
import com.denweisenseel.scotlandsaarexperimental.dialogFragments.UsernameInputFragment;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class StartMenuActivity extends AppCompatActivity {

    private final String TAG = "StartMenu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startGame = (Button) findViewById(R.id.STARTMENU_StartGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
        Button joinGame = (Button) findViewById(R.id.STARTMENU_JoinGame);
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinGame();
            }
        });

    }

    @Override
    protected void onStart() {
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE);

        if(sharedPref.contains(getString(R.string.username))) {
            Toast.makeText(this, "Hallo " +sharedPref.getString(getString(R.string.username), "NULL"),Toast.LENGTH_SHORT).show();;
        } else {
            FragmentManager fm = getFragmentManager();
            UsernameInputFragment editNameDialogFragment = UsernameInputFragment.newInstance();
            editNameDialogFragment.show(fm, "Dialog");
        }

        if(sharedPref.contains(getString(R.string.gameId))) {
            sharedPref.edit().remove(getString(R.string.gameId));
            //TODO check if game is still alive, rejoin! @Issue 001 (10 Hours)
        }

        super.onStart();
    }

    private void startGame() {

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String username = getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.username),"NULL");
        String gameName = "Game"; //TODO create gameName dialog for further customization (2 Hours)
        String[] requestARGS = {firebaseToken,username,gameName};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.buildRequestUrl(RequestBuilder.CREATE_GAME, requestARGS),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    long gameId = response.getLong(getString(R.string.protocol_gameId));
                    saveGameId(gameId);
                    Log.i(TAG, "Game created! ID: "+gameId);
                    Intent i = new Intent(StartMenuActivity.this, LobbyActivity.class);
                    i.putExtra(getString(R.string.host), true);
                    startActivity(i);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        VolleyRequestQueue.getInstance(this).addToRequestQueue(gameRequest);

    }


    private void joinGame() {
        Intent i = new Intent(StartMenuActivity.this, GameListActivity.class);
        startActivity(i);
    }



    private void saveGameId(long gameId) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).edit();
        editor.putLong(getString(R.string.gameId), gameId);
        editor.commit();
    }
}
