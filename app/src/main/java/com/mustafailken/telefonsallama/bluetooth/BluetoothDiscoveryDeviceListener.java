
package com.mustafailken.telefonsallama.bluetooth;

import android.bluetooth.BluetoothDevice;


public interface BluetoothDiscoveryDeviceListener {


    void onDeviceDiscovered(BluetoothDevice device);
//cihaz keşfedilğinde kullanılcak fonksiyon

    void onDeviceDiscoveryStarted();
//cihaz arama başlatıldığında çağrılacak fonksiyon

    void setBluetoothController(BluetoothController bluetooth);
//Bluetooth controller

    void onDeviceDiscoveryEnd();
//Cihaz arama sona erdiğinde çağrılacak fonksiyon.

    void onBluetoothStatusChanged();
//Bluetooth durumu değiştiğinde çağrılacak fonksiyon.

    void onBluetoothTurningOn();
//Bluetooth açıldığında çağrılacak fonksiyon

    void onDevicePairingEnded();
//Cihaz eşleştirme bittiğinde çağrılacak fonksiyon
}
