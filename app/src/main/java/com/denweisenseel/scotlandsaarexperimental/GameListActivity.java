package com.denweisenseel.scotlandsaarexperimental;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denweisenseel.scotlandsaarexperimental.adapter.GameListModelAdapter;
import com.denweisenseel.scotlandsaarexperimental.api.RequestBuilder;
import com.denweisenseel.scotlandsaarexperimental.data.GameListInfoParcelable;
import com.denweisenseel.scotlandsaarexperimental.data.VolleyRequestQueue;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {


    private ArrayList<GameListInfoParcelable> gameList = new ArrayList<>();
    private GameListModelAdapter gameListAdapter;
    private final String TAG = "GAMELIST";

    SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        gameListAdapter = new GameListModelAdapter(this, gameList);
        final ListView lv = (ListView) findViewById(R.id.GAMELOBBY_list);

        srl = (SwipeRefreshLayout) findViewById(R.id.GAMELOBBY_swipeRefresh);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchGameList();
            }
        });

        lv.setAdapter(gameListAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                joinGame(gameList.get(i).getGameId());
            }
        });

        fetchGameList();

    }

    private void joinGame(final long gameId) {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String gameIdentiier = String.valueOf(gameId);
        String playerName = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).getString(getString(R.string.username),"NULL");

        String args[] = {gameIdentiier,firebaseToken,playerName};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.buildRequestUrl(RequestBuilder.JOIN_GAME, args ),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has(getString(R.string.protocol_success))) {
                        if(response.getBoolean(getString(R.string.protocol_success))) {
                            Intent i = new Intent(GameListActivity.this, LobbyActivity.class);

                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).edit();
                            editor.putLong(getString(R.string.gameId), Long.valueOf(gameId));
                            editor.commit();
                            //TODO Add information: Whos in the lobby? ( 3 hours )

                            startActivity(i);
                            Log.i(TAG, "Join GameLobby");
                        }
                    }
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


    private void fetchGameList() {
        String noArgs[] = {}; //TODO Refactor this! its silly to pass a empty string array (1 hour)
        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.GET, RequestBuilder.buildRequestUrl(RequestBuilder.GET_GAMELIST, noArgs ),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray games = response.getJSONArray("items");
                    gameList.clear();
                    for(int i = 0; i <games.length();i++) {
                        String gameName = games.getJSONObject(i).getString(getString(R.string.GAME_LIST_ITEM_GAMENAME));
                        long gameId   = Long.valueOf(games.getJSONObject(i).getString(getString(R.string.GAME_LIST_ITEM_GAMEID)));
                        GameListInfoParcelable gameListItem = new GameListInfoParcelable(gameName,0,0,gameId); //TODO Implement MaxPlayerSize if applicable, playerCount (1 hour)
                        gameList.add(gameListItem);
                    }
                    gameListAdapter.notifyDataSetChanged();
                    srl.setRefreshing(false);
                    Log.i(TAG, "Fetched gameList with "+gameList.size() + " elements.");
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

}