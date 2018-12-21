
package com.mustafailken.telefonsallama;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.squareup.seismic.ShakeDetector;

import com.mustafailken.telefonsallama.bluetooth.BluetoothController;
import com.mustafailken.telefonsallama.view.DeviceRecyclerViewAdapter;
import com.mustafailken.telefonsallama.view.ListInteractionListener;
import com.mustafailken.telefonsallama.view.RecyclerViewProgressEmptySupport;


public class MainActivity extends AppCompatActivity implements  ListInteractionListener<BluetoothDevice> ,ShakeDetector.Listener {
    final BluetoothAdapter adaptor = BluetoothAdapter.getDefaultAdapter();
    ShakeDetector sd;
    MediaPlayer mp;

    private static final String TAG = "MainActivity";


    private BluetoothController bluetooth;


    private ProgressDialog bondingProgressDialog;


    private DeviceRecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewProgressEmptySupport recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        SystemClock.sleep(getResources().getInteger(R.integer.splashscreen_duration));
        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mp = MediaPlayer.create(this,R.raw.konusmaturkcemetin);


        //Recylerview kurma
        this.recyclerViewAdapter = new DeviceRecyclerViewAdapter(this);
        this.recyclerView = (RecyclerViewProgressEmptySupport) findViewById(R.id.list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));


        View emptyView = findViewById(R.id.empty_list);
        this.recyclerView.setEmptyView(emptyView);

        //İlerleme sırasında gösterilecek görünümü ayarlar.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.recyclerView.setProgressView(progressBar);

        this.recyclerView.setAdapter(recyclerViewAdapter);

        //Cihazda bluetooth olup olmadığını kontrol eder.
        boolean hasBluetooth = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if(!hasBluetooth) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle(getString(R.string.bluetooth_not_available_title));
            dialog.setMessage(getString(R.string.bluetooth_not_available_message));
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }


        this.bluetooth = new BluetoothController(this, BluetoothAdapter.getDefaultAdapter(), recyclerViewAdapter);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();




        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onItemClick(BluetoothDevice device) {
        Log.d(TAG, "Item tıklanıldı : " + BluetoothController.deviceToString(device));
        if (bluetooth.isAlreadyPaired(device)) {
            Log.d(TAG, "Cihaz eşleştirildi!");
            Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Cihaz eşleştirilmedi.Eşleştiriliyor");
            boolean outcome = bluetooth.pair(device);


            String deviceName = BluetoothController.getDeviceName(device);
            if (outcome) {
               // Eşleştirme başladı, diyaloğu gösterme.
                Log.d(TAG, "Eşleştime dialog gösterme");
                bondingProgressDialog = ProgressDialog.show(this, "", "Cihaz ile eşleştiriliyor " + deviceName + "...", true, false);
            } else {
                Log.d(TAG, "Cihaz ile eşleştirilirken hata " + deviceName + "!");
                Toast.makeText(this, "Cihaz eşleştirilirken hata !!! " + deviceName + "!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void startLoading() {
        this.recyclerView.startLoading();


    }


    @Override
    public void endLoading(boolean partialResults) {
        this.recyclerView.endLoading();


    }


    @Override
    public void endLoadingWithDialog(boolean error, BluetoothDevice device) {
        if (this.bondingProgressDialog != null) {
            View view = findViewById(R.id.main_content);
            String message;
            String deviceName = BluetoothController.getDeviceName(device);


            if (error) {
                message = "Cihaz ile eşleştirilemedi " + deviceName + "!";
            } else {
                message = "Cihaz ile eşleştirme başarılı " + deviceName + "!";
            }


            this.bondingProgressDialog.dismiss();
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
//ileitişm kutusunu kapat kullanıcıya mesaj yaz.

            this.bondingProgressDialog = null;
        }//Durum temizleme

    }


    @Override
    protected void onDestroy() {
        bluetooth.close();
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }//Cihaz aramayı durdur.

        if (this.recyclerViewAdapter != null) {
            this.recyclerViewAdapter.cleanView();
        }//Ekranı temizle/View ' ı temizle.
    }


    @Override
    protected void onStop() {
        super.onStop();
//Cihaz aramayı durdur.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }

    @Override
    public void hearShake() {
        Toast.makeText(this, "Telefon Sallandı...", Toast.LENGTH_SHORT).show();

        if (!bluetooth.isBluetoothEnabled()) {//Bluetooth açma
            Toast.makeText(this, R.string.enabling_bluetooth, Toast.LENGTH_SHORT).show();
            bluetooth.turnOnBluetoothAndScheduleDiscovery();
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(500);
            mp.start();
        } else {

            if (!bluetooth.isDiscovering()) {

                Toast.makeText(this, R.string.device_discovery_started, Toast.LENGTH_SHORT).show();
                bluetooth.startDiscovery();
            } else {//Cihaz arama başlatma.
                Toast.makeText(this, R.string.device_discovery_stopped, Toast.LENGTH_SHORT).show();
                bluetooth.cancelDiscovery();
            }
        }

    }
    protected void onResume() {
        super.onResume();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd = new ShakeDetector(this);
        sd.start(sensorManager);
    }
    protected void onPause() {
        super.onPause();
        sd.stop();
    }

}
