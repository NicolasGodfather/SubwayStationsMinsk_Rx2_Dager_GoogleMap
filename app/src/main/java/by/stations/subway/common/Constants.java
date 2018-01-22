package by.stations.subway.common;

import com.google.android.gms.maps.model.LatLng;

public class Constants {

    public static final String URL_STATIONS = "https://my-json-server.typicode.com/BeeWhy/metro/";
    public static final String URL_MAPS = "https://maps.googleapis.com/maps/api/directions/";
    public static final int MY_PERMISSIONS_REQUEST_CODE = 7000;
    public static final int PLAY_PERMISSIONS_RES_REQUEST = 7001;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FATEST_INTERVAL = 3000;
    public static final int DISPLACEMENT = 10;
    public static final float DEFAULT_ZOOM = 11.0f;
    public static final int ZOOM_CITY = 13;
    public final static LatLng MINSK_CITY = new LatLng(53.9, 27.56667);

}
