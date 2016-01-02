package com.example.asg.view;

import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.Sentron;
import com.example.asg.util.CommonFunction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ToggleButton;


public class PasswdActivity extends FatherActivity implements OnClickListener {
	public EditText et_oldpasswd = null;
	public EditText et_newpasswd = null;
	public EditText et_repeatpasswd = null;// current password
	public ToggleButton cb_oldpasswdState = null;
	public ToggleButton cb_newpasswdState = null;
	public Button btn_modify = null;
	public Button btn_crack = null;
	public Button btn_cancel = null;
	public ProgressBar pb = null;
	// these are the current passwd and state
	public int passwd = 0;
	public int passwdState = 0;// 0 for nonProtection
	Handler handler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passwd);
		connector = ((ASGApplication) getApplication()).getConnector();
		meter = ((ASGApplication) getApplication()).getMeter();
		et_oldpasswd = (EditText) this.findViewById(R.id.passwd_etOldPasswd);
		et_oldpasswd.setEnabled(false);
		et_newpasswd = (EditText) this.findViewById(R.id.passwd_etNewPasswd);
		et_repeatpasswd = (EditText) this.findViewById(R.id.passwd_etCurPasswd);
		cb_oldpasswdState = (ToggleButton) this
				.findViewById(R.id.passwd_tbOldState);
		cb_oldpasswdState.setEnabled(false);
		cb_newpasswdState = (ToggleButton) this
				.findViewById(R.id.passwd_tbNewState);
		btn_crack = (Button) this.findViewById(R.id.passwd_btnRead);
		btn_modify = (Button) this.findViewById(R.id.passwd_btnModify);
		btn_cancel = (Button) this.findViewById(R.id.passwd_btnCanel);
		btn_modify.setEnabled(false);
		pb = (ProgressBar) this.findViewById(R.id.sentronpasswd_pb);
		pb.setVisibility(View.INVISIBLE);
		btn_modify.setOnClickListener(this);
		btn_crack.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);

		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle = msg.getData();

				if (bundle.getString("type").equals("crackPressed")) {

					btn_crack.setEnabled(false);
					btn_modify.setEnabled(false);
					pb.setVisibility(View.VISIBLE);

					CrackThread crkThread = new CrackThread();
					crkThread.start();

				} else if (bundle.getString("type").equals("crackSuc")) {

					btn_crack.setEnabled(true);
					btn_modify.setEnabled(true);
					pb.setVisibility(View.INVISIBLE);

					meter.passwd = Sentron.changeIntToPasswdStr(bundle
							.getInt("passwd"));
					passwd = bundle.getInt("passwd");
					passwdState = bundle.getInt("passwdState");

					et_oldpasswd.setText(meter.passwd + "");
					cb_oldpasswdState.setChecked(passwdState == 1 ? true
							: false);

					CommonFunction.showDialog("success!", "Crack success!",
							PasswdActivity.this);

				} else if (bundle.getString("type").equals("crackFail")) {

					btn_crack.setEnabled(true);
					btn_modify.setEnabled(false);
					pb.setVisibility(View.INVISIBLE);

					CommonFunction.showDialog("Fail", "Crack fail!",
							PasswdActivity.this);

				} else if (bundle.getString("type").equals("modifyPressed")) {
					// password must be number and not larger than 9999
					try {
						if (Integer.parseInt(et_newpasswd.getText().toString()
								.trim()) > 9999
								|| Integer.parseInt(et_repeatpasswd.getText()
										.toString().trim()) > 9999) {
							CommonFunction
									.showDialog(
											"Fail!",
											"Password must be nonempty, should be digitals and not larger than 9999!",
											PasswdActivity.this);
						} else {

							btn_crack.setEnabled(false);
							btn_modify.setEnabled(false);
							pb.setVisibility(View.VISIBLE);

							PasswdChangeThread thread = new PasswdChangeThread(
									et_newpasswd.getText().toString().trim(),
									cb_newpasswdState.isChecked());
							thread.start();
						}
					} catch (Exception ex) {
						CommonFunction
								.showDialog(
										"Fail!",
										"Password must be digitals and not larger than 9999!",
										PasswdActivity.this);
					}

				} else if (bundle.getString("type").equals("modifySuc")) {

					btn_crack.setEnabled(true);
					btn_modify.setEnabled(true);
					pb.setVisibility(View.INVISIBLE);

					CommonFunction.showDialog("success!",
							"Modification success!", PasswdActivity.this);

				} else if (bundle.getString("type").equals("modifyFail")) {

					btn_crack.setEnabled(true);
					btn_modify.setEnabled(true);
					pb.setVisibility(View.INVISIBLE);

					CommonFunction.showDialog("Fail", "Modification fail!",
							PasswdActivity.this);
				}

			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_passwd, menu);
		return true;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btn_cancel) {
			Intent myintent = new Intent();
			myintent.setClass(this, MenuActivity.class);
			this.startActivity(myintent);
			this.finish();
		} else if (v == btn_crack) {

			Bundle data = new Bundle();
			data.putString("type", "crackPressed");
			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);

		} else if (v == btn_modify) {

			Bundle data = new Bundle();
			data.putString("type", "modifyPressed");
			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);

		}

	}

	class PasswdChangeThread extends Thread {
		String pwd = "";
		boolean pwdState = false;

		public PasswdChangeThread(String pwd, boolean pwdState) {
			this.pwd = pwd;
			this.pwdState = pwdState;

		}

		@Override
		public void run() {
			// GE
			if (connector.type == 0) {

				// sentron
			} else if (connector.type == 1) {

				// change passwd
				boolean success = true;
				int desPasswd = Integer.parseInt(pwd);
				connector.connectNonjamod(connector.addrStr, connector.port,
						connector.unitID, connector.type);

				short tempPasswd = (short) Integer.parseInt(et_repeatpasswd
						.getText().toString().trim());
				Log.i("PasswdChangeThread.run", "tempPasswd=" + tempPasswd);
				if (((Sentron) ((ASGApplication) getApplication()).getMeter())
						.changeSentronPasswd(tempPasswd, (short) desPasswd,
								(short) passwdState) == -1) {
					success = false;
				}

				// change state
				int desState = (pwdState ? 1 : 0);

				if (success && (passwdState != desState)) {
					if (!((Sentron) ((ASGApplication) getApplication())
							.getMeter()).changeSentronPasswdState(
							(short) desPasswd, (short) desState)) {
						success = false;
					} else {

					}
				}

				Bundle data = new Bundle();

				if (success) {
					passwd = desPasswd;
					meter.passwd = Sentron.changeIntToPasswdStr(desPasswd);
					passwdState = desState;
					data.putString("type", "modifySuc");

				} else {

					data.putString("type", "modifyFail");
				}

				connector.disconnect();

				Message msg = new Message();
				msg.setData(data);
				handler.sendMessage(msg);

			}
		}
	}

	class CrackThread extends Thread {
		@Override
		public void run() {
			// if(connector.type == 1){//This is a comment of org_code
			{
				connector.connectNonjamod(connector.addrStr, connector.port,
						connector.unitID, connector.type);
				int[] passwdAndState = ((Sentron) ((ASGApplication) getApplication())
						.getMeter()).getSentronPasswdAndState();
				connector.disconnect();

				Bundle data = new Bundle();

				if (passwdAndState == null) {

					data.putString("type", "crackFail");
				} else {

					data.putString("type", "crackSuc");
					data.putInt("passwd", passwdAndState[0]);
					data.putInt("passwdState", passwdAndState[1]);

				}

				Message msg = new Message();
				msg.setData(data);
				handler.sendMessage(msg);

			}

		}
	}

}
