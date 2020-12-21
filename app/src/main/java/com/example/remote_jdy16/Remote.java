package com.example.remote_jdy16;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

public class Remote extends Activity {
    Boolean btScanning = false, activeF = false, activeB = false;
    Boolean activeL = false, activeR = false;
    Button bt_connect, button_f,button_b,button_l,button_r;
    SeekBar seekBar;
//    Connect con = new Connect();

    @SuppressLint({"ClickableViewAccessibility", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote1);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        button_f = (Button)findViewById(R.id.button_f);
        button_b = (Button)findViewById(R.id.button_b);
        button_l = (Button)findViewById(R.id.button_l);
        button_r = (Button)findViewById(R.id.button_r);

        button_f.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!activeB){
                            moveFPWM("ff");
                            activeF = true;
                        }

                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (!activeB){
                            moveFPWM("00");
                            activeF = false;
                        }
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });


        button_b.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!activeF) {
                            moveB("01");
                            activeB = true;
                        }
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (!activeF) {
                            moveB("00");
                            activeB = false;
                        }
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });

        button_l.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!activeR) {
                            moveL("01");
                            activeL = true;
                        }
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (!activeR) {
                            moveL("00");
                            activeL = false;
                        }
                        return true;
                    }



                    default:
                        return false;
                }
            }
        });

        button_r.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!activeL) {
                            moveR("01");
                            activeR = true;
                        }
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (!activeL) {
                            moveR("00");
                            activeR = false;
                        }
                        return true;
                    }



                    default:
                        return false;
                }
            }
        });

//        seekBar=(SeekBar)findViewById(R.id.seekBar);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,
//                                          boolean fromUser) {
////                Toast.makeText(getApplicationContext(),"seekbar progress: "+progress, Toast.LENGTH_SHORT).show();
//                if (!activeB) {
//                    String va = Integer.toHexString(progress);
//                    if (va.length() == 1){
//                        va = "0" + va;
//                    }
//                    System.out.println(va);
//
//                    try {
//                        moveFPWM(va);
//                    } catch (Exception e) {
//                        System.out.println("error data: "+ Integer.toHexString(progress));
//                        System.out.println(e);
//                    }
//
//                    activeF = true;
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
////                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
//                System.out.println("onStartTrackingTouch");
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
////                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
//                if (!activeB) {
//                    System.out.println("onStopTrackingTouch");
//                    moveFPWM("00");
//                    activeF = false;
//                }
//            }
//        });
        //

    }



    public void moveFPWM(String a){
//        Toast.makeText(this, "moveFPWM " + a, Toast.LENGTH_SHORT)
//                .show();
//        characteristic.setValue(hexStringToByteArray("e8a3"+a));
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//        bluetoothGatt.writeCharacteristic(characteristic);
        Connect.senData("e8a4"+a);
    }

    public void moveB(String a){
//        Toast.makeText(this, "moveB " + a, Toast.LENGTH_SHORT)
//                .show();
//        characteristic.setValue(hexStringToByteArray("e7f1"+a));
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//        bluetoothGatt.writeCharacteristic(characteristic);
        Connect.senData("e7f1"+a);
    }

    public void moveL(String a){
//        Toast.makeText(this, "moveL " + a, Toast.LENGTH_SHORT)
//                .show();
//        characteristic.setValue(hexStringToByteArray("e7f3"+a));
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//        bluetoothGatt.writeCharacteristic(characteristic);
        Connect.senData("e7f3"+a);
    }

    public void moveR(String a){
//        Toast.makeText(this, "moveR " + a, Toast.LENGTH_SHORT)
//                .show();
//        characteristic.setValue(hexStringToByteArray("e7f2"+a));
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//        bluetoothGatt.writeCharacteristic(characteristic);
        Connect.senData("e7f2"+a);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Connect.disconnectDeviceSelected();
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        Connect.disconnectDeviceSelected();
//    }
}
