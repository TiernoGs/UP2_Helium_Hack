package me.brilli.stefano.up2test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static String TAG = "MAIN";

    private ListView listview;
    private ArrayList<String> mDeviceList = new ArrayList<String>();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview1);

        // Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        device = mBluetoothAdapter.getRemoteDevice("E9:7C:2F:27:15:00");


        Button b = (Button)findViewById(R.id.testButton);
        b.setOnClickListener(this);

        Button disconnect = (Button)findViewById(R.id.button2);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        Button refresh = (Button)findViewById(R.id.button3);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.cancelDiscovery();
                mBluetoothAdapter.startDiscovery();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                listview.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "handshake");
//        mBluetoothAdapter.disable();

//        try {
//            Thread.sleep(200, 0);
//            mBluetoothAdapter.enable();
//            Thread.sleep(200, 0);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        JawboneGattCallback jbGattCb = new JawboneGattCallback();
        JawboneGatt jbGatt = new JawboneGatt(device.connectGatt(this, true, jbGattCb));
        try {
            JawboneDevice jbDevice = new JawboneDevice(jbGatt, jbGattCb);
//            jbDevice.reset();
            jbDevice.handshake();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jbGatt.close();
        }
    }


    public void disconnect () {
        Log.d(TAG, "disconnect");

        JawboneGattCallback jbGattCb = new JawboneGattCallback();
        JawboneGatt jbGatt = new JawboneGatt(device.connectGatt(this, true, jbGattCb));
        try {
            JawboneDevice jbDevice = new JawboneDevice(jbGatt, jbGattCb);
            jbDevice.reset();
            jbGatt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jbGatt.close();
        }
    }
}
