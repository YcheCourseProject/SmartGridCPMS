package com.example.asg.view;


import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.GEConnector;
import com.example.asg.services.meteraccess.Scheider;
import com.example.asg.services.meteraccess.Sentron;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

public class ConnectingActivity extends FatherActivity {

	int devicetype;
	boolean mutex = false;
	//Button btnPause;
	//Button btnContinue;
	Button btnLoad;
	Button btnBack;
	TextView textViewL1_N;
	TextView textViewL2_N;
	TextView textViewL3_N;
	TextView textViewo_N;
	TextView textViewL1_L2;
	TextView textViewL2_L3;
	TextView textViewL3_L1;
	TextView textViewo_V;
	TextView textViewL1;
	TextView textViewL2;
	TextView textViewL3;
	TextView textViewN;
	TextView textViewL1_A;
	TextView textViewL2_A;
	TextView textViewL3_A;
	TextView textViewsegma;
	TextView textViewL1_RA;
	TextView textViewL2_RA;
	TextView textViewL3_RA;
	TextView textViewsegmaRA;
	TextView textViewL1_AO;
	TextView textViewL2_AO;
	TextView textViewL3_AO;
	TextView textViewsegmaAO;
	TextView textViewI_f1;
	TextView textViewI_f2;
	TextView textViewE_f1;
	TextView textViewE_f2;
	TextView textViewCTN;
	TextView textViewCTD;
	TextView textViewPTN;
	TextView textViewPTD;
	TextView title0;
	TextView title1;
	TextView title2;
	TextView title3;
	TextView title4;
	TextView title5;
	TextView title6;
	TextView title7;
	ImageButton pushbutton0;
	ImageButton pushbutton1;
	ImageButton pushbutton2;
	ImageButton pushbutton3;
	ImageButton pushbutton4;
	ImageButton pushbutton5;
	ImageButton pushbutton6;
	ImageButton pushbutton7;
	
	boolean b1 = false, b2 = false, b3 = false, b4 = false, b5 = false, b6 = false, b7 = false;

	public int CTN, CTD;
	public int PTN;
	public int PTD;
	TableLayout tablelayout0;
	TableLayout tablelayout1;
	TableLayout tablelayout2;
	TableLayout tablelayout3;
	TableLayout tablelayout4;
	TableLayout tablelayout5;
	TableLayout tablelayout6;
	TableLayout tablelayout7;
	
	Get_Data get_data;
	Handler mHandler;
	// Bundle mybundle;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecting);

		connector = ((ASGApplication) getApplication()).getConnector();
		meter = ((ASGApplication) getApplication()).getMeter();

		Log.i("tips", "onCreate");

		/*btnPause = (Button) this.findViewById(R.id.buttonpause);
		btnContinue = (Button) this.findViewById(R.id.buttoncontinue);
		btnContinue.setEnabled(false);*/
		btnLoad = (Button) this.findViewById(R.id.buttonLoad);
		btnBack = (Button) this.findViewById(R.id.buttonback);
		
		if(this.connector.type!=0&&this.connector.type!=1)
		{
			btnLoad.setEnabled(false);
		}
		StopConnectListener cl = new StopConnectListener();
		/*btnPause.setOnClickListener(cl);
		btnContinue.setOnClickListener(cl);*/
		btnLoad.setOnClickListener(cl);
		btnBack.setOnClickListener(cl);
		

		textViewL1_N = (TextView) this.findViewById(R.id.textViewL1_N);
		textViewL2_N = (TextView) this.findViewById(R.id.textViewL2_N);
		textViewL3_N = (TextView) this.findViewById(R.id.textViewL3_N);
		textViewo_N = (TextView) this.findViewById(R.id.textViewo_N);
		textViewL1_L2 = (TextView) this.findViewById(R.id.textViewL1_L2);
		textViewL2_L3 = (TextView) this.findViewById(R.id.textViewL2_L3);
		textViewL3_L1 = (TextView) this.findViewById(R.id.textViewL3_L1);
		textViewo_V = (TextView) this.findViewById(R.id.textViewo_V);
		textViewL1 = (TextView) this.findViewById(R.id.textViewL1);
		textViewL2 = (TextView) this.findViewById(R.id.textViewL2);
		textViewL3 = (TextView) this.findViewById(R.id.textViewL3);
		textViewN = (TextView) this.findViewById(R.id.textViewN);
		textViewL1_A = (TextView) this.findViewById(R.id.textViewL1_A);
		textViewL2_A = (TextView) this.findViewById(R.id.textViewL2_A);
		textViewL3_A = (TextView) this.findViewById(R.id.textViewL3_A);
		textViewsegma = (TextView) this.findViewById(R.id.textViewsegma);
		textViewL1_RA = (TextView) this.findViewById(R.id.textViewL1_RA);
		textViewL2_RA = (TextView) this.findViewById(R.id.textViewL2_RA);
		textViewL3_RA = (TextView) this.findViewById(R.id.textViewL3_RA);
		textViewsegmaRA = (TextView) this.findViewById(R.id.textViewsegmaRA);
		textViewL1_AO = (TextView) this.findViewById(R.id.textViewL1_AO);
		textViewL2_AO = (TextView) this.findViewById(R.id.textViewL2_AO);
		textViewL3_AO = (TextView) this.findViewById(R.id.textViewL3_AO);
		textViewsegmaAO = (TextView) this.findViewById(R.id.textViewsegmaAO);
		textViewI_f1 = (TextView) this.findViewById(R.id.textViewI_f1);
		textViewI_f2 = (TextView) this.findViewById(R.id.textViewI_f2);
		textViewE_f1 = (TextView) this.findViewById(R.id.textViewE_f1);
		textViewE_f2 = (TextView) this.findViewById(R.id.textViewE_f2);
		textViewCTN = (TextView) this.findViewById(R.id.textViewCTN);
		textViewCTD = (TextView) this.findViewById(R.id.textViewCTD);
		textViewPTN = (TextView) this.findViewById(R.id.textViewPTN);
		textViewPTD = (TextView) this.findViewById(R.id.textViewPTD);

		tablelayout0 = (TableLayout) this.findViewById(R.id.tablelayoutA0);
		tablelayout1 = (TableLayout) this.findViewById(R.id.tablelayoutA1);
		tablelayout2 = (TableLayout) this.findViewById(R.id.tablelayoutA2);
		tablelayout3 = (TableLayout) this.findViewById(R.id.tablelayoutA3);
		tablelayout4 = (TableLayout) this.findViewById(R.id.tablelayoutA4);
		tablelayout5 = (TableLayout) this.findViewById(R.id.tablelayoutA5);
		tablelayout6 = (TableLayout) this.findViewById(R.id.tablelayoutA6);
		tablelayout7 = (TableLayout) this.findViewById(R.id.tablelayoutA7);

		tablelayout0.setVisibility(View.GONE);
		tablelayout1.setVisibility(View.GONE);
		tablelayout2.setVisibility(View.GONE);
		tablelayout3.setVisibility(View.GONE);
		tablelayout4.setVisibility(View.GONE);
		tablelayout5.setVisibility(View.GONE);
		tablelayout6.setVisibility(View.GONE);
		tablelayout7.setVisibility(View.GONE);

		title0 = (TextView) this.findViewById(R.id.textViewTitle0);
		title1 = (TextView) this.findViewById(R.id.textViewTitle1);
		title2 = (TextView) this.findViewById(R.id.textViewTitle2);
		title3 = (TextView) this.findViewById(R.id.textViewTitle3);
		title4 = (TextView) this.findViewById(R.id.textViewTitle4);
		title5 = (TextView) this.findViewById(R.id.textViewTitle5);
		title6 = (TextView) this.findViewById(R.id.textViewTitle6);
		title7 = (TextView) this.findViewById(R.id.textViewTitle7);

		
		pushbutton0 = (ImageButton) this.findViewById(R.id.imageButtonPush0);
		pushbutton1 = (ImageButton) this.findViewById(R.id.imageButtonPush1);
		pushbutton2 = (ImageButton) this.findViewById(R.id.imageButtonPush2);
		pushbutton3 = (ImageButton) this.findViewById(R.id.imageButtonPush3);
		pushbutton4 = (ImageButton) this.findViewById(R.id.imageButtonPush4);
		pushbutton5 = (ImageButton) this.findViewById(R.id.imageButtonPush5);
		pushbutton6 = (ImageButton) this.findViewById(R.id.imageButtonPush6);
		pushbutton7 = (ImageButton) this.findViewById(R.id.imageButtonPush7);

		title0.setOnClickListener(cl);
		pushbutton0.setImageResource(R.drawable.down);
		title1.setOnClickListener(cl);
		pushbutton1.setImageResource(R.drawable.down);
		title2.setOnClickListener(cl);
		pushbutton2.setImageResource(R.drawable.down);
		title3.setOnClickListener(cl);
		pushbutton3.setImageResource(R.drawable.down);
		title4.setOnClickListener(cl);
		pushbutton4.setImageResource(R.drawable.down);
		title5.setOnClickListener(cl);
		pushbutton5.setImageResource(R.drawable.down);
		title6.setOnClickListener(cl);
		pushbutton6.setImageResource(R.drawable.down);
		title7.setOnClickListener(cl);
		pushbutton7.setImageResource(R.drawable.down);
		

		pushbutton0.setOnClickListener(cl);
		pushbutton1.setOnClickListener(cl);
		pushbutton2.setOnClickListener(cl);
		pushbutton3.setOnClickListener(cl);
		pushbutton4.setOnClickListener(cl);
		pushbutton5.setOnClickListener(cl);
		pushbutton6.setOnClickListener(cl);
		pushbutton7.setOnClickListener(cl);

		// 设置文本颜色(如透明色：Color.TRANSPARENT)
		// textViewL1_N.setTextColor(Color.MAGENTA);
		devicetype = connector.type;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				textViewL1_N.setText(msg.getData().getString("L1_N"));
				textViewL2_N.setText(msg.getData().getString("L2_N"));
				textViewL3_N.setText(msg.getData().getString("L3_N"));
				textViewo_N.setText(msg.getData().getString("o_N"));
				textViewL1_L2.setText(msg.getData().getString("L1_L2"));
				textViewL2_L3.setText(msg.getData().getString("L2_L3"));
				textViewL3_L1.setText(msg.getData().getString("L3_L1"));
				textViewo_V.setText(msg.getData().getString("o_V"));
				textViewL1.setText(msg.getData().getString("L1"));
				textViewL2.setText(msg.getData().getString("L2"));
				textViewL3.setText(msg.getData().getString("L3"));
				textViewN.setText(msg.getData().getString("N"));
				textViewL1_A.setText(msg.getData().getString("L1_A"));
				textViewL2_A.setText(msg.getData().getString("L2_A"));
				textViewL3_A.setText(msg.getData().getString("L3_A"));
				textViewsegma.setText(msg.getData().getString("segma"));
				textViewL1_RA.setText(msg.getData().getString("L1_RA"));
				textViewL2_RA.setText(msg.getData().getString("L2_RA"));
				textViewL3_RA.setText(msg.getData().getString("L3_RA"));
				textViewsegmaRA.setText(msg.getData().getString("segmaRA"));
				textViewL1_AO.setText(msg.getData().getString("L1_AO"));
				textViewL2_AO.setText(msg.getData().getString("L2_AO"));
				textViewL3_AO.setText(msg.getData().getString("L3_AO"));
				textViewsegmaAO.setText(msg.getData().getString("segmaAO"));
				textViewI_f1.setText(msg.getData().getString("I_f1"));
				textViewI_f2.setText(msg.getData().getString("I_f2"));
				textViewE_f1.setText(msg.getData().getString("E_f1"));
				textViewE_f2.setText(msg.getData().getString("E_f2"));

			}
		};

		get_data = new Get_Data();
		get_data.start();
		while (!mutex)
			;

		textViewCTN.setText(String.valueOf(CTN));
		textViewCTD.setText(String.valueOf(CTD));
		textViewPTN.setText(String.valueOf(PTN));
		textViewPTD.setText(String.valueOf(PTD));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class StopConnectListener implements OnClickListener {

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			/*if (arg0 == btnPause) {
				get_data.isRunning = false;
				btnContinue.setEnabled(true);
				btnPause.setEnabled(false);
			}
			if (arg0 == btnContinue) {
				get_data = new Get_Data();
				get_data.start();
				btnContinue.setEnabled(false);
				btnPause.setEnabled(true);
			}*/
			if (arg0 == btnBack) {
				get_data.isRunning = false;
				//让正在采集数据的线程准备结束
			/*	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				Intent myintent = new Intent();
				myintent.setClass(ConnectingActivity.this, MenuActivity.class);
				 startActivity(myintent);
				 finish();
			}
			if (arg0 == btnLoad) {
				/*try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				get_data.isRunning = false;
				Intent myintent = new Intent();
				myintent.setClass(ConnectingActivity.this, LoadCheckActivity.class);
				 startActivity(myintent);
				 finish();
			}
			
			if (arg0 == title0 || arg0 == pushbutton0)
				if (b1) {
					tablelayout0.setVisibility(View.GONE);
					pushbutton0.setImageResource(R.drawable.down);
					b1 = false;
				} else {
					tablelayout0.setVisibility(View.VISIBLE);
					pushbutton0.setImageResource(R.drawable.up);
					b1 = true;
				}
			if (arg0 == title1 || arg0 == pushbutton1)
				if (b1) {
					tablelayout1.setVisibility(View.GONE);
					pushbutton1.setImageResource(R.drawable.down);
					b1 = false;
				} else {
					tablelayout1.setVisibility(View.VISIBLE);
					pushbutton1.setImageResource(R.drawable.up);
					b1 = true;
				}
			if (arg0 == title2 || arg0 == pushbutton2)
				if (b2) {
					tablelayout2.setVisibility(View.GONE);
					pushbutton2.setImageResource(R.drawable.down);
					b2 = false;
				} else {
					tablelayout2.setVisibility(View.VISIBLE);
					pushbutton2.setImageResource(R.drawable.up);
					b2 = true;
				}
			if (arg0 == title3 || arg0 == pushbutton3)
				if (b3) {
					tablelayout3.setVisibility(View.GONE);
					pushbutton3.setImageResource(R.drawable.down);
					b3 = false;
				} else {
					tablelayout3.setVisibility(View.VISIBLE);
					pushbutton3.setImageResource(R.drawable.up);
					b3 = true;
				}
			if (arg0 == title4 || arg0 == pushbutton4)
				if (b4) {
					tablelayout4.setVisibility(View.GONE);
					pushbutton4.setImageResource(R.drawable.down);
					b4 = false;
				} else {
					tablelayout4.setVisibility(View.VISIBLE);
					pushbutton4.setImageResource(R.drawable.up);
					b4 = true;
				}
			if (arg0 == title5 || arg0 == pushbutton5)
				if (b5) {
					tablelayout5.setVisibility(View.GONE);
					pushbutton5.setImageResource(R.drawable.down);
					b5 = false;
				} else {
					tablelayout5.setVisibility(View.VISIBLE);
					pushbutton5.setImageResource(R.drawable.up);
					b5 = true;
				}
			if (arg0 == title6 || arg0 == pushbutton6)
				if (b6) {
					tablelayout6.setVisibility(View.GONE);
					pushbutton6.setImageResource(R.drawable.down);
					b6 = false;
				} else {
					tablelayout6.setVisibility(View.VISIBLE);
					pushbutton6.setImageResource(R.drawable.up);
					b6 = true;
				}
			if (arg0 == title7 || arg0 == pushbutton7)
				if (b7) {
					tablelayout7.setVisibility(View.GONE);
					pushbutton7.setImageResource(R.drawable.down);
					b7 = false;
				} else {
					tablelayout7.setVisibility(View.VISIBLE);
					pushbutton7.setImageResource(R.drawable.up);
					b7 = true;
				}

		}
	}

	class Get_Data extends Thread {

		boolean isRunning = true;

		int timer = 0;

		/**
		 * 线程体代码
		 */
		@Override
		public void run() {

			GetRatio();
			mutex = true;
			try{
				while (isRunning) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.i("tips", "run");

					Bundle data = new Bundle();
					
					

					if (devicetype == 1) {
						
						connector.connectJamod(connector.addrStr, connector.port,
								connector.unitID, connector.type);
						
						data.putString("L1_N", connector.getFloatData(1, 2));
						data.putString("L2_N", connector.getFloatData(3, 2));
						data.putString("L3_N", connector.getFloatData(5, 2));
						data.putString("o_N", connector.getFloatData(57, 2));
						data.putString("L1_L2", connector.getFloatData(7, 2));
						data.putString("L2_L3", connector.getFloatData(9, 2));
						data.putString("L3_L1", connector.getFloatData(11, 2));
						data.putString("o_V", connector.getFloatData(59, 2));
						data.putString("L1", connector.getFloatData(13, 2));
						data.putString("L2", connector.getFloatData(15, 2));
						data.putString("L3", connector.getFloatData(17, 2));
						data.putString("N", connector.getFloatData(295, 2));
						data.putString("L1_A", connector.getFloatData(25, 2));
						data.putString("L2_A", connector.getFloatData(27, 2));
						data.putString("L3_A", connector.getFloatData(29, 2));
						data.putString("segma", connector.getFloatData(65, 2));
						data.putString("L1_RA", connector.getFloatData(31, 2));
						data.putString("L2_RA", connector.getFloatData(33, 2));
						data.putString("L3_RA", connector.getFloatData(35, 2));
						data.putString("segmaRA", connector.getFloatData(67, 2));
						data.putString("L1_AO", connector.getFloatData(19, 2));
						data.putString("L2_AO", connector.getFloatData(21, 2));
						data.putString("L3_AO", connector.getFloatData(23, 2));
						data.putString("segmaAO", connector.getFloatData(63, 2));
						data.putString("I_f1", connector.getDoubleData(801, 4));
						data.putString("I_f2", connector.getDoubleData(805, 4));
						data.putString("E_f1", connector.getDoubleData(809, 4));
						data.putString("E_f2", connector.getDoubleData(813, 4));
						
						connector.disconnect();
						
					} else if (devicetype == 0) {
						
						connector.connectJamod(connector.addrStr, connector.port,
								connector.unitID, connector.type);
						
						data.putString("L1_N", ((GEConnector) (connector))
								.getF7Data(179, 2, 1,  PTN,  PTD,  CTN,  CTD));
						data.putString("L2_N", ((GEConnector) (connector))
								.getF7Data(181, 2, 1,  PTN,  PTD,  CTN,  CTD));
						data.putString("L3_N", ((GEConnector) (connector))
								.getF7Data(183, 2, 1,  PTN,  PTD,  CTN,  CTD));
						data.putString("o_N", ((GEConnector) (connector))
								.getF7Data(185, 2, 1,  PTN,  PTD,  CTN,  CTD));
						data.putString("L1_L2", ((GEConnector) (connector))
								.getF7Data(197, 2, 2,  PTN,  PTD,  CTN,  CTD));
						data.putString("L2_L3", ((GEConnector) (connector))
								.getF7Data(199, 2, 2,  PTN,  PTD,  CTN,  CTD));
						data.putString("L3_L1", ((GEConnector) (connector))
								.getF7Data(201, 2, 2,  PTN,  PTD,  CTN,  CTD));
						data.putString("o_V", "0.0");
						data.putString("L1", ((GEConnector) (connector)).getF7Data(
								187, 2, 3,  PTN,  PTD,  CTN,  CTD));
						data.putString("L2", ((GEConnector) (connector)).getF7Data(
								189, 2, 3,  PTN,  PTD,  CTN,  CTD));
						data.putString("L3", ((GEConnector) (connector)).getF7Data(
								191, 2, 3,  PTN,  PTD,  CTN,  CTD));
						data.putString("N", ((GEConnector) (connector)).getF7Data(
								193, 2, 3,  PTN,  PTD,  CTN,  CTD));
						data.putString("L1_A", ((GEConnector) (connector))
								.getF7Data(219, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L2_A", ((GEConnector) (connector))
								.getF7Data(221, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L3_A", ((GEConnector) (connector))
								.getF7Data(223, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("segma",  ((GEConnector) (connector))
								.getF7Data(225, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L1_RA", ((GEConnector) (connector))
								.getF7Data(211, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L2_RA", ((GEConnector) (connector))
								.getF7Data(213, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L3_RA", ((GEConnector) (connector))
								.getF7Data(215, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("segmaRA",  ((GEConnector) (connector))
								.getF7Data(217, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L1_AO", ((GEConnector) (connector))
								.getF7Data(203, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L2_AO", ((GEConnector) (connector))
								.getF7Data(205, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("L3_AO", ((GEConnector) (connector))
								.getF7Data(207, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("segmaAO",  ((GEConnector) (connector))
								.getF7Data(209, 2, 4,  PTN,  PTD,  CTN,  CTD));
						data.putString("I_f1", "0");
						data.putString("I_f2", "0");
						data.putString("E_f1", "0");
						data.putString("E_f2", "0");
						
						connector.disconnect();
						
					}else if(devicetype == 2){
						
						connector.connectNonjamod(connector.addrStr, connector.port,
								connector.unitID, connector.type);
						
						Log.i("ConnectingActivity.run()", "beforeReadRegs"+(connector == null));
						data.putString("L1_N", connector.getFloatForNonjamod(11719, 1)[0]+"");
						data.putString("L2_N", connector.getFloatForNonjamod(11721, 1)[0]+"");
						data.putString("L3_N", connector.getFloatForNonjamod(11723, 1)[0]+"");
						data.putString("o_N", connector.getFloatForNonjamod(11725, 1)[0]+"");
						data.putString("L1_L2", connector.getFloatForNonjamod(11711, 1)[0]+"");
						data.putString("L2_L3", connector.getFloatForNonjamod(11713, 1)[0]+"");
						data.putString("L3_L1", connector.getFloatForNonjamod(11715, 1)[0]+"");
						data.putString("o_V", connector.getFloatForNonjamod(11717, 1)[0]+"");
						data.putString("L1", connector.getFloatForNonjamod(11699, 1)[0]+"");
						data.putString("L2", connector.getFloatForNonjamod(11701, 1)[0]+"");
						data.putString("L3", connector.getFloatForNonjamod(11703, 1)[0]+"");
						data.putString("N", connector.getFloatForNonjamod(11705, 1)[0]+"");
						data.putString("L1_A", connector.getFloatForNonjamod(11729, 1)[0]+"");
						data.putString("L2_A", connector.getFloatForNonjamod(11731, 1)[0]+"");
						data.putString("L3_A", connector.getFloatForNonjamod(11733, 1)[0]+"");
						data.putString("segma", connector.getFloatForNonjamod(11735, 1)[0]+"");
						data.putString("L1_RA", connector.getFloatForNonjamod(11737, 1)[0]+"");
						data.putString("L2_RA", connector.getFloatForNonjamod(11739, 1)[0]+"");
						data.putString("L3_RA", connector.getFloatForNonjamod(11741, 1)[0]+"");
						data.putString("segmaRA", connector.getFloatForNonjamod(11743, 1)[0]+"");
						data.putString("L1_AO", connector.getFloatForNonjamod(11745, 1)[0]+"");
						data.putString("L2_AO", connector.getFloatForNonjamod(11747, 1)[0]+"");
						data.putString("L3_AO", connector.getFloatForNonjamod(11749, 1)[0]+"");
						data.putString("segmaAO", connector.getFloatForNonjamod(11751, 1)[0]+"");
						data.putString("I_f1", "——");
						data.putString("I_f2", "——");
						data.putString("E_f1", "——");
						data.putString("E_f2", "——");

						connector.disconnect();
					}

					

					Message msg = new Message();
					msg.setData(data);
					mHandler.sendMessage(msg);
				}
			}catch(Exception ex){
				
			}
			
		}

		void GetRatio() {
			// GE
			if (connector.type == 0) {
				int ls = 0;

				connector.connectJamod(connector.addrStr, connector.port,
						connector.unitID, connector.type);

				ls = Integer.parseInt(connector.getIntegerData(45908, 2));
				CTN = ls / 100;
				ls = Integer.parseInt(connector.getIntegerData(45910, 2));
				CTD = ls / 100;
				ls = Integer.parseInt(connector.getIntegerData(45916, 2));
				PTN = ls / 100;
				ls = Integer.parseInt(connector.getIntegerData(45918, 2));
				PTD = ls / 100;

				connector.disconnect();
				// sentron
			} else if (connector.type == 1) {
				connector.connectNonjamod(connector.addrStr, connector.port,
						connector.unitID, connector.type);

				CTN = ((Sentron) meter).getSentronPriCurrent();
				Log.i("ConnectingActivity", "CTN=" + CTN);
				CTD = ((Sentron) meter).getSentronSecCurrent();
				PTN = ((Sentron) meter).getSentronPriVoltage();
				PTD = ((Sentron) meter).getSentronSecVoltage();

				connector.disconnect();
			}else if(connector.type == 2){
				//scheider
				
				connector.connectNonjamod(connector.addrStr, connector.port,
						connector.unitID, connector.type);

				CTN = ((Scheider) meter).getPriCurrent();
				Log.i("ConnectingActivity", "CTN=" + CTN);
				CTD = ((Scheider) meter).getSecCurrent();
				PTN = ((Scheider) meter).getPriVoltage();
				PTD = ((Scheider) meter).getSecVoltage();

				connector.disconnect();
			}

		}

	}

}
