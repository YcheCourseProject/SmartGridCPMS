package com.example.asg.services.meteraccess;


public class MeterFather {
	public Connector connector = null;
	public String passwd = "";
	public void setConnector(Connector connector){
		this.connector = connector;
	}
	public Connector getConnector(){
		return this.connector;
	}
}
