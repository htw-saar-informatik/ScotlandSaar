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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denweisenseel.scotlandsaarexperimental.adapter.GameListModelAdapter;
import com.denweisenseel.scotlandsaarexperimental.api.RequestBuilder;
import com.denweisenseel.scotlandsaarexperimental.data.GameListInfoParcelable;
import com.denweisenseel.scotlandsaarexperimental.api.VolleyRequestQueue;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {


    private ArrayList<GameListInfoParcelable> gameList = new ArrayList<>();
    private GameListModelAdapter gameListAdapter;
    private final String TAG = "GAMELIST";
    View progressOverlay;
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
        progressOverlay = findViewById(R.id.progress_overlay);
        progressOverlay.setVisibility(View.VISIBLE);
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        final String gameIdentiier = String.valueOf(gameId);
        String playerName = getSharedPreferences(getString(R.string.gameData),Context.MODE_PRIVATE).getString(getString(R.string.username),"NULL");

        String args[] = {gameIdentiier,firebaseToken,playerName};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.buildRequestUrl(RequestBuilder.JOIN_GAME, args ),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){ //response.has(getString(R.string.protocol_success))) {
                        progressOverlay.setVisibility(View.GONE);
                        Intent i = new Intent(GameListActivity.this, GameActivity.class);
                        ArrayList<String> players = new ArrayList<String>();
                        i.putExtra(getString(R.string.gameId), gameIdentiier);
                        int playerId = response.getInt(getString(R.string.playerId));
                        savePlayerId(playerId);
                        for (int x = 0; x < response.getJSONArray("playerInLobby").length(); x++){
                            players.add(response.getJSONArray("playerInLobby").getJSONObject(x).getString("name"));
                        }
                        i.putExtra(getString(R.string.host),false);
                        i.putExtra("players", players);
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    progressOverlay.setVisibility(View.GONE);
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

    private void fetchGameList() {
        String noArgs[] = {}; //TODO Refactor this! its silly to pass a empty string array (1 hour)
        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.GET, RequestBuilder.buildRequestUrl(RequestBuilder.GET_GAMELIST, noArgs ),null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray games = response.getJSONArray("items");
                    gameList.clear();
                    for(int i = 0; i <games.length();i++) {
                        String hostName = games.getJSONObject(i).getString(getString(R.string.host));
                        String gameName = games.getJSONObject(i).getString(getString(R.string.GAME_LIST_ITEM_GAMENAME));
                        long gameId     = Long.valueOf(games.getJSONObject(i).getString(getString(R.string.GAME_LIST_ITEM_GAMEID)));
                        int playerCount = Integer.valueOf(games.getJSONObject(i).getString(getString(R.string.GAME_LIST_ITEM_PLAYERCOUNT)));
                        GameListInfoParcelable gameListItem = new GameListInfoParcelable(hostName,gameName,playerCount,0,gameId);
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
