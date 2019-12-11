package com.example.applamp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public class Funcoes extends AppCompatActivity {
    public static int REQUEST_ENABLE_BT = 1;

    String code;
    FloatingActionButton botaoOnOff;
    FloatingActionButton botaoCor;
    FloatingActionButton botaoParear;
    Context context;

    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothObject> devices;
    BluetoothSocket socket;
    BluetoothObject lamp;

    private OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcoes);
        context = this;
        Intent intent = getIntent();
        code = intent.getStringExtra("code");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBluetoothOnDevice();
        devices = getArrayOfAlreadyPairedBluetoothDevices();

        while(!handleBluetoothConnection());

        Log.d("Bluetooth", "connected to " + code);

        try {
            socket = (BluetoothSocket) lamp.device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(lamp.device,1);
            socket.connect();
            outputStream = socket.getOutputStream();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        botaoOnOff = (FloatingActionButton) findViewById(R.id.onoff);
        botaoCor = (FloatingActionButton) findViewById(R.id.trocar_cor);
        botaoParear = (FloatingActionButton) findViewById(R.id.parear);

        botaoOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    writeToBluetooth("ONOFF");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        botaoCor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    writeToBluetooth("CHANGECOLOR");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        botaoParear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(context, Parear.class);
                startActivity(myIntent);
            }
        });
    }

    public void writeToBluetooth(String s) throws IOException {
        outputStream.write(s.getBytes());
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

    private boolean handleBluetoothConnection() {
        boolean lampWasFound = false;

        if(devices == null) {
            new AlertDialog.Builder(context)
                    .setTitle("Erro")
                    .setMessage("Lampada nao conectada, por favor conecte-a nas configuraçoes de Bluetooth. Nome: ESP32_Lamp_" + code)
                    .setPositiveButton(R.string.ok, null).show();
            return false;
        }

        for(BluetoothObject d : devices) {
            String name = d.name;

            if(name.contains("ESP32_Lamp_" + code)) {
                lamp = d;
                lampWasFound = true;
                break;
            }
        }

        if(!lampWasFound) {
            new AlertDialog.Builder(context)
                    .setTitle("Erro")
                    .setMessage("Lampada nao conectada, por favor conecte-a nas configuraçoes de Bluetooth. Nome: ESP32_Lamp_" + code)
                    .setPositiveButton(R.string.ok, null).show();
        }

        return lampWasFound;
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

}
