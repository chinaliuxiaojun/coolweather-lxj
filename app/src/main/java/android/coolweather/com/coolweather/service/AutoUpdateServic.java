package android.coolweather.com.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.coolweather.com.coolweather.gson.Weather;
import android.coolweather.com.coolweather.util.HttpUtil;
import android.coolweather.com.coolweather.util.Utility;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateServic extends Service {
    public AutoUpdateServic() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPic();
        updateWeather();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        //int anHour=1*1*30*1000;//8小时的毫秒数
        int anHour=8*60*60*1000;//8小时的毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateServic.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新天气信息**/
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存直接解析天气数据
            Weather weather= Utility.hangdleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?city="+weatherId+
                    "&key=b562bbd810f14b2e84d9947b8929ce0d";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                //解析收到的GSON
                Weather weather=Utility.hangdleWeatherResponse(responseText);
                if(weather!=null&&"ok".equals(weather.status)){
                    SharedPreferences.Editor editor=PreferenceManager
                            .getDefaultSharedPreferences(AutoUpdateServic.this).edit();
                    editor.putString("weather",responseText);
                    editor.apply();
                }
                }
            });
        }
    }
    /**
     * 更新必应的魅力一图**/
    private  void updateBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            String bingPic=response.body().string();
            SharedPreferences.Editor editor=PreferenceManager
                    .getDefaultSharedPreferences(AutoUpdateServic.this).edit();
            editor.putString("bing_pic",bingPic);
            editor.apply();
            }
        });
    }
}
