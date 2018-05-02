package com.example.x.servicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals("com.example.x.servicetest.destory")){
            Intent service =new Intent(context,MyService.class);
            context.startService(service);
        }
    }
}
