package com.example.thesocialquadruped;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class ManualActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "bluetooth2";
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module 
    private static String address = "20:15:07:27:84:09";

    // intialize layout of ManualActivity.java
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        Button button_forward, button_right, button_left, button_backward, button_stand, button_wave, button_sleep, button_roam;

        button_forward = (Button) findViewById(R.id.button_forward);
        button_forward.setOnClickListener(this);

        button_right = (Button) findViewById(R.id.button_right);
        button_right.setOnClickListener(this);

        button_roam = (Button) findViewById(R.id.button_roam);
        button_roam.setOnClickListener(this);

        button_left = (Button) findViewById(R.id.button_left);
        button_left.setOnClickListener(this);

        button_backward = (Button) findViewById(R.id.button_backward);
        button_backward.setOnClickListener(this);

        button_stand = (Button) findViewById(R.id.button_stand);
        button_stand.setOnClickListener(this);

        button_wave = (Button) findViewById(R.id.button_wave);
        button_wave.setOnClickListener(this);

        button_sleep = (Button) findViewById(R.id.button_sleep);
        button_sleep.setOnClickListener(this);

        // get bluetooth adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();		
        checkBTState();
    }

    // attempts to create a connection with the Bluetooth module
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    // what to do when ManualActivity is in the foreground
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // cancel discovery because it is resource intensive
        btAdapter.cancelDiscovery();

        // establish the connection
        // this will block until it connects
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // create a data stream 
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    // what to do when ManualActivity is paused
    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    // check for Bluetooth support and then check to make sure it is turned on
    private void checkBTState() {
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                // prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // appends error essage to response
    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    // client thread initiates bluetooth connection
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            // buffer store for the stream
            byte[] buffer = new byte[256];  
            // bytes returned from read()
            int bytes; 

            // keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // read from the InputStream
                    // get number of bytes and message in "buffer"
                    bytes = mmInStream.read(buffer);		
                } catch (IOException e) {
                    break;
                }
            }
        }

        // sends data to the remote device 
        public void write(String message) {
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                //Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

   // commands that each button sends via bluetooth after being clicked
   private void button_forward() {
        mConnectedThread.write("f");
    }

    private void button_right() {
        mConnectedThread.write("r");
    }

    private void button_roam() {
        mConnectedThread.write("a");
    }

    private void button_left() {
        mConnectedThread.write("l");
    }

    private void button_backward() {
        mConnectedThread.write("b");

    }

    private void button_stand() {
        mConnectedThread.write("u");
    }

    private void button_wave() {
        mConnectedThread.write("w");
    }

    private void button_sleep() {
        mConnectedThread.write("s");
    }

    public void onClick (View v)
    {
        switch (v.getId ())
        {
            case R.id.button_forward:
                button_forward();
                break;
            case R.id.button_right:
                button_right();
                break;
            case R.id.button_roam:
                button_roam();
                break;
            case R.id.button_left:
                button_left();
                break;
            case R.id.button_backward:
                button_backward();
                break;
            case R.id.button_stand:
                button_stand();
                break;
            case R.id.button_wave:
                button_wave();
                break;
            case R.id.button_sleep:
                button_sleep();
                break;
        }
    }
}
