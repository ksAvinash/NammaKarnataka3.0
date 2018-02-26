package smartAmigos.com.nammakarnataka.helper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by avinashk on 25/02/18.
 */

public class gallery_adapter implements Parcelable {
    private String name, url;

    public gallery_adapter() {

    }

    public gallery_adapter(String url, String name){
        this.name = name;
        this.url = url;
    }

    protected gallery_adapter(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<gallery_adapter> CREATOR = new Creator<gallery_adapter>() {
        @Override
        public gallery_adapter createFromParcel(Parcel in) {
            return new gallery_adapter(in);
        }

        @Override
        public gallery_adapter[] newArray(int size) {
            return new gallery_adapter[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }




}
