package com.example.asg.view;

import java.io.BufferedInputStream;

import java.io.FilterInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.Connector;
import com.example.asg.util.CommonFunction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ScanActivity extends FatherActivity  implements OnClickListener{	
	Button btnScan = null;
	Button btnStop = null;
	Button btnBack = null;
	Button btnInput=null;
	EditText edtTxtStartIP = null;
	EditText edtTxtEndIP = null;
	LinearLayout linearLayout_ipGroup = null;	
	ProgressBar pb = null;
	Handler handler;
	boolean isScaning = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);     
        connector = ((ASGApplication)getApplication()).getConnector();
		btnScan = (Button) this.findViewById(R.id.scan_btnScan);
		btnStop = (Button) this.findViewById(R.id.scan_btnStop);
		btnBack = (Button) this.findViewById(R.id.scan_btnCanel);
		btnInput = (Button) this.findViewById(R.id.scan_btninputIP);
		btnStop.setEnabled(false);
		edtTxtStartIP = (EditText) this.findViewById(R.id.scan_etStartIP);
		edtTxtEndIP = (EditText) this.findViewById(R.id.scan_etEndIP);
		linearLayout_ipGroup = (LinearLayout)this.findViewById(R.id.linearLayout_ipGroup);	
		pb = (ProgressBar)this.findViewById(R.id.scan_pb);
		pb.setVisibility(View.INVISIBLE);	
		btnScan.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnInput.setOnClickListener(this);
		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				//to stop or continue scanning
				if(data.getString("type") == null){
					return;
				}
				if(data.getString("type").equals("control")){
					if(msg.getData().getBoolean("toScan")){
						Log.i("ScanActivity.handleMessage", "start scan");
						startScanning(data.getString("ipHead"),data.getInt("forthPartStartIP"),data.getInt("forthPartEndIP"));
					}else{
						Log.i("ScanActivity.handleMessage", "stop scan");
						stopScanning();
					}
				//inform that a meter is scanned
				}else if(data.getString("type").equals("scanSuc")){
					Log.i("ScanActivity.handleMessage", "add one button");
					ScanActivity.this.addIPButton(data.getString("meterData"));
				}
				
			}
		};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_scan, menu);
        return true;
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.btnScan){
			
			String startIP = this.edtTxtStartIP.getText().toString().trim();
			String endIP = this.edtTxtEndIP.getText().toString().trim();
			
			if(checkIP(startIP) && checkIP(endIP)){
				
				String[] startIPParts = startIP.split("\\.");
				String[] endIPParts = endIP.split("\\.");
				
				if(startIPParts[0].equals(endIPParts[0]) && startIPParts[1].equals(endIPParts[1])){
					int thirdPartStartIP = Integer.parseInt(startIPParts[2]);
					int thirdPartEndIP = Integer.parseInt(endIPParts[2]);
					
					for(int thirdIP = thirdPartStartIP;thirdIP <= thirdPartEndIP;thirdIP++){
						
						int forthPartStartIP = Integer.parseInt(startIPParts[3]);
						int forthPartEndIP = Integer.parseInt(endIPParts[3]);
						String ipHead = startIPParts[0] + "." + startIPParts[1] + "." + thirdIP + ".";
					
						Bundle data = new Bundle();
						Message msg = new Message();
						data.putString("type", "control");
						data.putBoolean("toScan", true);
						data.putInt("forthPartStartIP", forthPartStartIP);
						data.putInt("forthPartEndIP", forthPartEndIP);
						data.putString("ipHead", ipHead);
						msg.setData(data);
						handler.sendMessage(msg);
					}
					
					
				}else{
					CommonFunction.showDialog("Fail!", "The start ip and end ip should have the same 2 parts!goooood....", this);
				}
			}else{
				CommonFunction.showDialog("Fail!", "The ip is illegal", this);
			}
		}else if(v == btnStop){
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putString("type", "control");
			data.putBoolean("toScan", false);
			msg.setData(data);
			handler.sendMessage(msg);
			
		}else if(v == btnBack){
			
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putString("type", "control");
			data.putBoolean("toScan", false);
			msg.setData(data);
			handler.sendMessage(msg);
			
			Intent myintent = new Intent();
			myintent.setClass(this, MenuActivity.class);
			this.startActivity(myintent);
			this.finish();
			
		}else if(v == btnInput){
			
			Intent myintent = new Intent();
			myintent.setClass(this, MainActivity.class);
			this.startActivity(myintent);
			this.finish();
			
		}else{
			//the ip buttons
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putString("type", "control");
			data.putBoolean("toScan", false);
			msg.setData(data);
			handler.sendMessage(msg);
			
			Intent myintent = new Intent();
			Bundle mybundle = new Bundle();
			mybundle.putString("meterData", ((Button)v).getText().toString());
			
			myintent.putExtras(mybundle);
			myintent.setClass(this, MainActivity.class);
			this.startActivity(myintent);
			this.finish();	
		}		
	}
	
	public void stopScanning(){
		this.btnScan.setEnabled(true);
		this.btnStop.setEnabled(false);
		pb.setVisibility(View.INVISIBLE);
		if(this.isScaning == true){
			this.isScaning = false;
		}
	}
	
	public void startScanning(String ipHead, int forthPartStartIP, int forthPartEndIP){
		
		pb.setVisibility(View.VISIBLE);
		linearLayout_ipGroup.removeAllViews();
		this.btnScan.setEnabled(false);
		this.btnStop.setEnabled(true);
		if(this.isScaning == false){
			this.isScaning = true;
		}
		ThreadManager manager = new ThreadManager(30, ipHead,forthPartStartIP,forthPartEndIP,1, 0xff, 502,Connector.conWaitTime);
		manager.start();
	}
	
	public void addIPButton(String text){
		Button button = new Button(this);
		button.setText(text);
		button.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		button.setOnClickListener(this);
		this.linearLayout_ipGroup.addView(button);
		this.linearLayout_ipGroup.invalidate();
	}
	
	/**
	 * check the ip str if it is legal
	 * @param ip
	 * @return
	 */
	public boolean checkIP(String ip){
		Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher m = p.matcher(ip);
		return m.matches();
	}
	
	class ScanThread extends Thread{
		public int scanPort = 0;
		public String scanIP = "";
		public int minUnitID = 0;
		public int maxUnitID = 0;
		public int timeOutMs = 0;
		
		ThreadManager manager = null;
		
		/**
		 * 
		 * @param scanIP:String
		 * @param scanPort:int
		 * @param manager:ThreadManager
		 */
		public ScanThread(String scanIP,int scanPort,int minUnitID,int maxUnitID,int timeOutMs, ThreadManager manager){
			this.scanIP = scanIP;
			this.scanPort = scanPort;
			this.minUnitID = minUnitID;
			this.maxUnitID = maxUnitID;
			this.timeOutMs = timeOutMs;
			this.manager = manager;
			
		}
		
		@Override
		public void run(){
			try{	
				manager.total++;
				OutputStream os = null;
				FilterInputStream is = null;
				int responseLength = 0;
				byte ibuf[] = new byte[261];
				boolean getType = false;
				String meterName = "";
				
			 	SocketAddress sockaddr = new InetSocketAddress(scanIP,scanPort);
				Socket scans=new Socket();
				scans.connect(sockaddr, timeOutMs);
				scans.setSoTimeout(timeOutMs);
				
				os= scans.getOutputStream();
				is = new BufferedInputStream(scans.getInputStream());
				
				for(int uID=minUnitID;uID<maxUnitID;uID++){				
					try{
						if(!isScaning){
							break;
						}
						if(scans.isClosed()){
							System.out.println("接收超时或者长时间没有再沟通会断开");
							try{
								scans.connect(sockaddr, timeOutMs);
							}catch(Exception ex){
								Log.i("ScanActivity.ScanThread.run", "重连失败");
							}
						}
						//check the type						
						//check if GE
						try{
							if(getType == false){
								byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x01,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08};
								b[6] = (byte)uID;
								os.write(b, 0, b.length);
								os.flush();
		
								responseLength = is.read(ibuf, 0, 261);
								meterName = CommonFunction.getASCStr(ibuf,responseLength);
								
								if(meterName.contains("Nexus 1272")){
									getType = true;
									Log.i("ScanActivity.ScanThread.run", "1272 is found");
									//this.manager.resultArray.add(scanIP+";  unitID:"+uID+";"+"  meter type:GE Nexus 1272");
									Bundle data = new Bundle();
									Message msg = new Message();
									data.putString("type", "scanSuc");
									
									data.putString("meterData", scanIP+";unitID:"+uID+";"+"meterType:Nexus 1272\n"+
									"Risk:Data privacy;Password Authentication Bug;DoS;Parameter Tampering"	);
	
										
									msg.setData(data);
									handler.sendMessage(msg);
									
									break;
								}
	
							}
						}catch(Exception geException){
							Log.i("ScanActivity.ScanThread.run", "GE Exception");
						}
	 
						try{
							if(getType == false){
								byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
								b[6] = (byte)uID;
								os.write(b, 0, b.length);
								os.flush();
			
								
								responseLength = is.read(ibuf, 0, 261);
								meterName = CommonFunction.getASCStr(ibuf,responseLength);
								if(meterName.contains("SIEMENS") && meterName.contains("PAC4200")){
							
									getType = true;
									Bundle data = new Bundle();
									Message msg = new Message();
									data.putString("type", "scanSuc");
									data.putString("meterData",scanIP+";unitID:"+uID+";"+"meterType:\nPAC4200 ;"+
										"Risk:Data privacy;Weak Password;DoS;Parameter Tampering");
									msg.setData(data);
									handler.sendMessage(msg);
									break;
								}	
								
							}
						}catch(Exception sentronException){
							Log.i("ScanActivity.ScanThread.run", "sentron Exception");
						}
						
						//check if schneider 
						try{
							if(getType == false){
								byte[] b = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,
										(byte)0x01,(byte)0x03,(byte)0x0b,(byte)0xb7,(byte)0x00,(byte)0x02};
								b[6] = (byte)uID;
								os.write(b, 0, b.length);
								os.flush();
			
								
								responseLength = is.read(ibuf, 0, 261);
								meterName = CommonFunction.getASCStr(ibuf,responseLength);
								if(meterName.contains("PM8")){
							
									getType = true;
									Bundle data = new Bundle();
									Message msg = new Message();
									data.putString("type", "scanSuc");
									data.putString("meterData",scanIP+";unitID:"+uID+";"+"meterType:PM8\n"+
										"Risk:Data privacy;Weak Password;DoS;Parameter Tampering");
									msg.setData(data);
									handler.sendMessage(msg);
									break;
								}	
								
							}
						}catch(Exception sentronException){
							Log.i("ScanActivity.ScanThread.run", "schneider Exception");
						}
					
					}catch(Exception ex){
						Log.i("ScanActivity.ScanThread.run","inner Exception "+ex.getMessage() );
						getType = false;

					}
				}//对应for的}
				
				
				os.close();
				is.close();
				scans.close();
				manager.total--;
				this.manager.currentNum--;
			}catch(Exception ex){
				Log.i("ScanActivity.ScanThread.run","outer Exception "+ ex.getMessage() );
				manager.total--;
				this.manager.currentNum--;
			}
		}
		
		
	}

	class ThreadManager extends Thread{
		public int maxThreadNum = 0;
		public int currentNum = 0;
		String ipHead = "";
		int ipMin = 0;
		int ipMax = 0;
		int uIDMin = 0;
		int uIDMax = 0;
		int port = 0;
		int curIP = 0;
		int timeOutMs = 0;
		//HashSet<String> hash = new HashSet<String>();
		ArrayList<String> resultArray = new ArrayList<String>();
		
		/**the total num of thread running
		 */
		int total = 0;
		
		public ThreadManager(int maxThreadNum, String ipHead,int ipMin,int ipMax,int uIDMin,int uIDMax, int port, int timeOutMs){
			this.maxThreadNum = maxThreadNum;
			this.ipHead = ipHead;
			this.ipMax = ipMax;
			this.ipMin = ipMin;
			this.uIDMax = uIDMax;
			this.uIDMin = uIDMin;
			this.port = port;
			this.curIP = this.ipMin;
			this.timeOutMs = timeOutMs;
		}
		
		@Override
		public void run(){
			while(isScaning){
				if(curIP >= ipMax){
					break;
				}
				if(this.currentNum < this.maxThreadNum){
					this.currentNum++;
					new ScanThread(ipHead+curIP++, this.port, this.uIDMin, this.uIDMax, this.timeOutMs, this).start();
				}
				try {
					sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				while(total != 0 && isScaning){
					sleep(10);
				}			
			
				Bundle data1 = new Bundle();
				Message msg1 = new Message();
				data1.putString("type", "control");
				data1.putBoolean("toScan", false);
				Log.i("ScanActivity.ThreadManager.run", "end thread Manager");
				msg1.setData(data1);
				handler.sendMessage(msg1);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
		
	

}
