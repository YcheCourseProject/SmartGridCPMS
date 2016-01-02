package com.example.asg.view;


import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.GE;
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

public class GEPasswdActivity extends FatherActivity implements OnClickListener {

	
	public EditText et_passwd = null;
	public Button btn_stop = null;
	public Button btn_crack = null;
	public Button btn_cancel = null;
	public ProgressBar pb = null;
	public ToggleButton tb_passwdState = null;
	
	String passwd = "";
	//these is the current passwd and state
	boolean isMonitor = false;
	Handler handler = null; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gepasswd);
        
        connector = ((ASGApplication)getApplication()).getConnector();
        meter = ((ASGApplication)getApplication()).getMeter();
        
        et_passwd = (EditText) this.findViewById(R.id.gepasswd_EdtTxtPasswd);
        tb_passwdState = (ToggleButton) this.findViewById(R.id.gepasswd_passwd_state);
        btn_crack = (Button) this.findViewById(R.id.gepasswd_btnCrack);
		btn_stop = (Button) this.findViewById(R.id.gepasswd_btnStop);
		btn_cancel = (Button) this.findViewById(R.id.gepasswd_btnCanel);
		
		btn_stop.setEnabled(false);
		
		btn_stop.setOnClickListener(this);
		btn_crack.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		
		pb = (ProgressBar)this.findViewById(R.id.gepasswd_pb);
		pb.setVisibility(View.INVISIBLE);
		
		//initialize the password state ToggleButton
		new PasswdStateThread().start();

		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle = msg.getData();
				if(bundle.getString("type").equals("crackSuc")){
					
					isMonitor = false;
					meter.passwd = passwd;
					
					pb.setVisibility(View.INVISIBLE);
					btn_stop.setEnabled(false);
					btn_crack.setEnabled(true);
					
					CommonFunction.showDialog("success!","Crack success!",GEPasswdActivity.this);
					
				}else if(bundle.getString("type").equals("crackPressed")){
					
					//密码字数一定要为10
					if( et_passwd.getText().toString().trim().length() != 10){
						CommonFunction.showDialog("Warning!","Password's length must be 10!",GEPasswdActivity.this);
					}else{
						
						isMonitor = true;
						
						btn_stop.setEnabled(true);
						btn_crack.setEnabled(false);
						pb.setVisibility(View.VISIBLE);
						
						CrackThread crkThread = new CrackThread();
						passwd = et_passwd.getText().toString().trim();
						crkThread.start();
					}
				
				}else if(bundle.getString("type").equals("stopPressed")){
					
					btn_stop.setEnabled(false);
					btn_crack.setEnabled(true);
					isMonitor = false;
					pb.setVisibility(View.INVISIBLE);
				}
				
			
			}
		};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gepasswd, menu);
        return true;
    }

    public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btn_cancel){
			isMonitor = false;
			
			Intent myintent=new Intent();
   	      	myintent.setClass(this,MenuActivity.class);
   	      	this.startActivity(myintent);
   	      	this.finish();
   	      	
		}else if(v == btn_crack){
			
			Bundle data = new Bundle();
			data.putString("type", "crackPressed");
			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);
			
		}else if(v == btn_stop){
			
			Bundle data = new Bundle();
			data.putString("type", "stopPressed");
			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);

		}
		
	}
    
    class CrackThread extends Thread {
		@Override
        public void run() {
			
			connector.connectNonjamod(connector.addrStr, connector.port, connector.unitID, connector.type);
			while(isMonitor){
				if(((GE)meter).monitorAndChangePasswd(passwd)){
					GEPasswdActivity.this.passwd = passwd;
					
					Bundle data = new Bundle();
					data.putString("type", "crackSuc");
					Message msg = new Message();
					msg.setData(data);
					handler.sendMessage(msg);
					
					Log.i("changePass", "suc");
					break;
				}
				try {
					Log.i("changePass", "fail");
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			connector.disconnect();
		}
	}
    
    class PasswdStateThread extends Thread {
 		@Override
         public void run() {
 			
 			connector.connectNonjamod(connector.addrStr, connector.port, connector.unitID, connector.type);
 			boolean protection = ((GE)meter).getProtectState();
 			GEPasswdActivity.this.tb_passwdState.setChecked(protection);
 			connector.disconnect();
 		}
 	}
    
}
