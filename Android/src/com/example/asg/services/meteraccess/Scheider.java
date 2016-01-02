package com.example.asg.services.meteraccess;

import android.util.Log;


public class Scheider  extends MeterFather{
	
	
	public int getPriCurrent(){
		try {
			//read the 3104 registers 
			int[] result = connector.getRegistersDataForNonjamod(3200, 1);
			
			if(result != null){
				return result[0];
			}else{
				return -1;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("sentron", "Exception");
			return -1;
		}
	}
	
	public int getSecCurrent(){
		try {
			//read the 3206 registers 
			int[] result = connector.getRegistersDataForNonjamod(3201, 1);
			
			if(result != null){
				return result[0];
			}else{
				return -1;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("sentron", "Exception");
			return -1;
		}
	}
	
	public int getPriVoltage(){
		try {
			//read the 3200 registers 
			int[] result = connector.getRegistersDataForNonjamod(3204, 1);
			
			if(result != null){
				return result[0];
			}else{
				return -1;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("sentron", "Exception");
			return -1;
		}
	}
	
	public int getSecVoltage(){
		try {
			//read the 3201 registers 
			int[] result = connector.getRegistersDataForNonjamod(3206, 1);
			
			if(result != null){
				return result[0];
			}else{
				return -1;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("sentron", "Exception");
			return -1;
		}
	}
	
	public int getConType(){
		try {
			//read the 3201 registers 
			int[] result = connector.getRegistersDataForNonjamod(3199, 1);
    			
			if(result != null){
				switch(result[0]){
				case 10:
					return 0;
				case 11:
					return 1;
				case 12:
					return 2;
				case 30:
					return 3;
				case 31:
					return 4;
				case 40:
					return 5;
				case 42:
					return 6; 
				}
				return -1;
			}else{
				return -1;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("sentron", "Exception");
			return -1;
		}
	}
	
	/**
	 * 改变互感比和连接方式
	 * @return
	 */
	public boolean changeBasicSetting(int CTN, int CTD,int PTN, int PTD,  int type){
		int[] data = new int[1];
		boolean b = false;
		try{
			
			//first step, command
			data[0] = 9020;
			b = connector.writeRegistersDataForNonjamod(7999, 1, data);
			if(!b){
				return false;
			}
			
			//change PT primary
			data = new int[1];
			data[0] = PTN;
			b = connector.writeRegistersDataForNonjamod(3204, 1, data);
			if(!b){
				return false;
			}
			
			//change PT second
			data[0] = PTD;
			b = connector.writeRegistersDataForNonjamod(3206, 1, data);
			if(!b){
				return false;
			}
			
			//change connection type
			switch(type){
			case 0:
				type = 10;
			case 1:
				type = 11;
			case 2:
				type = 12;
			case 3:
				type = 30;
			case 4:
				type = 31;
			case 5:
				type = 40;
			case 6:
				type = 42;
			default:
				type = 10;
			}
			data[0] = type;
			b = connector.writeRegistersDataForNonjamod(3199, 1, data);
			if(!b){
				return false;
			}
			
			//change CT primary
			data[0] = CTN;
			b = connector.writeRegistersDataForNonjamod(3200, 1, data);
			if(!b){
				return false;
			}
			
			//change CT second
			data[0] = CTD;
			b = connector.writeRegistersDataForNonjamod(3201, 1, data);
			if(!b){
				return false;
			}
			
			//end
			data[0] = 1;
			b = connector.writeRegistersDataForNonjamod(8000, 1, data);
			if(!b){
				return false;
			}
			
			//end
			data[0] = 9021;
			b = connector.writeRegistersDataForNonjamod(7999, 1, data);
			if(!b){
				return false;
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return true;
	}
	
	
	
	
	
	
	
}
