package com.example.x.servicetest;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import static android.widget.Toast.*;

public class MyService extends Service {
    private AlarmManager manager = null;
    public LocationClient mlocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public String info=null;
    private int Times = 0;//control for the information to be the second time it get the location information

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService", "onCreate executed");


        mlocationClient = new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(myListener);


        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst);
        option.setCoorType("bd09ll");
        //option.setIgnoreKillProcess(true);
        option.setScanSpan(5000);
        mlocationClient.setLocOption(option);


        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this).setContentTitle("this is content title").setContentText("this is content text").setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher).
                setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setContentIntent(pi).build();
        startForeground(1, notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Times = 0;
        Log.d("MyService", "onStartCommand executed");
//        String data = intent.getStringExtra("data");
//        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Looper.prepare();
////                Toast.makeText(getApplicationContext(),"running", Toast.LENGTH_LONG).show();
////                Looper.loop();
//            }
//        }).start();
//        Toast.makeText(getApplicationContext(), "onStartCommand", Toast.LENGTH_SHORT).show();
        mlocationClient.restart();
        if (mlocationClient == null) {
            Log.d("client", "null");
        }
        if (!mlocationClient.isStarted()) {
            Log.d("locationclientStarted", "null");
        }
        mlocationClient.requestLocation();

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int timegap = 20 * 1000;//time for the gap
        long triggerAtTime = SystemClock.elapsedRealtime() + timegap;
        Intent i = new Intent(this, MyService.class);
        //i.putExtra("data", "hi");
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
        //return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        //Intent intent=new Intent("com.example.x.servicetest.destory");
        //sendBroadcast(intent);.
        Intent i = new Intent(this, MyService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        mlocationClient.unRegisterLocationListener(myListener);
        mlocationClient.stop();
        Log.d("MyService", "onDestory executed");
        super.onDestroy();


    }

    private void getLocationInfo(BDLocation bdLocation) {
        if (bdLocation != null) {
            double lat = bdLocation.getLatitude();
            double lng = bdLocation.getLongitude();
            String way = "unkonwn";
            if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                way = "Network";
            } else if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                way = "GPS";
            }
            String str = "lat:" + lat + " lng:" + lng + " way:" + way;
            Log.d("location", str);
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
//            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            String Phonenum = telephonyManager.getLine1Number();
            String Phonenum="";
            PostInfo(lat,lng,Phonenum,way);

        }
    }
    public void PostInfo(double lat,double lng,String Phonenum,String way){
        Toast.makeText(getApplicationContext(),"inPostinfo",Toast.LENGTH_SHORT).show();
        //Phonenum=Phonenum.substring(3,Phonenum.length());
        Phonenum="18795865708";
        //get data
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date=simpleDateFormat.format(new java.util.Date());
        info="insert into info values ('"+date+"','"+Phonenum+"','"+lat+"','"+lng+"','"+way+"')";
        Log.d("info",info);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url=new URL("http://120.79.46.162:8080/serverlet/MyServlet");//address unknown
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestProperty("charset","UTF-8");
                    connection.setDoOutput(true);
                    DataOutputStream outputStream=new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(info);
                    if(connection.getResponseCode()==200){
                        InputStream inputStream=connection.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder=new StringBuilder();
                        String line;
                        while((line=reader.readLine())!=null){
                            stringBuilder.append(line);
                        }
                        reader.close();
                    }
                    outputStream.close();
                }catch (MalformedURLException e){
                    //e.printStackTrace();
                    e.getMessage();
                }catch (IOException e){
                    //e.printStackTrace();
                    e.getMessage();
                }
            }
        }).start();

    }
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation==null){
                return;
            }
            Times=Times+1;
            //mlocationClient.stop();
            if(Times==2) {
                getLocationInfo(bdLocation);
            }
        }
    }
}
