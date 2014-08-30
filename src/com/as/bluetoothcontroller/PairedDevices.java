package com.as.bluetoothcontroller;

import java.io.OutputStream;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PairedDevices extends ListActivity {

	private BluetoothAdapter BA;
	private Set<BluetoothDevice> pairedDevices;
	ArrayAdapter<String>  adapter;
	BluetoothDevice mPairedDevices [];
	private OutputStream outputStream;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paired_devices);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		BA = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = BA.getBondedDevices();
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked);
		mPairedDevices = new BluetoothDevice [pairedDevices.size()];
		setListAdapter(adapter);
		int i=0;
		for(BluetoothDevice bt: pairedDevices){
			adapter.add(bt.getName()+"\n"+bt.getAddress());
			mPairedDevices[i] = bt;
			i++;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		
		if(getListView().isItemChecked(position)) {
			Toast.makeText(PairedDevices.this, "", Toast.LENGTH_LONG).show();
		}
	}
}
