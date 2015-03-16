package il.co.anyway.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends ActionBarActivity implements OnInfoWindowClickListener,
        OnMapLongClickListener, OnCameraChangeListener, LocationListener, TextView.OnEditorActionListener {

    @SuppressWarnings("unused")
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final LatLng AZZA_METUDELA_LOCATION = new LatLng(31.772126, 35.213678);
    private static final boolean CLEAR_MAP_AFTER_EACH_FETCH = true;
    private static final int MINIMUM_ZOOM_LEVEL_TO_SHOW_ACCIDENTS = 16;
    private static final int MAXIMUM_CHARACTERS_IN_INFO_WINDOWS = 100;

    // index of parameters in the parameters array for fetching accidents from server task
    public static final int JSON_STRING_PARAMETERS_COUNT = 12;
    public static final int JSON_STRING_NE_LAT = 0;
    public static final int JSON_STRING_NE_LNG = 1;
    public static final int JSON_STRING_SW_LAT = 2;
    public static final int JSON_STRING_SW_LNG = 3;
    public static final int JSON_STRING_ZOOM_LEVEL = 4;
    public static final int JSON_STRING_START_DATE = 5;
    public static final int JSON_STRING_END_DATE = 6;
    public static final int JSON_STRING_SHOW_FATAL = 7;
    public static final int JSON_STRING_SHOW_SEVERE = 8;
    public static final int JSON_STRING_SHOW_LIGHT = 9;
    public static final int JSON_STRING_SHOW_INACCURATE = 10;
    public static final int JSON_STRING_FORMAT = 11;

    private GoogleMap map;
    private List<Accident> accidentsList;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private int nextMarkerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // first run set to true only when this is the first time onCreate called
        // used to handle the case of screen rotation
        boolean firstRun = false;
        if(savedInstanceState == null)
            firstRun = true;

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(provider);


        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled && firstRun)
            new EnableGpsDialogFragment().show(getSupportFragmentManager(),"");

        setUpMapIfNeeded(firstRun);

        // add a listener to handle address search EditText
        EditText address_search = (EditText) findViewById(R.id.address_search);
        address_search.setOnEditorActionListener(this);

        nextMarkerID = 0;
    }

    // action handler for address search
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            // hide the keyboard
            v.clearFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            searchAddress(v);
            handled = true;
        }
        return handled;
    }

    /**
     * Search for an address, show a dialog and move the map to the searched location
     * @param v TextView contain the address to search, in free speech
     */
    private void searchAddress(final TextView v) {

        Geocoder geoCoder = new Geocoder(this);

        try {
            // Search for the address
            final List<Address> addresses = geoCoder.getFromLocationName(v.getText().toString(),5);

            if(addresses.size() > 0)
            {
                // arrange all the address in String array for the AlertDialog
                final String[] addressList = new String[addresses.size()];
                for(int i=0; i< addresses.size(); i++) {

                    // Address received as an address lines, join them all to one line
                    String tempAddress = "";
                    for (int j=0; j<=addresses.get(i).getMaxAddressLineIndex(); j++)
                        tempAddress += addresses.get(i).getAddressLine(j) + ", ";

                    // remove the last ", " from the address
                    tempAddress = tempAddress.substring(0, tempAddress.length()-2);
                    // add it to the array, the index match to the address checked
                    addressList[i] = tempAddress;
                }

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(getString(R.string.address_result_title))
                .setItems(addressList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        LatLng p = new LatLng(addresses.get(which).getLatitude(), addresses.get(which).getLongitude());
                        setMapToLocationAndAddMarkers(p);

                        // set the address found back to the TextView
                        v.setText(addressList[which]);

                        // TODO - when markers behavior set, check this marker,
                        // now the marker disappear when getting new markers
                        //map.addMarker(new MarkerOptions().position(p).title("searchResult").snippet(addressList[which]));
                    }
                });
                adb.show();

            }

            else
            {
                // address not found, prompt user
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(getString(R.string.address_not_found_title));
                adb.setMessage(getString(R.string.address_not_found_details));
                adb.setPositiveButton(getString(R.string.address_not_found_close),null);
                adb.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpMapIfNeeded(boolean firstRun) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {

            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap(firstRun);
            }
        }
    }

    private void setUpMap(boolean firstRun) {

        // Enable location buttons
        map.setMyLocationEnabled(true);

        // Hide My Location button
        // this because it implemented is the action bar
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // Disable toolbar on the right bottom corner(taking user to google maps app)
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        map.setOnInfoWindowClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnCameraChangeListener(this);

        if(firstRun) {

            // try to move map to user location, if not succeed go to default
            if (!setMapToMyLocationAndAddMarkers())
                setMapToLocationAndAddMarkers(AZZA_METUDELA_LOCATION, 17);

        }
        else {
            // this happening only on screen rotation, markers have been delete so re-fetch them but do not move map
            // calling only getAccidentsFromASyncTask is not working because it happening too fast and map is not initialized yet
            LatLng currentLocation = map.getCameraPosition().target;
            int currentZoomLevel = (int)map.getCameraPosition().zoom;
            setMapToLocationAndAddMarkers(currentLocation, currentZoomLevel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_back_to_start_location) {
            setMapToMyLocationAndAddMarkers();
            return true;
        }
        if (id == R.id.action_fetch_markers) {
            getAccidentsFromASyncTask();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // TODO open location based discussion
        Toast.makeText(this, "Long pressed: " + latLng, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // TODO add service(?) to update map in the background, fetching only accidents not already shown
        //
        // when this enabled, updating happening too much, not allowing to focus on marker
        //getAccidentsFromASyncTask();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the location listener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    /**
     * move the camera to specific location, should be called on after checking map!=null
     * when camera finish moving - fetching accidents of current location
     * @param location location to move to
     * @param zoomLevel move camera to this specific
     */
    private void setMapToLocationAndAddMarkers(LatLng location, int zoomLevel) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.latitude, location.longitude), zoomLevel),
                new CancelableCallback() {

                    @Override
                    public void onFinish() {
                        getAccidentsFromASyncTask();
                    }

                    @Override
                    public void onCancel() {
                        //Log.d(LOG_TAG, "onCancel");
                    }
            });
    }

    /**
     * same as setMapToLocationAndAddMarkers(LatLng location, int zoomLevel)
     * only zoom level is set to current value
     * @param location location to move to
     */
    private void setMapToLocationAndAddMarkers(LatLng location) {
        setMapToLocationAndAddMarkers(location, (int)map.getCameraPosition().zoom);
    }

    /**
     * Move the camera to current user location(received from gps sensors)
     * @return true if location is found and set, false otherwise
     */
    private boolean setMapToMyLocationAndAddMarkers() {

        if (location != null) {
            setMapToLocationAndAddMarkers(new LatLng(location.getLatitude(), location.getLongitude()));
            return true;
        }
        else {
            return false;
        }

    }

    private void getAccidentsFromASyncTask() {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        int zoomLevel = (int) map.getCameraPosition().zoom;

        if(zoomLevel < MINIMUM_ZOOM_LEVEL_TO_SHOW_ACCIDENTS) {
            // If zoom level too high, move the camera to minimum zoom level required
            Toast.makeText(getBaseContext(), getString(R.string.zoom_in_to_display), Toast.LENGTH_LONG).show();

            LatLng currentLocation = map.getCameraPosition().target;
            setMapToLocationAndAddMarkers(currentLocation, MINIMUM_ZOOM_LEVEL_TO_SHOW_ACCIDENTS);

            // setMapToLocationAndAddMarkers calls this method again when it finish moving the camera, so no need to keep going
            return;
        }

        FetchAccidents accidentTask = new FetchAccidents();

        String[] params = new String[JSON_STRING_PARAMETERS_COUNT];
        params[JSON_STRING_NE_LAT] = Double.toString(bounds.northeast.latitude);
        params[JSON_STRING_NE_LNG] = Double.toString(bounds.northeast.longitude);
        params[JSON_STRING_SW_LAT] = Double.toString(bounds.southwest.latitude);
        params[JSON_STRING_SW_LNG] = Double.toString(bounds.southwest.longitude);
        params[JSON_STRING_ZOOM_LEVEL] = Integer.toString(zoomLevel);

        // Get preferences form SharedPreferncses
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean show_fatal = sharedPrefs.getBoolean(getString(R.string.pref_accidents_fatal_key), true);
        Boolean show_severe = sharedPrefs.getBoolean(getString(R.string.pref_accidents_severe_key), true);
        Boolean show_light = sharedPrefs.getBoolean(getString(R.string.pref_accidents_light_key), true);
        Boolean show_inaccurate = sharedPrefs.getBoolean(getString(R.string.pref_accidents_inaccurate_key), false);

        String fromDate = sharedPrefs.getString(getString(R.string.pref_from_date_key), getString(R.string.pref_default_from_date));
        String toDate = sharedPrefs.getString(getString(R.string.pref_to_date_key), getString(R.string.pref_default_to_date));

        // getting timestamp for Anyway API
        params[JSON_STRING_START_DATE] = Utility.getTimeStamp(fromDate);
        params[JSON_STRING_END_DATE] = Utility.getTimeStamp(toDate);

        params[JSON_STRING_SHOW_FATAL] = show_fatal ? "1" : "0";
        params[JSON_STRING_SHOW_SEVERE] = show_severe ? "1" : "0";
        params[JSON_STRING_SHOW_LIGHT] = show_light ? "1" : "0";
        params[JSON_STRING_SHOW_INACCURATE] = show_inaccurate ? "1" : "0";
        params[JSON_STRING_FORMAT] = "json";

        accidentTask.setCallingActivity(this);
        accidentTask.execute(params);
    }

    // add accidents from array list to map
    private void addAccidentsToMap(boolean clearMap) {

        if(clearMap) {
            nextMarkerID = 0;
            map.clear();
        }

        for(Accident a : accidentsList) {

            // make sure info windows don't get to messy
            String desc = a.getDescription();
            if(desc.length() > MAXIMUM_CHARACTERS_IN_INFO_WINDOWS)
                desc = desc.substring(0, 30).concat("...");

            a.setMarkerID("m"+nextMarkerID);
            map.addMarker(new MarkerOptions()
                    .title(Utility.getAccidentTypeByIndex(a.getSubType(), getApplicationContext()))
                    .snippet(getString(R.string.marker_default_desc))
                    .icon(BitmapDescriptorFactory.fromResource(Utility.getIconForMarker(a.getSeverity(), a.getSubType())))
                    .position(a.getLocation()));

            nextMarkerID++;
        }
    }

    public void setAccidentsListAndUpdateMap(List<Accident> accidentsList) {
        this.accidentsList = accidentsList;
        addAccidentsToMap(CLEAR_MAP_AFTER_EACH_FETCH);
    }


}
