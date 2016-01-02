package com.example.asg.services.meteraccess;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

import com.example.asg.util.CommonFunction;
import com.example.asg.view.MainActivity;



public class Sentron extends MeterFather{
		

	
	/**
	 * get the sentron's connection type
	 * 
	 * @return
	 * 	0:3p4w; 1:3p3w ; 2: 3p4wb: 3:3p3wb; 4:1p2w
	 * 	if -1 the operation is fail,so please check the return value
	 */
	public int getSentronConType(){
		try {
			//encapsulate
			byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
			b[6] = (byte)connector.unitID;
			b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
			//not exactly
			if(b == null || b.length < 40){
				Log.i("sentron", "encapsulate length wrong");
				return -1;
			}
			//read the 50001 registers 
			int[] result = connector.getRegistersDataForNonjamod(50001, 2);
			
			return (result == null)?-1:(result[0]*0x10000)+result[1];
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("sentron", "Exception");
			return -1;
		}
	}
	
	
	
	/**
	 * change the sentron's connection type
	 * 
	 * @param conType:integer
	 *    0:3p4w; 1:3p3w ; 2: 3p4wb: 3:3p3wb; 4:1p2w
	 * @return
	 */
	public boolean changeSentronConType(int conType){
		if(conType<0 || conType>4){
			return false;
		}
		try {
			//encapsulate
			byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
			b[6] = (byte)connector.unitID;
			b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
			//not exactly
			if(b.length < 40){
				return false;
			}
			//write to the 50001 registers
			
			int[] data = new int[2];
			data[0] = conType / 0x10000;
			data[1] = conType % 0x10000;
			Log.i("Sentron.changeSentronConType", "before write");
			return connector.writeRegistersDataForNonjamod(50001, 2, data);
			
		}catch(Exception ex){
			Log.i("Sentron.changeSentronConType", "exception");
			return false;
		}
	}
	
	/**
	 * get the sentron's primary current
	 * @return
	 * 	if -1 the operation is fail,so please check the return value
	 */
	public int getSentronPriCurrent(){
		try {
			//encapsulate
			byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
			b[6] = (byte)connector.unitID;
			b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
			//not exactly
			if(b.length < 40){
				return -1;
			}
			//read the 50011 registers to get the primary current 
			
			int[] result = connector.getRegistersDataForNonjamod(50011, 2);
			
			return (result == null)?-1:(result[0]*0x10000)+result[1];
			
		}catch(Exception ex){
			ex.printStackTrace();
			return -1;
		}
	}
		
	
	
		/**
		 * change the sentron's primary current
		 * @param curValue:integer
		 * @return
		 */
		public boolean changeSentronPriCurrent(int curValue){
			try {
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return false;
				}
				//write to the 50011 registers to change the primary current
				
				int[] data = new int[2];
				data[0] = curValue / 0x10000;
				data[1] = curValue % 0x10000;
				
				return connector.writeRegistersDataForNonjamod(50011, 2, data);
				
			}catch(Exception ex){
				Log.i("Sentron.changeSentronPriCurrent", "exception");
				return false;
			}
		}
	
	
		
		/**
		 * get the sentron's second current
		 * @return
		 * 	if -1 the operation is fail,so please check the return value
		 */
		public int getSentronSecCurrent(){
			try {
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return -1;
				}
				//read the 50013 registers to get the primary current 
				int[] result = connector.getRegistersDataForNonjamod(50013, 2);
				
				return (result == null)?-1:(result[0]*0x10000)+result[1];
			}catch(Exception ex){
				ex.printStackTrace();
				return -1;
			}
		}
			
		
		
		/**
		 * change the sentron's second current
		 * @param curValue:integer
		 * @return
		 */
		public boolean changeSentronSecCurrent(int curValue){
			try {
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return false;
				}
				//write to the 50013 registers to change the second current

				int[] data = new int[2];
				data[0] = curValue / 0x10000;
				data[1] = curValue % 0x10000;
				
				return connector.writeRegistersDataForNonjamod(50013, 2, data);
				
			}catch(Exception ex){
				Log.i("Sentron.changeSentronSecCurrent", "exception");
				return false;
			}
		}
			
		
		/**
		 * set if the voltage transformer is open
		 * @param isOpen: true for open and false for not open
		 * @return
		 * 		
		 */
		public boolean setSentronVoltageTran(boolean isOpen){
			//encapsulate
			try{
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return false;
				}
				//write to the 50003 registers to change the voltage transformer
				
				int[] data = new int[2];
				data[0] = 0;
				data[1] = (isOpen == true)?1:0;
				
				return connector.writeRegistersDataForNonjamod(50003, 2, data);
				
			}catch(Exception ex){
				return false;
			}
		}
	
		/**
		 * get the sentron's primary Voltage
		 * @return
		 * 	if -1 the operation is fail,so please check the return value
		 */
		public int getSentronPriVoltage(){
			try {
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return -1;
				}
				//read the c355 registers to get the primary Voltage
				int[] result = connector.getRegistersDataForNonjamod(50005, 2);
				Log.i("Sentron.getSentronPriVoltage", "result[0]="+result[0]+ "  result[1]="+result[1]);
				return (result == null)?-1:(result[0]*0x10000)+result[1];
				
			}catch(Exception ex){
				ex.printStackTrace();
				return -1;
			}
		}
			
		
		
		/**
		 * change the sentron's primary voltage
		 * @param curValue:integer
		 * @return
		 */
		public boolean changeSentronPriVoltage(int curValue){
			try {
				//to open the voltage transformer first
				if(!this.setSentronVoltageTran(true)){
					return false;
				}
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return false;
				}
				//write to the 50005 registers to change the primary current
				
				int[] data = new int[2];
				data[0] = curValue / 0x10000;
				data[1] = curValue % 0x10000;
				
				return connector.writeRegistersDataForNonjamod(50005, 2, data);
				
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
		}
		
		/**
		 * get the sentron's second Voltage
		 * @return
		 * 	if -1 the operation is fail,so please check the return value
		 */
		public int getSentronSecVoltage(){
			try {
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return -1;
				}
				//read the 50007 registers to get the primary current 
				int[] result = connector.getRegistersDataForNonjamod(50007, 2);
				
				return (result == null)?-1:(result[0]*0x10000)+result[1];
			}catch(Exception ex){
				ex.printStackTrace();
				return -1;
			}
		}
			
		
		
		/**
		 * change the sentron's second current
		 * @param curValue:integer
		 * @return
		 */
		public boolean changeSentronSecVoltage(int curValue){
			try {
				//to open the voltage transformer first
				if(!this.setSentronVoltageTran(true)){
					return false;
				}
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					return false;
				}
				//write to the 50007 registers to change the second current
				int[] data = new int[2];
				data[0] = curValue / 0x10000;
				data[1] = curValue % 0x10000;
				
				return connector.writeRegistersDataForNonjamod(50007, 2, data);
			
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
		}
			
		/**
		 * 
		 * @return int[2]:int[0] is the passwd,int[1] is the state(1 for protection,otherwise 0)
		 * 			null if fail
		 */
		public int[] getSentronPasswdAndState(){
			
			int result[] = new int[2];
			try {
				//get the passwdState
				int[] tempArray = connector.getRegistersDataForNonjamod(63009, 2);
				if(tempArray == null){
					Log.i("owen", "null");
					return null;
				}
				result[1] = tempArray[0] * 0x10000 + tempArray[1];
				
				//violate the passwd
				
				byte obuf[] = new byte[261];
				obuf[2] = 0x00;
				obuf[3] = 0x00;
				obuf[4] = 0x00;
				obuf[5] = 0x0d;
				obuf[6] = 0x0d;
				
				byte[] data = new byte[]{0x67, (byte) 0xff,0x0e,0x00,0x02,0x04,0x00,0x00,0x16,0x2e,0x16,0x2e};
				byte[] tempByteArray = null;
				int len = data.length;
				for(int i=0;i<9999;i++){
					data[len-4] = (byte)(i>>8);
					data[len-3] = (byte)(i);
					data[len-2] = (byte)data[len-4];
					data[len-1] = (byte)data[len-3];
					//获得密码的byte数组
					tempByteArray = CRCGenerator.CalculateCrc(data, 0, data.length);
					data[data.length - 2] = tempByteArray[0];
					data[data.length - 1] = tempByteArray[1];
					//构建最终发的包
					obuf[0] = (byte)(i>>8);
					obuf[1] = (byte)(i);
					for(int j=0;j<data.length;j++){
						obuf[j+7] = data[j];
					}
					tempByteArray = connector.sendPackageAndGetResponseNoBlock(obuf,19).data;
					if(tempByteArray[7] != 0x67){
						
					}else{
						result[0] = i;
						break;
					}
					
				}
				return result;
			}catch(Exception ex){
				ex.printStackTrace();
				return null;
			}
			
		}
		
		
		/**
		 * change the passwd State
		 * @param currentPasswd: short
		 * @param passwdState:short(0 for nonprotection,1 for protection)
		 * @return
		 */
		public boolean changeSentronPasswdState(short currentPasswd, short passwdState){
			if(passwdState == 0){
				return (changeSentronPasswd(currentPasswd, currentPasswd, passwdState) == -1)?false:true;
			}else{
				try {
					//encapsulate
					byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
					b[6] = (byte)connector.unitID;
					b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
					//not exactly
					if(b.length < 40){
						return false;
					}
					//input passwd
					//byte[] b1 = {0x00,0x07,0x00,0x00,0x00,0x0d,(byte) 0xf7,0x67,(byte) 0xff,0x0e,0x00,0x02,0x04,0x00,0x00,0x00,(byte) 0x02,(byte) 0xa7,(byte) 0xb3};
					short[] value = new short[2];
					value[0] = 0;
					value[1] = currentPasswd;
					b = ((SentronConnector)(connector)).PAC4200WriteMultiRegsPasswordRequest((short)0xff0e, value, currentPasswd);
					b = ((SentronConnector)(connector)).addPackageHead(b,(short)2,(byte)connector.unitID);
					b = ((SentronConnector)(connector)).sendPackageAndGetResponseNoBlock(b,b.length).data;
					if(b[7] != 0x67){
						return false;
					}
					//change passwd state
					//byte[] b2 = {(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x15,(byte)0xf7,(byte)0x67,(byte)0xff,(byte)0x0e,(byte)0x00,(byte)0x06,(byte)0x0c,
					//		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x1a,(byte)0x31};
					value = new short[2];
					value[0] = 0;
					value[1] = passwdState;
					b = ((SentronConnector)(connector)).PAC4200WriteMultiRegsPasswordRequest((short)0xff12, value, currentPasswd);
					b = ((SentronConnector)(connector)).addPackageHead(b,(short)3,(byte)connector.unitID);
					b = ((SentronConnector)(connector)).sendPackageAndGetResponseNoBlock(b,b.length).data;
					if(b[7] != 0x67){
						Log.i("FailState", b[7]+"");
						return false;
					}
					
					return true;
				}catch(Exception ex){
					ex.printStackTrace();
					return false;
				}
			}
		}
		/**
		 *	change the Password 
		 * @param currentPasswd : short
		 * @param passwdState : short(1:protection;0:nonProtection)
		 * @return
		 *  if success, return the current password, otherwise return -1
		 */
		public int changeSentronPasswd(short currentPasswd, short desPasswd, short passwdState){
			try {
		
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)connector.unitID;
				b = connector.sendPackageAndGetResponseNoBlock(b, b.length).data;
				//not exactly
				if(b.length < 40){
					Log.i("log", "leng<40");
					return -1;
				}
				//input passwd
				//byte[] b1 = {0x00,0x07,0x00,0x00,0x00,0x0d,(byte) 0xf7,0x67,(byte) 0xff,0x0e,0x00,0x02,0x04,0x00,0x00,0x00,(byte) 0x02,(byte) 0xa7,(byte) 0xb3};
				short[] value = new short[2];
				value[0] = 0;
				value[1] = currentPasswd;
				b = ((SentronConnector)(connector)).PAC4200WriteMultiRegsPasswordRequest((short)0xff0e, value, currentPasswd);
				b = ((SentronConnector)(connector)).addPackageHead(b,(short)2,(byte)((SentronConnector)(connector)).unitID);
				
				
				
				b = ((SentronConnector)(connector)).sendPackageAndGetResponseNoBlock(b,b.length).data;
				if(b[7] != 0x67){
					Log.i("log", "input passwd fail"+b[6]+ " "+ b[7]);
					return -1;
					
				}
				//change passwd
				//byte[] b2 = {(byte)0x00,(byte)0x0a,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x15,(byte)0xf7,(byte)0x67,(byte)0xff,(byte)0x0e,(byte)0x00,(byte)0x06,(byte)0x0c,
				//		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x1a,(byte)0x31};
				value = new short[6];
				value[0] = 0;
				value[1] = currentPasswd;
				value[2] = 0;
				value[3] = desPasswd;
				value[4] = 0;
				value[5] = passwdState;
				b = ((SentronConnector)(connector)).PAC4200WriteMultiRegsPasswordRequest((short)0xff0e, value, currentPasswd);
				b = ((SentronConnector)(connector)).addPackageHead(b,(short)3,(byte)((SentronConnector)(connector)).unitID);
				b = ((SentronConnector)(connector)).sendPackageAndGetResponseNoBlock(b,b.length).data;
				if(b == null){
					return -1;
				}
				int n = 0x00ff & b[7];
				if(b[7] != 0x67){
					Log.i("log", "chagne passwd fail value= "+n);
					return -1;
				}
		
				return desPasswd;
			}catch(Exception ex){
				ex.printStackTrace();
				return -1;
			}
		} 
		
		
		

		 /**
			 * used by sentron, to format the integer password to the string for showing
			 * @param pwd
			 * @return
			 */
			public static String changeIntToPasswdStr(int pwd){
				String result = String.valueOf(pwd);
				int len = result.length();
				while(len<4){
					result = 0 + result;
					len++;
				}
				return result;
			}
	
		
}


class CRCGenerator {
	
	 static int[] crcTable = new int[] { 
		        0,0xc0c1, 0xc181, 320, 0xc301, 960, 640, 0xc241, 0xc601, 0x6c0, 0x780, 0xc741, 0x500, 0xc5c1, 0xc481, 0x440, 
		        0xcc01, 0xcc0, 0xd80, 0xcd41, 0xf00, 0xcfc1, 0xce81, 0xe40, 0xa00, 0xcac1, 0xcb81, 0xb40, 0xc901, 0x9c0, 0x880, 0xc841, 
		        0xd801, 0x18c0, 0x1980, 0xd941, 0x1b00, 0xdbc1, 0xda81, 0x1a40, 0x1e00, 0xdec1, 0xdf81, 0x1f40, 0xdd01, 0x1dc0, 0x1c80, 0xdc41, 
		        0x1400, 0xd4c1, 0xd581, 0x1540, 0xd701, 0x17c0, 0x1680, 0xd641, 0xd201, 0x12c0, 0x1380, 0xd341, 0x1100, 0xd1c1, 0xd081, 0x1040, 
		        0xf001, 0x30c0, 0x3180, 0xf141, 0x3300, 0xf3c1, 0xf281, 0x3240, 0x3600, 0xf6c1, 0xf781, 0x3740, 0xf501, 0x35c0, 0x3480, 0xf441, 
		        0x3c00, 0xfcc1, 0xfd81, 0x3d40, 0xff01, 0x3fc0, 0x3e80, 0xfe41, 0xfa01, 0x3ac0, 0x3b80, 0xfb41, 0x3900, 0xf9c1, 0xf881, 0x3840, 
		        0x2800, 0xe8c1, 0xe981, 0x2940, 0xeb01, 0x2bc0, 0x2a80, 0xea41, 0xee01, 0x2ec0, 0x2f80, 0xef41, 0x2d00, 0xedc1, 0xec81, 0x2c40, 
		        0xe401, 0x24c0, 0x2580, 0xe541, 0x2700, 0xe7c1, 0xe681, 0x2640, 0x2200, 0xe2c1, 0xe381, 0x2340, 0xe101, 0x21c0, 0x2080, 0xe041, 
		        0xa001, 0x60c0, 0x6180, 0xa141, 0x6300, 0xa3c1, 0xa281, 0x6240, 0x6600, 0xa6c1, 0xa781, 0x6740, 0xa501, 0x65c0, 0x6480, 0xa441, 
		        0x6c00, 0xacc1, 0xad81, 0x6d40, 0xaf01, 0x6fc0, 0x6e80, 0xae41, 0xaa01, 0x6ac0, 0x6b80, 0xab41, 0x6900, 0xa9c1, 0xa881, 0x6840, 
		        0x7800, 0xb8c1, 0xb981, 0x7940, 0xbb01, 0x7bc0, 0x7a80, 0xba41, 0xbe01, 0x7ec0, 0x7f80, 0xbf41, 0x7d00, 0xbdc1, 0xbc81, 0x7c40, 
		        0xb401, 0x74c0, 0x7580, 0xb541, 0x7700, 0xb7c1, 0xb681, 0x7640, 0x7200, 0xb2c1, 0xb381, 0x7340, 0xb101, 0x71c0, 0x7080, 0xb041, 
		        0x5000, 0x90c1, 0x9181, 0x5140, 0x9301, 0x53c0, 0x5280, 0x9241, 0x9601, 0x56c0, 0x5780, 0x9741, 0x5500, 0x95c1, 0x9481, 0x5440, 
		        0x9c01, 0x5cc0, 0x5d80, 0x9d41, 0x5f00, 0x9fc1, 0x9e81, 0x5e40, 0x5a00, 0x9ac1, 0x9b81, 0x5b40, 0x9901, 0x59c0, 0x5880, 0x9841, 
		        0x8801, 0x48c0, 0x4980, 0x8941, 0x4b00, 0x8bc1, 0x8a81, 0x4a40, 0x4e00, 0x8ec1, 0x8f81, 0x4f40, 0x8d01, 0x4dc0, 0x4c80, 0x8c41, 
		        0x4400, 0x84c1, 0x8581, 0x4540, 0x8701, 0x47c0, 0x4680, 0x8641, 0x8201, 0x42c0, 0x4380, 0x8341, 0x4100, 0x81c1, 0x8081, 0x4040
		     };
		
	 public static byte[] CalculateCrc(byte[] data, int offset, int length)
	 {
	     short num = (short) 0xffff;
	     //System.out.println(num);
	     for (int i = offset; i < (offset + length); i++){
	    	 //System.out.println("i="+i+"  data[i]="+data[i]);
	         short index = (short) (((num & 0x00ff) ^ data[i]) & 0x00ff);
	         
	         //System.out.println("index="+index);
	         num = (short) ((num >> 8) & 0x00ff);
	         num = (short) (num ^ (short)crcTable[index]);
	     }
	     byte[] b = new byte[2];
	     b[0] = (byte)(num & 0x00ff);
	     b[1] = (byte)((num>>8)&0x00ff);	     
	     return b;
	 }


}