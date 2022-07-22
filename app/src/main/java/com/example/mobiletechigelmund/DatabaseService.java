package com.example.mobiletechigelmund;
import android.bluetooth.BluetoothDevice;
import android.database.Cursor;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiletechigelmund.models.BluetoothDeviceInfo;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;

public class DatabaseService {
    private static DatabaseService databaseServiceInstance = null;
    private static File databaseFile;
    private static SQLiteDatabase database;
    public DatabaseService(AppCompatActivity appCompatActivity) {
        SQLiteDatabase.loadLibs(appCompatActivity);
        databaseFile = appCompatActivity.getDatabasePath("network.db");
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile,
                "test123",
                null);
    }
    public static DatabaseService getInstance(AppCompatActivity appCompatActivity) {
        if (databaseServiceInstance == null) {
            databaseServiceInstance = new DatabaseService(appCompatActivity);
        }
        return databaseServiceInstance;
    }
    public void saveBluetoothDevice(BluetoothDevice bluetoothDevice) {
        //database.execSQL("create table bluetoothDevices(mac, type, name)");
        if (database != null) {
            database.execSQL(
                    "insert into bluetoothDevices(mac, type, name) values (?, ?, ?)",
                    new Object[] {
                            bluetoothDevice.getAddress(),
                            bluetoothDevice.getType(),
                            bluetoothDevice.getName()
                    }
            );
        }
        Log.d("databaseService", "!!!!!!!!!!!!!!!!!!!!!save: database is null !!!!! ");
    }
    public ArrayList<BluetoothDeviceInfo> getBlueToothDevices() {
        ArrayList<BluetoothDeviceInfo> bluetoothDevices = new ArrayList<>();
        if (database != null) {
            Cursor cursor = database.query("select * from bluetoothDevices;");
            while (!cursor.isLast()) {
                cursor.moveToNext();
                BluetoothDeviceInfo bluetoothDevice = new BluetoothDeviceInfo();
                bluetoothDevice.setDeviceAddress(cursor.getString(0));
                bluetoothDevice.setDeviceType(cursor.getString(1));
                bluetoothDevice.setDeviceName(cursor.getString(2));
                bluetoothDevices.add(bluetoothDevice);
            }
            return bluetoothDevices;
        }
        Log.d("databaseService", "!!!!!!!!!!!!!!!!!!!!!get: database is null !!!!! ");
        return null;

    }

}
