package by.stations.subway.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import static by.stations.subway.common.Constants.MY_PERMISSIONS_REQUEST_CODE;


public class Permissions {

    public static boolean checkLocationPermission(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        }, MY_PERMISSIONS_REQUEST_CODE);
    }

  /*  public static boolean checkLocationPermission(Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe();
        return rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION) &&
                rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION);
    }*/

}
