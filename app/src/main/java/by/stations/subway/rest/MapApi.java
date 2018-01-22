package by.stations.subway.rest;

import by.stations.subway.rest.response.RoutesResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Example request:
 * https://maps.googleapis.com/maps/api/directions/json?origin=53.9403994,27.6532641&
 * destination=53.922115325927734,27.600507736206055&key=AIzaSyB4NczlGS1_v3ERF9r_J50JMhcZf96LoBo
 *
 * Replace my key to yours for create the route!!!
 */
public interface MapApi {

    @GET(ApiMethods.GET_JSON)
    Observable<RoutesResponse> getMapRoute(@Query(value = "origin") String departure,
                                           @Query(value = "destination") String arrival,
                                           @Query(value = "key") String apiKey);

}
