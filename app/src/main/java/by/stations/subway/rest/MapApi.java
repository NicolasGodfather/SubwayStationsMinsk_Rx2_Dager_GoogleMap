package by.stations.subway.rest;

import by.stations.subway.rest.response.RoutesResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MapApi {

    @GET(ApiMethods.GET_JSON)
    Observable<RoutesResponse> getMapRoute(@Query(value = "origin") String departureId,
                                           @Query(value = "destination") String arrivalId,
                                           @Query(value = "waypoints") String waypointsID,
                                           @Query(value = "key") String apiKey);

}
