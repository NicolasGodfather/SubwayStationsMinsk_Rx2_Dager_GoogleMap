package by.stations.subway.ui;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import by.stations.subway.MyApplication;
import by.stations.subway.R;

import static by.stations.subway.common.Constants.MY_PERMISSIONS_REQUEST_CODE;
import static by.stations.subway.common.Permissions.checkLocationPermission;
import static by.stations.subway.common.Permissions.requestPermission;
import static by.stations.subway.common.Utils.checkGps;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_PERMISSIONS_RES_REQUEST = 7001;
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;
    private static final float DEFAULT_ZOOM = 6.0f;
    private boolean isFirstStart = true;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private List<LatLng> decodedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getApplicationComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isFirstStart) {
            if (checkLocationPermission(this)) {
                setUpLocation();
            }
        }
        isFirstStart = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                    }
                }
            }
        }
    }

    public void setUpLocation() {
        if (!checkLocationPermission(this)) {
            requestPermission(this);
        } else {
            if (checkGps(this)) {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                }
            } else {
                buildAlertMessageNoGps(this, this);
            }
        }
    }

    private void buildAlertMessageNoGps(MainActivity mainActivity, MainActivity mainActivity1) {
    }

    public void showError(int text) {
        Toast.makeText(this, getResources().getString(text), Toast.LENGTH_SHORT).show();
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_PERMISSIONS_RES_REQUEST).show();
            } else {
                showError(R.string.not_support);
            }
            return false;
        }
        return true;
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setUpLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            createLocationRequest();
        } catch (IllegalStateException e) {
            Log.e(TAG, "onConnected: " + e.getCause());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showError(R.string.error_sorry);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // todo logic to make route
        return true;
    }

    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (map == null) {
            Log.e(TAG, "MapPresenter: " + getResources().getString(R.string.google_map_create_error));
        } else {
            if (!checkLocationPermission(this)) {
                return;
            }
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setRotateGesturesEnabled(false);
            map.getUiSettings().setCompassEnabled(false);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setOnMarkerClickListener(this);
            map.setMyLocationEnabled(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            displayLocation();
        }
    }

    private void displayLocation() {
        if (!checkLocationPermission(this)) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), DEFAULT_ZOOM));
        } else {
            Log.d("ERROR", "Cannot get your location");
        }
    }

   /*
    public void onRoutesDownloaded(RoutesResponse response) {
        if (mView != null) {
            String points = response.getRoutes().get(0).getOverviewPolyline().getPoints();
            decodedPath = PolyUtil.decode(points);
            CustomCap cap = new CustomCap(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_my_route_end), 10);
            PolylineOptions polyline = new PolylineOptions()
                    .addAll(decodedPath)
                    .color(mView.getActivity().getResources().getColor(R.color.active_orange))
                    .endCap(cap);
            map.addPolyline(polyline);
            getAllCargo();
        }
    }*/

}
