package by.stations.subway.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.view.ContextThemeWrapper;

import by.stations.subway.R;

public class Utils {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return ((networkInfo != null && networkInfo.isConnected()));
    }

    public static boolean checkGps(Activity activity) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return !(manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER));
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

}
