package com.example.mobiletechigelmund;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SetPinActivity extends AppCompatActivity {
    private Button setPinButton;
    private Button deleteDatabaseButton;
    private Button exportDatabaseButton;

    private static File databaseFile;
    private static SQLiteDatabase database;

    private SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Context mContext= this;

        final int REQUEST = 112;
        int WRITE_EXTERNAL_STORAGE = 0;
        //if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(PERMISSIONS, WRITE_EXTERNAL_STORAGE);
        //}
            /*
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                //do here
            }
        } else {
            //do here
        }

             */



        databaseFile = getDatabasePath("ScanResult.db");
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile,
                "test123",
                null);

        setContentView(R.layout.activity_set_pin);
        deleteDatabaseButton = findViewById(R.id.deleteDatabaseButton);
        deleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.execSQL("delete from bluetoothDevices");
                database.execSQL("delete from wifiDevices");
            }
        });
        exportDatabaseButton = findViewById(R.id.exportDatabaseButton);
        exportDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {




                //File root = new File(String.valueOf(Environment.getExternalStorageDirectory()), "Netzwerk-Scan-Export");
                //File root = new File(getApplicationContext().getFilesDir(), "Netzwerk-Scan-Export");
                //File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Netzwerk-Scan-Export");
                File root = new File("/sdcard/", "Netzwerk-Scan-Export");
                //Log.d();
                if (!root.exists()) {
                    root.mkdirs();
                }
                File filepath = new File(root, "export.txt");
                if (!filepath.exists()) {
                    try {
                        filepath.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("filepath", filepath.getAbsolutePath());
                try {
                    FileWriter writer = new FileWriter(filepath);
                    writer.append("WIFI");
                    writer.append(System.getProperty("line.separator"));
                    //Cursor cursor = database.query("wifiDevices", new String[]{"bssid", "name", "signalStrength"});
                    Cursor cursor = database.query("select * from wifiDevices");
                    if (cursor.getCount()>0) {
                        cursor.moveToNext();
                        do{
                            String newLine = "";
                            //int colIdx = cursor.getColumnIndex("name");
                            newLine += "name: "+cursor.getString(1)+", ";
                            //colIdx = cursor.getColumnIndex("bssid");
                            newLine += "bssid: "+cursor.getString(0)+", ";
                            //colIdx = cursor.getColumnIndex("signalStrength");
                            newLine += "dBm:  "+cursor.getString(2);

                            writer.append(newLine);
                            writer.append(System.getProperty("line.separator"));
                        }while(cursor.moveToNext());
                    }


                    writer.flush();

                    writer.append("BLUETOOTH");
                    writer.append(System.getProperty("line.separator"));
                    //cursor = database.query("bluetoothDevices", new String[]{"address", "type", "name", "rssiStrength"});
                    cursor = database.query("select * from bluetoothDevices");

                    if (cursor.getCount()>0) {
                        cursor.moveToNext();
                        do{
                            String newLine = "";
                            //int colIdx = cursor.getColumnIndex("name");
                            newLine += "name: "+cursor.getString(2)+", ";
                            //colIdx = cursor.getColumnIndex("address");
                            newLine += "address: "+cursor.getString(0)+", ";
                            //colIdx = cursor.getColumnIndex("type");
                            newLine += "type:  "+cursor.getString(1);
                            //colIdx = cursor.getColumnIndex("rssiStrength");
                            newLine += "dBm:  "+cursor.getString(3);

                            writer.append(newLine);
                            writer.append(System.getProperty("line.separator"));
                        }while(cursor.moveToNext());
                    }


                    writer.flush();
                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        setPinButton = findViewById(R.id.setPinButton);
        //not working
        //sharedPreferences =this.getPreferences(Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("set_pin", "button clicked");

                TextView pinSelection = findViewById(R.id.pinSelection);
                Log.d("set_pin", "pin entered: \""+pinSelection.getText().toString().trim()+"\"");
                TextView pinConfirmation = findViewById(R.id.pinConfirmation);
                if (pinSelection.getText().toString().trim().equals("")) {
                    Log.d("set_pin", "pin empty");
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("PIN darf nicht leer sein!");
                    AlertDialog pinEmptyDialog = builder.create();
                    pinEmptyDialog.show();
                    return;
                }
                if (!pinSelection.getText().toString().trim().equals(pinConfirmation.getText().toString().trim())) {
                    Log.d("set_pin", "pin empty");
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Fehler bei der wiederholten Eingabe!");
                    AlertDialog pinUnequalDialog = builder.create();
                    pinUnequalDialog.show();
                    return;
                }
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString(getString(R.string.PinPrefsKey), pinSelection.getText().toString().trim());
                edit.apply();
                edit.commit();
                Log.d("set_pin", "R.string.PinPrefsKey "+R.string.PinPrefsKey);
                edit.apply();
                edit.commit();
                Intent pinIntent = new Intent(SetPinActivity.this, PinActivity.class);
                startActivity(pinIntent);
            }
        });
    }
}
