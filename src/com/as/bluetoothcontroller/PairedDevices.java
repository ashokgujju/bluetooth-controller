package com.as.bluetoothcontroller;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PairedDevices extends ListActivity {

	private BluetoothAdapter mBluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;
	ArrayAdapter<String>  adapter;
	BluetoothDevice mPairedDevices [];
	String command;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paired_devices);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked);
		mPairedDevices = new BluetoothDevice [pairedDevices.size()];
		setListAdapter(adapter);
		int i = 0;
		for(BluetoothDevice bt: pairedDevices) {
			adapter.add(bt.getName()+"\n"+bt.getAddress());
			mPairedDevices[i] = bt;
			i++;
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		command = "100"+(position+1);
		
		if(getListView().isItemChecked(position)) {
			Toast.makeText(PairedDevices.this, command+"03", Toast.LENGTH_LONG).show();
			command = command+"03";
		}
		else {
			Toast.makeText(PairedDevices.this, command+"02", Toast.LENGTH_LONG).show();
			command = command+"02";
		}
		
		sendData(position);
	}
	
	OutputStream os;
	
	public void sendData(int position) {
		BluetoothDevice device = mPairedDevices[position];
		 try {
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			BluetoothSocket clientSocket =  (BluetoothSocket) m.invoke(device, 1);
			clientSocket.connect();
			os = new DataOutputStream(clientSocket.getOutputStream());
			new clientSock().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class clientSock extends Thread {
	    public void run () {
	        try {
	            os.write(command.getBytes()); 
	            os.flush();
	        } catch (Exception e1) {
	            e1.printStackTrace();
	            return;
	        }
	    }
	}
}