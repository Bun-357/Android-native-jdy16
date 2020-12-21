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
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class Remote2 extends Activity {
    Boolean btScanning = false, activeF = false, activeB = false;
    Boolean activeL = false, activeR = false;
    Button bt_connect, button_f,button_b,button_l,button_r;
    SeekBar seekBar;
    TextView text_data_joy_left;
    JoystickView joystick_left;
//    Connect con = new Connect();

    @SuppressLint({"ClickableViewAccessibility", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_pwm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        button_f = (Button)findViewById(R.id.button_f);
//        button_b = (Button)findViewById(R.id.button_b2);
        button_l = (Button)findViewById(R.id.button_l2);
        button_r = (Button)findViewById(R.id.button_r2);

//        button_f.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        if (!activeB){
//                            moveFPWM("ff");
//                            activeF = true;
//                        }
//
//                        return true;
//                    }
//
//                    case MotionEvent.ACTION_UP: {
//                        if (!activeB){
//                            moveFPWM("00");
//                            activeF = false;
//                        }
//                        return true;
//                    }
//
//                    default:
//                        return false;
//                }
//            }
//        });


//        button_b.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        if (!activeF) {
//                            moveB("01");
//                            activeB = true;
//                        }
//                        return true;
//                    }
//
//                    case MotionEvent.ACTION_UP: {
//                        if (!activeF) {
//                            moveB("00");
//                            activeB = false;
//                        }
//                        return true;
//                    }
//
//                    default:
//                        return false;
//                }
//            }
//        });

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

                    case MotionEvent.ACTION_BUTTON_PRESS: {
                        if (!activeR) {
                            moveL("01");
                            activeL = true;
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

                    case MotionEvent.ACTION_BUTTON_PRESS: {
                        if (!activeL) {
                            moveR("01");
                            activeR = true;
                        }
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });

        joystick_left = (JoystickView) findViewById(R.id.joy_left);
//        joystick_left.setFixedCenter(false); // set up auto-define center
        joystick_left.setButtonDirection(1); // vertical only
        text_data_joy_left = (TextView)findViewById(R.id.text_status_joy_left);
        joystick_left.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                float data_x =  joystick_left.getNormalizedX();
                float data_y =  joystick_left.getNormalizedY();
                text_data_joy_left.setText("data_x: " + data_x + "     data_y: " + data_y + "\n"+
                        "angle: "+ angle + " strength: "+ strength);
                //
                if (angle == 90 && !activeB){
//                    if (!activeB) {
                        float bit8 = strength*(255.0f/100.0f);
                        if (bit8 > 245){
                            bit8 = 255;
                        }
                        String va = Integer.toHexString((int)bit8);
                        if (va.length() == 1){
                            va = "0" + va;
                        }
                        System.out.println(va);

                        try {
                            moveFPWM(va);
                        } catch (Exception e) {
                            System.out.println("error data: "+ Integer.toHexString((int)bit8));
                            System.out.println(e);
                        }

                        activeF = true;
//                }
                } else if (angle == 0 && !activeB){
                    try {
                        moveFPWM("00");
                    } catch (Exception e) {
//                        System.out.println("error data: "+ Integer.toHexString(bit8));
                        System.out.println(e);
                    }
                    activeF = false;
                } else if (angle == 0 && !activeF){
                    moveB("00");
                    activeB = false;
                } else if (angle == 270 && !activeF){
                    if (strength < 20){
                        moveB("00");
                        activeB = false;
                    } else {
                        moveB("01");
                        activeB = true;
                    }
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


    private int getStrength(int mPosX, int mPosY) {
        return (int) (100 * Math.sqrt((mPosX - 50)
                * (mPosX - 50) + (mPosY - 50)
                * (mPosY - 50)) / 100);
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
