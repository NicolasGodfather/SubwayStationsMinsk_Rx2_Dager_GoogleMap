package by.stations.subway.rest.response;
import com.google.gson.annotations.SerializedName;

public class StationResponse {

    @SerializedName("name")
    private String name;
    @SerializedName("latitude")
    private int latitude;
    @SerializedName("longitude")
    private int longitude;

    public StationResponse() {
    }

    public StationResponse(String name, int latitude, int longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

}