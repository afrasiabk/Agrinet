package alast.hm.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.bl.BubbleLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import alast.hm.R;
import alast.hm.Data.PolygonSingleton;

import static alast.hm.Data.PolygonSingleton.getLocObject;

public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    //for gps check
    protected GoogleApiClient mGoogleApiClient;
    int REQUEST_CHECK_SETTINGS = 100;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private View constraint_view;


    private TextView searchResult;
    private SearchView searchView;

    private BubbleLayout bubble_address;
    private BubbleLayout bubble_set_marker;
    private TextView bubble_address_tv;
    private Button back_btn, confirm_btn;

    private List<Address> results;
    private List<Address> pop_address;
    private Geocoder geocoder;
    private FusedLocationProviderClient mFusedLocationClient;

    //runnables
    private long delay = 300; // search address after user stops typing/ camera idle pop ud address
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        checkGps();
        assignViews();
        inits();
        mapFragment.getMapAsync(this);
    }

    ///// gps check
    private void checkGps() {
        //check gps and proceed
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create())
                .setAlwaysShow(true);
        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build()).setResultCallback(this);
    }
    @Override
    public void onConnectionSuspended(int i) {}
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    //if gps is already enabled or not
    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break; //Gps already on
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  gps not on, show dialog by calling startResolutionForResult()
                //  check result cases in onActivityResult().
                try {
                    status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {}
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                Toast.makeText(this, "Turn on your location in settings", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    //if gps is now enabled or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode != RESULT_OK) { //else enabled
                Toast.makeText(this, "GPS Not Enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ///// gps check

    private void assignViews() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        constraint_view = (View) mapFragment.getView();
        bubble_address = (BubbleLayout) findViewById(R.id.user_map_popup);
        bubble_set_marker = (BubbleLayout) findViewById(R.id.user_info_popup);
        bubble_address_tv = (TextView) findViewById(R.id.user_map_popupt);
        searchResult = (TextView) findViewById(R.id.user_search_result);
        searchView = (SearchView) findViewById(R.id.user_search_view);
        back_btn = (Button) findViewById(R.id.user_map_back_btn);
        confirm_btn = (Button) findViewById(R.id.user_map_confirm_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }); //the only thing ready before map ready
    }

    private void inits() {
        results = new ArrayList<>();
        pop_address = new ArrayList<>();
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        handler = new Handler();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        map();
    }
    private void map() {
        polygon();
        listeners();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.6844,73.0479),11));
        requestcurrentLoc();
    }

    private void polygon() {
        getLocObject(); //if not already initialized
        mMap.addPolygon(new PolygonOptions()
                .addAll(PolygonSingleton.polygonArray)
                .strokeColor(Color.GREEN));
    }

    private void listeners() {
        map_adjusts();
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                checkGps();
                requestcurrentLoc();
                return false;
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                handler.postDelayed(pop_up_address, delay);
                bubble_address.setVisibility(View.VISIBLE);
                bubble_set_marker.setVisibility(View.VISIBLE);
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                handler.removeCallbacks(pop_up_address);
                bubble_address_tv.setText("...");
                bubble_address.setVisibility(View.INVISIBLE);
                bubble_set_marker.setVisibility(View.INVISIBLE);
            }
        });

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_add(); //populate pop_address list if not already
                double lati_dbl = mMap.getCameraPosition().target.latitude;
                double longi_dbl = mMap.getCameraPosition().target.longitude;
                String add_str = "null";
                if (!(lati_dbl==0 || longi_dbl == 0)){
                    if (PolyUtil.containsLocation(new LatLng(lati_dbl,longi_dbl), PolygonSingleton.polygonArray, true)) {
                        if (pop_address.size() > 0) {
                            add_str = pop_address.get(0).getAddressLine(0);
                        }
                        Intent intent = new Intent(MapsActivity.this, ConfirmOrderActivity.class);
                        intent.putExtra("lati", String.valueOf(lati_dbl));
                        intent.putExtra("longi", String.valueOf(longi_dbl));
                        intent.putExtra("address", add_str);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(MapsActivity.this, "Location outside our delivery area (Islamabad)", Toast.LENGTH_LONG).show();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.6844,73.0479),11));
                    }
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.equals("")) {
                    callSearch(s);
                }
                handler.removeCallbacks(stopped_typing);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                handler.removeCallbacks(stopped_typing);
                if (!s.equals("")) {
                    searchResult.setText("");
                    searchResult.setVisibility(View.VISIBLE);
                    handler.postDelayed(stopped_typing, delay);
                }
                else{
                    searchResult.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(stopped_typing);
                callSearch(searchView.getQuery().toString());
            }
        });

        searchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchResult.getText().equals("No Results")) {
                    if (results.size() > 0) {
                        if (results.get(0).getCountryCode().equals("PK")) {
                            jumpTo(results.get(0).getLatitude(), results.get(0).getLongitude());
                            searchResult.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                searchView.clearFocus();
            }
        });
    }

    private void map_adjusts() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setMyLocationEnabled(true);
        View locationButton = ((View) constraint_view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);
    }

    private void callSearch(String input) {
        searchResult.setText("");
        searchResult.setVisibility(View.VISIBLE);
        results.clear();
        try {
            results = geocoder.getFromLocationName(input, 1);
        } catch (Exception e) { }

        if (results.size() > 0) {
            if (results.get(0).getCountryCode().equals("PK")) {
                String loc = results.get(0).getAddressLine(0);
                searchResult.setText(loc);
            }
        } else {
            searchResult.setText("No Results");
        }
    }

    private void display_add() {
        pop_address.clear();
        try {
            pop_address = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
        } catch (Exception e) {
        }

        if (pop_address.size() > 0) {
            if (pop_address.get(0).getCountryCode().equals("PK")) {
                bubble_address_tv.setText(pop_address.get(0).getAddressLine(0));
                bubble_address.setVisibility(View.VISIBLE);
                bubble_address.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MapsActivity.this, pop_address.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            bubble_address.setVisibility(View.INVISIBLE);
        }
    }

    //current location listener call back sent
    private void requestcurrentLoc() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    //jump to current loc when request callback recieved(only once at start then on click on loc_btn)
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            jumpTo(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
        }
    };

    private void jumpTo(double lati, double longi) { //moves camera to searched/current location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longi),18));
    }

    //Runnables
    private Runnable stopped_typing = new Runnable() {
        public void run() { callSearch(searchView.getQuery().toString());
        }
    };
    private Runnable pop_up_address = new Runnable() {
        public void run() {
            display_add();
        }
    };
    //Runnables

    //others
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (searchResult != null && (searchResult.getVisibility() == View.VISIBLE) || (searchView.hasFocus())) {
                // touch coordinates
                int touchX = (int) event.getX();
                int touchY = (int) event.getY();
                // get your view coordinates
                final int[] vl = new int[2];//view location array
                searchResult.getLocationOnScreen(vl);

//                // The left coordinate of the view
//                int viewX1 = vl[0];
//                // The right coordinate of the view
//                int viewX2 = vl[0] + searchResult.getWidth();
//                // The top coordinate of the view
//                int viewY1 = vl[1];
//                // The bottom coordinate of the view
//                int viewY2 = vl[1] + searchResult.getHeight();

                if (!((touchX >= vl[0] && touchX <= (vl[0] + searchResult.getWidth()))
                        && (touchY >= vl[1] && touchY <= (vl[1] + searchResult.getHeight())))) {
                    // If you don't want allow touch outside (for example, only hide keyboard or dismiss popup)
                    searchResult.setVisibility(View.INVISIBLE);
                    searchView.clearFocus();
                    return false;
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Location Access Not Granted. Try Again.", Toast.LENGTH_LONG).show();
                onBackPressed();
                return;
            }
            map();
        }
    }

}