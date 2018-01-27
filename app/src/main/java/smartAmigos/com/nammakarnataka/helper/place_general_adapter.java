package smartAmigos.com.nammakarnataka.helper;

/**
 * Created by avinashk on 09/01/18.
 */

public class place_general_adapter {
    private int id;
    private String title, description, district;
    private String bestSeason,additionalInformation, category;
    private Double latitude, longitude;

    public place_general_adapter(int id, String title, String description, String district, String bestSeason, String additionalInformation, Double latitude, Double longitude, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.district = district;
        this.bestSeason = bestSeason;
        this.additionalInformation = additionalInformation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {

        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setBestSeason(String bestSeason) {
        this.bestSeason = bestSeason;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }


    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDistrict() {
        return district;
    }

    public String getBestSeason() {
        return bestSeason;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
