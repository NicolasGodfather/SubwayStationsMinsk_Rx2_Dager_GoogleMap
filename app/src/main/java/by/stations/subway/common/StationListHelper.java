package by.stations.subway.common;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import by.stations.subway.rest.response.Station;
import by.stations.subway.ui.custom_view.StationMarkerView;

public class StationListHelper {

    public static List<Station> getStationList(List<Station> response, GoogleMap map, Activity activity) {
        List<Station> stationList = new ArrayList<>(response);
        for (Station station : stationList) {
            StationMarkerView cargoMarkerView = new StationMarkerView(activity, map, station.getName());
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(station.getLatitude(), station.getLongitude()))
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) // default icon
                            .icon(BitmapDescriptorFactory.fromBitmap(cargoMarkerView.getBitmapIcon()))
                            .title(station.getName())
            );
            marker.setTag(0);
        }
        return stationList;
    }

}
