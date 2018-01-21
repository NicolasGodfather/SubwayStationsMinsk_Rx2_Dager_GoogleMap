package by.stations.subway.di;

import javax.inject.Singleton;

import by.stations.subway.common.NetworkManager;
import by.stations.subway.di.module.ApplicationModule;
import by.stations.subway.di.module.RestModule;
import by.stations.subway.ui.MainActivity;
import dagger.Component;


@Singleton
@Component(modules = {ApplicationModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);

    void inject(NetworkManager manager);

}
