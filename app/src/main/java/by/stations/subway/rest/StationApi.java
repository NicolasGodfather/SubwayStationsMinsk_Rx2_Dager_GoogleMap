package by.stations.subway.rest;

import java.util.List;

import by.stations.subway.rest.response.Station;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;


public interface StationApi {

    @GET(ApiMethods.GET_STATIONS)
    Observable<List<Station>> getStations();

}
