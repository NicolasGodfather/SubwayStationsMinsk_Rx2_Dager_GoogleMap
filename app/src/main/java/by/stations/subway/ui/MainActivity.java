package by.stations.subway.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import by.stations.subway.MyApplication;
import by.stations.subway.R;
import io.reactivex.disposables.Disposable;

import static by.stations.subway.common.Utils.showToast;
import static by.stations.subway.common.Utils.shutDownApp;
import static by.stations.subway.common.Utils.startNetworkObservable;


public class MainActivity extends FragmentActivity {

    public boolean doubleBackToExitPressedOnce = false;
    private Disposable networkDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getApplicationComponent().inject(this);

        networkDisposable = startNetworkObservable(this);

        MapFragment mapFragment = new MapFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainContainer, mapFragment, MapFragment.TAG);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkDisposable != null && !networkDisposable.isDisposed()) {
            networkDisposable.dispose();
        }
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
