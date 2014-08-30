package com.as.bluetoothcontroller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ScanforNewDevices extends ListActivity {

	private BluetoothAdapter BA;
	private Set<BluetoothDevice> pairedDevices;
	ArrayAdapter<String>  adapter;
	BluetoothDevice mPairedDevices [];
	ArrayList<BluetoothDevice> bdevices;
	BluetoothDevice BTD [];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paired_devices);
				
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked);
		setListAdapter(adapter);
		
		bdevices = new ArrayList<BluetoothDevice>();
		BA = BluetoothAdapter.getDefaultAdapter();
		BA.startDiscovery();
		
		Toast.makeText(ScanforNewDevices.this, "Scanning.. ", Toast.LENGTH_LONG).show();
		
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		
		registerReceiver(mReceiver, filter);
		
		
	}
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            // Add the name and address to an array adapter to show in a ListView
	            bdevices.add(device);
	            adapter.add(device.getName() + "\n" + device.getAddress());
	            Toast.makeText(ScanforNewDevices.this, "Scanning.. Wait till \"SCAN OVER\" notification", Toast.LENGTH_LONG).show();
	            adapter.notifyDataSetChanged();
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	Toast.makeText(ScanforNewDevices.this, "SCAN OVER", Toast.LENGTH_LONG).show();
	        	storeBD(bdevices.size());
	        }
	        else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(ScanforNewDevices.this, "Paired", Toast.LENGTH_LONG).show();
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                	Toast.makeText(ScanforNewDevices.this, "Un Paired", Toast.LENGTH_LONG).show();
                }

           }
	    }

		private void storeBD(int size) {
			BTD = new BluetoothDevice[size];
			int i=0;
			for(BluetoothDevice bd: bdevices){
				BTD[i] = bd;
				i++;
			}
			
		}
	};

	private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//Pairing
		if(getListView().isItemChecked(position)) {
			Toast.makeText(ScanforNewDevices.this, "Pairing Started", Toast.LENGTH_LONG).show();
			pairDevice(BTD[position]);
		}
		else {
			//Upair
			unpairDevice(BTD[position]);
		}
	}
	
}
