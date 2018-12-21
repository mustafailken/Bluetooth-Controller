
package com.mustafailken.telefonsallama.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.Closeable;


public class BroadcastReceiverDelegator extends BroadcastReceiver implements Closeable {


    private final BluetoothDiscoveryDeviceListener listener;


    private final String TAG = "BroadcastReceiver";


    private final Context context;


    public BroadcastReceiverDelegator(Context context, BluetoothDiscoveryDeviceListener listener, BluetoothController bluetooth) {
        this.listener = listener;
        this.context = context;
        this.listener.setBluetoothController(bluetooth);


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(this, filter);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "aksiyon getir : " + action);
        switch (action) {
            case BluetoothDevice.ACTION_FOUND :
        //Bluetooth cihaz buldu.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "Cihaz bulundu " + BluetoothController.deviceToString(device));
                listener.onDeviceDiscovered(device);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :
        //cihaz arama sonlandırma
                Log.d(TAG, "Cihaz bulma sonlandırıldı");
                listener.onDeviceDiscoveryEnd();
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED :
        //Bluetooth durumu değiştiğinde ---***
                Log.d(TAG, "Bluetooth durumu değişti.");
                listener.onBluetoothStatusChanged();
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED :
        //eşleştirme durumu değiştirildi.
                Log.d(TAG, "Bluetooth bağlanma şekli değişti");
                listener.onDevicePairingEnded();
                break;
            default :

                break;
        }
    }


    public void onDeviceDiscoveryStarted() {
        listener.onDeviceDiscoveryStarted();
    }
//cihaz arama başlatıldığında çağır.
    public void onDeviceDiscoveryEnd() {
        listener.onDeviceDiscoveryEnd();
    }
//cihaz arama bittiğinde çağır.

    public void onBluetoothTurningOn() {
        listener.onBluetoothTurningOn();
    }
//Bluetooth açık olduğunda çağır.

    @Override
    public void close() {
        context.unregisterReceiver(this);
    }
}
