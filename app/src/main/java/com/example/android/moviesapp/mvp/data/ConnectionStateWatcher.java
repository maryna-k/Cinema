package com.example.android.moviesapp.mvp.data;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

/*A class that created BroadcastReceiver to monitor data connection. As soon as internet is available,
* it notifies the presenter that will update the view*/
public class ConnectionStateWatcher {

    private Context context;
    private IntentFilter internetFilter;
    private BroadcastReceiver broadcastReceiver;

    public interface ConnectionCallback{
        void connected();
        void disconnected();
    }

    public ConnectionStateWatcher(Context context){
        this.context = context;
    }

    public void startWatching(final ConnectionCallback callback){
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    NetworkInfo info = extras.getParcelable("networkInfo");
                    NetworkInfo.State state = info.getState();
                    if(state == NetworkInfo.State.CONNECTED){
                        callback.connected();
                    } else {
                        callback.disconnected();
                    }
                }
            };
            internetFilter = new IntentFilter();
            internetFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        }
        context.registerReceiver(broadcastReceiver, internetFilter);
    }

    public void stopWatching(){
        context.unregisterReceiver(broadcastReceiver);
    }
}
