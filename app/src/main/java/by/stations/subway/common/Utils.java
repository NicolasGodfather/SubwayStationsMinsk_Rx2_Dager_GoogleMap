package by.stations.subway.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import by.stations.subway.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static by.stations.subway.common.Constants.PLAY_PERMISSIONS_RES_REQUEST;


public class Utils {

    public static Disposable startNetworkObservable(Activity activity) {
        return ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnectedToInternet -> {
                    if (!isConnectedToInternet) {
                        showToast(R.string.error_connection, activity);
                    } else {
                        showToast(R.string.success_connection, activity);
                    }
                });
    }

    public static boolean hasLocationPermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkGps(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return !(manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return ((networkInfo != null && networkInfo.isConnected()));
    }

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_PERMISSIONS_RES_REQUEST).show();
            } else {
                showToast(R.string.not_support, activity);
            }
            return false;
        }
        return true;
    }

    public static void buildAlertMessageNoGps(Activity activity, Context context) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                    android.R.style.Theme_Material_Light_Dialog_Alert));
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(R.string.gps_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, id) -> activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showToast(@StringRes int message, Context context) {
        Toast toast = Toast.makeText(context, context.getText(message), Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(toast::cancel, 3000);
    }

    public static String replaceSpace(String line) {
        return line.replace(" ", "\n");
    }

    public static void shutDownApp(Activity activity) {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(homeIntent);
    }

}
