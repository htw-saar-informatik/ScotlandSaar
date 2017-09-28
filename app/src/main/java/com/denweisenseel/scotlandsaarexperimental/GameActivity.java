package com.denweisenseel.scotlandsaarexperimental;

import android.*;
import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.denweisenseel.scotlandsaarexperimental.util.AndroidUtil;
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
        GameLocationListener.GPSCallbackInterface {

    //LAYOUT
    private CustomViewPager pager;
    private SupportMapFragment mapFragment;
    private DashboardFragment dashboardFragment;
    private ChatFragment chatFragment;

    //CHAT
    private int unreadNotficationCounter = 0;
    private AHBottomNavigation navigation;

    //GAME
    private BroadcastReceiver gameStateReceiver;
    private boolean isHost = false;
    private String gameId;
    private GoogleMap map;
    private Graph graph;
    private GameModel gameModel;

    //GPS
    private GameLocationListener locationListener;
    private LocationManager locationManager;

    //CONSTANTS for logging purposes
    private String TAG = "GameActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initLayout();
        initGamePushReceivers();

        //GPS Init
        locationListener = new GameLocationListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Gamemodel Init
        gameModel = new GameModel();
        initGraph();
    }

    private void initLayout() {
        //Get some vars out of intent
        isHost = getIntent().getBooleanExtra(getString(R.string.host), false);
        gameId = getIntent().getStringExtra(getString(R.string.gameId));

        //Styling the bottom navigation
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

        navigation = (AHBottomNavigation) findViewById(R.id.navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.game_map, R.drawable.ic_home_black_24dp, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.game_chat, R.drawable.ic_notifications_black_24dp, R.color.color_tab_1);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.game_dashboard, R.drawable.ic_dashboard_black_24dp, R.color.color_tab_1);
        navigation.addItem(item1);
        navigation.addItem(item2);
        navigation.addItem(item3);

        navigation.setCurrentItem(1);

        // Set clicklisteners
        navigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position) {
                    case 0:
                        if (pager.isEnabled()) {
                            AndroidUtil.hideKeyboard(GameActivity.this);
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
                        AndroidUtil.hideKeyboard(GameActivity.this);
                        pager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        // Disable map tab, because game hasnt started yet
        navigation.disableItemAtPosition(0);
    }

    private void initGraph() {
        graph = new Graph();
        graph.initialize(this, R.raw.graph);
        gameModel.setGraph(graph);
    }

    public void requestQuitGameDialog() {
        FragmentManager fm = getFragmentManager();
        QuitGameFragment editGamenameDialogFragment = QuitGameFragment.newInstance();
        editGamenameDialogFragment.show(fm, "QuitGameDialog");
    }

    private void sendToChatFragment(ChatDataParcelable chatMessage) {
        chatFragment.sendMessage(chatMessage);
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

            JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST,
                    RequestBuilder.buildRequestUrl(RequestBuilder.START_GAME, args), null, new Response.Listener<JSONObject>() {
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
        listenForUpdates();
    }

    private void populateGameModel(String input) throws JSONException {
        //TODO Those constants should be moved to.. constants
        JSONObject json = new JSONObject(input);
        gameModel.setMisterX(json.getBoolean("isMisterX"));
        gameModel.setId(json.getInt("playerId"));

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

        placeDataOnMap();
    }

    private void placeDataOnMap() {
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
                        case 1: color = Color.parseColor("#79FF0D");
                            break;
                        case 2:
                            color = Color.parseColor("#FF6B62");
                            break;
                        case 3:
                            color = Color.parseColor("#804EE8");
                            break;
                        case 4:
                            color = Color.parseColor("#55F9FF");
                            break;
                        case 5:
                            color = Color.parseColor("#9CFF6F");
                            break;
                        default:
                            color = Color.LTGRAY;
                    }

                    if(id == gameModel.getId()) navigation.setDefaultBackgroundColor(color);

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
                    if(response.getBoolean("success") && response.has("data")) {
                        String responseData = response.getString("data");
                        if(responseData.equals("IS_MOVING")) {
                            int position = response.getInt("positionId");
                            LatLng targetLocation = gameModel.getGraph().getNodeById(position).getPosition();
                            locationListener.setTargetLocation(targetLocation);
                            listenForUpdates();
                        }
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

    //GPS STUFF IS HERE!

    @Override
    public void gpsDeactivated(String s) {
        //TODO Show dialog that GPS is needed. Ask for activation. If declined, quit game
    }

    @Override
    public void updatePosition(Location location) {
        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());

        String[] requestARGS = new String[]{gameId, firebaseToken, latitude, longitude};

        JsonObjectRequest gameRequest = new JsonObjectRequest(Request.Method.POST, RequestBuilder.
                buildRequestUrl(RequestBuilder.UPDATE_POSITION, requestARGS), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, response.toString());
                stopListening();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString() + "PUT");
            }
        });

        VolleyRequestQueue.getInstance(this).addToRequestQueue(gameRequest);
    }

    @Override
    public void updateDistance(Float distance) {
        Log.v(TAG, "Distance will now be Updated");
        dashboardFragment.updateDistance(distance);
    }

    public void listenForUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1001);
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                }
            }
        }
    }

    public void stopListening() {
        locationManager.removeUpdates(locationListener);
    }
    //GPS DONE

    //This is the reveal marker.
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

    @Override
    public void onBackPressed() {
        requestQuitGameDialog();
    }

    private void initGamePushReceivers() {
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
                    dashboardFragment.setPlayerType(gameModel.isMisterX());
                    dashboardFragment.showGameState();
                } else if (intent.getAction().equals(getString(R.string.GAME_TURN_START_X))) {
                    dashboardFragment.setGameState(getString(R.string.GAME_TURN_START_X));
                } else if (intent.getAction().equals(getString(R.string.TURN_START_PLAYER))) {
                    dashboardFragment.setGameState(getString(R.string.TURN_START_PLAYER));
                } else if(intent.getAction().equals(getString(R.string.GAME_POSITION_SELECTED))) {
                    int boardPosition = intent.getIntExtra("boardPosition", -1);
                    int playerId = intent.getIntExtra("playerId", -1);
                    gameModel.updatePlayerSelection(playerId, boardPosition);
                } else if (intent.getAction().equals(getString(R.string.GAME_POSITION_REACHED))) {
                    int playerId = intent.getIntExtra("playerId", -1);
                    int boardPosition = intent.getIntExtra("boardPosition", -1);
                    Log.v(TAG, "Updating playerPosition");
                    gameModel.updatePlayerReachedMarker(playerId, boardPosition);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(gameStateReceiver, new IntentFilter(getString(R.string.GAME_POSITION_SELECTED)));
    }

    @Override
    public void onMessageReceived() {
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