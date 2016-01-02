package com.example.asg.view;


import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.GE;
import com.example.asg.services.meteraccess.GEConnector;
import com.example.asg.services.meteraccess.Scheider;
import com.example.asg.services.meteraccess.ScheiderConnector;
import com.example.asg.services.meteraccess.Sentron;
import com.example.asg.services.meteraccess.SentronConnector;
import com.example.asg.util.CommonFunction;

import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.content.Intent;

import android.view.Menu;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class MainActivity extends FatherActivity {

	public Button btnConnect;
	public Button btnCancel;
	public EditText editTextDeviceAddress;
	public EditText editTextHost;
	public EditText editTextNetWorkPort;
	public Spinner spinnertype;
	public ImageButton ibtnScan;
	Handler handler = null;
	int addr = 0;// the input addr(unitID)
	String host = "";// the input host ip
	int port = 502;// the input port
	int type = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnConnect = (Button) this.findViewById(R.id.main_btnConnect);
		btnCancel = (Button) this.findViewById(R.id.main_btnCancel);
		ibtnScan = (ImageButton) this.findViewById(R.id.main_btnScan);

		ConnectListener cl = new ConnectListener();
		btnConnect.setOnClickListener(cl);
		btnCancel.setOnClickListener(cl);
		ibtnScan.setOnClickListener(cl);

		editTextDeviceAddress = (EditText) this
				.findViewById(R.id.main_etDeviceAddress);
		editTextHost = (EditText) this.findViewById(R.id.main_etHost);
		editTextNetWorkPort = (EditText) this
				.findViewById(R.id.main_etNetWorkPort);

		spinnertype = (Spinner) this.findViewById(R.id.main_spinType);

		String[] strs = { "Nexus 1272", "PAC 4200", "Scheider PM8" };
		ArrayAdapter aa = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, strs);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnertype.setAdapter(aa);
		spinnertype.setSelection(1);

		// if the meter is got by scanning
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			String str = bundle.getString("meterData");
			// addIPButton(scanIP+";unitID:"+uID+";"+"meterType:Nexus 1272");
			strs = str.split(";");
			this.editTextHost.setText(strs[0]);
			this.editTextDeviceAddress.setText(strs[1].substring(7));

			int type = 0;
			if (strs[2].contains("Nexus 1272")) {
				type = 0;
			} else if (strs[2].contains("PAC4200")) {
				type = 1;
			} else if (strs[2].contains("PM8")) {
				type = 2;
			}
			this.spinnertype.setSelection(type);
		}

		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				if (msg.getData().getBoolean("isConnected")) {
					turnToMenuActivity();
				} else {
					CommonFunction.showDialog("Fail",
							"Can't connect to the meter!", MainActivity.this);
				}

			}
		};
	}

	public void turnToMenuActivity() {
		// redirect the activity
		Intent myintent = new Intent();
		myintent.setClass(MainActivity.this, MenuActivity.class);
		MainActivity.this.startActivity(myintent);
		MainActivity.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class ConnectListener implements OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0 == btnConnect) {
				// meter address/unitID
				addr = Integer.parseInt(editTextDeviceAddress.getText()
						.toString());
				// meter ip
				host = editTextHost.getText().toString();
				// port
				port = Integer.parseInt(editTextNetWorkPort.getText()
						.toString());
				// 0 is GE£¬1 is sentron, 2 is scheider
				type = spinnertype.getSelectedItemPosition();

				if (connector != null) {
					connector.disconnect();
				}
				// start the thread for connection, android can't put i/0
				// operation on the ui thread
				ConnectThread conThread = new ConnectThread();
				conThread.start();
			} else if (arg0 == btnCancel) {
				/*
				 * if(connector != null){ connector.disconnect(); }
				 * MainActivity.this.finish();
				 */
				Intent myintent = new Intent();
				myintent.setClass(MainActivity.this, ScanActivity.class);
				MainActivity.this.startActivity(myintent);
				MainActivity.this.finish();
			} else if (arg0 == ibtnScan) {
				Intent myintent = new Intent();
				myintent.setClass(MainActivity.this, ScanActivity.class);
				MainActivity.this.startActivity(myintent);
				MainActivity.this.finish();
			}
		}
	}

	class ConnectThread extends Thread {
		@Override
		public void run() {

			Bundle data = new Bundle();
			// GE
			if (type == 0) {
				meter = new GE();
				connector = new GEConnector();
				boolean suc = connector.connectNonjamod(host, port, addr, type);
				connector.disconnect();
				data.putBoolean("isConnected", suc);
				meter.setConnector(connector);
				((ASGApplication) getApplication()).setMeter(meter);
				((ASGApplication) getApplication()).setConnector(connector);
				// sentron
			} else if (type == 1) {
				meter = new Sentron();
				connector = new SentronConnector();
				boolean suc = connector.connectNonjamod(host, port, addr, type);
				connector.disconnect();
				data.putBoolean("isConnected", suc);
				meter.setConnector(connector);
				((ASGApplication) getApplication()).setMeter(meter);
				((ASGApplication) getApplication()).setConnector(connector);
				// scheider
			} else if (type == 2) {
				meter = new Scheider();
				connector = new ScheiderConnector();
				boolean suc = connector.connectNonjamod(host, port, addr, type);
				connector.disconnect();
				data.putBoolean("isConnected", suc);
				meter.setConnector(connector);
				((ASGApplication) getApplication()).setMeter(meter);
				((ASGApplication) getApplication()).setConnector(connector);
			}
			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);

		}
	}

}
