package com.example.mobiletechigelmund.models;

import static android.content.Context.BLUETOOTH_SERVICE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class BluetoothDeviceHandler {
    private BluetoothManager blManager;
    private BluetoothAdapter blAdapter;
    private Context context;

    public BluetoothDeviceHandler(Context context) {
        this.context = context;
        blManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        blAdapter = blManager.getAdapter();

    }

    public boolean checkBluetoothState() {
        if (blManager.getAdapter().getState() != BluetoothAdapter.STATE_ON) {
            Log.i("BluetoothDeviceHandler", "Bluetooth is deactivated");

            // Create an AlertDialogBuilder object.
            AlertDialog.Builder blDisableNotificationBuilder
                    = new AlertDialog.Builder(context);
            // Add a message for the alert dialog
            blDisableNotificationBuilder.setMessage("Bluetooth ist deaktiviert");
            // Set the positive button only. Its just a notification.
            // So user choice ist not necessary
            blDisableNotificationBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            // Create and show the alert dialog
            AlertDialog blDisableNotification = blDisableNotificationBuilder.create();
            blDisableNotification.show();
            return false;
        } else {
            Log.i("BluetoothDeviceHandler", "Bluetooth is activated");
            return true;
        }
    }

    public void scanBluetoothDevices() {
        Log.i("BluetoothDeviceHandler", "Bluetooth scan started");
        IntentFilter blFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        blFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == BluetoothDevice.ACTION_FOUND) {
                    Log.d("bluetoothBroadcastReceiver", "Action Found triggered");
                    BluetoothDevice blDevice = intent.getParcelableExtra(
                            BluetoothDevice.EXTRA_DEVICE);
                    if (blDevice != null) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.d("bluetoothBroadcastReceiver", "missing permissions!");
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        String deviceName = blDevice.getName();
                        String deviceAddress = blDevice.getAddress();
                        int deviceType = blDevice.getType();
                        String deviceTypeString = "Unknown";
                        switch (deviceType) {
                            case BluetoothDevice.DEVICE_TYPE_CLASSIC :
                                deviceTypeString = "Classic"; break;
                            case BluetoothDevice.DEVICE_TYPE_LE:
                                deviceTypeString = "LE"; break;
                            case BluetoothDevice.DEVICE_TYPE_DUAL:
                                deviceTypeString = "DUAL"; break;
                            default:deviceTypeString = "Unknown";
                        }
                        Log.d("bluetoothBroadcastReceiver",
                                "Found Bluetooth Device: " + deviceName + "," + deviceAddress + ", Type: " + deviceTypeString);
                        AlertDialog.Builder bluetoothDeviceNotificationBuilder
                                = new AlertDialog.Builder(context);
                        // Add a message for the alert dialog
                        bluetoothDeviceNotificationBuilder.setMessage("Found Bluetooth Device: " + deviceName + "," + deviceAddress + ", Type: " + deviceTypeString);
                        // Set the positive button only. Its just a notification.
                        // So user choice ist not necessary
                        bluetoothDeviceNotificationBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        // Create and show the alert dialog
                        AlertDialog bluetoothDeviceNotification = bluetoothDeviceNotificationBuilder.create();
                        bluetoothDeviceNotification.show();
                    }
                } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                    //if(blScanActive) {
                        blAdapter.startDiscovery();
                    //}
                }
            }
        };

        context.registerReceiver(bluetoothBroadcastReceiver, blFilter);
        blAdapter.startDiscovery();
    }

}
