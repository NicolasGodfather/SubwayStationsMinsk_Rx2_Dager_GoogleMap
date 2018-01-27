package by.stations.subway.di;

import javax.inject.Singleton;

import by.stations.subway.di.module.ApplicationModule;
import by.stations.subway.di.module.RestModule;
import by.stations.subway.mvp.MapPresenter;
import by.stations.subway.ui.MainActivity;
import by.stations.subway.ui.MapFragment;
import dagger.Component;


@Singleton
@Component(modules = {ApplicationModule.class, RestModule.class})
public interface ApplicationComponent {

    void inject(MainActivity activity);

    void inject(MapFragment fragment);

    void inject(MapPresenter presenter);

}
