package by.stations.subway.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.SupportMapFragment;
import com.patloew.rxlocation.RxLocation;

import java.util.concurrent.TimeUnit;

import by.stations.subway.MyApplication;
import by.stations.subway.R;
import by.stations.subway.mvp.MapPresenter;
import by.stations.subway.mvp.MapView;

import static by.stations.subway.common.Utils.isOnline;
import static by.stations.subway.common.Utils.showToast;


public class MapFragment extends Fragment implements View.OnClickListener, MapView {

    public static final String TAG = MainActivity.class.getSimpleName();
    private MapPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplicationComponent().inject(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxLocation rxLocation = new RxLocation(getActivity());
        rxLocation.setDefaultTimeout(15, TimeUnit.SECONDS);
        presenter = new MapPresenter(this, rxLocation);
        ImageView circleLocation = view.findViewById(R.id.circleLocation);
        circleLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isOnline(getActivity())) {
            presenter.isClickLocation = true;
            presenter.setUpLocation(getActivity());
        } else {
            showToast(R.string.error_connection, getActivity());
        }
    }

    @Override
    public SupportMapFragment getMapFragmentManager() {
        return (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    }

}
