package com.example.applamp;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

class BluetoothObject {
    String name;
    String address;
    int state;
    ParcelUuid[] uuids;
    BluetoothDevice device;

    public void setBluetooth_name(String name) {
        this.name = name;
    }

    public void setBluetooth_address(String address) {
        this.address = address;
    }

    public void setBluetooth_state(int state) {
        this.state = state;
    }

    public void setBluetooth_uuids(ParcelUuid[] uuids) {
        this.uuids = uuids;
    }

    public void setBluetooth_device(BluetoothDevice device) {
        this.device = device;
    }

    public String getCode() {
        String[] arr = this.name.split("_");

        return arr[2];
    }

}
