package com.example.applamp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public class SenhaWifi extends AppCompatActivity {
    public static int REQUEST_ENABLE_BT = 1;

    TextView senhaWifi;
    EditText senha;
    String ssid;
    Button conectar;

    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothObject> devices;
    Context context;
    BluetoothObject lamp;
    BluetoothSocket socket;

    private OutputStream outputStream;
    private InputStream inStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senhawifi);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothOnDevice();
        devices = getArrayOfAlreadyPairedBluetoothDevices();
        handleBluetoothConnection(devices);

        Intent intent = getIntent();
        conectar = (Button) findViewById(R.id.conectar);
        ssid = intent.getStringExtra("ssid");
        senha = (EditText) findViewById(R.id.senha);
        senhaWifi = (TextView) findViewById(R.id.senha_wifi);
        senhaWifi.setText("Digite a senha para " + ssid);

        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWifiDataToEsp();

                Intent myIntent = new Intent(SenhaWifi.this, Apelido.class);
                myIntent.putExtra("code", lamp.getCode());
                startActivity(myIntent);
            }
        });
    }

    private void sendWifiDataToEsp() {
        try {
            socket = (BluetoothSocket) lamp.device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(lamp.device,1);
            socket.connect();
            outputStream = socket.getOutputStream();
            inStream = socket.getInputStream();
            writeToBluetooth(ssid + "/ssid");
            writeToBluetooth(senha.getText().toString() + "/password");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void handleBluetoothConnection(ArrayList<BluetoothObject> devices) {
        for(BluetoothObject d : devices) {
            String name = d.name;

            if(name.contains("ESP32_Lamp")) {
                lamp = d;
                break;
            }
        }
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
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
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

    public void writeToBluetooth(String s) throws IOException {
        outputStream.write(s.getBytes());
    }
}
