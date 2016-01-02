package com.example.asg.models;
public class LoadInfo {
	public LoadInfo(double ap, double rp) {
		super();
		this.ap = ap;
		this.rp = rp;
		this.times=0;
		this.everytimechangeap=0;
	}

	private int  times;					//记录增长的次数
	private double everytimechangeap;
	private double ap;
	private double rp;

	public double getAp() {
		return ap;
	}
	public double getRp() {
		return rp;
	}
	public void plus(double changeap,double changerp)
	{
		this.ap=this.ap+changeap;
		this.rp=this.rp+changerp;
		times++;
		this.everytimechangeap=this.ap/times;
	}
	public double getEverytimeChangeAp()
	{
		return everytimechangeap;
	}
	
	public void init()
	{
		this.ap=0;
		this.rp=0;
		this.times=0;
	}
	
}
