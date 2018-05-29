package android.coolweather.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lxj on 2018/5/29.
 */

public class province extends DataSupport {
    private int id;
    private String provinceName;
    private int PrvinceCode;
    public int getId(){
        return id;
    }
    public void setId(int id){
     this.id=id;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName=provinceName;
    }
    public int getPrvinceCode(){
        return PrvinceCode;
    }
    public void setPrvinceCode(int prvinceCode){
        this.PrvinceCode=prvinceCode;
    }
}
