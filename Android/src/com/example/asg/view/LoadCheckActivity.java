package com.example.asg.view;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Date;
  
import org.apache.http.conn.scheme.PlainSocketFactory;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.widget.DomainLabelWidget;
import com.androidplot.ui.widget.TitleWidget;
import com.androidplot.util.PlotStatistics;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.asg.ASGApplication;
import com.example.asg.FatherActivity;
import com.example.asg.R;
import com.example.asg.models.LoadInfo;
import com.example.asg.services.db.LoadInfoDB;
import com.example.asg.services.meteraccess.GEConnector;
import com.example.asg.services.meteraccess.ScheiderConnector;
import com.example.asg.services.meteraccess.Sentron;

 

import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadCheckActivity extends FatherActivity implements
		OnClickListener {
	static final int WINDOW_SIZE = 4;
	static final double EXPONENTIAL_AVG_RATE = 0.5;
	// 刷新的时间
	private final int TimeInterval = 1500;
	private final int HistorySize = 20; // 历史数据的长度设为了20可以利用的有20个点
	private final double LoadChangeBase = 3; // 处理后的毛刺大概在2.8电脑已经达到差不多最大的毛刺了
	private double LoadChangeBoundary = LoadChangeBase; // 要根据接入负载的变化来改变
	DecimalFormat df = new DecimalFormat("0.00");
	// 用于保留两位有效数字显示

	private LoadInfoDB loadinfodb = null;
	private XYPlot plot = null;
	Button button_log = null;
	Button btnBack = null;
	// Button buuton_config=null;
	private CheckBox checkboxAp = null;
	private CheckBox checkboxRp = null;
	private CheckBox checkboxVAp = null;

	private SimpleXYSeries apSeries = null;
	private SimpleXYSeries rpSeries = null;
	private SimpleXYSeries VApSeries = null;

	LinearLayout linearLayout = null;
	private LoadInfo loadinfo = new LoadInfo(0, 0);
	// 保存得到的active power
	double intAP = 0;
	double intRP = 0;
	double intVAP = 0;
	double lastAP = 0;
	double lastRP = 0;
	double lastVAP = 0;

	// 第一次获得数据
	boolean isFirst = true;
	boolean mutex = false;
	boolean checkloadin = false; // 表示还没有负载接入
	boolean checkloadout = false; // 表示还没有负载接出

	boolean confirmsteady = false; // 表示对稳态的确认
	boolean showApstatus = true;
	boolean showRpstatus = true;
	boolean showVApstatus = true; // 控制是否显示视在功率的布尔变量

	public int CTN, CTD;
	public int PTN;
	public int PTD;
	public int firstdatatime = 0;
	Handler handler;
	// 获取数据的线程的对象
	Get_Data get_data;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.setClass(this, MenuActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState); // 保存有上一次的焦点的信息
		setContentView(R.layout.activity_load_check);

		connector = ((ASGApplication) getApplication()).getConnector(); // getApplication干什么用呢?????????
		meter = ((ASGApplication) getApplication()).getMeter();

		plot = (XYPlot) findViewById(R.id.loadCheck_plot);
		linearLayout = (LinearLayout) this
				.findViewById(R.id.loadCheck_linearLayout);
		 btnBack = (Button) this.findViewById(R.id.loadCheck_btnCanel);
		// this.buuton_config=(Button)this.findViewById(R.id.button_config);
		 this.button_log=(Button)this.findViewById(R.id.button_config);
		 this.button_log.setOnClickListener(this);
		this.checkboxAp = (CheckBox) this.findViewById(R.id.checkBoxAp);
		this.checkboxRp = (CheckBox) this.findViewById(R.id.checkBoxRp);
		this.checkboxVAp = (CheckBox) this.findViewById(R.id.checkBoxVAp);

		this.loadinfodb = new LoadInfoDB(this); // 相当于获得了数据库操作的封装体
		// SQLiteDatabase sqldb=this.loadinfodb.getWritableDatabase();
		// loadinfodb.onUpgrade(sqldb, 1, 1); //保证数据库中loadinfo表中有我们需要的信息

		 btnBack.setOnClickListener(this);
		 this.btnBack.setOnClickListener(this);
		this.checkboxAp.setOnClickListener(this);
		this.checkboxRp.setOnClickListener(this);
		this.checkboxVAp.setOnClickListener(this);

		apSeries = new SimpleXYSeries("Active Power");
		//plot.setDomainLabelWidget(new DomainLabelWidget(plot, new SizeMetrics(20f,SizeLayoutType.ABSOLUTE,40f,SizeLayoutType.ABSOLUTE, TextOrientationType.HORIZONTAL), null);
		
		
		apSeries.useImplicitXVals();
		rpSeries = new SimpleXYSeries("Reactive Power");
		rpSeries.useImplicitXVals();
		this.VApSeries = new SimpleXYSeries("Aparrant Power");
		VApSeries.useImplicitXVals();
		plot.setTitle("Power Plot");
		
		 
		this.checkboxAp.setChecked(true);
		this.checkboxRp.setChecked(true);
		this.checkboxVAp.setChecked(true);
		plot.setRangeBoundaries(-200, 300, BoundaryMode.FIXED); // 改这个属性可以调整纵坐标的一些性质
		plot.setDomainBoundaries(0, HistorySize, BoundaryMode.FIXED);
		plot.addSeries(apSeries,
				new LineAndPointFormatter(Color.rgb(100, 100, 200),
						Color.BLACK, null));
		plot.addSeries(rpSeries, new LineAndPointFormatter(
				Color.rgb(0, 100, 0), Color.BLUE, null));
		plot.addSeries(this.VApSeries,
				new LineAndPointFormatter(Color.rgb(200, 100, 0), Color.RED,
						null));

		plot.setDomainStepValue(2);
		plot.setTicksPerRangeLabel(3);
		plot.setDomainLabel("Time(S)");
		plot.getDomainLabelWidget().pack();
		plot.setRangeLabel("Active/Reactive/Apparant power(W/VAR)"); // 设置纵坐标轴
		plot.getRangeLabelWidget().pack(); // pack干什么用的????

		final PlotStatistics histStats = new PlotStatistics(1000, false);
		plot.addListener(histStats); // ?这个监听机制干什么的？

		handler = new Handler() {

			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				Bundle data = msg.getData();
				if (data.getString("type") == null) {
					return;
				}

				if (data.getString("type").equals("refresh")) {

					// get rid the oldest sample in history:
					if (apSeries.size() >= HistorySize) {
						apSeries.removeFirst();
						rpSeries.removeFirst();
						VApSeries.removeFirst();
						// plot.setDomainBoundaries(firstdatatime,
						// HistorySize+firstdatatime, BoundaryMode.FIXED);
						// firstdatatime+=0.5;
					}

					// add the latest history sample:
					// 把新获得的点加进去
					apSeries.addLast(null, intAP);
					rpSeries.addLast(null, intRP);
					VApSeries.addLast(null, intVAP);

					// 接下来:对纵坐标进行处理
					double miny = 0;
					double maxy = 0;
					for (int i = 0; i < apSeries.size(); i++) {
						double temppower = (Double) apSeries.getY(i);
						if (temppower < miny)
							miny = temppower;
						else if (temppower > maxy)
							maxy = temppower;
					}
					for (int i = 0; i < rpSeries.size(); i++) {
						double temppower = (Double) rpSeries.getY(i);
						if (temppower < miny)
							miny = temppower;
						else if (temppower > maxy)
							maxy = temppower;
					}
					for (int i = 0; i < VApSeries.size(); i++) {
						double temppower = (Double) VApSeries.getY(i);
						if (temppower < miny)
							miny = temppower;
						else if (temppower > maxy)
							maxy = temppower;
					}
					plot.setRangeBoundaries((miny / 100 - 1) * 100,
							(maxy / 100 + 1) * 100, BoundaryMode.FIXED);

					// redraw the Plots:
					plot.redraw(); // 然后画出来

				} else if (data.getString("type").equals("loadcheck")) {
					addLoadChangingNotice(data.getDouble("loadinAp"),
							data.getDouble("loadinRp"));
					Log.i("loadinAp",
							String.valueOf(data.getDouble("loadinAp")));
					Log.i("loadinRp",
							String.valueOf(data.getDouble("loadinRp")));
				} else if (data.getString("type").equals("stop")) {
					get_data.isRunning = false;
				}
			}
		};

		get_data = new Get_Data();
		get_data.start();

	}

	public void addLoadChangingNotice(double loadinAp, double loadinRp) { // 告诉读入了多少大小的负载变化

		// 需要处理缓慢变化的情况

		if (loadinAp >= LoadChangeBoundary) {
			this.confirmsteady = false;
			if (this.checkloadin == false) // 表示刚有电器接入的情况,要对电器信息进行重新的创建

			{
				loadinfo.init(); // 初始化一些参数
				this.checkloadin = true;
				TextView view = new TextView(this);
				Date date = new Date();
				view.setPadding(20, 1, 20, 1);
				view.setText("At " + date.toLocaleString()
						+ " detected load in");
				view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				this.linearLayout.addView(view);
				this.linearLayout.invalidate(); // 使得无效

			}
			loadinfo.plus(loadinAp, loadinRp); // 对于变化的负载的信息进行处理
			this.LoadChangeBoundary = Math
					.abs(loadinfo.getEverytimeChangeAp() / 15);
			// Math.abs(loadinfo.getAp())/15; //动态地进行变化以得以符合大小功率不一的用电器
			// 如果抖动控制得好的话 可以把15分之1改得更小
			this.LoadChangeBoundary = Math.max(this.LoadChangeBoundary,
					this.LoadChangeBase);
		}

		else if (loadinAp <= -LoadChangeBoundary) {
			this.confirmsteady = false;
			if (this.checkloadout == false) // 表示刚有电器断开的情况,要对电器信息进行重新的创建

			{
				loadinfo.init();
				this.checkloadout = true;
				TextView view = new TextView(this);
				Date date = new Date();
				view.setPadding(20, 1, 20, 1);
				view.setText("At " + date.toLocaleString()
						+ " detected load out");
				view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				this.linearLayout.addView(view);
				this.linearLayout.invalidate(); // 使得无效

			}
			loadinfo.plus(loadinAp, loadinRp); // 对于变化的负载的信息进行处理
			// this.LoadChangeBoundary=Math.abs(loadinfo.getAp())/15;
			this.LoadChangeBoundary = Math
					.abs(loadinfo.getEverytimeChangeAp() / 15);
			this.LoadChangeBoundary = Math.max(this.LoadChangeBoundary,
					this.LoadChangeBase);
		}

		else // 针对的是基本稳定后的情况
		{

			if (this.checkloadin == true) // 表示之前的接入的负载可能已经达到了稳定的情况，但还需要2次判断
			{
				if (this.confirmsteady == true) {
					TextView view = new TextView(this);
					// view.setGravity(Gravity.CENTER_HORIZONTAL);
					view.setPadding(20, 1, 20, 1);
					view.setText("in device is:"
							+ matchload(Double.parseDouble(df.format(loadinfo
									.getAp())), Double.parseDouble(df.format(
									loadinfo.getRp()).toString())) + "\nAp:"
							+ df.format(loadinfo.getAp()) + "W;Rp:"
							+ df.format(loadinfo.getRp()) + "W");
					view.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					this.linearLayout.addView(view);
					this.linearLayout.invalidate(); // 使得无效
					this.checkloadin = false;
					// this.LoadChangeBoundary=this.LoadChangeBase;

				} else {
					this.confirmsteady = true;// 表示第一次进入了有可能出现稳态的点
				}
			} else if (this.checkloadout == true) {
				if (this.confirmsteady == true) {
					TextView view = new TextView(this);
					view.setPadding(20, 1, 20, 1);
					view.setText("out device is:"
							+ matchload(Double.parseDouble(df.format(-loadinfo
									.getAp())), Double.parseDouble(df.format(
									-loadinfo.getRp()).toString())) + "\nAp:"
							+ df.format(-loadinfo.getAp()) + "W;Rp :"
							+ df.format(-loadinfo.getRp()) + "W");
					view.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					this.linearLayout.addView(view);
					this.linearLayout.invalidate(); // 使得无效
					this.checkloadout = false;
					this.LoadChangeBoundary = this.LoadChangeBase;
				} else {
					this.confirmsteady = true;// 表示第一次进入了有可能出现稳态的点
				}
			}

		}
		return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_load_check, menu);
		return true;
	}

	class Get_Data extends Thread {
		boolean isRunning = true;
		int timer = 0;

		// **
		// 线程体代码
		// *
		@Override
		public void run() {

			Queue<Double> hisApQueue = new ArrayDeque<Double>();
			Queue<Double> hisRpQueue = new ArrayDeque<Double>();
			boolean firstin = true; // 判断是否是第一次进入

			double hisAp = 0;
			double hisRp = 0;

			double tempAp = 0;
			double tempRp = 0;
			double loadinAp = 0;
			double loadinRp = 0;
			GetRatio();
			mutex = true;
			try {
				while (isRunning) {
					try {
						Thread.sleep(TimeInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					connector.connectJamod(connector.addrStr, connector.port,
							connector.unitID, connector.type);
					int devicetype = connector.type;
					if (devicetype == 2)
						connector.connectNonjamod(connector.addrStr,
								connector.port, connector.unitID,
								connector.type);

					// 先把数据读入到tempAp和tempRp 变量中去
					switch (devicetype) {
					case 0:
						try {
							tempAp = Double
									.parseDouble(((GEConnector) (connector))
											.getF7Data(225, 2, 4, PTN, PTD,
													CTN, CTD));
							tempRp = Double
									.parseDouble(((GEConnector) (connector))
											.getF7Data(217, 2, 4, PTN, PTD,
													CTN, CTD));

						} catch (Exception ex) {
							Log.e("ge", "error");

						}
						break;

					case 1:

						try {
							tempAp = Double.parseDouble(connector.getFloatData(
									65, 2));
							tempRp = Double.parseDouble(connector.getFloatData(
									67, 2));
							// data.putString("segmaAO",
							// connector.getFloatData(63, 2));
						} catch (Exception ex) {
							Log.e("semen", "error");

						}
						break;
					case 2: // 也就是为2
						try {
							tempAp = ((ScheiderConnector) connector)
									.getFloatForNonjamod(11729, 1)[0];
							//Log.i("AP", tempAp + "");
							tempRp = ((ScheiderConnector) connector)
									.getFloatForNonjamod(11737, 1)[0];
							//Log.i("RP", tempRp + "");
						} catch (Exception ex) {
							Log.e("scheider", "error");
						}
					}
					// 如果有异常抛出那就赋值为之前的那个数据 这句话的代码我已经删除掉了

					if (firstin == false) {

						hisAp = tempAp * EXPONENTIAL_AVG_RATE
								+ (1 - EXPONENTIAL_AVG_RATE) * hisAp;
						hisRp = tempRp * EXPONENTIAL_AVG_RATE
								+ (1 - EXPONENTIAL_AVG_RATE) * hisRp;
						connector.disconnect();
						if (hisApQueue.size() == 2 && hisRpQueue.size() == 2) {
							double formerAp = hisApQueue.poll();
							double latterAp = hisApQueue.element();
							loadinAp = latterAp - formerAp;
							hisApQueue.add(hisAp);
							double formerRp = hisRpQueue.poll();
							double latterRp = hisRpQueue.element();
							loadinRp = latterRp - formerRp;
							hisRpQueue.add(hisRp);

							// send message to handle update textview
							Bundle data = new Bundle();
							Message msg = new Message();
							data.putDouble("loadinAp", loadinAp);
							data.putDouble("loadinRp", loadinRp);
							data.putString("type", "loadcheck");
							msg.setData(data);
							handler.sendMessage(msg);

						} else {
							hisApQueue.add(hisAp);
							hisRpQueue.add(hisRp);
						}
						intAP = hisAp;
						intRP = hisRp;
						intVAP = Math.sqrt(Math.pow(intAP, 2)
								+ Math.pow(intRP, 2));
						// send message to flush the xyplot
						Bundle data = new Bundle();
						Message msg = new Message();
						data.putString("type", "refresh");
						msg.setData(data);
						handler.sendMessage(msg);
					} else // first time :no history
					{
						firstin = false;
						hisAp = tempAp;
						hisRp = tempRp;
					}
				}
			} catch (Exception ex) {
				Log.e("getinfoerror", "getinfoerror");
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

				CTD = ((Sentron) meter).getSentronSecCurrent();
				PTN = ((Sentron) meter).getSentronPriVoltage();
				PTD = ((Sentron) meter).getSentronSecVoltage();

				connector.disconnect();
			}

		}
	}

	public String matchload(double loadinap, double loadinrp) {

		// if (loadinap > 1800 && loadinap < 1900)
		// return "电热水壶";
		// else if (loadinap > 20 && loadinap < 50) {
		// double absrp = Math.abs(loadinrp);
		// if (absrp > 11.5 && absrp < 20)
		// return "sony笔记本";
		// else if (absrp > 7 && absrp < 11.5)
		// return "小风扇";
		// else
		// return "未知的有功功率在20到50W的电器";
		// } else if (loadinap < 5) {
		// return "出现了毛刺和抖动";
		// } else
		// return "未知的电器";
		this.loadinfodb = new LoadInfoDB(this);
		Cursor cursor = loadinfodb.select(loadinap, loadinrp);
		String name = "";
		while (cursor.moveToNext()) {
			if (name.equals(""))
				name = " " + name + cursor.getString(0);
			else
				name = name + " or : " + cursor.getString(0);

		}
		if (loadinap < 5)
			return "maybe possible unsteady";
		else if (name.equals(""))
			return "unkowned device";
		else
			return name;
	}

	public boolean transbool(boolean bool) {
		if (bool == true)
			return false;
		else
			return true;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == this.button_log) {
			Bundle data = new Bundle();
			Message msg = new Message();
			data.putString("type", "stop");
			msg.setData(data);
			handler.sendMessage(msg); // 让子线程先停下来

			Intent intent = new Intent();
			intent.setClass(this, ShowSQLActivity.class);
			this.startActivity(intent);
			this.finish();
		}
		 if(v==this.button_log)
		 {
		 Bundle data = new Bundle();
		 Message msg = new Message();
		 data.putString("type", "stop");
		 msg.setData(data);
		 handler.sendMessage(msg); //让子线程先停下来
		
		 Intent intent=new Intent();
		 intent.setClass(this, ShowSQLActivity.class);
		 this.startActivity(intent);
		 this.finish();
		 }
		 if (v == btnBack) {
		 Bundle data = new Bundle();
		 Message msg = new Message();
		 data.putString("type", "stop");
		 msg.setData(data);
		 handler.sendMessage(msg);
		
		 Intent myintent = new Intent();
		 myintent.setClass(this, ConnectingActivity.class);
		 this.startActivity(myintent);
		 this.finish();
		
		 }
		else if (v == this.checkboxAp) {
			this.showApstatus = transbool(this.showApstatus);
			if (this.showApstatus == false)
				this.plot.removeSeries(this.apSeries);
			else {
				plot.addSeries(apSeries,

				new LineAndPointFormatter(Color.rgb(100, 100, 200),
						Color.BLACK, null));
				plot.redraw();
			}

		} else if (v == this.checkboxRp) {
			this.showRpstatus = transbool(this.showRpstatus);
			if (this.showRpstatus == false)
				this.plot.removeSeries(this.rpSeries);
			else {
				plot.addSeries(rpSeries,
						new LineAndPointFormatter(Color.rgb(0, 100, 0),
								Color.BLUE, null));
				plot.redraw();
			}
		} else if (v == this.checkboxVAp) {
			this.showVApstatus = transbool(this.showVApstatus);
			if (this.showVApstatus == false)
				this.plot.removeSeries(this.VApSeries);
			else {
				plot.addSeries(this.VApSeries,
						new LineAndPointFormatter(Color.rgb(200, 100, 0),
								Color.RED, null));
				plot.redraw();
				// Log.i("aaaaa", String.valueOf(VApSeries.size()));
			}
		}
	}
}
