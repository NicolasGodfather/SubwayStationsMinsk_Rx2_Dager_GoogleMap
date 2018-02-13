package by.stations.subway.mvp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.patloew.rxlocation.RxLocation;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import by.stations.subway.MyApplication;
import by.stations.subway.R;
import by.stations.subway.common.MapHelper;
import by.stations.subway.rest.MapApi;
import by.stations.subway.rest.StationApi;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static by.stations.subway.common.Constants.DEFAULT_ZOOM;
import static by.stations.subway.common.Constants.DISPLACEMENT;
import static by.stations.subway.common.Constants.FASTEST_INTERVAL;
import static by.stations.subway.common.Constants.UPDATE_INTERVAL;
import static by.stations.subway.common.Constants.ZOOM_STREET;
import static by.stations.subway.common.Utils.buildAlertMessageNoGps;
import static by.stations.subway.common.Utils.checkGps;
import static by.stations.subway.common.Utils.checkPlayServices;
import static by.stations.subway.common.Utils.hasLocationPermission;
import static by.stations.subway.common.Utils.isOnline;
import static by.stations.subway.common.Utils.showToast;


public class MapPresenter implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapPresenter.class.getSimpleName();
    private CompositeDisposable disposable = new CompositeDisposable();
    private RxLocation rxLocation;
    private Location location;
    private LocationRequest locationRequest;
    private MapView view;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Polyline polyline;
    public boolean isClickLocation;

    @Inject
    MapApi mapApi;
    @Inject
    StationApi stationApi;

    public MapPresenter(MapView view, RxLocation rxLocation) {
        MyApplication.getApplicationComponent().inject(this);
        this.view = view;
        this.rxLocation = rxLocation;
        if (view != null) {
            SupportMapFragment supportMapFragment = view.getMapFragmentManager();
            try {
                supportMapFragment.getMapAsync(this);
            } catch (Exception ex) {
                Log.e(TAG, "MapPresenter: " + view.getActivity().getResources().getString(R.string.google_map_create_error));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (view != null) {
            attachView(view);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (view != null) {
            try {
                createRxLocationRequest();
            } catch (IllegalStateException e) {
                Log.e(TAG, "onConnected: " + e.getCause());
            }
        }
    }

    public void createRxLocationRequest() {
        locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(DISPLACEMENT);
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (map == null) {
            Log.e(TAG, "createRxLocationRequest: " + view.getActivity().getResources().getString(R.string.google_map_create_error));
        } else {
            if (!hasLocationPermission(view.getActivity())) {
                return;
            }
            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setRotateGesturesEnabled(false);
            map.getUiSettings().setCompassEnabled(false);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setOnMarkerClickListener(this);
            map.setMyLocationEnabled(true);

            updateCurrentLocation(DEFAULT_ZOOM);
        }
    }

    @SuppressLint("MissingPermission")
    private void updateCurrentLocation(float zoom) {
        disposable.add(rxLocation.location().updates(locationRequest)
                .flatMap(location -> rxLocation.location().lastLocation().toObservable())
                .filter(location1 -> rxLocation != null)
                .subscribe(location -> {
                    this.location = location;
                    if (!hasLocationPermission(view.getActivity())) {
                        return;
                    }
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
                    getStations();
                })
        );
    }

    public void getStations() {
        stationApi.getStations()
                .flatMap(Observable::just)        //get list from response
                .flatMapIterable(list -> list)    //make the list iterable
                .filter(v -> v.getLongitude() != 0 || v.getLatitude() != 0) // pass all pin that coordinates != 0
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dataResponse -> MapHelper.getStationList(dataResponse, map, view.getActivity()),
                        error -> Log.e(TAG, "Got Error:" + error)
                );
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (view != null) {
            showToast(R.string.error_sorry, view.getActivity());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (view != null) {
            if (isOnline(view.getActivity())) {
                Integer clickCount = (Integer) marker.getTag();
                if (clickCount != null && location != null) {
                    String departure = String.valueOf(location.getLatitude() + "," + location.getLongitude());
                    String arrival = String.valueOf(marker.getPosition().latitude + "," + marker.getPosition().longitude);
                    String key = view.getActivity().getResources().getString(R.string.google_maps_key);
                    if (polyline != null) {
                        polyline.remove(); // for delete previous route
                    }
                    createRoute(departure, arrival, key);
                }
            } else {
                showToast(R.string.error_connection, view.getActivity());
            }
        }
        return true; // true - not display place name
    }

    private void createRoute(String departure, String arrival, String key) {
        mapApi.getMapRoute(departure, arrival, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> polyline = MapHelper.createRoute(response, map, view.getActivity()),
                        error -> Log.e(TAG, "Got Error:" + error)
                );
    }

    public void setUpLocation(Activity activity) {
        if (!hasLocationPermission(activity)) {
            requestRxPermission();
        } else {
            if (checkGps(activity) && checkPlayServices(activity)) {
                buildGoogleApiClient();
                if (isClickLocation && map != null) {
                    updateCurrentLocation(ZOOM_STREET);// when click to get current location
                }
            } else {
                buildAlertMessageNoGps(activity, activity);
            }
        }
    }

    public void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(view.getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    public void requestRxPermission() {
        RxPermissions rxPermissions = new RxPermissions(view.getActivity());
        rxPermissions.requestEach(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(permission -> { // will emit 2 Permission objects
                    if (permission.granted) {
                        if (checkPlayServices(view.getActivity())) {
                            buildGoogleApiClient();
                        }
                    }
                }, throwable -> Log.e("TAG", "onError," + throwable.getMessage()));
    }

    public void attachView(MapView view) {
        this.view = view;
        setUpLocation(view.getActivity());
    }

    public void detachView() {
        this.view = null;
        disposable.clear();
    }

}
