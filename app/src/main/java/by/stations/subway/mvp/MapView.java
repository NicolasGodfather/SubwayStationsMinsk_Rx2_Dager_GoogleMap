package by.stations.subway.mvp;

import android.app.Activity;

import com.google.android.gms.maps.SupportMapFragment;

public interface MapView {

    SupportMapFragment getMapFragmentManager();

    Activity getActivity();

}
