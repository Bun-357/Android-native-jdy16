package com.example.remote_jdy16;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static androidx.core.content.ContextCompat.getSystemService;


public class Connect extends Activity {
    Button bt_connect;
    TextView text_status;
    EditText ble_name;
    Switch sw_pwm;
//    Ble ble;
    String deviceName = "";
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<BluetoothDevice>();
    ArrayList<String> ble_all_name = new ArrayList<String>();
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    static BluetoothGatt bluetoothGatt;
    UUID characteristicUUID;
    static BluetoothGattCharacteristic characteristic;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public Map<String, String> uuids = new HashMap<String, String>();

    // Stops scanning after 5 seconds.
    private Handler mHandler = new Handler();
    private static final long SCAN_PERIOD = 5000;
    int deviceIndex = 0;
    String statusWork = "", service_uuid = "0000ffe2-0000-1000-8000-00805f9b34fb";
    Boolean pwm_select = false;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        bt_connect = (Button)findViewById(R.id.button_connect);
        text_status = (TextView)findViewById(R.id.textView_status);
        ble_name = (EditText)findViewById(R.id.editText_ble_name);
        bt_connect.setVisibility(View.INVISIBLE);
        sw_pwm = (Switch)findViewById(R.id.switch_pwm);

        sw_pwm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    System.out.println("pwm on");
                    pwm_select = true;
                } else {
                    System.out.println("pwm off");
                    pwm_select = false;
                }
            }
        });


        btManager = (android.bluetooth.BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        startScanning();
//        return true;

    }// end On create



    public void btConnect(View view){
        deviceName = ble_name.getText().toString();
        text_status.setText("Connect to " + deviceName);
        System.out.println("Connect to " + deviceName);
        deviceIndex = 0;
        Boolean found_de = false;
        for (String ble_name: ble_all_name) {
            System.out.println("ble_name: " + ble_name);
            if (ble_name.equals(deviceName)){
                bluetoothGatt = devicesDiscovered.get(deviceIndex).connectGatt(this, false, btleGattCallback);
                found_de = true;
                setViewToRemote();
                break;
            }
            deviceIndex++;
            if (!found_de){
                text_status.setText(deviceName+ " not found");
            }

        }
        //bluetoothGatt = devicesDiscovered.get(deviceIndex).connectGatt(this, false, btleGattCallback);


    }

    public void setViewToRemote(){

        if (pwm_select){
            System.out.println("pwm select");
            Intent i = new Intent(getApplicationContext(), Remote2.class);
            startActivity(i);
            finish();
        } else {
            System.out.println("No pwm select");
            Intent i = new Intent(getApplicationContext(), Remote.class);
            startActivity(i);
            finish();
        }

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static void senData(String data){
        characteristic.setValue(hexStringToByteArray(data));
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        bluetoothGatt.writeCharacteristic(characteristic);
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            text_status.setText("Scanning.......");
            statusWork = "Scan";
            System.out.println("found :" + result.getDevice().getName());
            if (result.getDevice().getName() != null){
                ble_all_name.add(result.getDevice().getName());
                devicesDiscovered.add(result.getDevice());
            }

//            peripheralTextView.append("Index: " + deviceIndex + ", Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");

//            deviceIndex++;
//            // auto scroll for text view
//            final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
//            // if there is no need to scroll, scrollAmount will be <=0
//            if (scrollAmount > 0) {
//                peripheralTextView.scrollTo(0, scrollAmount);
//            }
        }
    };

    // Device connect call back
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation

//            MainActivity.this.runOnUiThread(new Runnable() {
//                public void run() {
//                    peripheralTextView.append("device read or wrote to\n");
//                }
//            });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            System.out.println(newState);
            switch (newState) {
                case 0:
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            peripheralTextView.append("device disconnected\n");
//                            connectToDevice.setVisibility(View.VISIBLE);
//                            disconnectDevice.setVisibility(View.INVISIBLE);
//                        }
//                    });
                    text_status.setText("Connect to " + deviceName + " error");
                    break;
                case 2:
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            peripheralTextView.append("device connected\n");
//                            connectToDevice.setVisibility(View.INVISIBLE);
//                            disconnectDevice.setVisibility(View.VISIBLE);
//                        }
//                    });

                    // discover services and characteristics for this device
                    text_status.setText(deviceName + " connected");
                    // goto remote
//                    setViewToRemote();
                    bluetoothGatt.discoverServices();


                    break;
                default:
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            peripheralTextView.append("we encounterned an unknown state, uh oh\n");
//                        }
//                    });
                    text_status.setText("Connect to " + deviceName + " error 2");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a 			BluetoothGatt.discoverServices() call
//            MainActivity.this.runOnUiThread(new Runnable() {
//                public void run() {
//                    peripheralTextView.append("device services have been discovered\n");
//                }
//            });
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
    };

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {

        System.out.println(characteristic.getUuid());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    public void startScanning() {
        System.out.println("start scanning");
//        btScanning = true;
//        deviceIndex = 0;
//        devicesDiscovered.clear();
//        peripheralTextView.setText("");
//        peripheralTextView.append("Started Scanning\n");
//        startScanningButton.setVisibility(View.INVISIBLE);
//        stopScanningButton.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
            }
        }, SCAN_PERIOD);
    }

    public void stopScanning() {
        System.out.println("stopping scanning");
        statusWork = "Scan complete";
        bt_connect.setVisibility(View.VISIBLE);
        text_status.setText("Scan complete.");
//        peripheralTextView.append("Stopped Scanning\n");
//        btScanning = false;
//        startScanningButton.setVisibility(View.VISIBLE);
//        stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    public void connectToDeviceSelected() {
        //peripheralTextView.append("Trying to connect to device at index: " + deviceIndexInput.getText() + "\n");
//        int deviceSelected = Integer.parseInt(deviceIndexInput.getText().toString());
//        bluetoothGatt = devicesDiscovered.get(deviceSelected).connectGatt(this, false, btleGattCallback);
    }

    public static void disconnectDeviceSelected() {
//        peripheralTextView.append("Disconnecting from device\n");
        bluetoothGatt.disconnect();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            final String uuid = gattService.getUuid().toString();
            System.out.println("Service discovered: " + uuid);
//            MainActivity.this.runOnUiThread(new Runnable() {
//                public void run() {
//                    peripheralTextView.append("Service disovered: "+uuid+"\n");
//                }
//            });
            new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {

                final String charUuid = gattCharacteristic.getUuid().toString();
                if (charUuid.equals(service_uuid)){
                    System.out.println(charUuid);
                    characteristic = gattCharacteristic;
                    characteristic.setValue(hexStringToByteArray("e8a101"));
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
                System.out.println("Characteristic discovered for service: " + charUuid);
//                Connect.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        //peripheralTextView.append("Characteristic discovered for service: "+charUuid+"\n");
//                    }
//                });

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        client.connect();
//        Notification.Action viewAction = Notification.Action.newAction(
//                Notification.Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.joelwasserman.androidbleconnectexample/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

//        Notification.Action viewAction = Notification.Action.newAction(
//                Notification.Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.joelwasserman.androidbleconnectexample/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }


}
