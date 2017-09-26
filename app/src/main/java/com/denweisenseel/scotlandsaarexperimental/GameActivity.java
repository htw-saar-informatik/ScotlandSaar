package com.denweisenseel.scotlandsaarexperimental;

import android.*;
import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.denweisenseel.scotlandsaarexperimental.adapter.BottomBarAdapter;
import com.denweisenseel.scotlandsaarexperimental.api.RequestBuilder;
import com.denweisenseel.scotlandsaarexperimental.customView.CustomViewPager;
import com.denweisenseel.scotlandsaarexperimental.data.ChatDataParcelable;
import com.denweisenseel.scotlandsaarexperimental.data.GameModel;
import com.denweisenseel.scotlandsaarexperimental.data.Graph;
import com.denweisenseel.scotlandsaarexperimental.data.Player;
import com.denweisenseel.scotlandsaarexperimental.dialogFragments.QuitGameFragment;
import com.denweisenseel.scotlandsaarexperimental.api.VolleyRequestQueue;
import com.denweisenseel.scotlandsaarexperimental.services.GameLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//TODO Cleanup those interfaces. Create and pass them to the fragment or whatever they belong to

public class GameActivity extends AppCompatActivity implements ChatFragment.ChatFragmentInteractionListener,
        OnMapReadyCallback, DashboardFragment.DashboardInteractionListener,
        QuitGameFragment.OnFragmentInteractionListener, GameLocationListener.GPSCallbackInterface {


    private static final int NOTIFICATION_ID = 001;
    CustomViewPager pager;
    //CHAT
    private ChatFragment chatFragment;
    private BroadcastReceiver chatMessageReceiver;
    private ArrayList<ChatDataParcelable> chatList;
    boolean isHost = false;

    String gameId;
    int unreadNotficationCounter = 0;

    private SupportMapFragment mapFragment;
    private BroadcastReceiver gameStateReceiver;
    private DashboardFragment dashboardFragment;

    GameModel gameModel = new GameModel();

    //GPS
    LocationListener locationListener;
    LocationManager locationManager;


    private String TAG = "GameActivity";
    private GoogleMap map;

    private Graph graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        locationListener = new GameLocationListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isHost = getIntent().getBooleanExtra(getString(R.string.host), false);
        gameId = getIntent().getStringExtra(getString(R.string.gameId));

        pager = (CustomViewPager) findViewById(R.id.viewpager);
        pager.setPagingEnabled(false);

        BottomBarAdapter bottomBarAdapter = new BottomBarAdapter(getSupportFragmentManager());


        mapFragment = MapFragment.newInstance();
        mapFragment.getMapAsync(this);
        bottomBarAdapter.addFragments(mapFragment);

        chatFragment = ChatFragment.newInstance(gameId);
        bottomBarAdapter.addFragments(chatFragment);

        dashboardFragment = DashboardFragment.newInstance("null", "null");
        bottomBarAdapter.addFragments(dashboardFragment);


        pager.setAdapter(bottomBarAdapter);

        pager.setCurrentItem(1);
        pager.setOffscreenPageLimit(3);


        final AHBottomNavigation navigation = (AHBottomNavigation) findViewById(R.id.navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.game_map, R.drawable.ic_home_black_24dp, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.game_chat, R.drawable.ic_notifications_black_24dp, R.color.color_tab_1);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.game_dashboard, R.drawable.ic_dashboard_black_24dp, R.color.color_tab_1);

        navigation.addItem(item1);
        navigation.addItem(item2);
        navigation.addItem(item3);

        navigation.setCurrentItem(1);


        // Set listeners
        navigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        if (pager.isEnabled()) {
                            pager.setCurrentItem(0);
                        } else {
                            Toast.makeText(GameActivity.this, "Game hasnt started yet", Toast.LENGTH_SHORT).show();
                            pager.setCurrentItem(0);
                        }
                        break;
                    case 1:
                        pager.setCurrentItem(1);
                        navigation.setNotification(new AHNotification(), 1);
                        unreadNotficationCounter = 0;
                        break;
                    case 2:
                        pager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        navigation.disableItemAtPosition(0);

//TODO move this to own method!
        chatList = new ArrayList<ChatDataParcelable>();
        chatMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getString(R.string.LOBBY_PLAYER_JOIN))) {
                    ArrayList<String> argList = intent.getStringArrayListExtra(getString(R.string.BROADCAST_DATA));

                    String playerName = argList.get(0);
                    ChatDataParcelable chatMessage = new ChatDataParcelable(playerName, "joined the lobby", new SimpleDateFormat("HH.mm").format(new Date()));
                    sendToChatFragment(chatMessage);
                    chatList.add(chatMessage);

                } else if (intent.getAction().equals(getString(R.string.LOBBY_PLAYER_MESSAGE))) {
                    ArrayList<String> argList = intent.getStringArrayListExtra(getString(R.string.BROADCAST_DATA));
                    ChatDataParcelable chatMessage = new ChatDataParcelable(argList.get(0), argList.get(1), argList.get(2));
                    chatList.add(chatMessage);
                    sendToChatFragment(chatMessage);
                    if (pager.getCurrentItem() != 1) {
                        unreadNotficationCounter++;
                        AHNotification notification = new AHNotification.Builder()
                                .setText(String.valueOf(unreadNotficationCounter))
                                .setBackgroundColor(ContextCompat.getColor(GameActivity.this, R.color.colorBottomNavigationNotification))
                                .setTextColor(ContextCompat.getColor(GameActivity.this, R.color.colorBottomNavigationDisable))
                                .build();
                        navigation.setNotification(notification, 1);
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(chatMessageReceiver, new IntentFilter(getString(R.string.LOBBY_PLAYER_JOIN)));
        LocalBroadcastManager.getInstance(this).registerReceiver(chatMessageReceiver, new IntentFilter(getString(R.string.LOBBY_PLAYER_MESSAGE)));
        ////UP TO HERE

        gameStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getString(R.string.LOBBY_GAME_START))) {
                    String args = intent.getStringExtra(getString(R.string.BROADCAST_DATA));
                    try {
                        populateGameModel(args);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    navigation.enableItemAtPosition(0);
                } else if (intent.getAction().equals(getString(R.string.GAME_TURN_START_X))) {
                    setNotification("Mister X ist an der Reihe!");
                } else if (intent.getAction().equals(getString(R.string.TURN_START_PLAYER))) {
                    setNotification("Spieler sind an der Reihe!");
                } else if (intent.getAction().equals(getString(R.string.GAME_POSITION_REACHED))) {
                    int playerId = intent.getIntExtra("playerId", -1);
                    int boardPosition = intent.getIntExtra("boardPosition", -1);
                    Log.v(TAG, "Updating playerPosition");

                    try {
                        gameModel.getPlayerById(playerId).getMarker().setCenter(graph.getNodeById(boardPosition).getPosition());
                        Log.v(TAG, "Placed "+gameModel.getPlayerById(playerId).getName()+" on " + boardPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(intent.getAction().equals(getString(R.string.GAME_REVEAL_X))) {
                    int misterXPos = intent.getIntExtra("boardPosition", -1);

                    setMisterXMarker(misterXPos);
                } else if (intent.getAction().equals(getString(R.string.GAME_WON))){
                    sendToChatFragment(new ChatDataParcelable("System", "Player WON", "Now"));
                    GameEndedFragment newFragment = new GameEndedFragment();
                    newFragment.setMessage("Players won");
                    FragmentManager fm = getFragmentManager();
                    newFragment.show(fm, "game_ended");
                }

                else if (intent.getAction().equals(getString(R.string.GAME_LOST))){
                    sendToChatFragment(new ChatDataParcelable("System", "Mr. X WON", "Now"));
                    GameEndedFragment newFragment = new GameEndedFragment();
                    newFragment.setMessage("Mr. X won");
                    FragmentManager fm = getFragmentManager();
                    newFragment.show(fm, "game_ended");
                }
            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.LOBBY_GAME_START)));
        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.GAME_TURN_START_X)));
        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.TURN_START_PLAYER)));
        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.GAME_POSITION_REACHED)));
        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.GAME_REVEAL_X)));

        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.GAME_WON)));
        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.GAME_LOST)));



        int id = getSharedPreferences(getString(R.string.gameData), Context.MODE_PRIVATE).getInt(getString(R.string.playerId), -1);
        Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

    public void requestQuitGameDialog() {
        FragmentManager fm = getFragmentManager();
        QuitGameFragment editGamenameDialogFragment = QuitGameFragment.newInstance();
        editGamenameDialogFragment.show(fm, "QuitGameDialog");
    }

    private void sendToChatFragment(ChatDataParcelable chatMessage) {
        chatFragment.sendMessage(chatMessage);
    }


    @Override
    public void retrieveChatMessages() {
        if (!getIntent().getBooleanExtra(getString(R.string.host), false)) {
            ArrayList<String> playersInLobby = getIntent().getStringArrayListExtra("players");
            ArrayList<ChatDataParcelable> chatMessages = new ArrayList<ChatDataParcelable>();

            for (int i = 0; i < playersInLobby.size(); i++) {
                chatMessages.add(new ChatDataParcelable("System", playersInLobby.get(i) + " is in the lobby", new SimpleDateFormat("HH.mm").format(new Date())));
            }

            for (int i = 0; i < chatMessages.size(); i++) {
                sendToChatFragment(chatMessages.get(i));
            }
            Log.i(TAG, "There were already "+ playersInLobby.size() + "players in the lobby. Printed those to the chat.");
        }
    }

    @Override
    public void onYesButtonClicked() {
        //TODO Actually kick the player out of the game.
        finish();
    }

    public void onMapReady(final GoogleMap googleMap) {
        //TODO Setup map constraints - Those var values should be finals somewhere! Please redo (5 min)
        final LatLng UPPER_BOUND = new LatLng(49.234012, 6.995120);
        final LatLng LOWER_BOUND = new LatLng(49.237760, 7.006214);
        final LatLng CAMERA_POSITION = new LatLng(49.236127, 7.000402);
        final float ZOOM_FACTOR = 16.0f;

        map = googleMap;

        LatLngBounds bounds = new LatLngBounds(UPPER_BOUND, LOWER_BOUND);
        googleMap.setLatLngBoundsForCameraTarget(bounds);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(CAMERA_POSITION));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_FACTOR));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    if (marker.getTag() != null) makeMove((Integer) marker.getTag());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                float minZoom = ZOOM_FACTOR;
                CameraPosition cameraPosition = googleMap.getCameraPosition();
                if (cameraPosition.zoom < minZoom) {
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
                }
            }
        });
    }


    @Override
    public void onStartGame() {
        if (isHost) {
            Log.i(TAG, "Trying to start game!");

            String firebaseToken = FirebaseInstanceId.getInstance().getToken();
            String[] args = {gameId, firebaseToken};

            JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.buildRequestUrl(RequestBuilder.START_GAME, args), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.i(TAG, "Successfully started game!");
                    } catch (Exception e) {
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
        } else {
            Toast.makeText(this, R.string.ERROR_START, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMakeMove() {
        Log.i(TAG, "Test drückt");
        listenForUpdates();
    }

    private void populateGameModel(String input) throws JSONException {

        JSONObject json = new JSONObject(input);
        gameModel.setMisterX(json.getBoolean("isMisterX"));

        JSONObject gameState = json.getJSONObject("gameState");
        JSONArray playerArray = gameState.getJSONArray("playerList");

        for (int i = 0; i < playerArray.length(); i++) {
            JSONObject player = playerArray.getJSONObject(i);
            Player p = new Player();
            p.setName(player.getString("name"));
            p.setBoardPosition(player.getInt("boardPosition"));
            p.setId(player.getInt("id"));
            p.setMisterX(player.getBoolean("misterX"));
            gameModel.addPlayer(p);
        }

        placePlayersOnMap();

    }

    private void placePlayersOnMap() {
        graph = new Graph();
        graph.initialize(this, R.raw.graph);

        gameModel.setGraph(graph);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for (Graph.Node n : graph.getNodes()) {
                    Marker m = googleMap.addMarker(new MarkerOptions().position(n.getPosition()).title(String.valueOf(n.getId())));
                    m.setTag(n.getId());
                    gameModel.addMarker(n.getId(), m);
                }

                for (Graph.Node n : graph.getNodes()) {
                    for (Integer i : n.getNeighbours()) {
                        googleMap.addPolyline(new PolylineOptions().add(n.getPosition(), gameModel.getMarker(i).getPosition()));
                    }
                }

                for (Player p : gameModel.getPlayerList()) {

                    int id = gameModel.getPlayerList().indexOf(p);

                    int color;

                    switch(id) {
                        case 0: color = Color.BLACK;
                            break;
                        case 1: color = Color.GREEN;
                            break;
                        case 2:
                            color = Color.MAGENTA;
                            break;
                        case 3:
                            color = Color.RED;
                            break;
                        case 4:
                            color = Color.YELLOW;
                            break;
                        case 5:
                            color = Color.BLUE;
                            break;
                        default:
                            color = Color.LTGRAY;
                    }
                    if(p.isMisterX() && !gameModel.isMisterX()) {
                        Circle c = googleMap.addCircle(new CircleOptions()
                                .center(gameModel.getMarker(p.getBoardPosition()).getPosition())
                                .fillColor(color).zIndex(5f).radius(20).visible(false));
                        p.setMarker(c);
                    } else {
                        Circle c = googleMap.addCircle(new CircleOptions()
                                .center(gameModel.getMarker(p.getBoardPosition()).getPosition())
                                .fillColor(color).zIndex(5f).radius(20));
                        Log.i(TAG, "Added marker for:" + p.getName() + "to node Id " + p.getBoardPosition() + ". Location is:" + gameModel.getMarker(p.getBoardPosition()).getPosition());
                        p.setMarker(c);
                    }
                }
            }
        });
    }

    private boolean makeMove(int targetPosition) {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

        final String[] requestARGS = new String[]{gameId, firebaseToken, String.valueOf(targetPosition)};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.
                buildRequestUrl(RequestBuilder.MAKE_MOVE, requestARGS), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, response.toString());
                try {
                    if(response.getBoolean("success") && gameModel.isMisterX()) {
                        // gameModel.getPlayerById(gameModel.getId()).getMarker().setCenter(graph.getNodeById(response.getInt("positionId")).getPosition());
                    }
                } catch (Exception e) {
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
        return true;
    }

    public void setNotification(String s) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getString(R.string.SCOTLANDSAAR))
                .setContentText(s);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, builder.build());
    }


    @Override
    public void gpsDeactivated(String s) {
        //TODO Show dialog that GPS is needed. Ask for activation. If declined, quit game
    }

    @Override
    public void updatePosition(Location location) {

        Log.v(TAG, "POSITION HAS CHANGED AND IS CLOOOOSE" + location);

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        String[] requestARGS = new String[]{gameId, firebaseToken, latitude, longitude};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.
                buildRequestUrl(RequestBuilder.UPDATE_POSITION, requestARGS), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });

        VolleyRequestQueue.getInstance(this).addToRequestQueue(gameRequest);
    }

    public void listenForUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.CESS_COARSE_LOCATION}, 1001);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1001);

            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                   Log.i(TAG, "GOT GPS PERMISSIONS");
                }
            }
        }
    }

    public void stopListening() {
        locationManager.removeUpdates(locationListener);
    }

    public void setMisterXMarker(final int misterXMarker) {
        if(gameModel.getMisterXCircle() == null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Circle c = googleMap.addCircle(new CircleOptions()
                            .center(gameModel.getGraph().getNodeById(misterXMarker).getPosition())
                            .fillColor(Color.CYAN).zIndex(5f).radius(20));
                    gameModel.setMisterXCircle(c);
                }
            });
        } else {
            gameModel.getMisterXCircle().setCenter(gameModel.getGraph().getNodeById(misterXMarker).getPosition());
        }
    }
}
