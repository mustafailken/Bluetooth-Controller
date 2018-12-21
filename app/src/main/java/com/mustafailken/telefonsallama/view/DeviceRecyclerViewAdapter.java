
package com.mustafailken.telefonsallama.view;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.mustafailken.telefonsallama.R;
import com.mustafailken.telefonsallama.bluetooth.BluetoothController;
import com.mustafailken.telefonsallama.bluetooth.BluetoothDiscoveryDeviceListener;


public class DeviceRecyclerViewAdapter
        extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder>
        implements BluetoothDiscoveryDeviceListener {


    private final List<BluetoothDevice> devices;
//Cihaz Listele

    private final ListInteractionListener<BluetoothDevice> listener;
//Etkileşim olayları

    private BluetoothController bluetooth;


    public DeviceRecyclerViewAdapter(ListInteractionListener<BluetoothDevice> listener) {
        this.devices = new ArrayList<>();
        this.listener = listener;
    }//Yeni DeviceRecyclerViewAdapter


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_device_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = devices.get(position);
        holder.mImageView.setImageResource(getDeviceIcon(devices.get(position)));
        holder.mDeviceNameView.setText(devices.get(position).getName());
        holder.mDeviceAddressView.setText(devices.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {

                    listener.onItemClick(holder.mItem);
                }//öğe seçimi işlemi
            }
        });
    }


    private int getDeviceIcon(BluetoothDevice device) {
        if (bluetooth.isAlreadyPaired(device)) {
            return R.drawable.ic_bluetooth_connected_black_24dp;
        } else {
            return R.drawable.ic_bluetooth_black_24dp;
        }
    }//cihaz yanına ikon getirme


    @Override
    public int getItemCount() {
        return devices.size();
    }


    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        listener.endLoading(true);
        devices.add(device);
        notifyDataSetChanged();
    }


    @Override
    public void onDeviceDiscoveryStarted() {
        cleanView();
        listener.startLoading();
    }


    public void cleanView() {
        devices.clear();
        notifyDataSetChanged();
    }//ekranı /listeleme ekranını temizleme


    @Override
    public void setBluetoothController(BluetoothController bluetooth) {
        this.bluetooth = bluetooth;
    }


    @Override
    public void onDeviceDiscoveryEnd() {
        listener.endLoading(false);
    }


    @Override
    public void onBluetoothStatusChanged() {

        bluetooth.onBluetoothStatusChanged();
    }


    @Override
    public void onBluetoothTurningOn() {
        listener.startLoading();
    }


    @Override
    public void onDevicePairingEnded() {
        if (bluetooth.isPairingInProgress()) {
            BluetoothDevice device = bluetooth.getBoundingDevice();
            switch (bluetooth.getPairingDeviceStatus()) {
                case BluetoothDevice.BOND_BONDING:

                    break;
                case BluetoothDevice.BOND_BONDED:
        //Başarılı eşleştirme.
                    listener.endLoadingWithDialog(false, device);


                    notifyDataSetChanged();
                    break;
                case BluetoothDevice.BOND_NONE:
        //başarısız eşleştirme
                    listener.endLoadingWithDialog(true, device);
                    break;
            }
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {


        final View mView;


        final ImageView mImageView;


        final TextView mDeviceNameView;


        final TextView mDeviceAddressView;


        BluetoothDevice mItem;


        ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.device_icon);
            mDeviceNameView = (TextView) view.findViewById(R.id.device_name);
            mDeviceAddressView = (TextView) view.findViewById(R.id.device_address);
        }


        @Override
        public String toString() {
            return super.toString() + " '" + BluetoothController.deviceToString(mItem) + "'";
        }
    }
}
