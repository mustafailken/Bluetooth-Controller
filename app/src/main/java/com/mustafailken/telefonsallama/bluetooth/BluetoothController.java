
package com.mustafailken.telefonsallama.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.Closeable;


public class BluetoothController implements Closeable {


    private static final String TAG = "BluetoothManager";

//Bluetooth OS hizmetleri için arayüz.
    private final BluetoothAdapter bluetooth;


    private final BroadcastReceiverDelegator broadcastReceiverDelegator;


    private final Activity context;


    private boolean bluetoothDiscoveryScheduled; //Bluetooth cihaz keşfi


    private BluetoothDevice boundingDevice;
//Bluetooh deneyleyicisi / sınırlayıcısı.

    public BluetoothController(Activity context,BluetoothAdapter adapter, BluetoothDiscoveryDeviceListener listener) {
        this.context = context;
        this.bluetooth = adapter;
        this.broadcastReceiverDelegator = new BroadcastReceiverDelegator(context, listener, this);
    }


    public boolean isBluetoothEnabled() {
        return bluetooth.isEnabled();
    }
//Bluetooth açık mı değil mi kontrolü

    public void startDiscovery() {
        broadcastReceiverDelegator.onDeviceDiscoveryStarted();
//Yakındaki bluetooth cihazlarını aramaya başlama

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }///Cihaz arama izni


        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }//cihaz arama yapılıyorsa yeni cihaz arama için işlemi durdurma.


        Log.d(TAG, "Bluetooth cihazı aranmaya başlandı.");
        if (!bluetooth.startDiscovery()) {
            Toast.makeText(context, "Cihaz arama başlatılamadı", Toast.LENGTH_SHORT)
                    .show();
            //cihaz arama başlatılmaya çalışılır bluetooh ile ilgili problem olduğunda uyarı verir.



            broadcastReceiverDelegator.onDeviceDiscoveryEnd();//cihaz arama sona erer.
        }
    }


    public void turnOnBluetooth() {
        Log.d(TAG, "Bluetooth açıldı!!!!");
        broadcastReceiverDelegator.onBluetoothTurningOn();
        bluetooth.enable();
    }//bluetooth aktifleştirme


    public boolean pair(BluetoothDevice device) {
    //cihaz eşleştime işlemi ---- * eşleştirme yapılcaksa cihaz arama işleminden çıkılır ve eşleştirme işlemi gerçekleştirilir.
        if (bluetooth.isDiscovering()) {
            Log.d(TAG, "Bluetooth cihazı aramadan çıkıldı.");
            bluetooth.cancelDiscovery();
        }
        Log.d(TAG, "Cihaz: " + deviceToString(device));
        boolean outcome = device.createBond();


//sonuç true ise bu cihazla bounding yapılıyo.
        if (outcome == true) {
            this.boundingDevice = device;
        }
        return outcome;
    }


    public boolean isAlreadyPaired(BluetoothDevice device) {
        return bluetooth.getBondedDevices().contains(device);
    }// cihaz ile daha önce eşleştirilme durumunu kontrol eder


    public static String deviceToString(BluetoothDevice device) {
        return "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]";
    }//cihaz verilerini alıyoruz.


    @Override
    public void close() {
        this.broadcastReceiverDelegator.close();
    }


    public boolean isDiscovering() {
        return bluetooth.isDiscovering();
    }
//cihaz arama şu anda çalışıyor mu kontrolü

    public void cancelDiscovery() {
        if(bluetooth != null) {
            bluetooth.cancelDiscovery();
            broadcastReceiverDelegator.onDeviceDiscoveryEnd();
        }//cihaz aramadan çıkış işlemi
    }


    public void turnOnBluetoothAndScheduleDiscovery() {
        this.bluetoothDiscoveryScheduled = true;
        turnOnBluetooth();
    }//Bluetooth açılır ve yakındaki cihazlar aranmaya başlar.


    public void onBluetoothStatusChanged() {
//Blueottoh durumu değiştiğinde çağrılacak fonksiyon.
        if (bluetoothDiscoveryScheduled) {

            int bluetoothState = bluetooth.getState();
            switch (bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                //Bluetooth açık
                    Log.d(TAG, "Bluetooth başarıyla açıldı ,ve cihazlar aranıyor");
                    startDiscovery();

                    bluetoothDiscoveryScheduled = false;
                    break;
                case BluetoothAdapter.STATE_OFF:
                //Bluetooth kapalı
                    Log.d(TAG, "Bluetooth açılırken hata oluştu");
                    Toast.makeText(context, "Bluetooth açılırken hata oluştu", Toast.LENGTH_SHORT);

                    bluetoothDiscoveryScheduled = false;
                    break;
                default:

                    break;
            }
        }
    }


    public int getPairingDeviceStatus() { //Mevcut eşleştirme durumu.
        if (this.boundingDevice == null) {
            throw new IllegalStateException("Eşleşmiş cihaz yok");
        }
        int bondState = this.boundingDevice.getBondState();

        if (bondState != BluetoothDevice.BOND_BONDING) {
            this.boundingDevice = null;//bounding durumunda değilse eşleştirme tamamlanır.
        }
        return bondState;
    }


    public String getPairingDeviceName() {
        return getDeviceName(this.boundingDevice);
    }
//Eşleştirilmiş cihaz adını getir

    public static String getDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null) {
            deviceName = device.getAddress();
        }//cihaz adı yoksa cihaz adresini getir.
        return deviceName;
    }


    public boolean isPairingInProgress() {
        return this.boundingDevice != null;
    }
//eşleştirme sürüyo.
    public BluetoothDevice getBoundingDevice() {
        return boundingDevice;
    }
}//bounding cihazları getir.
