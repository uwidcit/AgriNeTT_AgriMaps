package dcit.uwi.agrimaps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlFeature;
import org.osmdroid.bonuspack.kml.KmlFolder;
import org.osmdroid.bonuspack.kml.KmlLineString;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.bonuspack.kml.KmlPoint;
import org.osmdroid.bonuspack.kml.KmlPolygon;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapActivity extends ActionBarActivity implements MapEventsReceiver {
    //Drawer Variables
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    //Title of Action Bar
    private CharSequence mTitle;
    private String[] viewOptions;
    private String[] recommendOptions;

    //Map variables for manipulating the map objects
    private MapView mapView;
    private MapController mapController;
    private LocationManager lm = null;

    //Host url to connect to get the data
    //private String url = "http://10.0.2.2:8000";
    private String url = "http://mcc.lab.tt:8000";
    //private String url = "http://localhost:8000";
    private GeoPoint centre = null;
    private Location location = null;
    private Location previousLocation = null;
    private GeoPoint defaultLocation = new GeoPoint(10.365243, -61.33152);

    //Variable used to store where in array of internalView Options that the user currently is
    //Note that this changes based on mode where on the recommender mode it takes the value of
    //countProfileOptions+1
    private int currPos = 0;

    private int requestSize = 1000;
    private int currZoomLevel = 15;

    //map event receiver
    private MapEventsOverlay mapOverlay = null;
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location locationLocal) {
            // Set current location
            centre = new GeoPoint(locationLocal.getLatitude(), locationLocal.getLongitude());
            requestUrl = centre.getLongitude() + "&" + centre.getLatitude();
            Log.i("Location Listener", "Location is:" + centre);
            location = locationLocal;
            if (splashScreen.isShowing()) {
                splashText.setText("Loading Data");
            }
            useLocation();
            //    if(progressDialog.isShowing()) {
            //        progressDialog.dismiss();
            //splashScreen.dismiss();
            //    }

            //remove overlay from the set
            //remove the option from the 'saved' overlays.
            //retrieve current data
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
    private ProgressDialog progressDialog;

    //Kml document to store overlays to be placed on the map
    private KmlDocument kmlDocument;

    private Context context = this;

    //Strings to be used for request logic to the server
    private String option = "default";
    private String requestUrl = "";

    private static final int RESULT_SETTINGS = 1;

    //Internal view options, upon interaction with the user this array is used to change the overlay of the map by sending
    //a request to the host server for data
    private String[] internalViewOptions = {"rivers", "soilCapability", "roads", "contours", "rainfall",
            "landUse", "parcels","recommendCassava","recommendOnion","recommendCocoa",
            "recommendLettuce", "recommendTomato", "recommendBreadfruit", "recommendPapaya", "recommendCucumber",
            "recommendPigeonPea", "recommendCorn", "recommendSweetPepper", "recommendPineapple", "recommendCitrus", "recommendSweetPotato",
            "recommendAll"};

    //A count of the number of crops which this application shows to the user
    int countRecommendCrops = 14;
    //A count of the number of profile options presented to the user
    int countProfileOptions = 7;

    //JSON Object to hold the specifications for each of the downloaded crops to be used
    private JSONObject cropSpecifications;

    // ArrayLists to input the overlays
    private ArrayList<OverlayItem> overlayItemArray;
    private ArrayList<FolderOverlay> viewOverlays;
    private ArrayList<String> overlaySet;

    //Legend Button + Layout
    private Button viewLegend;
    private Button viewLandMarks;
    private TableLayout viewRoadCapability;
    private View landmarkLayout;

    private Button dismissButton;

    //Starting splashscreen
    private Dialog splashScreen;
    private TextView splashText;

    private double closestRiver;
    private double closestRoad;

    private Toast currentToast;

    //Action bar menu
    private Menu myMenu;

    //appMode=true represents the application being in profile mode while false represents the
    //recommender mode
    private boolean appMode = true;

    //This hashmap is used to store descriptions which are being parsed for the recommender
    private HashMap<String, String> hash = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // On startup render activity_map.xml
        super.onCreate(savedInstanceState);
        Log.v("state", "++ ON CREATE ++");

        //Initial Activity is set to a loading screen
        splashScreen = new Dialog(this, android.R.style.Theme_Holo_NoActionBar_Fullscreen);
        splashScreen.setCancelable(false);
        splashScreen.setCanceledOnTouchOutside(false);
        splashScreen.setContentView(R.layout.splashscreen);

        splashText = (TextView) splashScreen.findViewById(R.id.app_loading);
        splashText.setText("Finding Location");
        splashScreen.show();
        currPos = 1;
        setContentView(R.layout.activity_map);

        //Initialize Map settings
        mapInit(savedInstanceState);

        //Initialize Drawer set
        drawerInit(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("state", "++ ON START ++");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("state", "+ ON RESUME +");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("state", "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("state", "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("state", "- ON DESTROY -");
    }

    //Function to initialize the map components of the application
    private void mapInit(Bundle savedInstanceState) {
        //AsyncLocation locationProcess = new AsyncLocation();
        //locationProcess.execute();
        // Start to initialize the map settings
        mapView = (MapView) this.findViewById(R.id.map_frame);
        mapController = (MapController) this.mapView.getController();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        requestSize = Integer.parseInt(sharedPrefs.getString("radius", "1000"));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        // Set mapview settings
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setTilesScaledToDpi(true);

        // Set the current zoom to max
        //mapController.setZoom(mapView.getMaxZoomLevel());
        mapView.setMaxZoomLevel(16);

        // Initialize array for usage with markers
        overlayItemArray = new ArrayList<OverlayItem>();
        overlaySet = new ArrayList<String>();
        viewOverlays = new ArrayList<FolderOverlay>();


        mapOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapOverlay);

        //initialize refresh button on the map
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(location!=null) {
                    previousLocation = location;
                }

                if (checkLocation() == true) {
                    if (checkInternet() == true) {
                        //If there is location finding enabled and there is internet
                        //then the current Location of the user is found
                        showToast("Determining Current Location");
                        getLocation();
                    }else{
                        showToast("No Internet available to fetch data for new point.");
                    }
                } else {// if no location services are available then alert user
                    Log.e("Location", "Check GPS failed");
                    AlertDialog noLocation = new AlertDialog.Builder(context).create();
                    noLocation.setTitle("Location");
                    noLocation.setCancelable(false);
                    noLocation.setMessage("Location settings are disabled, please enable location finding setting for full usage of application. For fast location" +
                            "  finding, enable location finding using Wifi or the Network Provider.");
                    noLocation.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // android.os.Process.killProcess(android.os.Process.myPid());
                            //centre = new GeoPoint(10.365243,-61.33152);
                            //requestUrl = centre.getLatitude() + "&" + centre.getLongitude();
                            Log.e("Location Listener", "Location is:" + centre);
                            //location = new Location("dummy");
                            //location.setLatitude(10.365243);
                            //location.setLongitude(-61.33152);
                            if (splashScreen.isShowing()) {
                                splashText.setText("Loading Data");
                            }
                            //useLocation();
                        }
                    });

                    noLocation.show();
                }
                //  t.setText("Location set");
                //  t.cancel();

            }
        });

        //Check if the location services for application is working
        if (checkLocation() && checkInternet()) {
            Log.i("Location", "Getting Location");
            location = getLocation();
        } else if(!checkLocation() && checkInternet()){// if no location services are available then alert user
            Log.e("Location", "Check GPS failed");
            //Show message alerting user
            AlertDialog noLocation = new AlertDialog.Builder(this).create();
            noLocation.setTitle("Location");
            noLocation.setCancelable(false);
            noLocation.setMessage("Location settings are disabled, you can still load data " +
                    "for points of interest on the map. Please enable location settings for full usage of application. For fast location" +
                    " finding, enable location finding using Wifi or the Network Provider.");
            noLocation.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // android.os.Process.killProcess(android.os.Process.myPid());
                    //centre = defaultLocation;
                    //requestUrl = centre.getLatitude() + "&" + centre.getLongitude();
                    //Log.i("Location Listener", "Location is:" + centre);
                    //location = new Location("dummy");
                    //location.setLatitude(10.365243);
                    //location.setLongitude(-61.33152);
                    if (splashScreen.isShowing()) {
                        splashScreen.dismiss();
                    }
                    mapController.setCenter(defaultLocation);
                    mapController.setZoom(8);
                    //useLocation();
                }
            });

            noLocation.show();
        }else if(!checkInternet()){
            AlertDialog noInternet = new AlertDialog.Builder(context).create();
            noInternet.setTitle("Connection Error");
            noInternet.setMessage("There is no internet available to use application. Please enable wifi or Mobile Data to use application.");
            noInternet.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Undefined for now
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });

            //  noLocation.setIcon(R.drawable.icon);
            noInternet.show();
        }

    }

    //Function to initialize the swipe/drawer part of the application
    private void drawerInit(Bundle savedInstanceState) {

        mTitle = getTitle();

        //Setup of drawer for usage by user
        viewOptions = getResources().getStringArray(R.array.view_array);
        recommendOptions  = getResources().getStringArray(R.array.recommender_crops);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, viewOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //Setup of the Legend Button
        viewLegend = (Button) findViewById(R.id.button_legend);
        viewLegend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                final Dialog yourDialog = new Dialog(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = null;
                //Legend button changes contents based on the current overlay being seen by the user.
                if (option.equalsIgnoreCase("soilCapability")) {
                    layout = inflater.inflate(R.layout.legend_soils, (ViewGroup) findViewById(R.id.legend_Soils));
                } else if (option.equalsIgnoreCase("rainfall")) {
                    layout = inflater.inflate(R.layout.legend_rainfall, (ViewGroup) findViewById(R.id.legend_rainfall));
                } else if (option.equalsIgnoreCase("landUse")){
                    layout = inflater.inflate(R.layout.legend_landuse, (ViewGroup) findViewById(R.id.legend_landUse));
                } else if (appMode==false){
                 //   layout = inflater.inflate(R.layout.legend_landuse, (ViewGroup) findViewById(R.id.legend_landUse));
                }

                yourDialog.setTitle("Legend");
                yourDialog.setContentView(layout);
                dismissButton = (Button) layout.findViewById(R.id.dismiss_button);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        yourDialog.dismiss();
                    }
                });
                yourDialog.show();
            }
        });

        //Landmarks button in the recommender is setup to generate new contents each time it is loaded
        viewLandMarks = (Button) findViewById(R.id.button_landmarks);
        viewLandMarks.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                try {
                    final Dialog yourDialog = new Dialog(context);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    landmarkLayout = inflater.inflate(R.layout.landmarks, (ViewGroup) findViewById(R.id.landmarks));
                    viewLandMarks = (Button) findViewById(R.id.button_landmarks);

                    yourDialog.setTitle("Landmarks");
                    yourDialog.setContentView(landmarkLayout);

                    TableLayout landmarkTable = (TableLayout) landmarkLayout.findViewById(R.id.tableLayout_landmarks);
                    TableRow tbrow;
                    TextView textV1;
                    TextView textV2;
                    landmarkTable.removeAllViewsInLayout();
                    tbrow = new TableRow(context);
                    textV1 = new TextView(context);
                    textV1.setText("Landmark");
                    textV1.setPadding(0, 0, 15, 5);
                    textV1.setTypeface(null, Typeface.BOLD);
                    tbrow.addView(textV1);
                    textV2 = new TextView(context);
                    textV2.setText("Closest Distance(Meters)");
                    textV2.setTypeface(null, Typeface.BOLD);
                    tbrow.addView(textV2);
                    landmarkTable.addView(tbrow);

                    tbrow = new TableRow(context);
                    textV1 = new TextView(context);
                    textV1.setText("River");
                    textV1.setPadding(0, 0, 15, 0);
                    tbrow.addView(textV1);
                    textV2 = new TextView(context);
                    textV2.setText((int) closestRiver + " m");
                    tbrow.addView(textV2);
                    landmarkTable.addView(tbrow);
                    tbrow = new TableRow(context);
                    textV1 = new TextView(context);
                    textV1.setText("Road");
                    textV1.setPadding(0, 0, 15, 0);
                    tbrow.addView(textV1);
                    textV2 = new TextView(context);
                    textV2.setText((int) closestRoad + " m");
                    tbrow.addView(textV2);
                    landmarkTable.addView(tbrow);

                    dismissButton = (Button) landmarkLayout.findViewById(R.id.dismiss_button);
                    dismissButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Perform action on click
                            yourDialog.dismiss();
                        }
                    });
                    yourDialog.show();
                } catch (Exception e) {
                    Log.e("Drawer Init", e + "");
                }
            }
        });

        viewRoadCapability = (TableLayout) findViewById(R.id.legendRoads);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        android.support.v7.widget.Toolbar tb = (android.support.v7.widget.Toolbar) findViewById(R.id.action_Toolbar);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                tb,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Views");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.openDrawer(Gravity.LEFT);
        if (savedInstanceState == null) {
            //  selectItem(0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        myMenu = menu;
        //menu.findItem(R.id.recommender_Change).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_setting).setVisible(!drawerOpen);
        menu.findItem(R.id.action_help).setVisible(!drawerOpen);
        menu.findItem(R.id.recommender_Change).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //Function which executes when an item in the action bar is signalled.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent i = new Intent(this, dcit.uwi.agrimaps.userPreferences.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            case R.id.recommender_Change:
                if (appMode) {
                    viewOptions = getResources().getStringArray(R.array.recommender_crops);
                    appMode = false;
                    setTitle("Recommender");
                    viewLandMarks.setVisibility(View.VISIBLE);
                    myMenu.findItem(R.id.recommender_Change).setIcon(R.drawable.ic_profile);
                    mapView.getOverlays().clear();
                    mapView.getOverlays().add(0, mapOverlay);
                    setCentreMarker();
                    mapView.invalidate();
                    currPos = -1;
                    showToast("Recommendation Mode");
                    InfoWindow.closeAllInfoWindowsOn(mapView);

                    if (viewRoadCapability.isShown()) {
                        viewRoadCapability.setVisibility(View.GONE);
                    }
                    if (viewLegend.isShown()) {
                        viewLegend.setVisibility(View.GONE);
                    }

                } else {
                    viewOptions = getResources().getStringArray(R.array.view_array);
                    appMode = true;
                    setTitle("Land Profiles");
                    myMenu.findItem(R.id.recommender_Change).setIcon(R.drawable.ic_recommender);
                    mapView.getOverlays().clear();
                    mapView.getOverlays().add(0, mapOverlay);
                    InfoWindow.closeAllInfoWindowsOn(mapView);
                    setCentreMarker();
                    mapView.invalidate();
                    currPos = -1;
                    if (viewLandMarks.isShown()) {
                        viewLandMarks.setVisibility(View.GONE);
                    }
                    showToast("Profile Mode");
                }
                mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, viewOptions));
                return true;
            case R.id.action_help:
                Log.i("Action Bar click", " help was clicked");
                final Dialog helpDialog = new Dialog(this);
                LayoutInflater helpInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View helpLayout = helpInflater.inflate(R.layout.help_layout, (ViewGroup) findViewById(R.id.help_dialog));
                helpDialog.setTitle("Help");
                helpDialog.setContentView(helpLayout);
                helpDialog.show();

                Button generalHelp = (Button) helpLayout.findViewById(R.id.button_generalHelp);
                generalHelp.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.help_definitions, (ViewGroup) findViewById(R.id.help_definitions));
                        helpDialog.setContentView(layout);
                        helpDialog.show();
                        dismissButton = (Button) layout.findViewById(R.id.dismiss_button3);
                        dismissButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                helpDialog.dismiss();
                            }
                        });
                    }
                });

                Button subSeries = (Button) helpDialog.findViewById(R.id.button_soilSeriesDefinitions);
                subSeries.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.subseries_definitions, (ViewGroup) findViewById(R.id.subseries_definitions));
                        helpDialog.setContentView(layout);
                        helpDialog.show();
                        dismissButton = (Button) layout.findViewById(R.id.dismiss_button2);
                        dismissButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Perform action on click
                                helpDialog.dismiss();
                            }
                        });
                    }
                });

                Button cropFamilies = (Button) helpDialog.findViewById(R.id.button_cropFamilies);
                cropFamilies.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.help_cropfamilies, (ViewGroup) findViewById(R.id.help_cropfamilies));
                        helpDialog.setContentView(layout);
                        helpDialog.show();
                        dismissButton = (Button) layout.findViewById(R.id.dismiss_button2);
                        dismissButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Perform action on click
                                helpDialog.dismiss();
                            }
                        });
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                int newRequestSize = Integer.parseInt(sharedPrefs.getString("radius", "1000"));
                Log.i("NewRequestSize", "Size is " + newRequestSize);
                if (newRequestSize != requestSize) {
                    requestSize = newRequestSize;
                    clearOverlays();
                    if (currPos != -1) {
                        selectItem(currPos);
                    } else {
                        mapView.getOverlays().clear();
                        mapView.getOverlays().add(0, mapOverlay);
                        mapView.invalidate();
                        //mapView.getOverlays().remove(0);
                        setCentreMarker();
                        mapController.setZoom(currZoomLevel);
                    }
                }
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit application?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).create().show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    //Function to check if there is internet service
    public boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetInfo = cm.getActiveNetworkInfo();

        return internetInfo != null && internetInfo.isConnected();
    }

    //Function to check if the location manager is working and can return a location to be used by user
    public boolean checkLocation() {
        try {

            lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled a notice will be delivered to the user
                return false;
            } else if (isGPSEnabled && !isNetworkEnabled) {
                currentToast.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location");
                builder.setMessage("Location finding is set to GPS only which takes 5-10 minutes to locate user. For fast location" +
                        " finding, use Wifi or the Network Provider in Location Settings of device. Press yes to" +
                        " be forwarded to Location Setting Screen otherwise press No to continue using GPS.")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                lm.removeUpdates(locationListener);
                                MapActivity.this.finish();
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            } else {
                // location manager will be able to return a location
                return true;
            }


        } catch (Exception e) {
            Log.e("Check Error", "Exception raise in checkLocation: " + e);
            return false;
        }
    }

    //Function to initialize the location variables which are being used in the application.
    //This function is called when the location of the user changes/ is initialized
    private void useLocation() {

        lm.removeUpdates(locationListener);
        //Check if map was already loaded
        //Once user's location is retrieved then set the current focus of the map to the user
        if (location == null && previousLocation == null) {

        } else if (location == null && previousLocation != null) {
            location = previousLocation;
        } else {
            float distPrev = 0;
            if (previousLocation != null) {
                distPrev = previousLocation.distanceTo(location);
            }
            //TODO
            //Horrible logic
            if (distPrev >= 100) {
                clearOverlays();
            } else if (distPrev > 50) {
                setCentreMarker();
            }

            if (previousLocation == null || (distPrev >= 100)) {
                // If current location was found, zoom to this location
                //mapController.zoomToSpan((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
                mapController.setZoom(mapView.getMaxZoomLevel());

                //Log.i("main", "I am here " + location.toString() + "  " + location.getLatitude() + " " + location.getLongitude());

                // Set variables detailing the current location
                // new GeoPoint()

                centre = new GeoPoint(location.getLatitude(), location.getLongitude());
                requestUrl = location.getLongitude() + "&" + location.getLatitude();
                //Fetch all data with the location
                //asyncKMZfetchAll AllKmzProcess = new asyncKMZfetchAll();
                //AllKmzProcess.execute(requestUrl);
                //option = internalViewOptions[1];
                //asyncKMZfetch KmzProcess = new asyncKMZfetch();
                //KmzProcess.execute(requestUrl);
                if (getUserCountry(location.getLatitude(), location.getLongitude()).equalsIgnoreCase("tt")) {
                    if (checkInternet() && (currPos != 0 && currPos != -1)) {
                        selectItem(currPos);
                        asyncJSONfetch js = new asyncJSONfetch();
                        js.execute(requestUrl);
                    } else if (checkInternet()) {
                        selectItem(1);
                        asyncJSONfetch js = new asyncJSONfetch();
                        js.execute(requestUrl);
                        //setCentreMarker();
                    } else {
                        if (splashScreen.isShowing()) {
                            splashScreen.dismiss();
                        }
                        mapController.setCenter(defaultLocation);
                        mapController.setZoom(10);
                        showToast("No Internet available to fetch data");
                    }
                }else {
                    Log.i("Foreign","Location of foreigner is: "+location);
                    AlertDialog wrongLocation = new AlertDialog.Builder(this).create();
                    wrongLocation.setTitle("Location");
                    wrongLocation.setCancelable(false);
                    wrongLocation.setMessage("Please note that the application only shows data for Trinidad and Tobago and " +
                            " your location is detected to be outside this service area.");
                    wrongLocation.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(splashScreen.isShowing()){
                                splashScreen.dismiss();
                            }
                            setCentreMarker();
                            mapController.setCenter(centre);
                            mapController.setZoom(10);
                        }
                    });

                    wrongLocation.show();
                }

                } else if (distPrev < 100) {
                    showToast("Your Location has not significantly changed");
                    mapController.setCenter(centre);
                }

        }
    }

    //On drawer clicked, the appropriate item is selected for the data to be downloaded/fetched.
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (appMode == false) {
                position = position + 7;
            }
            currPos = position;
            selectItem(position);
        }
    }

    //Function which is signalled when an item in the drawer is initiated. This function would use
    //the option selected by the user to fetch the necessary information from the cache or from the
    //server.
    private void selectItem(int position) {
        int savedPos = position;
        try {
            if (appMode == false) {
                if (currPos == -1) {
                    currPos = 8;
                }
                if (position == 1)
                    position = currPos;
                //Log.i("wth",viewOptions[position-7]);
                setTitle(viewOptions[position - 7]);
                mDrawerList.setItemChecked(position - 7, true);
            } else {
                mDrawerList.setItemChecked(position, true);
                setTitle(viewOptions[position]);
            }
        }catch(Exception e){
            Log.e("selectItem","Error is : "+e);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        if (location != null) {
            InfoWindow.closeAllInfoWindowsOn(mapView);
            // update selected item and title, then close the drawer
            option = internalViewOptions[position];
            Log.i("option", "option is: " + option);
            if (option.equalsIgnoreCase("soilCapability")) {
                viewRoadCapability.setVisibility(View.GONE);
                viewLegend.setVisibility(View.VISIBLE);
            } else if (option.equalsIgnoreCase("roads")) {
                viewLegend.setVisibility(View.GONE);
                viewRoadCapability.setVisibility(View.GONE);
            } else if (option.equalsIgnoreCase("landUse")) {
                viewRoadCapability.setVisibility(View.GONE);
                viewLegend.setVisibility(View.VISIBLE);
            } else if (option.equalsIgnoreCase("rainfall")) {
                viewRoadCapability.setVisibility(View.GONE);
                viewLegend.setVisibility(View.VISIBLE);
            } else {
                viewRoadCapability.setVisibility(View.GONE);
                viewLegend.setVisibility(View.GONE);
            }
            // If option was already loaded display that option, else do a request from server.
            // Clear all present overlays first
            mapView.getOverlays().clear();
            mapView.getOverlays().add(0, mapOverlay);
            currPos = position;
            // if overlay was added already then just load it from memory
            if (overlaySet.contains(option)) {
                int index = overlaySet.indexOf(option);
                mapView.getOverlays().add(viewOverlays.get(index));
                mapView.invalidate();
                //mapView.getOverlays().remove(0);
                setCentreMarker();
                //mapView.zoomToBoundingBox(m.mKmlRoot.getBoundingBox());
                mapView.zoomToBoundingBox(mapView.getBoundingBox());
                //mapController.setZoom(currZoomLevel);
            } else {
                if(appMode==true) {
                    asyncKMZfetch KmzProcess = new asyncKMZfetch();
                    KmzProcess.execute(requestUrl, requestSize + "");
                }else{
                    asyncJSONCropfetch jsonprocess = new asyncJSONCropfetch();
                    jsonprocess.execute();
                    //selectItem(currPos);
                }
            }
        }
    }

    //Function to show a toast based on the input text
    private void showToast(String text) {
        if (currentToast == null) {
            currentToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        }
        if (currentToast.getView().isShown()) {
            //    currentToast.cancel();
        }
        currentToast.setText(text);
        currentToast.setDuration(Toast.LENGTH_LONG);
        currentToast.show();
    }

    //Function to analyze current possibly location retrieving settings and get the location
    private Location getLocation() {
        try {

            lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled a notice will be delivered to the user
                return null;
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                    Log.d("Network", "Network Enabled");
                    if (lm != null) {
                        //    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                //get the location by gps
                if (isGPSEnabled) {
                    if (location == null) {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                        Log.d("GPS Enabled", "GPS Enabled" + lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                        if (lm != null) {
                            //        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    //Function which sets the marker to the user's location/target location when use location is
    //finished
    private void setCentreMarker() {
        // Overlay to set a marker
        OverlayItem overlayItem = new OverlayItem("Location", "My Location", centre);
        Drawable newMarker = context.getResources().getDrawable(R.drawable.marker_poi);
        overlayItem.setMarker(newMarker);

        // Clear overlay and add location marker
        overlayItemArray.clear();
        overlayItemArray.add(overlayItem);
        ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay
                = new ItemizedIconOverlay<OverlayItem>(context, overlayItemArray, null);
        mapView.getOverlays().add(anotherItemizedIconOverlay);
        mapController.setCenter(centre);
    }

    //TODO
    // include soil name in application
    // include ph in soil profile
    //TODO
    //TODO include distance to closest water source.
    //Function to start an asynchronous task to http GET KML data from the server and then place an overlay on the map
    private class asyncKMLRunner extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            try {
                progressDialog = ProgressDialog.show(context, "", "Loading Data", true);
            } catch (final Throwable th) {
                //TODO
            }
        }

        protected String doInBackground(String... params) {
            try {
                boolean connectStatus = kmlDocument.parseKMLUrl(url + "/" + option + "/" + params[0]);
                if (kmlDocument == null || connectStatus == false) {
                    return "false";
                }
                return "true";
            } catch (Exception e) {
                showToast("Please ensure phone has internet signal");
                return "Error is : " + e;
            }
        }

        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("true") && kmlDocument != null) {
                try {
                    overlaySet.add(option);
                    viewOverlays.add((FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument));
                    mapView.getOverlays().add(viewOverlays.get(viewOverlays.size() - 1));

                    mapView.invalidate();
                    //mapView.getOverlays().remove(0);
                    setCentreMarker();
                    // mapView.zoomToBoundingBox(kmlDocument.mKmlRoot.getBoundingBox());
                    mapController.setZoom(currZoomLevel);
                    progressDialog.dismiss();
                    showToast("Data Loaded");
                } catch (Exception e) {
                    progressDialog.dismiss();
                    showToast("No such data available close to location");
                    Log.e("onPostExecute", "Error is: " + e);
                }
            } else {
                progressDialog.dismiss();
                //The KML document could not be retrieved successfully
                if (overlaySet.size() > 0) {
                    AlertDialog noInternet = new AlertDialog.Builder(context).create();
                    noInternet.setTitle("Connection Error");
                    noInternet.setMessage("Server could not be reached please, enable internet connection and restart application.");
                    noInternet.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Undefined for now
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });

                    //  noLocation.setIcon(R.drawable.icon);
                    noInternet.show();
                } else {
                    progressDialog.dismiss();
                    showToast("No internet detected");
                }
                Log.i("Post execute", "Error: " + result);
            }

        }
    }

    private class asyncKMZfetchAll extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            try {
                //   progressDialog = ProgressDialog.show(context, "", "Loading Data", true);
            } catch (final Throwable th) {
                //TODO
            }
        }

        protected String doInBackground(String... params) {
            try {

                for (int x = 0; x < internalViewOptions.length - 2; x++) {
                    String inViewOption = internalViewOptions[x];
                    URL work = new URL(url + "/" + inViewOption + "/" + params[0]);

                    URLConnection connection = work.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    try {
                        kmlDocument = new KmlDocument();
                        File f = new File(Environment.getExternalStorageDirectory().getPath() + inViewOption + ".kmz");
                        FileOutputStream outputStream = new FileOutputStream(f);
                        byte buffer[] = new byte[1024];
                        long total = 0;
                        int count;
                        while ((count = inputStream.read(buffer)) != -1) {
                            total += count;
                            outputStream.write(buffer, 0, count);
                        }
                        outputStream.flush();
                        kmlDocument.parseKMZFile(f);
                        f.delete();
                        outputStream.close();
                        inputStream.close();
                        try {
                            overlaySet.add(inViewOption);
                            ArrayList<KmlFeature> myList = kmlDocument.mKmlRoot.mItems;
                            if (myList != null) {
                                Iterator<KmlFeature> iter = myList.iterator();
                                while (iter.hasNext()) {
                                    KmlFeature k = iter.next();
                                    if (k.mDescription != null) {
                                        String descrip = k.mDescription.replaceAll("\\n", "<br/>");
                                        k.mDescription = descrip;
                                    }
                                }
                            }
                            // descrip = descrip.replaceAll("\n","<br/>");
                            viewOverlays.add((FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument));
                            // mapView.getOverlays().add(viewOverlays.get(viewOverlays.size() - 1));
                        } catch (Exception e) {
                            Log.i("onPostExecute", "Error is: " + e);
                        }

                    } catch (Exception e) {
                        Log.i("Async Error", "The error is " + e);
                    }
                }

                return "true";
            } catch (Exception e) {
                Log.i("Async Error", "The error is " + e);
            }

            return "false";
        }

        protected void onPostExecute(String result) {

            mapView.invalidate();
            //mapView.getOverlays().remove(0);
            setCentreMarker();
            // mapView.zoomToBoundingBox(kmlDocument.mKmlRoot.getBoundingBox());
            mapController.setZoom(currZoomLevel);
            //   progressDialog.dismiss();
            splashScreen.dismiss();
        }
    }

    private class asyncTestKMZfetch extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            try {
                if (!splashScreen.isShowing()) {
                    progressDialog = ProgressDialog.show(context, "", "Loading Data", true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            } catch (final Throwable th) {
                //TODO
            }
        }

        protected String doInBackground(String... params) {
            try {
                Log.i("inside fetch", "params[1] is:" + params[1] + option);
                URL work = new URL(url + "/" + option + "/" + params[0] + "&" + params[1]);
                URLConnection connection = work.openConnection();
                InputStream inputStream = connection.getInputStream();
                kmlDocument = new KmlDocument();
                long total = 0;

                File f = new File(Environment.getExternalStorageDirectory().getPath() +"/" +option + ".kmz");
                FileOutputStream outputStream = new FileOutputStream(f);
                byte buffer[] = new byte[1024];
                total = 0;
                int count;
                while ((count = inputStream.read(buffer)) != -1) {
                    total += count;
                    outputStream.write(buffer, 0, count);
                }
                outputStream.flush();
                kmlDocument.parseKMZFile(f);
                f.delete();

                outputStream.close();
                inputStream.close();

                return "true";
            } catch (Exception e) {
                Log.i("Async Error", "The request was" + url + "/" + option + "/" + params[0]);
                Log.i("Async Error", "The error is " + e);
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            if (kmlDocument != null && result.equalsIgnoreCase("true")) {
                try {
                    overlaySet.add(option);
                    ArrayList<KmlFeature> myList = kmlDocument.mKmlRoot.mItems;

                    //TODO
                    //style list for changing the style of the legend etc.
                    String saveDescription = "";
                    if (myList != null && !myList.isEmpty()) {
                        Iterator<KmlFeature> iter = myList.iterator();
                        int mIDcount = 0;
                        while (iter.hasNext()) {
                            KmlFeature k = iter.next();
                            if (k.mDescription != null) {
                                k.mDescription = k.mDescription.replaceAll("\\n", "<br/>");
                                if (appMode == false && !option.equalsIgnoreCase("recommendAll")) {
                                    saveDescription = k.mDescription;
                                    k.mId = mIDcount + "";
                                    //Log.i("ID","id is "+k.getExtendedData("lowph"));
                                    //k.mExtendedData=null;
                                    mIDcount = mIDcount + 1;
                                    hash.put(k.mId, k.mDescription);
                                    k.mDescription = parseRString(k.mDescription);
                                }
                            }
                        }

                    }
                    if (appMode == false & !option.equalsIgnoreCase("recommendAll")) {
                        KmlFeature.Styler styler = new MyKmlStyler(kmlDocument, mapView);
                        viewOverlays.add((FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, styler, kmlDocument));
                    } else {
                        viewOverlays.add((FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument));
                    }
                    mapView.getOverlays().add(viewOverlays.get(viewOverlays.size() - 1));

                    mapView.invalidate();
                    //mapView.getOverlays().remove(0);
                    setCentreMarker();
                    mapView.zoomToBoundingBox(kmlDocument.mKmlRoot.getBoundingBox());
                    //mapController.setZoom(currZoomLevel);
                    if (splashScreen.isShowing()) {
                        splashScreen.dismiss();
                    } else {
                        progressDialog.dismiss();
                        showToast("Data Loaded");
                    }
                } catch (Exception e) {
                    if (splashScreen.isShowing()) {
                        splashScreen.dismiss();
                    } else {
                        progressDialog.dismiss();
                    }
                    showToast("No data available for this location");
                    Log.e("onPostExecute", "Error is: " + e);
                }
            } else {
                if (splashScreen.isShowing()) {
                    splashScreen.dismiss();
                } else if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                //The KML document could not be retrieved successfully
                showToast("Error fetching data from server");
                Log.e("Post execute", "Error: " + result);
            }
        }
    }

    //Function to which starts an asynchronous task which runs and fetches the required information
    //from the server before storing such data in an overlay array for usage by the user.
    private class asyncKMZfetch extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            try {
                if (!splashScreen.isShowing()) {
                    progressDialog = ProgressDialog.show(context, "", "Loading Data", true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            } catch (final Throwable th) {
                //TODO
            }
        }

        protected String doInBackground(String... params) {
            try {
                Log.i("inside fetch", "params[1] is:" + params[1] + option);
                URL work = new URL(url + "/" + option + "/" + params[0] + "&" + params[1]);
                URLConnection connection = work.openConnection();
                connection.setReadTimeout(15000);
                InputStream inputStream = connection.getInputStream();
                kmlDocument = new KmlDocument();
                long total = 0;

                File f = new File(Environment.getExternalStorageDirectory().getPath() +"/" +option + ".kmz");
                FileOutputStream outputStream = new FileOutputStream(f);
                byte buffer[] = new byte[1024];
                total = 0;
                int count;
                while ((count = inputStream.read(buffer)) != -1) {
                    total += count;
                    outputStream.write(buffer, 0, count);
                }
                outputStream.flush();
                kmlDocument.parseKMZFile(f);
                f.delete();

                outputStream.close();
                inputStream.close();

                return "true";
            } catch (Exception e) {
                Log.i("Async Error", "The request was" + url + "/" + option + "/" + params[0]);
                Log.i("Async Error", "The error is " + e);
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            if (kmlDocument != null && result.equalsIgnoreCase("true")) {
                try {
                    overlaySet.add(option);
                    ArrayList<KmlFeature> myList = kmlDocument.mKmlRoot.mItems;

                    //TODO
                    //style list for changing the style of the legend etc.
                    String saveDescription = "";
                    if (myList != null && !myList.isEmpty()) {
                        Iterator<KmlFeature> iter = myList.iterator();
                        int mIDcount = 0;
                        while (iter.hasNext()) {
                            KmlFeature k = iter.next();
                            if (k.mDescription != null) {
                                k.mDescription = k.mDescription.replaceAll("\\n", "<br/>");
                            }
                        }

                        if(option.equalsIgnoreCase("soilCapability")||option.equalsIgnoreCase("rainfall")||option.equalsIgnoreCase("landuse")) {
                            KmlFeature.Styler styler = new profileKmlStyler(kmlDocument, mapView);
                            viewOverlays.add((FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, styler, kmlDocument));
                        }else{
                            viewOverlays.add((FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(mapView, null, null, kmlDocument));
                        }
                        mapView.getOverlays().add(viewOverlays.get(viewOverlays.size() - 1));
                        mapView.invalidate();
                        //mapView.getOverlays().remove(0);
                        setCentreMarker();
                        mapView.zoomToBoundingBox(kmlDocument.mKmlRoot.getBoundingBox());
                        //mapController.setZoom(currZoomLevel);
                        if (splashScreen.isShowing()) {
                            splashScreen.dismiss();
                        } else {
                            progressDialog.dismiss();
                            showToast("Data Loaded");
                        }
                    }else{
                        showToast("No data available at this location.");
                        mapView.invalidate();
                        //mapView.getOverlays().remove(0);
                        setCentreMarker();
                    }
                } catch (Exception e) {
                    showToast("No data available at this location.");
                    Log.e("onPostExecute", "Error is: " + e);
                }
            } else {
                //The KML document could not be retrieved successfully
                if(checkInternet()==false){
                    showToast("No Internet available to fetch data");
                }else {
                    showToast("Error fetching data from server");
                }
                Log.e("Post execute", "Error: " + result);
            }
            if (splashScreen.isShowing()) {
                splashScreen.dismiss();
            } else if (progressDialog!=null&&progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    //Function to retrieve the closest objects to the user
    private class asyncJSONfetch extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            try {
            } catch (final Throwable th) {
            }
        }

        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + "/closeObjects/" + params[0]);
                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } else {
                        Log.e("Async Error", "Failed to download JSON object");
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.toString();
            } catch (Exception e) {
                Log.e("Async Error", "The request was" + url + "/" + option + "/" + params[0]);
                Log.e("Async Error", "The error is " + e);
            }
            return "false";
        }

        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("false")) {

            } else {
                try {
                    JSONObject closestObjts = new JSONObject(result);
                    closestRiver = closestObjts.getDouble("river");
                    closestRoad = closestObjts.getDouble("road");

                } catch (Exception e) {
                    Log.e("Closest", "Road: " + closestRoad + " River: " + closestRiver + e);
                }

            }
        }
    }

    //Function to retrieve the crop specifications to be used in the recommender
    private class asyncJSONCropfetch extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            try {
                if (!splashScreen.isShowing()) {
                    progressDialog = ProgressDialog.show(context, "", "Loading Data", true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            } catch (final Throwable th) {
                //TODO
            }
        }

        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url + "/cropSpecifications/");
                try {
                    HttpResponse response = client.execute(httpGet);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        return builder.toString();
                    } else {
                        Log.e("Async Error", "Failed to download JSON object");
                        return "false";
                    }
                } catch (Exception e) {
                    Log.e("Async Error", "The error is " + e);
                }

            } catch (Exception e) {
                Log.e("Async Error", "The request was" + url + "/" + option + "/" + params[0]);
                Log.e("Async Error", "The error is " + e);
            }
            return "false";
        }

        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("false")) {
                if (splashScreen.isShowing()) {
                    splashScreen.dismiss();
                } else if (progressDialog!=null&&progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                showToast("No Internet available to fetch data for new point");
                setCentreMarker();
            } else {
                try {
                    cropSpecifications = new JSONObject(result);
                    asyncRecommendAll KmzProcess = new asyncRecommendAll();
                    KmzProcess.execute(requestUrl, requestSize + "");
                } catch (Exception e) {
                    Log.e("Crop Specifications", "Error is: " + e);
                }

            }
        }
    }

    //Function which pulls all the rainfall+soil series polygons from the server and users the
    //Crop specifications to display recommendations to the user
    private class asyncRecommendAll extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            try {
                if (!splashScreen.isShowing()&&!progressDialog.isShowing()) {
                    progressDialog = ProgressDialog.show(context, "", "Loading Data", true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            } catch (final Throwable th) {
                //TODO
            }
        }

        protected String doInBackground(String... params) {
            try {
                URL work = new URL(url + "/soilAndRainfall/" + params[0] + "&" + params[1]);
                URLConnection connection = work.openConnection();
                connection.setReadTimeout(15000);
                InputStream inputStream = connection.getInputStream();
                kmlDocument = new KmlDocument();
                long total = 0;

                File f = new File(Environment.getExternalStorageDirectory().getPath()+"/" + option + ".kmz");
                FileOutputStream outputStream = new FileOutputStream(f);
                byte buffer[] = new byte[1024];
                total = 0;
                int count;
                while ((count = inputStream.read(buffer)) != -1) {
                    total += count;
                    outputStream.write(buffer, 0, count);
                }
                outputStream.flush();
                kmlDocument.parseKMZFile(f);
                f.delete();

                outputStream.close();
                inputStream.close();

                return "true";
            } catch (Exception e) {
                Log.i("Async Error", "The request was" + url + "/" + option + "/" + params[0]);
                Log.i("Async Error", "The error is " + e);
            }
            return "false";
        }

        protected void onPostExecute(String result) {
            try {
                if (kmlDocument != null && result.equalsIgnoreCase("true")) {
                    ArrayList<KmlFeature> myList = kmlDocument.mKmlRoot.mItems;

                    String saveDescription = "";
                    if (myList != null && !myList.isEmpty()) {
                        Iterator<KmlFeature> iter = myList.iterator();
                        int mIDcount = 0;
                        KmlDocument[] freshCrop = new KmlDocument[countRecommendCrops];
                        KmlDocument recommendAll = new KmlDocument();
                        String[] cropStatus = new String[countRecommendCrops];
                        String cropsSuitability = "";
                        for (int x = 0; x < countRecommendCrops; x++) {
                            freshCrop[x] = new KmlDocument();
                            cropStatus[x] = "";
                            freshCrop[x].mKmlRoot = new KmlFolder();
                        }

                        while (iter.hasNext()) {
                            KmlFeature k = iter.next();
                            String savedDescrip = k.mDescription;
                            cropsSuitability = "";
                            for (int x = 0; x < countRecommendCrops; x++) {

                                //k.mName = internalViewOptions[x + countProfileOptions];

                                k.mDescription = parseCropString(savedDescrip, internalViewOptions[x + countProfileOptions], k);
                                k.mId = mIDcount + "";
                                mIDcount = mIDcount + 1;
                                hash.put(k.mId, savedDescrip);
                                freshCrop[x].mKmlRoot.mItems.add(k.clone());
                                cropsSuitability = cropsSuitability + recommendOptions[x] + ": " + k.mName;
                                if (x == countRecommendCrops - 1) {
                                    //k.mName = "Suitability of All Crops";
                                    //Log.i("Descript2", cropsSuitability +  " "+recommendOptions[x]);
                                    k.mName = cropsSuitability;
                                    k.mStyle = "2";
                                    k.mDescription = "";
                                    recommendAll.mKmlRoot.mItems.add(k.clone());
                                } else {
                                    cropsSuitability = cropsSuitability + "\n";
                                }
                            }
                        }
                        for (int x = 0; x < countRecommendCrops; x++) {
                            overlaySet.add(internalViewOptions[x + countProfileOptions]);
                            KmlFeature.Styler styler = new MyKmlStyler(freshCrop[x], mapView);
                            viewOverlays.add((FolderOverlay) freshCrop[x].mKmlRoot.buildOverlay(mapView, null, styler, freshCrop[x]));
                        }
                        overlaySet.add("recommendAll");
                        KmlFeature.Styler styler = new MyKmlStyler(recommendAll, mapView);
                        viewOverlays.add((FolderOverlay) recommendAll.mKmlRoot.buildOverlay(mapView, null, styler, recommendAll));

                        if (splashScreen.isShowing()) {
                            splashScreen.dismiss();
                        } else if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (currPos != -1) {
                            selectItem(currPos);
                        }
                    }else{
                        showToast("No data available at this location.");
                        mapView.invalidate();
                        //mapView.getOverlays().remove(0);
                        setCentreMarker();
                    }

                } else {
                    showToast("Error fetching data from server");
                    setCentreMarker();
                }
            }catch(Exception e){
                Log.e("Post execute", "Error: " + result+ e);
                setCentreMarker();
            }
            if (splashScreen.isShowing()) {
                splashScreen.dismiss();
            } else if (progressDialog!=null&&progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            }
    }

    //Function to parse the string for each of the polygons for each of the crops
    private String parseRString(String mDescription) {
        String descriptionBuilder = "";
        String[] recommendations = mDescription.split(",");
        Log.i("Description Before Parsing", mDescription + "");
        if (Integer.parseInt(recommendations[0]) == 3) {
            //pH too high
            descriptionBuilder = descriptionBuilder + "Soil is too Acidic";

        } else if (Integer.parseInt(recommendations[0]) == 1) {
            //pH too low
            descriptionBuilder = descriptionBuilder + "Soil is too Alkaline";
        } else {
            //pH is optimum
            descriptionBuilder = descriptionBuilder + "Soil pH is Optimal";
        }

        if (Integer.parseInt(recommendations[1]) == 3) {
            //cec too high
            descriptionBuilder = descriptionBuilder + "<br/>EC is higher than Optimal";

        } else if (Integer.parseInt(recommendations[1]) == 1) {
            //cec too low
            descriptionBuilder = descriptionBuilder + "<br/>EC is lower than Optimal";
        } else {
            //cec is optimum
            descriptionBuilder = descriptionBuilder + "<br/>EC is Optimal";
        }

        if (Integer.parseInt(recommendations[2]) == 3) {
            //soil too high
            descriptionBuilder = descriptionBuilder + "<br/>Soil composition is not ideal";

        } else if (Integer.parseInt(recommendations[2]) == 1) {
            //soil too low
            descriptionBuilder = descriptionBuilder + "<br/>Soil composition is not ideal";
        } else {
            //soil is optimum
            descriptionBuilder = descriptionBuilder + "<br/>Soil composition is Optimal";
        }

        if (Integer.parseInt(recommendations[3]) == 3) {
            //soil too high
            descriptionBuilder = descriptionBuilder + "<br/>There is too much Rainfall";

        } else if (Integer.parseInt(recommendations[3]) == 1) {
            //soil too low
            descriptionBuilder = descriptionBuilder + "<br/>There is insufficient Rainfall";
        } else {
            //soil is optimum
            descriptionBuilder = descriptionBuilder + "<br/>Rainfall amount is Optimal";
        }

        //descriptionBuilder=descriptionBuilder+"<br/>Closest Road is "+(int)closestRoad+" meters away.";
        //descriptionBuilder=descriptionBuilder+"<br/>Closest River is "+(int)closestRiver+" meters away.";
        return descriptionBuilder;
    }

    //Function to parse the string for each of the polygons for each of the crops
    private String parseCropString(String mDescription, String crop, KmlFeature k) throws JSONException {
        String descriptionBuilder = "";
        String[] actualVals = mDescription.split(",");
        double lowph = cropSpecifications.getJSONObject(crop).getDouble("lowph");
        double highph = cropSpecifications.getJSONObject(crop).getDouble("highph");
        //double highEC = cropSpecifications.getJSONObject(crop).getDouble("highEC");
        double highRainfall = cropSpecifications.getJSONObject(crop).getDouble("highRainfall");
        double lowRainfall = cropSpecifications.getJSONObject(crop).getDouble("lowRainfall");
        double highClay = cropSpecifications.getJSONObject(crop).getDouble("highClay");
        double lowClay = cropSpecifications.getJSONObject(crop).getDouble("lowClay");

        int[] recommendations = {2, 2, 2};
        try {
            double actualph = Double.parseDouble(actualVals[0]);
            //double actualEC = Double.parseDouble(actualVals[1]);
            double actualClay = Double.parseDouble(actualVals[1]);
            int gridcode = Integer.parseInt(actualVals[2]);
            int thisHighRain;
            int thisLowRain;
            if (gridcode == 1) {
                thisHighRain = 1998;
                thisLowRain = 1716;
            } else if (gridcode == 2) {
                thisHighRain = 2166;
                thisLowRain = 1999;
            } else if (gridcode == 3) {
                thisHighRain = 2310;
                thisLowRain = 2167;
            } else if (gridcode == 4) {
                thisHighRain = 2478;
                thisLowRain = 2311;
            } else {
                thisHighRain = 2700;
                thisLowRain = 2479;
            }

            if (actualph >= lowph && actualph <= highph) {
                recommendations[0] = 2;
                //pH is optimum
                descriptionBuilder = descriptionBuilder + "Soil pH is Optimal";
            } else if (actualph < lowph) {
                recommendations[0] = 1;
                //pH too low
                descriptionBuilder = descriptionBuilder + "Soil is too Alkaline";
            } else {
                recommendations[0] = 3;
                //pH too high
                descriptionBuilder = descriptionBuilder + "Soil is too Acidic";
            }
            /*
            if (actualEC <= highEC) {
                recommendations[1] = 2;
            } else {
                recommendations[1] = 3;
            }*/
            if (actualClay >= lowClay && actualClay <= highClay) {
                recommendations[1] = 2;
                //soil is optimum
                descriptionBuilder = descriptionBuilder + "<br/>Soil composition is Optimal";
            } else if (actualClay < lowClay) {
                recommendations[1] = 1;
                //soil too high
                descriptionBuilder = descriptionBuilder + "<br/>Soil composition is not ideal";
            } else {
                recommendations[1] = 3;
                //soil too high
                descriptionBuilder = descriptionBuilder + "<br/>Soil composition is not ideal";
            }

            if (Math.max(lowRainfall, thisLowRain) <= Math.min(highRainfall, thisHighRain)) {
                recommendations[2] = 2;
                //soil is optimum
                descriptionBuilder = descriptionBuilder + "<br/>Rainfall amount is Optimal";
            } else if (thisLowRain > highRainfall) {
                recommendations[2] = 3;
                //soil too high
                descriptionBuilder = descriptionBuilder + "<br/>There is too much Rainfall";
            } else {
                recommendations[2] = 1;
                //Rainfall too low
                descriptionBuilder = descriptionBuilder + "<br/>There is insufficient Rainfall";
            }
            if(recommendations[0]==2&&recommendations[1]==2&&recommendations[2]==2){
                k.mStyle="1";
                k.mName = "Suitable";
            }else if(recommendations[0]==2||recommendations[1]==2||recommendations[2]==2){
                k.mStyle="2";
                k.mName = "Partially Suitable";
            }else{
                k.mStyle="3";
                k.mName = "Not Suitable";
            }
            Log.i("Description Before Parsing", mDescription + "");

        } catch (Exception e) {
            descriptionBuilder = "No Data";
        }
        //descriptionBuilder=descriptionBuilder+"<br/>Closest Road is "+(int)closestRoad+" meters away.";
        //descriptionBuilder=descriptionBuilder+"<br/>Closest River is "+(int)closestRiver+" meters away.";
        return descriptionBuilder;
    }

    public void clearOverlays() {
        overlaySet.clear();
        viewOverlays.clear();
    }

    private String getUserCountry(double lat, double lng) {
        try {
            Geocoder myGeocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = myGeocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            return obj.getCountryCode();
        }
        catch (Exception e) {
            Log.e("Error"," getUserCountry: Error is: "+ e);
        }
        return "error";
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        InfoWindow.closeAllInfoWindowsOn(mapView);
        return true;
    }

    @Override
    public boolean longPressHelper(final GeoPoint p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Setter");
        builder.setMessage("Would you like to set this location as the land profile point?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (checkInternet() == true) {
                            //Log.i("country is",getUserCountry(p.getLatitude(), p.getLongitude()));
                            centre = new GeoPoint(p.getLatitude(), p.getLongitude());
                            requestUrl = p.getLongitude() + "&" + p.getLatitude();
                            if(location==null)
                                location = new Location("manualLocation");
                            location.setLongitude(p.getLongitude());
                            location.setLatitude(p.getLatitude());
                            clearOverlays();
                            if (currPos != -1) {
                                selectItem(currPos);
                            } else {
                                mapView.getOverlays().clear();
                                mapView.getOverlays().add(0, mapOverlay);
                                mapView.invalidate();
                                //mapView.getOverlays().remove(0);
                                setCentreMarker();
                                mapController.setZoom(currZoomLevel);
                            }
                            if(appMode==false){
                                asyncJSONfetch js = new asyncJSONfetch();
                                js.execute(requestUrl);
                            }
                        }else{
                            //setCentreMarker();
                            showToast("No Internet available to fetch data for new point");
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        return true;
    }

    private class profileKmlStyler implements KmlFeature.Styler {
        private KmlDocument mKmlDocument;
        private MapView mMapview;
        private String savedDescript;

        profileKmlStyler(KmlDocument kmlDoc, MapView mapV) {
            mKmlDocument = kmlDoc;
            mMapview = mapV;
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            marker.setInfoWindow(new CustomInfoWindow(mapView, kmlPlacemark.mDescription));
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
            kmlPolygon.applyDefaultStyling(polygon, null, kmlPlacemark, mKmlDocument, mMapview);
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-51"))
                polygon.setFillColor(Color.argb(150, 40, 168, 31));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-52"))
                polygon.setFillColor(Color.argb(150,67,255,105));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-53"))
                polygon.setFillColor(Color.argb(150, 180, 199, 165));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-54"))
                polygon.setFillColor(Color.argb(150, 220, 93, 255));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-55"))
                polygon.setFillColor(Color.argb(150, 143, 114, 255));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-56"))
                polygon.setFillColor(Color.argb(150, 210, 240, 112));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-57"))
                polygon.setFillColor(Color.argb(150, 244, 185, 136));
            if(kmlPlacemark.mStyle.equals("poly-3F5BA9-9-58"))
                polygon.setFillColor(Color.argb(150, 250, 16, 28));

            if(kmlPlacemark.mStyle.equals("poly-forest"))
                polygon.setFillColor(Color.argb(150, 30, 120, 20));
            if(kmlPlacemark.mStyle.equals("poly-grass"))
                polygon.setFillColor(Color.argb(150, 95, 255, 0));
            if(kmlPlacemark.mStyle.equals("poly-coconut"))
                polygon.setFillColor(Color.argb(150, 255, 41, 0));
            if(kmlPlacemark.mStyle.equals("poly-agriculture"))
                polygon.setFillColor(Color.argb(150, 247, 164,0));
            if(kmlPlacemark.mStyle.equals("poly-residential"))
                polygon.setFillColor(Color.argb(150, 234, 255, 0));
            if(kmlPlacemark.mStyle.equals("poly-bamboo"))
                polygon.setFillColor(Color.argb(150, 255, 0, 175));
            if(kmlPlacemark.mStyle.equals("poly-quarry"))
                polygon.setFillColor(Color.argb(150, 130, 111, 255));
            if(kmlPlacemark.mStyle.equals("poly-other"))
                polygon.setFillColor(Color.argb(150, 255, 237, 208));

            if(kmlPlacemark.mStyle.equals("rain-1"))
                polygon.setFillColor(Color.argb(150, 233, 56, 59));
            if(kmlPlacemark.mStyle.equals("rain-2"))
                polygon.setFillColor(Color.argb(150, 218, 141, 112));
            if(kmlPlacemark.mStyle.equals("rain-3"))
                polygon.setFillColor(Color.argb(150, 79, 255, 109));
            if(kmlPlacemark.mStyle.equals("rain-4"))
                polygon.setFillColor(Color.argb(150, 234, 115, 255));
            if(kmlPlacemark.mStyle.equals("rain-5"))
                polygon.setFillColor(Color.argb(150, 133, 33, 255));

        }
    }

    private class MyKmlStyler implements KmlFeature.Styler {
        private KmlDocument mKmlDocument;
        private MapView mMapview;
        private String savedDescript;

        MyKmlStyler(KmlDocument kmlDoc, MapView mapV) {
            mKmlDocument = kmlDoc;
            mMapview = mapV;
        }

        @Override
        public void onFeature(Overlay overlay, KmlFeature kmlFeature) {

        }

        @Override
        public void onPoint(Marker marker, KmlPlacemark kmlPlacemark, KmlPoint kmlPoint) {
            marker.setInfoWindow(new CustomInfoWindow(mapView, kmlPlacemark.mDescription));
        }

        @Override
        public void onLineString(Polyline polyline, KmlPlacemark kmlPlacemark, KmlLineString kmlLineString) {
        }

        @Override
        public void onPolygon(Polygon polygon, KmlPlacemark kmlPlacemark, KmlPolygon kmlPolygon) {
            savedDescript = hash.get(kmlPlacemark.mId);
            kmlPolygon.applyDefaultStyling(polygon, null, kmlPlacemark, mKmlDocument, mMapview);
            if(kmlPlacemark.mStyle.equals("1"))
                polygon.setFillColor(Color.argb(150, 0, 120, 20));
            if(kmlPlacemark.mStyle.equals("2"))
                polygon.setFillColor(Color.argb(150,255,180,20));
            if(kmlPlacemark.mStyle.equals("3"))
                polygon.setFillColor(Color.argb(150, 255, 0, 20));
            if(!kmlPlacemark.mName.contains("All Crops"))
                polygon.setInfoWindow(new CustomInfoWindow(mapView, savedDescript));
        }
    }

    private class CustomInfoWindow extends BasicInfoWindow {

        String description = "";
        Dialog thisDialog;
        View layout;
        String[] actualVals;
        Button btn;

        public CustomInfoWindow(MapView mapView, String descrip) {
            super(R.layout.bonuspack_bubble, mapView);
            this.close();
            description = descrip;
            btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    thisDialog.show();
                }
            });
            actualVals = description.split(",");
        }

        @Override
        public void onOpen(Object item) {
            super.onOpen(item);
            this.close();
            try {
                double lowph = cropSpecifications.getJSONObject(option).getDouble("lowph");
                double highph = cropSpecifications.getJSONObject(option).getDouble("highph");
                //double highEC = cropSpecifications.getJSONObject(option).getDouble("highEC");
                int highRainfall = cropSpecifications.getJSONObject(option).getInt("highRainfall");
                int lowRainfall = cropSpecifications.getJSONObject(option).getInt("lowRainfall");
                double highClay = cropSpecifications.getJSONObject(option).getDouble("highClay");
                double lowClay = cropSpecifications.getJSONObject(option).getDouble("lowClay");

                int[] recommendations = {2, 2, 2};

                double actualph = Double.parseDouble(actualVals[0]);
                //double actualEC = Double.parseDouble(actualVals[1]);
                double actualClay = Double.parseDouble(actualVals[1]);
                double gridcode = Double.parseDouble(actualVals[2]);
                int thisHighRain, thisLowRain;
                if (gridcode == 1) {
                    thisHighRain = 1998;
                    thisLowRain = 1716;
                } else if (gridcode == 2) {
                    thisHighRain = 2166;
                    thisLowRain = 1999;
                } else if (gridcode == 3) {
                    thisHighRain = 2310;
                    thisLowRain = 2167;
                } else if (gridcode == 4) {
                    thisHighRain = 2478;
                    thisLowRain = 2311;
                } else {
                    thisHighRain = 2700;
                    thisLowRain = 2479;
                }
                if (actualph >= lowph && actualph <= highph) {
                    recommendations[0] = 2;
                } else if (actualph < lowph) {
                    recommendations[0] = 1;
                } else {
                    recommendations[0] = 3;
                }
                /*
                if (actualEC <= highEC) {
                    recommendations[1] = 2;
                } else {
                    recommendations[1] = 3;
                }*/
                if (actualClay >= lowClay && actualClay <= highClay) {
                    recommendations[1] = 2;
                } else if (actualClay < lowClay) {
                    recommendations[1] = 1;
                } else {
                    recommendations[1] = 3;
                }

                if (Math.max(lowRainfall, thisLowRain) <= Math.min(highRainfall, thisHighRain)) {
                    recommendations[2] = 2;
                } else if (thisLowRain > highRainfall) {
                    recommendations[2] = 3;
                } else {
                    recommendations[2] = 1;
                }
                if (recommendations[0] == 2 && recommendations[1] == 2 && recommendations[2] == 2) {
                    mView.findViewById(R.id.bubble_moreinfo).setVisibility(View.GONE);
                } else {
                    mView.findViewById(R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
                }

                Log.i("Descriptions Specific", description);
                thisDialog = new Dialog(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                layout = inflater.inflate(R.layout.recommendation_layout, (ViewGroup) findViewById(R.id.recommendation_layoutid));
                Button thisButton = (Button) layout.findViewById(R.id.dismiss_button3);
                thisButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        thisDialog.dismiss();
                    }
                });
                TableLayout recommendTable = (TableLayout) layout.findViewById(R.id.recommendation_table);
                TableRow tbrow;
                TextView textV1;
                TextView textV2;

                recommendTable.removeAllViewsInLayout();

                tbrow = new TableRow(context);
                textV1 = new TextView(context);
                textV1.setText("Issue");
                textV1.setPadding(0, 0, 15, 5);
                textV1.setTypeface(null, Typeface.BOLD);
                tbrow.addView(textV1);

                textV2 = new TextView(context);
                textV2.setText("Recommendation");
                textV2.setTypeface(null, Typeface.BOLD);
                tbrow.addView(textV2);
                recommendTable.addView(tbrow);

                if (recommendations[0] == 3) {
                    //pH too high
                    addRow(recommendTable,"Soil pH","Soil is too Acidic. The actual pH is " + actualph +
                            " while the recommended range is " + lowph + " - " + highph + ". To lower the pH of soil, apply elemental sulphur or ammonium sulphate fertilizers.");
                } else if (recommendations[0] == 1) {
                    //pH too low
                    addRow(recommendTable,"Soil pH","Soil is too Alkaline. The actual pH is " + actualph +
                            " while the recommended range is " + lowph + " - " + highph + ". To heighten the pH of soil, apply lime in either grounded or powdered form.");
                } else {
                    //pH is optimum
                }

                if (recommendations[1] == 3) {
                    //soil too high
                    addRow(recommendTable,"Soil Composition","The Soil is made up of " + actualClay +
                            "% Clay which is more than the recommended amount of " + highClay + "%. Install drainage channels to improve composition");

                } else if (recommendations[1] == 1) {
                    //soil too low
                    addRow(recommendTable,"Soil Composition","The Soil is made up of " + actualClay +
                            "% Clay which is less than the recommended amount of " + lowClay + "% ,Irrigate soil to improve composition.");
                } else {
                    //soil is optimum
                }

                if (recommendations[2] == 3) {
                    //soil too high
                    addRow(recommendTable,"Rainfall","There is too much Annual Rainfall at this location, Expected Rainfall: " +
                            getRainfallAmt((int) gridcode) +
                            " vs Recommended Rainfall for this crop: " + lowRainfall + "-" + highRainfall + "mm. " +
                            "Install drainage channels to improve water flow.");
                } else if (recommendations[2] == 1) {
                    //soil too low
                    addRow(recommendTable,"Rainfall","There is too little Annual Rainfall at this location, Expected Rainfall: " +
                            getRainfallAmt((int) gridcode) +
                            " vs Recommended Rainfall for this crop: " + lowRainfall + "-" + highRainfall + "mm. " +
                            "Add a water irrigation system.");
                } else {
                    //soil is optimum
                }
                thisDialog.setTitle("Recommendation");
                thisDialog.setContentView(layout);

            } catch (Exception e) {
                Log.e("Exception Info Window", "Exception " + e);
            }
        }
    }

    private void addRow(TableLayout tableL, String issueType ,String info){
        TableRow tbrow;
        TextView textV1;
        TextView textV2;
        tbrow = new TableRow(context);

        textV1 = new TextView(context);
        textV1.setText(issueType);
        textV1.setPadding(0, 0, 15, 0);
        tbrow.addView(textV1);

        textV2 = new TextView(context);
        textV2.setText(info);
        tbrow.addView(textV2);
        tableL.addView(tbrow);
    }

    //Function to map rainfall ids to the rainfall amounts
    private String getRainfallAmt(int i) {
        String amt = "";
        if (i == 1) {
            amt = "1716-1998mm";
        } else if (i == 2) {
            amt = "1999-2166mm";
        } else if (i == 3) {
            amt = "2167-2310mm";
        } else if (i == 4) {
            amt = "2311-2478mm";
        } else {
            amt = "2479-2700mm";
        }
        return amt;
    }
}