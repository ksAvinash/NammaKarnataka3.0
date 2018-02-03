package smartAmigos.com.nammakarnataka.helper;

/**
 * Created by avinashk on 03/02/18.
 */

public class place_images_metadata {

    private int place_id;
    private String s3_url, description, uploader;

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public void setS3_url(String s3_url) {
        this.s3_url = s3_url;
    }

    public String getS3_url() {

        return s3_url;
    }

    public int getPlace_id() {

        return place_id;
    }


    public String getDescription() {
        return description;
    }

    public String getUploader() {
        return uploader;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public place_images_metadata(int place_id, String s3_url, String description, String uploader) {

        this.place_id = place_id;
        this.s3_url = s3_url;
        this.description = description;
        this.uploader = uploader;
    }
}
