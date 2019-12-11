package com.example.applamp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Wifi extends AppCompatActivity {
    public static int REQUEST_ENABLE_BT = 1;
    public static int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 2;

    Button procurar;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothObject> devices;
    Context context;
    BluetoothObject lamp;
    TextView lamp_found;
    BluetoothSocket socket;
    TextView redes;
    WifiManager wifiManager;
    RecyclerView wifi_rv;

    List<String> wifis;

    private OutputStream outputStream;
    private InputStream inStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        context = this;

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        procurar = (Button) findViewById(R.id.procurar);
        redes = (TextView) findViewById(R.id.redes);
        redes.setVisibility(View.GONE);
        lamp_found = (TextView) findViewById(R.id.lamp_found);
        wifi_rv = (RecyclerView) findViewById(R.id.wifi_rv);
        wifi_rv.setLayoutManager(new LinearLayoutManager(this));
        wifis = new ArrayList<String>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothOnDevice();

        procurar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devices = getArrayOfAlreadyPairedBluetoothDevices();
                if(handleBluetoothConenction(devices)) {
                    procurar.setVisibility(View.GONE);
                    lamp_found.setText("Luminaria encontrada: " + lamp.getCode());
                    try {
                        socket = (BluetoothSocket) lamp.device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(lamp.device,1);
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();
                        writeToBluetooth(Util.getToken(context) + "/token");


                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                        }else{
                            handleWifiListing();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                };
            }
        });
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> results = wifiManager.getScanResults();
            unregisterReceiver(this);
            for (ScanResult scanResult : results) {
                wifis.add(scanResult.SSID);
            }

            Log.d("wifi", wifis.toString());
            redes.setVisibility(View.VISIBLE);
            MyAdapterWifi mAdapter3 = new MyAdapterWifi(context, wifis);
            wifi_rv.setAdapter(mAdapter3);
        }
    };

    private void handleWifiListing() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Por favor ative o WiFi", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        scanWifi();
    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Escaneando WiFi ...", Toast.LENGTH_SHORT).show();
    }


    private boolean handleBluetoothConenction(ArrayList<BluetoothObject> devices) {
        boolean lampWasFound = false;

        if(devices == null) {
            new AlertDialog.Builder(context)
                    .setTitle("Erro")
                    .setMessage("Lampada nao conectada, por favor conecte-a nas configuraçoes de Bluetooth. Seu nome contem ESP32_Lamp")
                    .setPositiveButton(R.string.ok, null).show();
            return false;
        }

        for(BluetoothObject d : devices) {
            String name = d.name;

            if(name.contains("ESP32_Lamp")) {
                lamp = d;
                lampWasFound = true;
                break;
            }
        }

        if(!lampWasFound) {
            new AlertDialog.Builder(context)
                    .setTitle("Erro")
                    .setMessage("Lampada nao conectada, por favor conecte-a nas configuraçoes de Bluetooth. Seu nome contem ESP32_Lamp")
                    .setPositiveButton(R.string.ok, null).show();
        }

        return lampWasFound;
    }

    public void writeToBluetooth(String s) throws IOException {
        outputStream.write(s.getBytes());
        socket.close();
    }


    private void enableBluetoothOnDevice() {
        if (mBluetoothAdapter == null) {
            Log.e("ERROR", "This device does not have a bluetooth adapter");
            finish();
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private ArrayList getArrayOfAlreadyPairedBluetoothDevices()
    {
        ArrayList <BluetoothObject> arrayOfAlreadyPairedBTDevices = null;

        // Query paired devices
        Set <BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0)
        {
            arrayOfAlreadyPairedBTDevices = new ArrayList<BluetoothObject>();

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices)
            {
                // Create the device object and add it to the arrayList of devices
                BluetoothObject bluetoothObject = new BluetoothObject();
                bluetoothObject.setBluetooth_name(device.getName());
                bluetoothObject.setBluetooth_address(device.getAddress());
                bluetoothObject.setBluetooth_state(device.getBondState());
                bluetoothObject.setBluetooth_uuids(device.getUuids());
                bluetoothObject.setBluetooth_device(device);

                arrayOfAlreadyPairedBTDevices.add(bluetoothObject);
            }
        }

        return arrayOfAlreadyPairedBTDevices;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            handleWifiListing();
        }
    }

}
