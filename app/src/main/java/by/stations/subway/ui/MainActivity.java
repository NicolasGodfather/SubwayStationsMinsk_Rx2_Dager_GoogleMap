package by.stations.subway.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import by.stations.subway.MyApplication;
import by.stations.subway.R;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getApplicationComponent().inject(this);

        MapFragment mapFragment = new MapFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainContainer, mapFragment, MapFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

}
