package com.example.plantgrowthchamber;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice raspberryPi;
    private String raspberryPiMacAddress = "B8:27:EB:DE:5C:4D";
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private MaterialButton[] ledOnButtons = new MaterialButton[7];
    private MaterialButton[] ledOffButtons = new MaterialButton[7];
    private MaterialButton allLightsOnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Replace with your Raspberry Pi's Bluetooth MAC Address
        raspberryPi = bluetoothAdapter.getRemoteDevice("Raspberry Pi's Bluetooth MAC Address");

        for (int i = 1; i <= 7; i++) {
            ledOnButtons[i - 1] = findViewById(getResources().getIdentifier("ledOnButton" + i, "id", getPackageName()));
            ledOffButtons[i - 1] = findViewById(getResources().getIdentifier("ledOffButton" + i, "id", getPackageName()));

            final int index = i;
            ledOnButtons[i - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBluetoothCommand("LED_ON_" + index);
                }
            });

            ledOffButtons[i - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendBluetoothCommand("LED_OFF_" + index);
                }
            });
        }

        allLightsOnButton = findViewById(R.id.allLightsOnButton);
        allLightsOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBluetoothCommand("ALL_ON");
            }
        });
    }

    private void sendBluetoothCommand(String command) {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Request necessary permissions here
                return;
            }

            BluetoothDevice raspberryPi = bluetoothAdapter.getRemoteDevice(raspberryPiMacAddress);
            socket = raspberryPi.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            outputStream = socket.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
