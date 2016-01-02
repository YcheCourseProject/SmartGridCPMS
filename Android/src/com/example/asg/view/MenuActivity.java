package com.example.asg.view;
import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.GE;
import com.example.asg.services.meteraccess.Sentron;
import com.example.asg.util.CommonFunction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.view.View.OnClickListener;

public class MenuActivity extends FatherActivity implements OnClickListener,
		android.content.DialogInterface.OnClickListener {

	public static final int REFRESH = 0;

	ImageButton btnCheck;
	ImageButton btnPasswd;
	ImageButton btnScan;
	ImageButton btnModify;
	TextView txtViewTypeName;
	TextView txtViewIP;
	TextView txtViewState;
	View processBar;

	EditText et_passwd;

	// 实例化一个handler
	Handler handler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		Log.i("MenuActivity.oncreate", "0");
		connector = ((ASGApplication) getApplication()).getConnector();
		meter = ((ASGApplication) getApplication()).getMeter();
		this.txtViewTypeName = (TextView) this
				.findViewById(R.id.menu_tvTypeName);
		this.txtViewIP = (TextView) this.findViewById(R.id.menu_tvIP);
		this.txtViewState = (TextView) this.findViewById(R.id.menu_tvState);

		btnCheck = (ImageButton) this.findViewById(R.id.menu_btnCheck);
		btnPasswd = (ImageButton) this.findViewById(R.id.menu_btnPasswd);
		btnModify = (ImageButton) this.findViewById(R.id.menu_btnModify);
		btnScan = (ImageButton) this.findViewById(R.id.menu_btnScan);

		btnCheck.setOnClickListener(this);
		btnPasswd.setOnClickListener(this);
		btnModify.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		if (connector == null || connector.addrStr.equals("")) {
			this.txtViewState.setText("Unconnected");
		} else {
			if (connector.type == 0) {

				this.txtViewTypeName.setText("Nexus 1272");

			} else if (connector.type == 1) {

				this.txtViewTypeName.setText("PAC4200");

			} else if (connector.type == 2) {

				this.txtViewTypeName.setText("Scheider PM8");

			}
			this.txtViewIP.setText(connector.addrStr);
			this.txtViewState.setText("Connected");
		}

		// Log.i("MenuActivity.oncreate", "1st");

		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();

				if (data.getString("type").equals("passwdIdentify")) {
					// 如果是施耐德电表，不用输入密码
					if (meter.connector.type == 2) {
						turnToModifyActivity();
						return;
					}

					MenuActivity.this.et_passwd = new EditText(
							MenuActivity.this);
					AlertDialog.Builder builder = new AlertDialog.Builder(
							MenuActivity.this)
							.setView(MenuActivity.this.et_passwd);
					builder.setPositiveButton("Yes", MenuActivity.this);
					builder.setNegativeButton("No", MenuActivity.this);

					if (meter.connector.type == 0) {

						builder.setTitle("Please input the key(10 characters):");
						builder.show();

					} else if (meter.connector.type == 1) {
						builder.setTitle("Please input the key(4 digitals):");
						builder.show();

					}

				} else if (data.getString("type").equals("passwdValidate")) {

					if (meter.connector.type == 0) {
						meter.passwd = et_passwd.getText().toString().trim();
						Log.i("MenuActivity.handleMessage", "gePasswd="
								+ meter.passwd);
					} else if (meter.connector.type == 1) {
						meter.passwd = Sentron
								.changeIntToPasswdStr(Integer
										.parseInt(et_passwd.getText()
												.toString().trim()));

					}

					turnToModifyActivity();

				} else if (data.getString("type").equals("passwdInvalidate")) {
					CommonFunction.showDialog("Fail!", "Password wrong",
							MenuActivity.this);
				}

			}
		};
		Log.i("MenuActivity.oncreate", "end");
		// processBar =this.findViewById(R.id.ProcessBarContainer);
		// Log.w("tips",((Integer)(processBar.getVisibility())).toString());
		// processBar.setVisibility(View.INVISIBLE);
		// Log.w("tips",((Integer)(processBar.getVisibility())).toString());
		// 在onCreate()中开启线程

	}

	public void turnToModifyActivity() {

		Intent myintent = new Intent();
		myintent.setClass(this, ModifyParaActivity.class);
		this.startActivity(myintent);
		this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	/*
	 * public void turnToPasswdAcitivity(int passwd, int passwdState){
	 * 
	 * Intent myintent = new Intent(); Bundle mybundle = new Bundle();
	 * mybundle.putInt("passwd", passwd); mybundle.putInt("passwdState",
	 * passwdState);
	 * 
	 * myintent.putExtras(mybundle); myintent.setClass(this,
	 * PasswdActivity.class); this.startActivity(myintent); this.finish();
	 * //processBar.setVisibility(View.INVISIBLE); }
	 */

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == this.btnCheck) {
			if (connector == null || connector.addrStr.equals("")) {

				new AlertDialog.Builder(this).setTitle("error")
						.setMessage("please connect the meter")
						.setPositiveButton("return", null).show();
			} else {
				Intent myintent = new Intent();
				myintent.setClass(this, ConnectingActivity.class);
				this.startActivity(myintent);
				this.finish();
			}

		} else if (v == this.btnPasswd) {
			if (connector == null || connector.addrStr.equals("")) {
				new AlertDialog.Builder(this).setTitle("error")
						.setMessage("please connect the meter")
						.setPositiveButton("return", null).show();
			} else {
				// 如果是scheider，没有密码这种东西
				if (connector.type == 2) {
					CommonFunction.showDialog("Notice!", "No Password!",
							MenuActivity.this);
					return;
				}

				Intent myintent = new Intent();

				if (connector.type == 1) {
					myintent.setClass(this, PasswdActivity.class);
				} else if (connector.type == 0) {
					myintent.setClass(this, GEPasswdActivity.class);
				}

				this.startActivity(myintent);
				this.finish();
			}

		} else if (v == this.btnScan) {

			Intent myintent = new Intent();
			myintent.setClass(this, ScanActivity.class);
			this.startActivity(myintent);
			this.finish();

		} else if (v == this.btnModify) {
			if (connector == null || connector.addrStr.equals("")) {
				new AlertDialog.Builder(this).setTitle("error")
						.setMessage("please connect the meter")
						.setPositiveButton("return", null).show();
			} else {
				Bundle data = new Bundle();
				data.putString("type", "passwdIdentify");
				Message msg = new Message();
				msg.setData(data);
				handler.sendMessage(msg);

			}
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		if (which == dialog.BUTTON_POSITIVE) {
			if (meter.connector.type == 0) {

				if (MenuActivity.this.et_passwd.getText().toString().trim()
						.length() != 10) {
					CommonFunction.showDialog("Warning!",
							"Password's length must be 10!", MenuActivity.this);
				} else {
					PasswdIdentifyThread thread = new PasswdIdentifyThread();
					thread.start();
				}

			} else if (meter.connector.type == 1) {
				int passwd = 0;
				try {
					passwd = Integer.parseInt(et_passwd.getText().toString()
							.trim());
				} catch (Exception exception) {
					CommonFunction.showDialog("Fail!",
							"Password must be digitals", MenuActivity.this);
					return;
				}
				if (passwd > 9999) {
					CommonFunction
							.showDialog(
									"Fail!",
									"Password must be digitals and not larger than 9999!",
									MenuActivity.this);
				} else {
					PasswdIdentifyThread thread = new PasswdIdentifyThread();
					thread.start();
				}
			}

		} else if (which == dialog.BUTTON_NEGATIVE) {

		}

		dialog.cancel();
	}

	class PasswdIdentifyThread extends Thread {

		@Override
		public void run() {
			Bundle data = new Bundle();
			boolean suc = false;
			connector.connectNonjamod(connector.addrStr, connector.port,
					connector.unitID, connector.type);

			if (meter.connector.type == 0) {
				suc = ((GE) meter).validatePasswd(et_passwd.getText()
						.toString().trim());
			} else if (meter.connector.type == 1) {
				suc = ((Sentron) meter)
						.changeSentronPasswdState(
								Short.parseShort(et_passwd.getText().toString()
										.trim()), (short) 0);
			}
			connector.disconnect();

			if (suc) {
				data.putString("type", "passwdValidate");
			} else {
				data.putString("type", "passwdInvalidate");
			}

			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);

		}
	}
}
