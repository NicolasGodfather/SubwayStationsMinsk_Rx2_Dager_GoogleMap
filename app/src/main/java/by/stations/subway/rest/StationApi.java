package by.stations.subway.rest;

import by.stations.subway.rest.response.StationResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;


public interface StationApi {

    @GET(ApiMethods.GET_STATIONS)
    Observable<StationResponse> getStations();

}
