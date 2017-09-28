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
import com.denweisenseel.scotlandsaarexperimental.api.VolleyRequestQueue;
import com.denweisenseel.scotlandsaarexperimental.dialogFragments.GamenameInputFragment;
import com.denweisenseel.scotlandsaarexperimental.dialogFragments.UsernameInputFragment;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class StartMenuActivity extends AppCompatActivity implements GamenameInputFragment.OnGamenameInputListener {

    private final String TAG = "StartMenu";
    private String gameName = "Null";
    private View progressOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.setVisibility(View.GONE);

        Button startGame = (Button) findViewById(R.id.STARTMENU_StartGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGameName();
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

        //TODO Check for Username, Check for Permissions (GPS, FileSystem), Check for exisiting variables, delete them if unnecessary


        if(sharedPref.contains(getString(R.string.username))) {
            Toast.makeText(this, "Hallo " +sharedPref.getString(getString(R.string.username), "NULL"),Toast.LENGTH_SHORT).show();;
        } else {
            FragmentManager fm = getFragmentManager();
            UsernameInputFragment editNameDialogFragment = UsernameInputFragment.newInstance();
            editNameDialogFragment.show(fm, "UsernameDialog");
        }

        if(sharedPref.contains(getString(R.string.gameId))) {
            sharedPref.edit().remove(getString(R.string.gameId));
            //TODO check if game is still alive, rejoin! @Issue 001 (10 Hours)
        }

        //TODO move those checks into functions, -> checkUsername() -> Callback to checkPermissions -> Callback to var checks!

        super.onStart();
    }

    /**
     * Start Dialog for User to be able to input their Gamename
     */
    public void requestGameName() {
        FragmentManager fm = getFragmentManager();
        GamenameInputFragment editGamenameDialogFragment = GamenameInputFragment.newInstance();
        editGamenameDialogFragment.show(fm, "GamenameDialog");
    }

    private void startGame() {

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String username = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).
                getString(getString(R.string.username),"NULL");

        String[] requestARGS = {firebaseToken,username,gameName};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.
                buildRequestUrl(RequestBuilder.CREATE_GAME, requestARGS),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String gameId = response.getString(getString(R.string.protocol_gameId));
                    int id = response.getInt(getString(R.string.playerId));
                    savePlayerId(id);
                    Log.i(TAG, "Game created! ID: "+gameId);
                    Intent i = new Intent(StartMenuActivity.this, GameActivity.class);
                    i.putExtra(getString(R.string.host), true);
                    i.putExtra(getString(R.string.gameId), gameId);
                    startActivity(i);
                    finish();

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

    private void savePlayerId(int id) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.playerId), id);
        editor.commit();
    }


    private void joinGame() {
        Intent i = new Intent(StartMenuActivity.this, GameListActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void saveGameId(long gameId) {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).edit();
        editor.putLong(getString(R.string.gameId), gameId);
        editor.commit();
    }

    /**
     * "Triggered" by requestGamename() and then starts game AFTER Gamename input
     * @param string the entered text by the user for the gamename
     */
    @Override
    public void onInput(String string) {
        gameName = string;
        progressOverlay.setVisibility(View.VISIBLE);
        startGame();
    }
}
