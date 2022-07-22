package com.example.mobiletechigelmund;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mobiletechigelmund.models.BluetoothDeviceHandler;
import com.example.mobiletechigelmund.models.BluetoothDeviceInfo;
import com.google.android.gms.common.util.CollectionUtils;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageButton settingsButton;
    private ImageButton exitAppButton;
    private ImageButton startScanButton;
    private ArrayList<BluetoothDeviceInfo> deviceInfos;
    private ArrayList<BluetoothDeviceInfo> currentDeviceInfos = new ArrayList<>();

    private static final int PERMISSION_REQUEST_BLUETOOTH = 0;
    BluetoothDeviceHandler bluetoothDeviceHandler;
    DatabaseService databaseService;
    BluetoothAdapter blAdapter;
    BluetoothManager blManager;

    WifiManager wifiManager;

    private boolean wifiScanActive = false;
    private boolean blScanActive = false;
    ArrayList<ScanResult>currentWifiDevices;

    //private static DatabaseService databaseServiceInstance = null;
    private static File databaseFile;
    private static SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SQLiteDatabase.loadLibs(this);

        databaseFile = getDatabasePath("ScanResult.db");
        //databaseFile.mkdirs();
        //databaseFile.delete();
        database = SQLiteDatabase.openOrCreateDatabase(databaseFile,
                "test123",
                null);

        database.execSQL("create table IF NOT EXISTS bluetoothDevices(address, type, name, rssiStrength)");
        database.execSQL("create table IF NOT EXISTS wifiDevices(bssid, name, signalStrength)");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);



        //database.execSQL("delete from bluetoothDevices");


        //databaseService = DatabaseService.getInstance(this);
        /* quit app dialog box
        AlertDialog.Builder quitDialogBuilder = new AlertDialog.Builder(this);
        quitDialogBuilder.setMessage(R.string.quitMessage);
        quitDialogBuilder.setPositiveButton(R.string.quit, new DialogInterface.
                OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.exit(0);
            }
        });
        quitDialogBuilder.show();
        */
        /* enter pin dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_pin); // Hier wird das Dialog Layout festgelegt
        AlertDialog pinDialog = builder.create();
        pinDialog.show();
        pinDialog.setCancelable(false);
        pinDialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        );
        */
        Log.d("MainActivity:onCreate", "Starting.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setPinIntent = new Intent(MainActivity.this, SetPinActivity.class);
                startActivity(setPinIntent);
            }
        });
        exitAppButton = findViewById(R.id.exitAppButton);
        exitAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                System.exit(0);
            }
        });
        startScanButton = findViewById(R.id.startScanButton);
        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "Scan button clicked");
                if (!wifiScanActive) {
                    startScanButton.setImageResource(android.R.drawable.ic_media_pause);
                    wifiScanActive = true;
                    blScanActive = true;
                } else {
                    startScanButton.setImageResource(android.R.drawable.ic_media_play);
                    wifiScanActive = false;
                    blScanActive = false;
                    return;
                }

                bluetoothDeviceHandler = new BluetoothDeviceHandler(view.getContext());
                Log.d("MainActivity", "bluetoothDeviceHandler init finished");
                boolean isBluetoothReady = bluetoothDeviceHandler.checkBluetoothState();
                Log.d("MainActivity", "bluetoothDeviceHandler checkBluetoothState finished");
                boolean isWifiReady = getWifiState();

                if (isWifiReady) {
                    /*
                    Log.d("MainActivity", "wifi scan started");
                    final BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                                Log.d("wifiBroadcastReceiver", "Action Found triggered");
                                List<ScanResult> scanResults = wifiManager.getScanResults();
                                Log.d("wifiBroadcastReceiver", "scanResults size "+scanResults.size());
                                currentWifiDevices = new ArrayList<ScanResult>(scanResults);

                                for (ScanResult wifiAPResult : scanResults ) {
                                    Log.d("wifiBroadcastReceiver",
                                            "Found AP: " + (wifiAPResult.toString()));
                                }
                                displayCurrentWifiDevices();
                                saveCurrentWifiDevices();
                                if (wifiScanActive) {
                                //if (wifiA.Ac)
                                    Handler delayRun = new Handler();
                                    delayRun.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (wifiScanActive) {
                                                Log.d("wifiBroadcastReceiver", "restart");
                                                wifiManager.startScan();
                                            }
                                        }
                                    }, 30000);
                                }
                            }
                        }
                    };

                    IntentFilter wifiFilter = new IntentFilter(
                            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                            //WifiManager.
                    registerReceiver(wifiBroadcastReceiver, wifiFilter);

                    wifiManager.disconnect();
                    wifiManager.startScan();
                    */

                }
                if (isBluetoothReady) {
                    Log.d("MainActivity", "bluetoothDeviceHandler scan started");

                    requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION/*, Manifest.permission.BLUETOOTH_SCAN*/ /*, Manifest.permission.BLUETOOTH_CONNECT*/}, PERMISSION_REQUEST_BLUETOOTH);
                    Log.d("MainActivity", "bluetoothDeviceHandler scan finished");
                };
            }
        });
        /*
        Handler handler = new Handler();
        //delayRun.postDelayed(new Runnable() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                    displayDevices();
                    handler.postDelayed(this, 5000);
            }
        };
        handler.post(task);

         */

        //ArrayList<BluetoothDeviceInfo> e = getBlueToothDevices();
        //displayDevices();

    }
    public void displayCurrentWifiDevices() {
        Log.d("wifiBroadcastReceiver", "displayCurrentWifiDevices");
        Log.d("wifiBroadcastReceiver", "currentWifiDevices.size() "+currentWifiDevices.size());
        String[] wifiDeviceStringArray = new String[currentWifiDevices.size()];
        for (int i = 0; i < wifiDeviceStringArray.length; i++) {
            wifiDeviceStringArray[i] =
                    "WIFI- BSSID: "+currentWifiDevices.get(i).BSSID+", "
                    +"SSID: "+currentWifiDevices.get(i).SSID+", "
                    +"dBm: "+currentWifiDevices.get(i).level;
            /*
                            +"name: "+currentDeviceInfos.get(i).getDeviceName()+", "
                            +"type: "+currentDeviceInfos.get(i).getDeviceType()+", "
                            +currentDeviceInfos.get(i).getSignalStrength()
            ;
             */
        }
        ArrayAdapter wifiListAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.textView, wifiDeviceStringArray);
        ListView listView = (ListView) findViewById(R.id.wifiDeviceListView);
        listView.setAdapter(wifiListAdapter);
    }
    public void saveCurrentWifiDevices() {
        for (int i = 0; i < currentWifiDevices.size(); i++) {
            if (database != null) {
                database.execSQL(
                        "insert into wifiDevices(bssid, name, signalStrength) values (?, ?, ?)",
                        new Object[] {
                                currentWifiDevices.get(i).BSSID,
                                currentWifiDevices.get(i).SSID,
                                currentWifiDevices.get(i).level
                        }
                );
            }
        }

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
                //bluetoothDeviceHandler.scanBluetoothDevices();
                scanBluetoothDevices();

                //////////

                Log.d("MainActivity", "wifi scan started");
                final BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                            Log.d("wifiBroadcastReceiver", "Action Found triggered");
                            List<ScanResult> scanResults = wifiManager.getScanResults();
                            Log.d("wifiBroadcastReceiver", "scanResults size "+scanResults.size());
                            currentWifiDevices = new ArrayList<ScanResult>(scanResults);

                            for (ScanResult wifiAPResult : scanResults ) {
                                Log.d("wifiBroadcastReceiver",
                                        "Found AP: " + (wifiAPResult.toString()));
                            }
                            displayCurrentWifiDevices();
                            saveCurrentWifiDevices();

                        }
                    }
                };
                IntentFilter wifiFilter = new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(wifiBroadcastReceiver, wifiFilter);
                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                wifiManager.disconnect();
                //wifiManager.startScan();
                Handler handler = new Handler();
                //delayRun.postDelayed(new Runnable() {
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        if (wifiScanActive) {

                            wifiManager.startScan();
                            //wifiManager.stopScan();
                            handler.postDelayed(this, 30000);
                            Log.d("wifiBroadcastReceiver", "restart");
                        } else {
                            Log.d("wifiBroadcastReceiver", "unregister");
                            unregisterReceiver(wifiBroadcastReceiver);
                        }
                    }
                };
                handler.post(task);
                /////////////
            }
        }
    }
    public boolean getWifiState() {
        if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            Log.i("MainActivity", "Wifi is deactivated");
            AlertDialog.Builder wifiDisableNotificationBuilder =
                    new AlertDialog.Builder(this);
            wifiDisableNotificationBuilder.setMessage("Wifi nicht aktiviert!");
            wifiDisableNotificationBuilder.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog wifiDisableNotification =
                    wifiDisableNotificationBuilder.create();
            wifiDisableNotification.show();
            return false;
        }
        return true;
    }
    public void scanBluetoothDevices() {
        blManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        blAdapter = blManager.getAdapter();
        Log.i("BluetoothDeviceHandler", "Bluetooth scan started");
        IntentFilter blFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        blFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        final BroadcastReceiver bluetoothBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == BluetoothDevice.ACTION_FOUND) {
                    Log.d("bluetoothBroadcastReceiver", "Action Found triggered");
                    int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    String rssiString = "RSSI="+rssi+"dBm";
                    //Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
                    BluetoothDevice blDevice = intent.getParcelableExtra(
                            BluetoothDevice.EXTRA_DEVICE);
                    if (blDevice != null) {
                        /*
                        //not needed, just ignore warning
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d("bluetoothBroadcastReceiver", "missing permissions!");
                            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
                        }

                         */
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
                        BluetoothDeviceInfo bluetoothDeviceInfo = new BluetoothDeviceInfo();
                        bluetoothDeviceInfo.setDeviceType(Integer.toString(blDevice.getType()));
                        bluetoothDeviceInfo.setDeviceAddress(blDevice.getAddress());
                        if (bluetoothDeviceInfo.getDeviceAddress() == null) {
                            bluetoothDeviceInfo.setDeviceAddress("null");
                        }
                        bluetoothDeviceInfo.setDeviceName(blDevice.getName());
                        if (bluetoothDeviceInfo.getDeviceName() == null) {
                            bluetoothDeviceInfo.setDeviceName("null");
                        }
                        bluetoothDeviceInfo.setSignalStrength(rssiString);
                        boolean isUnique = true;
                        for (int i = 0; i < currentDeviceInfos.size(); i++) {
                            if (currentDeviceInfos.get(i).isEqual(bluetoothDeviceInfo)) {
                                isUnique = false;
                            }
                        }
                        if (isUnique) {
                            currentDeviceInfos.add(bluetoothDeviceInfo);
                            saveBluetoothDevice(blDevice, rssiString);
                        }
                        //DatabaseService.getInstance().saveBluetoothDevice(blDevice);
                        Log.d("bluetoothBroadcastReceiver",
                                "Found Bluetooth Device: " + deviceName + "," + deviceAddress + ", Type: " + deviceTypeString);
                        /* debugging tool: gib bluetooth daten über popup aus
                        AlertDialog.Builder bluetoothDeviceNotificationBuilder
                                = new AlertDialog.Builder(context);
                        bluetoothDeviceNotificationBuilder.setMessage("Found Bluetooth Device: " + deviceName + "," + deviceAddress + ", Type: " + deviceTypeString);

                        bluetoothDeviceNotificationBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog bluetoothDeviceNotification = bluetoothDeviceNotificationBuilder.create();
                        bluetoothDeviceNotification.show();
                         */
                    }
                } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                    //todo baue abbruchbedingung ein
                    //
                    if(blScanActive) {
                    deviceInfos = getBlueToothDevices();
                    Log.d("DB", "Zahl Entitäten in BT db:"+deviceInfos.size());
                    //Log.d("DB", "XXXXXXXXXXXXXXXXXEntität 1:"+deviceInfos.get(0).getDeviceName());


                    blAdapter.startDiscovery();
                    displayCurrentDevices();
                    currentDeviceInfos = new ArrayList<>();
                    }
                }
            }
        };
        if (blScanActive) {
            registerReceiver(bluetoothBroadcastReceiver, blFilter);
            blAdapter.startDiscovery();
        }
        else {
            unregisterReceiver(bluetoothBroadcastReceiver);
        }

    }
    public void displayDevices() {
        //if (deviceInfos != null) {
            ArrayList<BluetoothDeviceInfo> e = getBlueToothDevices();
            String[] deviceStringArray = new String[e.size()];
            for (int i = 0; i < deviceStringArray.length; i++) {
                deviceStringArray[i] =
                        "BT- adr: "+e.get(i).getDeviceAddress()+", "
                                +"name: "+e.get(i).getDeviceName()+", "
                                +"type: "+e.get(i).getDeviceType()+", "
                                +e.get(i).getSignalStrength()
                ;

            }

        Log.d("displayDevices", "*****deviceStringArray.lengtg "+ deviceStringArray.length);
        //Log.d("displayDevices", "*****deviceStringArray tenth "+ deviceStringArray[10]);


            ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.textView, deviceStringArray);
            ListView listView = (ListView) findViewById(R.id.deviceListView);
            listView.setAdapter(adapter);
        //} else {
            //Log.d("displayDevices", "))))))))))))))))))))deviceInfos is null!");
        //}
    }
    public void displayCurrentDevices() {
        Log.d("displayCurrentDevices", "started");
        Log.d("displayCurrentDevices", "currentDeviceInfos.size() "+currentDeviceInfos.size());
        String[] deviceStringArray = new String[currentDeviceInfos.size()];
        for (int i = 0; i < deviceStringArray.length; i++) {
            deviceStringArray[i] =
                    "BT- adr: "+currentDeviceInfos.get(i).getDeviceAddress()+", "
                            +"name: "+currentDeviceInfos.get(i).getDeviceName()+", "
                            +"type: "+currentDeviceInfos.get(i).getDeviceType()+", "
                            +currentDeviceInfos.get(i).getSignalStrength()
            ;

        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.textView, deviceStringArray);
        ListView listView = (ListView) findViewById(R.id.deviceListView);
        listView.setAdapter(adapter);
    }




    public void saveBluetoothDevice(BluetoothDevice bluetoothDevice, String rssiString) {
        //database.execSQL("drop table bluetoothDevices");

        //database.execSQL("create table bluetoothDevices(address, type, name, rssiStrength)");
        if (database != null) {
            database.execSQL(
                    "insert into bluetoothDevices(address, type, name, rssiStrength) values (?, ?, ?, ?)",
                    new Object[] {
                            bluetoothDevice.getAddress(),
                            bluetoothDevice.getType(),
                            bluetoothDevice.getName(),
                            rssiString
                    }
            );
        }

        Log.d("databaseService", "!!!!!!!!!!!!!!!!!!!!!save: database is null !!!!! ");
    }
    public ArrayList<BluetoothDeviceInfo> getBlueToothDevices() {
        ArrayList<BluetoothDeviceInfo> bluetoothDevices = new ArrayList<>();
        if (database != null) {
            Cursor cursor = database.query("select * from bluetoothDevices;");
            if (cursor.getCount()>0) {
                while (!cursor.isLast()) {
                    cursor.moveToNext();
                    BluetoothDeviceInfo bluetoothDevice = new BluetoothDeviceInfo();
                    bluetoothDevice.setDeviceAddress(cursor.getString(0));
                    bluetoothDevice.setDeviceType(cursor.getString(1));
                    bluetoothDevice.setDeviceName(cursor.getString(2));
                    bluetoothDevice.setSignalStrength(cursor.getString(3));
                    bluetoothDevices.add(bluetoothDevice);
                }
            }
            return bluetoothDevices;
        }
        Log.d("databaseService", "!!!!!!!!!!!!!!!!!!!!!get: database is null !!!!! ");
        return null;

    }

}