package by.stations.subway.rest.response;
import com.google.gson.annotations.SerializedName;

public class Station {

    @SerializedName("name")
    private String name;
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("longitude")
    private float longitude;

    public Station() {
    }

    public Station(String name, int latitude, int longitude) {
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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

}