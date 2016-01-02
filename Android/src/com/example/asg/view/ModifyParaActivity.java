package com.example.asg.view;
import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.services.meteraccess.GE;
import com.example.asg.services.meteraccess.Scheider;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class ModifyParaActivity extends FatherActivity implements OnClickListener{

	TextView txtViewTypeName;
	TextView txtViewIP;
	Spinner sprConType;
	EditText edtTxtPriCurrent;
	EditText edtTxtSecCurrent;
	EditText edtTxtPriVoltage;
	EditText edtTxtSecVoltage;
	Button btnRead;
	Button btnModify;
	Button btnBack;
	
	public ProgressBar pb = null;
	
	Handler handler = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_para);
        
        connector = ((ASGApplication)getApplication()).getConnector();
        meter = ((ASGApplication)getApplication()).getMeter();
        
        this.txtViewTypeName = (TextView)this.findViewById(R.id.modify_tvTypeName);
        this.txtViewIP = (TextView)this.findViewById(R.id.modify_tvIP);
        this.sprConType = (Spinner)this.findViewById(R.id.modify_spinConType);
        
        pb = (ProgressBar)this.findViewById(R.id.modifypara_pb);
		pb.setVisibility(View.INVISIBLE);

        this.edtTxtPriCurrent = (EditText)this.findViewById(R.id.modify_etPriCurrent);
        this.edtTxtSecCurrent = (EditText)this.findViewById(R.id.modify_etSecCurrent);
        this.edtTxtPriVoltage = (EditText)this.findViewById(R.id.modify_etPriVoltage);
        this.edtTxtSecVoltage = (EditText)this.findViewById(R.id.modify_etSecVoltage);
        this.btnRead = (Button)this.findViewById(R.id.modify_btnRead);
        this.btnModify = (Button)this.findViewById(R.id.modify_btnModify);
        this.btnBack = (Button)this.findViewById(R.id.modify_btnBack);
        
        this.btnModify.setEnabled(false);
        
        this.btnRead.setOnClickListener(this);
        this.btnModify.setOnClickListener(this);
        this.btnBack.setOnClickListener(this);
        
        String[] sprTypes = null;
        //initialize the text
        if(connector.type == 0){
        	
        	this.txtViewTypeName.setText("Nexus 1272");
        	sprTypes = new String[]{
                	"Wye(No PTs or 3 PTs,3CTs)",
                	"Delta(No PTs,2 PTs or 3 PTs,3CTs)",
                	"Delta(No PTs or 2 PTs,2CTs)",
                	"2.5 Element Wye(No PTs or 2 PTs,3CTs)"};
        	
        }else if(connector.type == 1){
        	
        	this.txtViewTypeName.setText("PAC4200");
        	sprTypes = new String[]{  "3p4w", "3p3w", "3p4wb", "3p3wb", "1p2w"};
        	
        }else if(connector.type == 2){
        	
        	this.txtViewTypeName.setText("Scheider PM8");
        	sprTypes = new String[]{"10(1Ph 2Wire 1CT l-N)", "11(1Ph 2wire 1CT L-L)", 
        			"12(1Ph 3Wire 2CT)", "30(3Ph 3Wire 2CT)","31(3Ph 3Wire Delta 3CT)",
        			"40(3Ph 4Wire Wye)","42(3Ph 4Wire 3CT 2PT)"};
        }
        this.txtViewIP.setText(connector.addrStr);
        sprConType.setAdapter(new ArrayAdapter(this,android.R.layout.simple_spinner_item, sprTypes));
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				//if the button read is clicked
				if(data.getString("type").equals("readSuc")){
					btnRead.setEnabled(true);
					btnModify.setEnabled(true);
					pb.setVisibility(View.INVISIBLE);
					//sentron
					if(data.getInt("meterType") == 1){
						//0:3p4w; 1:3p3w ; 2: 3p4wb: 3:3p3wb; 4:1p2w
						//txtViewTypeName.setText("PAC4200");
						sprConType.setSelection(data.getInt("conType"));
						edtTxtPriCurrent.setText(""+data.getInt("priCurrent"));
						edtTxtSecCurrent.setText(""+data.getInt("secCurrent"));
						edtTxtPriVoltage.setText(""+data.getInt("priVoltage"));
						edtTxtSecVoltage.setText(""+data.getInt("secVoltage"));
					//GE
					}else if(data.getInt("meterType") == 0){
						//txtViewTypeName.setText("Nexus 1272");
						sprConType.setSelection(data.getInt("conType"));
						int tempFloat = 0;
						tempFloat = data.getInt("priCurrent");
						Log.i("ModifyParaActivity.handler", "priCurrent "+tempFloat/100);
						//Log.i("ModifyParaActivity.handler", (tempFloat/100)+ "." + tempFloat%100+"  --");
						edtTxtPriCurrent.setText(String.format("%.2f", (float)tempFloat/100));
						
						tempFloat = data.getInt("secCurrent");
						edtTxtSecCurrent.setText(String.format("%.2f", (float)tempFloat/100));
						tempFloat = data.getInt("priVoltage");
						edtTxtPriVoltage.setText(String.format("%.2f", (float)tempFloat/100));
						tempFloat = data.getInt("secVoltage");
						edtTxtSecVoltage.setText(String.format("%.2f", (float)tempFloat/100));
					}else if(data.getInt("meterType") == 2){
						
						sprConType.setSelection(data.getInt("conType"));
						edtTxtPriCurrent.setText(""+data.getInt("priCurrent"));
						edtTxtSecCurrent.setText(""+data.getInt("secCurrent"));
						edtTxtPriVoltage.setText(""+data.getInt("priVoltage"));
						edtTxtSecVoltage.setText(""+data.getInt("secVoltage"));
					}
					CommonFunction.showDialog("Success","Operation success",ModifyParaActivity.this);
				}if(data.getString("type").equals("readFail")){
					btnRead.setEnabled(true);
					btnModify.setEnabled(false);
					pb.setVisibility(View.INVISIBLE);
					CommonFunction.showDialog("Fail","Operation fail!",ModifyParaActivity.this);
				}else if(data.getString("type").equals("readPressed")){
					btnRead.setEnabled(false);
					btnModify.setEnabled(false);
					pb.setVisibility(View.VISIBLE);
					ModifyThread thread = new ModifyThread(0,0,0,0,0,0);
					thread.start();
				}else if(data.getString("type").equals("modifySuc")){
					btnRead.setEnabled(true);
					btnModify.setEnabled(true);
					pb.setVisibility(View.INVISIBLE);
					CommonFunction.showDialog("Success","Operation success",ModifyParaActivity.this);
				}else if(data.getString("type").equals("modifyFail")){
					btnRead.setEnabled(true);
					btnModify.setEnabled(true);
					pb.setVisibility(View.INVISIBLE);
					CommonFunction.showDialog("Fail","Operation fail!",ModifyParaActivity.this);
				}else if(data.getString("type").equals("modifyPressed")){
					btnRead.setEnabled(false);
					btnModify.setEnabled(false);
					pb.setVisibility(View.VISIBLE);
					
					int priCurrent = 0;
					int secCurrent = 0;
					int priVoltage = 0;
					int secVoltage = 0;
					//GE
					if(connector.type == 0){
						
						priCurrent = (int)(Float.parseFloat(edtTxtPriCurrent.getText().toString().trim())*100);
						secCurrent = (int)(Float.parseFloat(edtTxtSecCurrent.getText().toString().trim())*100);
						priVoltage = (int)(Float.parseFloat(edtTxtPriVoltage.getText().toString().trim())*100);
						secVoltage = (int)(Float.parseFloat(edtTxtSecVoltage.getText().toString().trim())*100);
						//sentron
					}else if(connector.type == 1){
						priCurrent = Integer.parseInt(edtTxtPriCurrent.getText().toString().trim());
						secCurrent = Integer.parseInt(edtTxtSecCurrent.getText().toString().trim());
						priVoltage = Integer.parseInt(edtTxtPriVoltage.getText().toString().trim());
						secVoltage = Integer.parseInt(edtTxtSecVoltage.getText().toString().trim());
					}else if(connector.type == 2){
						priCurrent = Integer.parseInt(edtTxtPriCurrent.getText().toString().trim());
						secCurrent = Integer.parseInt(edtTxtSecCurrent.getText().toString().trim());
						priVoltage = Integer.parseInt(edtTxtPriVoltage.getText().toString().trim());
						secVoltage = Integer.parseInt(edtTxtSecVoltage.getText().toString().trim());
					}
					ModifyThread thread = new ModifyThread( 1,sprConType.getSelectedItemPosition(),priCurrent,secCurrent,priVoltage,secVoltage);
					thread.start();
				}	
			}
		};
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_modify_para, menu);
        return true;
    }

	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == this.btnRead){
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putString("type", "readPressed");
			msg.setData(data);
			handler.sendMessage(msg);
		}else if(v == this.btnModify){
			
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putString("type", "modifyPressed");
			msg.setData(data);
			handler.sendMessage(msg);
			
		}else if(v == this.btnBack){
			
			Intent myintent=new Intent();
   	      	myintent.setClass(this,MenuActivity.class);
   	      	this.startActivity(myintent);
   	      	this.finish();
		}
		
	}
	
	class ModifyThread extends Thread {
		int btnType = -1;//0 for read, 1 for write
		int conType = -1;
		int priCurrent = -1;
		int secCurrent = -1;
		int priVoltage = -1;
		int secVoltage= -1;
		
		/**
		 * 
		 * @param activity
		 * @param btnType
		 * @param conType
		 * @param priCurrent
		 * @param secCurrent
		 */
		public ModifyThread(int btnType, int conType, int priCurrent, int secCurrent,int priVoltage, int secVoltage){
			this.btnType = btnType;
			
			this.conType = conType;
			this.priCurrent = priCurrent;
			this.secCurrent = secCurrent;
			this.priVoltage = priVoltage;
			this.secVoltage = secVoltage;
			
		}
		@Override
        public void run() {
			Bundle data = new Bundle();
			
			boolean suc = true;
			connector.connectNonjamod(connector.addrStr, connector.port, connector.unitID, connector.type);
			
			//read
			if(btnType == 0){
				
				//sentron
				if(connector.type == 1){
					int value;
					data.putInt("meterType", 1);
				
					//connection Type
					value = ((Sentron)meter).getSentronConType();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("conType", value);
					}
					
					//primary current
					value = ((Sentron)meter).getSentronPriCurrent();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("priCurrent", value);
					}
					
					//secondary current
					value = ((Sentron)meter).getSentronSecCurrent();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("secCurrent", value);
					}
					
					//primary Voltage
					value = ((Sentron)meter).getSentronPriVoltage();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("priVoltage", value);
					}
					
					//secondary Voltage
					value = ((Sentron)meter).getSentronSecVoltage();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("secVoltage", value);
					}
					
					if(suc){
						data.putString("type", "readSuc");
					}else{
						data.putString("type", "readFail");
					}
					
					
				//GE					
				}else if(connector.type == 0){
					 
					 String[] values = ((GE)meter).readGEData();
					 data.putInt("meterType", 0);
					 
					 if(values == null){
						 
						 data.putString("type", "readFail");
						 
					 }else{
						 
						 data.putInt("priCurrent", CommonFunction.changeHexStrToInteger(values[0]));
						 data.putInt("secCurrent", CommonFunction.changeHexStrToInteger(values[1]));
						 data.putInt("priVoltage", CommonFunction.changeHexStrToInteger(values[2]));
						 data.putInt("secVoltage", CommonFunction.changeHexStrToInteger(values[3]));
						 data.putInt("conType", ((GE)meter).getCorrespondingConType(Integer.parseInt(values[4]), true));
						 data.putString("type", "readSuc");
					 }
					 
				}else if(connector.type == 2){//scheider
					
					int value;
					data.putInt("meterType", 2);
				
					//connection Type
					value = ((Scheider)meter).getConType();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("conType", value);
					}
					
					//primary current
					value = ((Scheider)meter).getPriCurrent();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("priCurrent", value);
					}
					
					//secondary current
					value = ((Scheider)meter).getSecCurrent();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("secCurrent", value);
					}
					
					//primary Voltage
					value = ((Scheider)meter).getPriVoltage();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("priVoltage", value);
					}
					
					//secondary Voltage
					value = ((Scheider)meter).getSecVoltage();
					if(value == -1){
						suc = false;
					}else{
						data.putInt("secVoltage", value);
					}
					
					if(suc){
						data.putString("type", "readSuc");
					}else{
						data.putString("type", "readFail");
					}
				}
				
			//write
			}else if(btnType == 1){

				//sentron
				if(connector.type == 1){
					//connection type
					if(!((Sentron)meter).changeSentronConType(conType)){
						Log.i("ModifyParaActivity.run", "conType fail "+conType);
						suc = false;
					}
					

					//primary current
					if(!((Sentron)meter).changeSentronPriCurrent(priCurrent)){
						Log.i("ModifyParaActivity.run", "priCurrent fail "+priCurrent);
						suc = false;
					}
					
					//secondary current
					if(!((Sentron)meter).changeSentronSecCurrent(secCurrent)){
						Log.i("ModifyParaActivity.run", "secCurrent fail "+secCurrent);
						suc = false;
					}
					
					//primary Voltage
					if(!((Sentron)meter).changeSentronPriVoltage(priVoltage)){
						Log.i("ModifyParaActivity.run", "priVoltage fail "+priVoltage);
						suc = false;
					}
					
					//secondary Voltage
					if(!((Sentron)meter).changeSentronSecVoltage(secVoltage)){
						Log.i("ModifyParaActivity.run", "secVoltage fail "+secVoltage);
						suc = false;
					}
					
					if(suc){
						data.putString("type", "modifySuc");
					}else{
						data.putString("type", "modifyFail");
					}
					

				//GE
				}else if(connector.type == 0){
					
					suc = ((GE)meter).changeGEDataFile(priCurrent, secCurrent, priVoltage, secVoltage, conType);
					
					if(suc){
						data.putString("type", "modifySuc");
					}else{
						data.putString("type", "modifyFail");
					}
					
				}else if(connector.type == 2){//scheider
					
					suc = ((Scheider)meter).changeBasicSetting(priCurrent, secCurrent, priVoltage, secVoltage, conType);
					
					if(suc){
						data.putString("type", "modifySuc");
					}else{
						data.putString("type", "modifyFail");
					}
					
				}
				
				
			}
			
			connector.disconnect();

			Message msg = new Message();
			msg.setData(data);
			handler.sendMessage(msg);

		}
	}

}
