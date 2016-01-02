package com.example.asg;
import com.example.asg.services.meteraccess.Connector;
import com.example.asg.services.meteraccess.MeterFather;

import android.app.Application;
/**
 * 这是一个全局变量的共享类，在所有的activity中传递共享信息
 * @author Administrator
 *
 */
public class ASGApplication extends Application{
	Connector connector = null;
	MeterFather meter = null;
	
	public void setConnector(Connector connector){
		this.connector = connector;
	}
	
	public Connector getConnector(){
		return this.connector;
	}
	
	public MeterFather getMeter(){
		return this.meter;
	}
	
	public void setMeter(MeterFather meter){
		this.meter = meter;
	}
}
