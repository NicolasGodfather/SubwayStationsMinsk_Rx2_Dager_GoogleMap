package by.stations.subway.common;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import by.stations.subway.MyApplication;
import io.reactivex.Observable;

import static by.stations.subway.common.Utils.isOnline;

public class NetworkManager {

    @Inject
    Context mContext;
    private static final String TAG = NetworkManager.class.getSimpleName();

    public NetworkManager() {
        MyApplication.getApplicationComponent().inject(this);
    }

    private Callable<Boolean> isReachableCallable() {
        return () -> {
            try {
                if (!isOnline(mContext)) {
                    return false;
                }

                URL url = new URL("https://my-json-server.typicode.com");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(2000);
                urlc.connect();

                return true;
            } catch (Exception e) {
                return false;
            }
        };
    }

    public Observable<Boolean> getNetworkObservable() {
        return Observable.fromCallable(isReachableCallable());
    }

}