package android.coolweather.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lxj on 2018/5/29.
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int prvinceCode;

    public int getPrvinceCode() {
        return prvinceCode;
    }

    public void setPrvinceCode(int prvinceCode) {
        this.prvinceCode = prvinceCode;
    }

    public String getProvinceName() {

        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
