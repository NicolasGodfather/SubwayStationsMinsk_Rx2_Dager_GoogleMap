package by.stations.subway.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import by.stations.subway.MyApplication;
import by.stations.subway.R;

import static by.stations.subway.common.Utils.showToast;
import static by.stations.subway.common.Utils.shutDownApp;


public class MainActivity extends FragmentActivity {

    public boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getApplicationComponent().inject(this);

        MapFragment mapFragment = new MapFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainContainer, mapFragment, MapFragment.TAG);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            shutDownApp(this);
        } else {
            this.doubleBackToExitPressedOnce = true;
            showToast(R.string.back_double_click, this);
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1000);
        }
    }

}
