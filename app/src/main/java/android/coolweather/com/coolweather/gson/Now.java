package android.coolweather.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lxj on 2018/6/1.
 */

public class Now {
    @SerializedName("tmp")
    public String tmperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public  String info;
    }
}
