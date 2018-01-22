package by.stations.subway.ui.custom_view;


import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.ui.IconGenerator;

import by.stations.subway.R;

import static android.view.Gravity.CENTER;
import static by.stations.subway.common.Utils.replaceSpace;


public class StationMarkerView {

    private Activity activity;
    private GoogleMap map;
    private Bitmap icon;
    private FrameLayout.LayoutParams layoutParams;

    public StationMarkerView(Activity activity, GoogleMap map, String station) {
        this.map = map;
        this.activity = activity;
        if (activity.getApplicationContext() != null) {
            IconGenerator mIconGenerator = new IconGenerator(activity.getApplicationContext());
            mIconGenerator.setBackground(null);
            mIconGenerator.setContentView(getView(station));
            icon = mIconGenerator.makeIcon();
        }
    }

    private View getView(String distance) {
        View markerView = activity.getLayoutInflater().inflate(R.layout.item_station_marker, null);
        TextView txtLengthWay = markerView.findViewById(R.id.txtLengthWay);
        layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = CENTER;
        txtLengthWay.setText(replaceSpace(distance));
        return markerView;
    }

    public Bitmap getBitmapIcon() {
        return icon;
    }
}
