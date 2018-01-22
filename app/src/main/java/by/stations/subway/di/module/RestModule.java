package by.stations.subway.di.module;

import javax.inject.Singleton;

import by.stations.subway.rest.MapApi;
import by.stations.subway.rest.RestClient;
import by.stations.subway.rest.RestClientMaps;
import by.stations.subway.rest.StationApi;
import dagger.Module;
import dagger.Provides;

@Module
public class RestModule {

    private RestClient restClient;
    private RestClientMaps restClientMaps;

    public RestModule() {
        this.restClient = new RestClient();
        this.restClientMaps = new RestClientMaps();
    }

    @Provides
    @Singleton
    public StationApi provideStationApi() {
        return restClient.createService(StationApi.class);
    }

    @Provides
    @Singleton
    public MapApi provideMapApi() {
        return restClientMaps.createService(MapApi.class);
    }

}
