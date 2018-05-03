package com.example.x.servicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals("com.example.x.servicetest.destory")){
            Intent service =new Intent(context,MyService.class);
            context.startService(service);
        }
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")||intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")){
            Toast.makeText(context,"received",Toast.LENGTH_SHORT).show();
            Intent intent1=new Intent(context,MyService.class);
            context.startService(intent1);
        }
    }
}
