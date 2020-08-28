package com.example.http;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String detail = "";
    private ArrayList<City> Cities;
    private ImageView btn;
    private TextView cityName,upDateTime,wind,week,weather,humidity;
    @SuppressLint("HandlerLeak")
    final Handler myHandler=new Handler(){
        public void handleMessage(android.os.Message message){

            switch (message.what){
                case 1:
                    upDateText((City)message.obj);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();
        //点击按钮访问
        ImageView btn = findViewById(R.id.title_city_update);//绑定ID
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//监听按钮
                new Thread(new Runnable() {//创建子线程
                    @Override
                    public void run() {
                        try {
                            String json=getwebinfo();
                            City newCity=parseEasyJson(json);
                            if (newCity!=null){
                                Message message=new Message();
                                message.what=1;
                                message.obj=newCity;
                                myHandler.sendMessage(message);
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();//启动子线程
            }
        });
    }

    private City parseEasyJson(String json) {

        City city = new City();
        try {
            JSONObject jsonObject = new JSONObject(json);


            city.setName(jsonObject.getString("cityEn"));
            city.setCountry(jsonObject.getString("country"));
            city.setWeek(jsonObject.getString("week"));
            city.setCityId(jsonObject.getString("cityid"));
            city.setDate(jsonObject.getString("date"));
            city.setWea(jsonObject.getString("wea"));
            city.setTem(jsonObject.getString("tem"));
            city.setWin(jsonObject.getString("win"));
            city.setUpdate_time(jsonObject.getString("update_time"));
            city.setHumidity(jsonObject.getString("humidity"));

            return city;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getwebinfo() {
        try {
            //1,--创建URL
            URL url = new URL("https://yiketianqi.com/api?version=v61&appid=37396168&appsecret=u789kFOb");//放网站
            //2,--openConnection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.setRequestMethod("GET");
            //3，-InputStream
            InputStream inputStream = httpURLConnection.getInputStream();
            //4，-InputStreamReader
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            //5，-BufferedReader
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuilder buffer = new StringBuilder();
            String temp = null;

            while ((temp = bufferedReader.readLine()) != null) {
                //
                buffer.append(temp);
            }
            bufferedReader.close();//记得关闭
            reader.close();
            inputStream.close();
            String sb = buffer.toString();


            Log.e("MAIN", sb);
            return sb;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @SuppressLint("SetTextI18n")
    private void upDateText(City city){
            cityName.setText(city.getName());
            upDateTime.setText("更新时间："+city.getUpdate_time());//更新时间
            wind.setText(city.getWin());//
            weather.setText(city.getWea());
            week.setText(city.getWeek());
            humidity.setText("湿度："+city.getHumidity());
            Toast.makeText(MainActivity.this,"更新成功", Toast.LENGTH_SHORT).show();

    }

    private void initial(){
        cityName=findViewById(R.id.todayinfo1_cityName);
        upDateTime=findViewById(R.id.todayinfo1_updateTime);
        wind=findViewById(R.id.todayinfo2_wind);
        weather=findViewById(R.id.todayinfo2_weatherState);//
        week=findViewById(R.id.today_week);
        humidity=findViewById(R.id.todayinfo1_humidity);
    }



}