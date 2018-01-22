package by.stations.subway.common;

import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

import by.stations.subway.R;
import by.stations.subway.rest.response.RoutesResponse;
import by.stations.subway.rest.response.Station;
import by.stations.subway.ui.custom_view.StationMarkerView;

public class MapHelper {

    public static List<Station> getStationList(List<Station> response, GoogleMap map, Activity activity) {
        List<Station> stationList = new ArrayList<>(response);
        for (Station station : stationList) {
            StationMarkerView cargoMarkerView = new StationMarkerView(activity, map, station.getName());
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(station.getLatitude(), station.getLongitude()))
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) // default icon with changed color
                            .icon(BitmapDescriptorFactory.fromBitmap(cargoMarkerView.getBitmapIcon()))
                            .title(station.getName())
            );
            marker.setTag(0);
        }
        return stationList;
    }

    public static void createRoute(RoutesResponse response, GoogleMap map, Activity activity) {
        String points = response.getRoutes().get(0).getOverviewPolyline().getPoints();
        List<LatLng> decodedPath = PolyUtil.decode(points);
        PolylineOptions polyline = new PolylineOptions()
                .addAll(decodedPath)
                .color(activity.getResources().getColor(R.color.colorPrimary));
        double lat = (response.getRoutes().get(0).getLegs().get(0).getStartLocation().getLat() +
                response.getRoutes().get(0).getLegs().get(0).getEndLocation().getLat()) / 2;
        double lon = (response.getRoutes().get(0).getLegs().get(0).getStartLocation().getLng() +
                response.getRoutes().get(0).getLegs().get(0).getEndLocation().getLng()) / 2;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 12));
        map.addPolyline(polyline);
        map.addMarker(new MarkerOptions()
                .position(decodedPath.get(decodedPath.size() - 1)));
    }

}
