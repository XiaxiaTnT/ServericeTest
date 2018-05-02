package com.example.x.servicetest;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.Serializable;

import static android.widget.Toast.*;

public class MyService extends Service {
    private AlarmManager manager=null;
    public LocationClient mlocationClient=null;
    public BDLocationListener myListener=new MyLocationListener();
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("MyService","onCreate executed");


        mlocationClient=new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(myListener);



        LocationClientOption option=new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //option.setPriority(LocationClientOption.NetWorkFirst);
        option.setPriority(LocationClientOption.GpsFirst);
        option.setCoorType("bd09ll");
        //option.setIgnoreKillProcess(true);
        //option.setScanSpan(1000);
        mlocationClient.setLocOption(option);


        Intent intent=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0, intent,0);
        Notification notification =new NotificationCompat.Builder(this).setContentTitle("this is content title").setContentText("this is content text").setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher).
                setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)).setContentIntent(pi).build();
        startForeground(1,notification);

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.d("MyService","onStartCommand executed");
        String data=intent.getStringExtra("data");
//        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Looper.prepare();
////                Toast.makeText(getApplicationContext(),"running", Toast.LENGTH_LONG).show();
////                Looper.loop();
//            }
//        }).start();
        mlocationClient.restart();
        if(mlocationClient==null){
            Log.d("client","null");
        }
        if(!mlocationClient.isStarted()){
            Log.d("locationclientStarted","null");
        }
        mlocationClient.requestLocation();

        manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int timegap=2*10*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+timegap;
        Intent i=new Intent(this,MyService.class);
        i.putExtra("data","hi");
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
        //return Service.START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
        //Intent intent=new Intent("com.example.x.servicetest.destory");
        //sendBroadcast(intent);
        Intent i=new Intent(this,MyService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        mlocationClient.unRegisterLocationListener(myListener);
        mlocationClient.stop();
        Log.d("MyService","onDestory executed");
        super.onDestroy();


    }
    private void getLocationInfo(BDLocation bdLocation){
        if(bdLocation!=null){
            double lat=bdLocation.getLatitude();
            double lng=bdLocation.getLongitude();
            String way="";
            if(bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                way="Network";
            }else if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
                way="GPS";
            }
            String str="lat:"+lat+" lng:"+lng+" way:"+way;
            Log.d("location",str);
            Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
        }
    }
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation==null){
                return;
            }
            //mlocationClient.stop();
            getLocationInfo(bdLocation);
        }
    }
}
