package android.coolweather.com.coolweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.coolweather.com.coolweather.gson.Weather;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //public static boolean openSharedPreferences=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       
    }



    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null)!=null){
           //runDialog();
            OpenAcitviy();
        }

    }

   /** void runDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                //.setIcon(R.mipmap.icon)//设置标题的图片
                .setTitle("温馨提示")//设置对话框的标题
                .setMessage("是否直接打开上次查看的城市")//设置对话框的内容
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSharedPreferences=false;
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSharedPreferences=true;
                        OpenAcitviy();
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }**/
    void OpenAcitviy(){
        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }
}
