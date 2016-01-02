package com.example.asg.services.meteraccess;

import com.example.asg.util.CommonFunction;


/**
 * 用于在较低层的报文的基础上的操作
 * @author Administrator
 *
 */
public class SentronConnector extends com.example.asg.services.meteraccess.Connector{
	

	public byte[] PAC4200WriteMultiRegsPasswordRequest(short startAddress, short[] values, short password){
	     byte[] data = new byte[8 + (2 * values.length)];
	     data[0] = 0x67;
	     data[1] = (byte) ((startAddress>>8)&0x00ff);
	     data[2] = (byte) ((startAddress%0x0100));
	     data[3] = (byte) (values.length / 0x100);
	     data[4] = (byte) (values.length % 0x100);
	     data[5] = (byte) (2 * values.length);
	     int index = 6;
	     short num2 = 0;
	     for(int i=0;i<values.length;i++)
	     {
	    	 num2 = values[i];
	         data[index] = (byte) (num2 / 0x100);
	         index++;
	         data[index] = (byte) (num2 % 0x100);
	         index++;
	     }
	     data[data.length - 2] = (byte) (password / 0x100);
	     data[data.length - 1] = (byte) (password % 0x100);
	     
	     byte[] buffer2 = CRCGenerator.CalculateCrc(data, 0, data.length);
	     data[data.length - 2] = buffer2[0];
	     data[data.length - 1] = buffer2[1];
	     /*for(int i=0;i<data.length;i++){
	    	// System.out.print(data[i]+" ");
	    	// System.out.println(data[i]);
	    	 System.out.print(CommonFunction.changeDecimalTo2BitHexStr((int)(data[i] & 0x00ff))+" ");
	     }*/
	     return data;
	 }
	
	/**
	 * 添加上包头，包括transitionID，length，unitID等等,仅仅在密码保护开启时发包时候使用
	 * @return
	 */
	public byte[] addPackageHead(byte[] pack, short transitionID, byte unitID){
		byte[] data = new byte[7+pack.length];
		
		data[0] = (byte)(transitionID>>8);
		data[1] = (byte)(transitionID);
		data[2] = 0x00;
		data[3] = 0x00;
		data[4] = (byte)((pack.length+1)>>8);
		data[5] = (byte)(pack.length+1);
		data[6] = (byte)(unitID);
		
		for(int i=0;i<pack.length;i++){
			data[i+7] = pack[i];
		}
		
		return data;
	}
	
	
}
