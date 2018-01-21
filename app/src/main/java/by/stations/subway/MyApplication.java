package by.stations.subway;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import by.stations.subway.di.ApplicationComponent;
import by.stations.subway.di.DaggerApplicationComponent;
import by.stations.subway.di.module.ApplicationModule;

public class MyApplication extends Application{

    private static ApplicationComponent applicationComponent;
    @Inject
    Context context;

    Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initComponent();
    }

    private void initComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
    }

    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

}
