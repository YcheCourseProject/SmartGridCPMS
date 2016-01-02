package com.example.asg.services.meteraccess;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.example.asg.util.CommonFunction;

import android.util.Log;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

public class Connector {
	
	/**
	 * only used for build socket connection at the first time
	 */
	public static final int conWaitTime = 2000;	
	public TCPMasterConnection con;
	public ModbusTCPTransaction trans; // the transaction
	public ReadMultipleRegistersRequest req; // the request
	public ReadMultipleRegistersResponse res; // the response
	public ModbusCoupler modbuscoupler = null;
	public InetAddress addr;
	public String addrStr = "";
	/**
	 * type:(0:GE),(1:sentron)
	 */
	public int type = 0;
	public int port;
	public int unitID;

	public Socket socket = null;
	public SocketAddress sockaddr = null;
	public OutputStream os = null;
	public FilterInputStream is = null;
	
	/**
	 * 检查电表连接，如果连接成功就建立连接，返回true Param： addr(ip)：String port：int
	 * unitID(从机地址):int type:int(0:GE，1：西门子)
	 * 
	 * @return
	 */
	public boolean connectNonjamod(String addr, int port, int unitID, int type) {
		this.disconnect();
		
		try {
			this.port = port;
			this.unitID = unitID;
			this.type = type;
			Log.i("type", ""+this.type);
			this.addrStr = addr;
			
			//for nonjabus
			sockaddr = new InetSocketAddress(this.addrStr,port);
			socket=new Socket();
			socket.connect(sockaddr, Connector.conWaitTime);
			socket.setSoTimeout(conWaitTime);
			
			os= socket.getOutputStream();
			is = new BufferedInputStream(socket.getInputStream());
			Log.i("connection", "socket_con success");
			
			// GE
			if (this.type == 0) {
				byte[] b = {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x01,(byte)0x03,(byte)0xf6,(byte)0x21,(byte)0x00,(byte)0x02};
				b[6] = (byte)this.unitID;
				b = sendPackageAndGetResponseNoBlock(b, b.length).data;
				if(b != null){
					Log.i("connect", "true");
					return true;
				}else{
					return false;
				}
			// simen
			} else if (this.type == 1) {
				
				//encapsulate
				byte[] b = {(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0xf7,(byte)0x2b,(byte)0x0e,(byte)0x01,(byte)0x00};
				b[6] = (byte)this.unitID;
				Log.i("connection", "send package");
				b = sendPackageAndGetResponseNoBlock(b, b.length).data;
				Log.i("connection", "receive package");
				if(b != null){
					return true;
				}else{
					return false;
				}
			}
			
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
	
	/**
	 * 检查电表连接，如果连接成功就建立连接，返回true Param： addr(ip)：String port：int
	 * unitID(从机地址):int type:int(0:GE，1：西门子)
	 * 
	 * @return
	 */
	public boolean connectJamod(String addr, int port, int unitID, int type) {
		this.disconnect();
		try {
			this.port = port;
			this.unitID = unitID;
			this.type = type;
			this.addrStr = addr;
			
			//for jabus
			this.addr = InetAddress.getByName(this.addrStr);
			this.con = new TCPMasterConnection(this.addr);
			this.con.setPort(this.port);
			this.con.setTimeout(Connector.conWaitTime);
			this.con.connect();
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Disconnect the connection
	 */
	public void disconnect(){
		if(this.con != null && this.con.isConnected()){
			this.con.close();
		}
		if(this.socket != null && !this.socket.isClosed()){
			
			try {
				if(this.os != null){
					this.os.close();
				}
				if(this.is != null){
					this.is.close();	
				}
				if(this.socket != null){
					this.socket.close();	
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
		
	public String getDoubleData(int a, int b) {
		/*if(!this.checkConAndRepair()){
			return "";
		}*/
		
		double temp;
		
		req = new ReadMultipleRegistersRequest(a, b);
		//a为寄存器的号码
		
		req.setUnitID(this.unitID);
		// req.setUnitID(247);
		// 4. Prepare the transaction
		
		//准备发包
		trans = new ModbusTCPTransaction(con);
		trans.setRequest(req);				
		try {
			trans.execute();
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		//收包
		res = (ReadMultipleRegistersResponse) trans.getResponse();

		temp = Double
				.longBitsToDouble(((long) res.getRegisterValue(0) & 0xffff) << 48
						| ((long) res.getRegisterValue(1) & 0xffff) << 32
						| ((long) res.getRegisterValue(2) & 0xffff) << 16
						| ((long) res.getRegisterValue(3) & 0xffff));

		String str = String.valueOf(temp);
		if (str.length() <= 11){
			return str;
		}
		else{
			return str.substring(0, 10);
		}
	}
	public float[] getFloatForNonjamod(int referenceNum, int num){
		int[] tempData = null;
		float[] result = new float[num];
		for(int i=0;i<num;i++){
			tempData = this.getRegistersDataForNonjamod(referenceNum+i*2, 2);
			result[i] = Float.intBitsToFloat(tempData[0]*0x10000+tempData[1]);
		}
		return result;
	}
	
	/**
	 * 仅仅用于施耐德的寄存器的读取，为了节省代码用的函数，不可在其他地方使用
	 * 用于读取1个寄存器，判断它是否是32768，如果是，就置为0
	 * @param referenceNum
	 * @param registerNum
	 * @return
	 */
	public String getRegistersDataForNonjamodForScheider(int referenceNum, int registerNum) {
		/*	if(!this.checkConAndRepair()){
				return "";
			}*/
			byte[] b1 = {(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0xf7,(byte)0x03,(byte)0xc3,(byte)0x5d,(byte)0x00,(byte)0x02};
			b1[6] = (byte)unitID;
			b1[8] = (byte)(referenceNum / 0x0100);
			b1[9] = (byte)(referenceNum % 0x0100);
			b1[10] = (byte)(registerNum / 0x0100);
			b1[11] = (byte)(registerNum % 0x0100);
			Log.i("Connector.getRegistersDataForNonjamod()", "BeforeSend");
					
			b1 = sendPackageAndGetResponseNoBlock(b1, b1.length).data;
			//if the received package's function code != 0x03,fail
			if(b1 == null || b1[7] != 0x03){
				Log.i("owen", "getRegistersDataForNonjamod" + b1[7]);
				return null;
			}
			Log.i("Connector.getRegistersDataForNonjamod()", "AfterSend");
			
			int[] result = new int[registerNum];
			int index = 9;
			for(int i=0;i<registerNum;i++){
				result[i] = (b1[index++] & 0x00ff);
				result[i] = (result[i] << 8) | (b1[index++] & 0x00ff);
			}
			if(result[0] == 32768){
				return "NaN";
			}
			
			return result[0]+"";
		}
	
	/**
	 * 16bit per register
	 * @param referenceNum
	 * @param registerNum
	 * @return
	 */
	public int[] getRegistersDataForNonjamod(int referenceNum, int registerNum) {
		/*	if(!this.checkConAndRepair()){
				return "";
			}*/
			byte[] b1 = {(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0xf7,(byte)0x03,(byte)0xc3,(byte)0x5d,(byte)0x00,(byte)0x02};
			b1[6] = (byte)unitID;
			b1[8] = (byte)(referenceNum / 0x0100);
			b1[9] = (byte)(referenceNum % 0x0100);
			b1[10] = (byte)(registerNum / 0x0100);
			b1[11] = (byte)(registerNum % 0x0100);
			Log.i("Connector.getRegistersDataForNonjamod()", "BeforeSend");
					
			b1 = sendPackageAndGetResponseNoBlock(b1, b1.length).data;
			//if the received package's function code != 0x03,fail
			if(b1 == null || b1[7] != 0x03){
				Log.i("owen", "getRegistersDataForNonjamod" + b1[7]);
				return null;
			}
			Log.i("Connector.getRegistersDataForNonjamod()", "AfterSend");
			
			int[] result = new int[registerNum];
			int index = 9;
			for(int i=0;i<registerNum;i++){
				result[i] = (b1[index++] & 0x00ff);
				result[i] = (result[i] << 8) | (b1[index++] & 0x00ff);
			}
			
			return result;
		}
	
	static int TRANID = 0;
	/**
	 * TRANID 应该被优化一下，不过影响不大
	 * @param refNum
	 * @param regNum
	 * @param data
	 * @return
	 */
	
	public boolean writeRegistersDataForNonjamod(int refNum, int regNum, int[] data){
		
		byte[] b1 = new byte[13+data.length*2];
		//transition id
		b1[0] = (byte)(TRANID / 0x100);
		b1[1] = (byte)(TRANID % 0x100);
		TRANID++;
		//length
		b1[5] = (byte)(b1.length-6);
		//unitID
		b1[6] = (byte)unitID;
		//functionCode
		b1[7] = (byte)0x10;
		//reference numer
		b1[8] = (byte)(refNum / 0x100);
		b1[9] = (byte)(refNum % 0x100);
		//word length
		b1[10] = (byte)(data.length / 0x100);
		b1[11] = (byte)(data.length % 0x100);
		//byte length
		b1[12] = (byte)(data.length * 2);
		
		int index = 13;
		for(int i = 0;i<data.length;i++){
			b1[index++] = (byte)(data[i] / 0x100);
			b1[index++] = (byte)(data[i] % 0x100);
		}
		//|0b|f7|10|c3|5b|00|02|04|00|00|00|28|
		String str = "";
		for(int i=0;i<index;i++){
			str = str + CommonFunction.changeIntegerToHexStr(1, b1[i]&0x00ff, "")+" ";
		}
		Log.i("Connector.writeRegistersDataForNonjamod", "发送的报文为 "+ str);
		ResponsePackage rp = sendPackageAndGetResponseNoBlock(b1, b1.length);
		b1 = rp.data;
		
		str = "";
		for(int i=0;i<rp.packageLen;i++){
			str = str + CommonFunction.changeIntegerToHexStr(1, b1[i]&0x00ff, "")+" ";
		}
		Log.i("Connector.writeRegistersDataForNonjamod", "返回的报文为 "+ str);
		
		//if the received package's function code != 0x10,fail
		if(b1 == null || b1[7] != 0x10){
			Log.i("Connector.writeRegistersDataForNonjamod", "fail"+ " "+ b1[7]);
			return false;
		}
		return true;
	} 
		
	/**
	 * 
	 */
	public String getIntegerData(int a, int b) {
	/*	if(!this.checkConAndRepair()){
			return "";
		}*/
		
		req = new ReadMultipleRegistersRequest(a, b);
		req.setUnitID(this.unitID);

		// 4. Prepare the transaction
		trans = new ModbusTCPTransaction(con);
		trans.setRequest(req);
		try {
			trans.execute();
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		res = (ReadMultipleRegistersResponse) trans.getResponse();
		int result = ((res.getRegisterValue(0) &   0xffff)<<16 | (res.getRegisterValue(1) &   0xffff));
		return String.valueOf(result);
		
	}
	
	/**
	 * used by simenz, return the float String
	 * @param a
	 * @param b
	 * @return
	 */
	public String getFloatData(int a, int b) {
		/*if(!this.checkConAndRepair()){
			return "";
		}*/
		
		
		float temp;
		req = new ReadMultipleRegistersRequest(a, b);
		req.setUnitID(this.unitID);

		// 4. Prepare the transaction
		trans = new ModbusTCPTransaction(con);
		trans.setRequest(req);
		try {
			trans.execute();
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		res = (ReadMultipleRegistersResponse) trans.getResponse();

		temp = Float.intBitsToFloat((res.getRegisterValue(0) & 0xffff) << 16
				| (res.getRegisterValue(1) & 0xffff));
		String str = String.valueOf(temp);
		if (str.length() <= 11){
			return str;
		}
		else{
			return str.substring(0, 10);
		}
	}

	/**
	 * 
	 * @param data	the data to send
	 * @param len	the length of the data
	 * @return
	 * 		return null if exception throws
	 */
	public ResponsePackage sendPackageAndGetResponseNoBlock(byte[] data, int len){
		int responseLength = 0;
		byte[] ibuf = new byte[261];
		
		try{
			Log.i("Connection.sendPackageAndGetResponseNoBlock","beforeWrite");
			os.write(data, 0, len);
			responseLength = is.read(ibuf, 0, 261);
			Log.i("Connection.sendPackageAndGetResponseNoBlock","AfterWrite");
		}catch(Exception ex){
			ex.printStackTrace();
			Log.i("Connection.sendPackageAndGetResponseNoBlock","connection exception");
			return new ResponsePackage();
		}
		return new ResponsePackage(ibuf, responseLength);
		
	}

	@Override 
	public void finalize(){
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			Log.i("owen", "finalize");
			e.printStackTrace();
		}
		Log.i("owen", "finalize");
	} 
	
	/**
	 * 判断返回的报文是否有效是判断data是否为空，或者packagelen是否为0
	 * @author Administrator
	 *
	 */
	public class ResponsePackage{
		public int packageLen = 0;
		public byte[] data = null;
		
		ResponsePackage(){
			packageLen = 0;
			data = null;
		}
		
		public ResponsePackage(byte[] data, int packageLen){
			this.packageLen = packageLen;
			this.data = data;
		}
	}
	
	
}
