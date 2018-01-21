package by.stations.subway.ui;

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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import by.stations.subway.R;

import static by.stations.subway.common.Constants.MY_PERMISSIONS_REQUEST_CODE;
import static by.stations.subway.common.Permissions.checkLocationPermission;
import static by.stations.subway.common.Permissions.requestPermission;
import static by.stations.subway.common.Utils.buildAlertMessageNoGps;
import static by.stations.subway.common.Utils.checkGps;

public class MapFragment extends Fragment
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_PERMISSIONS_RES_REQUEST = 7001;
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;
    private static final float DEFAULT_ZOOM = 6.0f;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private List<LatLng> decodedPath;
    ImageView circleLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        circleLocation = view.findViewById(R.id.circleLocation);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        try {
            supportMapFragment.getMapAsync(this);
        } catch (Exception ex) {
            if (getView() != null) {
                Log.e(TAG, "MapPresenter: " + getResources().getString(R.string.google_map_create_error));
            }
        }
        circleLocation.setOnClickListener(this);
        createLocationRequest();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
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
        if (!checkLocationPermission(getActivity())) {
            requestPermission(getActivity());
        } else {
            if (checkGps(getActivity())) {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                }
            } else {
                buildAlertMessageNoGps(getActivity(), getActivity());
            }
        }
    }

    public void showError(int text) {
        Toast.makeText(getActivity(), getResources().getString(text), Toast.LENGTH_SHORT).show();
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_PERMISSIONS_RES_REQUEST).show();
            } else {
                showError(R.string.not_support);
            }
            return false;
        }
        return true;
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

    @Override
    public void onClick(View v) {
        setUpLocation();
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
