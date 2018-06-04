package android.coolweather.com.coolweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.coolweather.com.coolweather.gson.Forecast;
import android.coolweather.com.coolweather.gson.Weather;
import android.coolweather.com.coolweather.service.AutoUpdateServic;
import android.coolweather.com.coolweather.util.HttpUtil;
import android.coolweather.com.coolweather.util.Utility;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class WeatherActivity extends AppCompatActivity {
private ScrollView weatherLayout;
private TextView titleCity;
private TextView titleUpdateTime;
private TextView degreeText;
private TextView weatherInfoText;
private LinearLayout forecastLayout;
private TextView aqiText;
private TextView pm25Text;
private TextView comforText;
private TextView carWashText;
private TextView sprotText;
private ImageView bingPicImg;
public SwipeRefreshLayout swipeRefresh;
public DrawerLayout drawerLayout;
    public LocationClient mLocationClien;
    public TextView postionText;
    public TextView locantionText;

private Button navButton;
    /**保存天气ID **/
   public String weatherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        /**位置**/
        mLocationClien=new LocationClient(getApplicationContext());
        mLocationClien.registerLocationListener(new WeatherActivity.MyLocationListener());
        setContentView(R.layout.activity_weather);
        //初始化各种控件
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_content);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comforText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sprotText=(TextView)findViewById(R.id.sport_text);
        /**滑动菜单**/
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //地理位置空间
        postionText=(TextView)findViewById(R.id.postion_text);
        locantionText=(TextView)findViewById(R.id.locantion_text);
        //下拉条
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);


        // setContentView(R.layout.activity_baidu2);


        //查看缓存
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic=prefs.getString("bing_pic",null);
        String weatherString=prefs.getString("weather",null);

        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
        if(weatherString!=null){
            //有缓存时直接解析天气数据
            Weather weather= Utility.hangdleWeatherResponse(weatherString);
            weatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            //String weatherId=getIntent().getStringExtra("weather_id");
            weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
                //loadBingPic();
            }
        });

        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED) {

            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }if(ContextCompat.checkSelfPermission(WeatherActivity.this,Manifest.
                permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(WeatherActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(WeatherActivity.this,permissions,1);
        }else{
           // requestLocation();
            new Thread(){
                @Override
                public void run() {
                    requestLocation();
                }
            }.start(); ;
        }
    }

    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /**
     * 根据天气id请求城市天气情况信息**/
    public void requestWeather(final  String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="
                +weatherId+"&key=b562bbd810f14b2e84d9947b8929ce0d";
        HttpUtil.sendOkHttpRequest(weatherUrl,new Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.hangdleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }
    /**
     * 处理并且展示weather实体类的数据**/
    public void showWeatherInfo(Weather weather){
       if(weather !=null&&"ok".equals(weather.status)) {
           String cityName = weather.basic.cityName;
           String updateTime = weather.basic.update.updateTime;
           String degree = weather.now.tmperature + "℃";
           String weatherInfo = weather.now.more.info;
           //titleCity.setText(updateTime);
           titleCity.setText(cityName);
           titleUpdateTime.setText(updateTime);
           degreeText.setText(degree);
           weatherInfoText.setText(weatherInfo);
           forecastLayout.removeAllViews();
           for (Forecast forecast : weather.forecastList) {
               View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
               TextView dateText = (TextView) view.findViewById(R.id.date_text);
               TextView infoText = (TextView) view.findViewById(R.id.info_text);
               TextView maxText = (TextView) view.findViewById(R.id.max_text);
               TextView minText = (TextView) view.findViewById(R.id.min_text);
               dateText.setText(forecast.date);
               infoText.setText(forecast.more.info);
               maxText.setText(forecast.temperature.max);
               minText.setText(forecast.temperature.min);
               forecastLayout.addView(view);
           }
           if (weather.aqi != null) {
               aqiText.setText(weather.aqi.city.aqi);
               pm25Text.setText(weather.aqi.city.pm25);
           }
           String comfort = "舒适度：" + weather.suggestion.comfort.info;
           String carWash = "洗车指数:" + weather.suggestion.comfort.info;
           String sport = "运动建议：" + weather.suggestion.sport.info;
           comforText.setText(comfort);
           carWashText.setText(carWash);
           sprotText.setText(sport);
           weatherLayout.setVisibility(View.VISIBLE);

           //激活AutoUpdateReceiver服务
           Intent intent=new Intent(this, AutoUpdateServic.class);
           startService(intent);
       }else{
           Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
       }
    }
    /**位置**/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!= PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public  class  MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPostion=new StringBuilder();
            /**currentPostion.append("纬度： ").append(bdLocation.getLatitude()).append("\n");
            currentPostion.append("经线： ").append(bdLocation.getLongitude()).append("\n");
            currentPostion.append("国家： ").append(bdLocation.getCountry()).append("\n");
            currentPostion.append("省： ").append(bdLocation.getProvince()).append("\n");
            currentPostion.append("市： ").append(bdLocation.getCity()).append("\n");
            currentPostion.append("区： ").append(bdLocation.getDistrict()).append("\n");
            currentPostion.append("街道： ").append(bdLocation.getStreet()).append("\n");
            currentPostion.append("定位方式：");**/
            /**if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                currentPostion.append("GPS:");
            }else{
                currentPostion.append("网络:");
            }**/
            currentPostion.append("纬度:").append(bdLocation.getLatitude()).append("\n");
            currentPostion.append("经度:").append(bdLocation.getLongitude());
            postionText.setText(currentPostion);
            locantionText.setText(bdLocation.getCity()+bdLocation.getDistrict());
        }
    }
    private void requestLocation(){
        initLocation();
        mLocationClien.start();
    }
    private void initLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClien.setLocOption(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClien.stop();
    }
}
