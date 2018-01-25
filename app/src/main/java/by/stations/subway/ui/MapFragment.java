package by.stations.subway.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import by.stations.subway.MyApplication;
import by.stations.subway.R;
import by.stations.subway.common.MapHelper;
import by.stations.subway.rest.MapApi;
import by.stations.subway.rest.StationApi;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static by.stations.subway.common.Constants.DEFAULT_ZOOM;
import static by.stations.subway.common.Constants.DISPLACEMENT;
import static by.stations.subway.common.Constants.FATEST_INTERVAL;
import static by.stations.subway.common.Constants.MINSK_CITY;
import static by.stations.subway.common.Constants.MY_PERMISSIONS_REQUEST_CODE;
import static by.stations.subway.common.Constants.UPDATE_INTERVAL;
import static by.stations.subway.common.Constants.ZOOM_CITY;
import static by.stations.subway.common.Permissions.checkLocationPermission;
import static by.stations.subway.common.Permissions.requestPermission;
import static by.stations.subway.common.Utils.buildAlertMessageNoGps;
import static by.stations.subway.common.Utils.checkGps;
import static by.stations.subway.common.Utils.checkPlayServices;
import static by.stations.subway.common.Utils.isOnline;
import static by.stations.subway.common.Utils.showToast;

public class MapFragment extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();


    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private ImageView circleLocation;
    private Polyline polyline;
    @Inject
    MapApi mapApi;
    @Inject
    StationApi stationApi;
    private boolean isClickLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        try {
            supportMapFragment.getMapAsync(this);
        } catch (Exception ex) {
            if (getView() != null) {
                Log.e(TAG, "MapPresenter: " + getResources().getString(R.string.google_map_create_error));
            }
        }

        circleLocation = view.findViewById(R.id.circleLocation);
        circleLocation.setOnClickListener(this);
        createLocationRequest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices(getActivity())) {
                        buildGoogleApiClient();
                    }
                }
            }
        }
    }

    public void setUpLocation() {
        if (!checkLocationPermission(getActivity())) {
            requestPermission(getActivity());
        } else {
            if (checkGps(getActivity()) && checkPlayServices(getActivity())) {
                buildGoogleApiClient();
                if (isClickLocation) {
                    getDeviceLocation(map, getActivity());
                }
            } else {
                buildAlertMessageNoGps(getActivity(), getActivity());
            }
        }
    }

    public void getDeviceLocation(GoogleMap map, Context context) {
        isClickLocation = false;
        try {
            FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    final Location mLastKnownLocation = task.getResult();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), ZOOM_CITY));
                } else {
                    Log.e(TAG, "Exception: %s", task.getException());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(MINSK_CITY, ZOOM_CITY));
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
        showToast(R.string.error_sorry, getActivity());
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
            if (!checkLocationPermission(getActivity())) {
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
            getStations();
        }
    }

    private void displayLocation() {
        if (!checkLocationPermission(getActivity())) {
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

    public void getStations() {
        stationApi.getStations()
                .flatMap(Observable::just)        //get list from response
                .flatMapIterable(list -> list)    //make the list iterable
                .filter(v -> v.getLongitude() != 0 || v.getLatitude() != 0) // pass all pin that coordinates != 0
                .doOnNext(v -> Log.d(TAG, "station name: " + v.getName()))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dataResponse -> MapHelper.getStationList(dataResponse, map, getActivity()),
                        error -> Log.e(TAG, "Got Error:" + error)
                );
    }

    @Override
    public void onClick(View v) {
        if (isOnline(getActivity())) {
            isClickLocation = true;
            setUpLocation();
        } else {
            showToast(R.string.error_connection, getActivity());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (isOnline(getActivity())) {
            Integer clickCount = (Integer) marker.getTag();
            if (clickCount != null) {
                String departure = String.valueOf(mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                String arrival = String.valueOf(marker.getPosition().latitude + "," + marker.getPosition().longitude);
                String key = getResources().getString(R.string.google_maps_key);

                if (polyline != null) {
                    polyline.remove(); // for delete previous route
                }
                createRoute(departure, arrival, key);
            }
        } else {
            showToast(R.string.error_connection, getActivity());
        }
        return true; // true - not display place name
    }

    private void createRoute(String departure, String arrival, String key) {
        mapApi.getMapRoute(departure, arrival, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> polyline = MapHelper.createRoute(response, map, getActivity()),
                        error -> Log.e(TAG, "Got Error:" + error)
                );
    }

}
