package com.example.mobiletechigelmund;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

public class PinActivity extends AppCompatActivity {
    SharedPreferences sharedPrefs;
    private static final int PERMISSION_REQUEST_BLUETOOTH = 0;
    private Button loginButton;
    TextView pinInput;
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase.loadLibs(this);
/*
        File databaseFile = getDatabasePath("demo.db");
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile,
                "test123",
                null);

 */


        //SQLiteDatabase.loadLibs(this);
        // pin db logik
        //not working
        //SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);

        int WRITE_EXTERNAL_STORAGE = 0;
        //if (Build.VERSION.SDK_INT >= 23) {
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        requestPermissions(PERMISSIONS, WRITE_EXTERNAL_STORAGE);




        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String pinValue = sharedPrefs.getString(getString(R.string.PinPrefsKey), "not set");
        Log.d("check_pin", "pinValue "+pinValue);
        // pin not set
        if (pinValue == "" || pinValue == "not set") {
            Intent setPinIntent = new Intent(PinActivity.this, SetPinActivity.class);
            startActivity(setPinIntent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        //int hasPermission = ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        /*
        requestPermissions(new String[] { Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_ADVERTISE}, PERMISSION_REQUEST_BLUETOOTH);
        */
        //requestPermissions(new String[] {Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_BLUETOOTH);

        loginButton = findViewById(R.id.setPinButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pinInput = findViewById(R.id.pinInput);
                Log.d("check_pin", "pinValue "+pinValue);
                Log.d("check_pin", "pinInput "+pinInput.getText());
                if (!pinInput.getText().toString().equals(pinValue.toString())) {
                    Log.d("check_pin", "pin wrong");
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("PIN falsch!");
                    AlertDialog pinEmptyDialog = builder.create();
                    pinEmptyDialog.show();
                    return;
                }
                Intent mainIntent = new Intent(PinActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_BLUETOOTH) {
            boolean accessGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.w("onRequestPermissionsResult", permissions[i] +
                            " not Granted");
                    accessGranted = false;
                } else {
                    Log.w("onRequestPermissionsResult", "OK");
                }
            }
            if (accessGranted) {
                Log.w("onRequestPermissionsResult", "Starting Bluetooth Scan");
                //checkAnStartBluetoothScan();
            }
        }
    }
}
