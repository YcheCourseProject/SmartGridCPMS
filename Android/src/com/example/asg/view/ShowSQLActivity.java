package com.example.asg.view;

 
 

import com.example.asg.R;
import com.example.asg.services.db.LoadInfoDB;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ShowSQLActivity extends Activity implements OnClickListener {

	private LoadInfoDB  loadinfoDB;			//获得一个封装好的可以进行增删改查的对象
	private Cursor cursor;
	private EditText edittext_record;
	private ListView listview_records;
	private Button button_add;
	private Button button_delete;
	private Button button_modify;
	private int _id;
	private int trans_id;
	private double aplow;
	private double apup;
	private double rplow;
	private double rpup;
	private String devicename;
	
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sql);
        
        
        this.edittext_record=(EditText)this.findViewById(R.id.editText_hint_log_lasttime);
        
        this.listview_records=(ListView)this.findViewById(R.id.listView_detaillist);
        
        
        this.button_add=(Button)this.findViewById(R.id.button_add);
        this.button_add.setFocusable(true);
        this.button_add.requestFocus();
        this.button_add.setFocusableInTouchMode(true);
        
        this.button_delete=(Button)this.findViewById(R.id.button_delete);
        this.button_modify=(Button)this.findViewById(R.id.button_modify);
        
        this.button_add.setOnClickListener(this);
        this.button_delete.setOnClickListener(this);
        this.button_modify.setOnClickListener(this);
  
        this.button_delete.setEnabled(false);
        this.button_modify.setEnabled(false);
        this.button_add.setEnabled(true);
        
        this.loadinfoDB=new LoadInfoDB(this);
        this.cursor=loadinfoDB.select();
        
        
        Bundle bundle=this.getIntent().getExtras();
        String type="";
//        Log.i("c",String.valueOf( bundle==null));
        if(bundle!=null)
        {
        	
        	type=bundle.getString("type");
//        	Log.i("check", "识别出什么意图影响到了当前页面");
//        	Log.i("type", type);
        }
        if(type.equals("add"))
        {
        	String name=bundle.getString("name");
        	double aplow=bundle.getDouble("aplow");
        	double apup=bundle.getDouble("apup");
        	double rplow=bundle.getDouble("rplow");
        	double rpup=bundle.getDouble("rpup");
        	
        	this.handleAdd(name, aplow, apup, rplow, rpup);
        }
        else if(type.equals("modify"))
        {
        	int id=bundle.getInt("id");
        	String name=bundle.getString("name");
        	double aplow=bundle.getDouble("aplow");
        	double apup=bundle.getDouble("apup");
        	double rplow=bundle.getDouble("rplow");
        	double rpup=bundle.getDouble("rpup");
        	this.handleModify(id, name, aplow, apup, rplow, rpup);
        	Log.i("idididid", String.valueOf(id));
        }
        else
        {
        	//什么都不做
        }
        
        
        String[]from={LoadInfoDB.FIELD_ID,LoadInfoDB.FIELD_NAME,LoadInfoDB.FIELD_aplow
        		,LoadInfoDB.FIELD_apup,LoadInfoDB.FIELD_rplow,LoadInfoDB.FIELD_rpup};      
        //添加一个适配器   用来绑定cursor和listview视图
        //@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter=
        		new SimpleCursorAdapter
        		(this,R.layout.list,this.cursor,from,new int[]{
        				R.id.textView_column1,R.id.textView_column2,
        				R.id.textView_column3,R.id.textView_column4,
        				R.id.textView_column5,R.id.textView_column6});
        this.listview_records.setAdapter(adapter);		//以上两个步骤将结果集和Listview绑定
        
//        Log.i("crate ","能创建了适配器");
        
                
        
        
        
        this.listview_records.setOnItemClickListener
        (new AdapterView.OnItemClickListener() 
        {
        	public void onItemClick(AdapterView<?> arg0,View arg1 ,int arg2 ,long arg3)
        	{													//arg3对应的是id
        		
//				Log.i("click", ""+arg2);
        		cursor.moveToPosition(arg2);					//arg2对应的是postion
        		_id=cursor.getInt(0);
           		Log.i("idclick", _id+""+"click:"+arg2);
        		edittext_record.setText("device name:\r"+cursor.getString(1)+";\naplowbound:"+
        		String.valueOf(cursor.getDouble(2))+";\rapupbound:"+String.valueOf(cursor.getDouble(3))+
        		";\nrplowbound:"+String.valueOf(cursor.getDouble(4))
        		+";\rrpupbound:"+String.valueOf(cursor.getDouble(5)));
        		trans_id=cursor.getInt(0);
        		devicename=cursor.getString(1);
        		aplow=cursor.getDouble(2);
        		apup=cursor.getDouble(3);
        		rplow=cursor.getDouble(4);
        		rpup=cursor.getDouble(5);
        		
        		button_delete.setEnabled(true);
                button_modify.setEnabled(true);
        	}
		});
        
        this.listview_records.setOnItemSelectedListener
        (new AdapterView.OnItemSelectedListener() 
        {
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				Log.i("select", ""+arg2);
        		cursor.moveToPosition(arg2);
        		_id=cursor.getInt(0);
        		Log.i("idclick", _id+"");
        		edittext_record.setText("设备名:"+cursor.getString(1)+";有功功率下限:"+
        		String.valueOf(cursor.getDouble(2))+";有功功率上限:"+String.valueOf(cursor.getDouble(3))+
        		";无功功率下限:"+String.valueOf(cursor.getDouble(4))
        		+";无功功率上限:"+String.valueOf(cursor.getDouble(5)));
				// TODO Auto-generated method stub
        		trans_id=cursor.getInt(0);
        		devicename=cursor.getString(1);
        		aplow=cursor.getDouble(2);
        		apup=cursor.getDouble(3);
        		rplow=cursor.getDouble(4);
        		rpup=cursor.getDouble(5);
        		button_delete.setEnabled(true);
                button_modify.setEnabled(true);
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
        //Log.i("create", "创建好了监听机制");
        
        //之后是对有可能要进行数据更新的情况做处理           包括有 更新的类型
       
//        Log.i("create", "全创建好了");
        
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	 
    	 this.button_add.setFocusable(true);
         this.button_add.requestFocus();
         this.button_add.setFocusableInTouchMode(true);
		super.onResume();
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_sql, menu);
        return true;
    }
 
    public void handleModify(int id,String name,double aplow,double apup,double rplow,double rpup)
    {
    	
    	this.loadinfoDB.update(id, name, aplow, apup, rplow, rpup);
    	this.cursor.requery();							//重新查询一下
    	this.listview_records.invalidateViews();		//根据新的cursor里面的值来进行重画
    	this.edittext_record.setText("");
    	this._id=0;
    	Log.i("success", "更新成功");
    }
    public void gotomodify()
    {
		if(this._id==0)
			return;
		Intent intent=new Intent();
		intent.setClass(this, ModifySQLDataActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("operateType", "modify");
		bundle.putString("id",""+this.trans_id);
		Log.i("idid!!!!!!!!", cursor.getString(0));
		bundle.putString("name", this.devicename);
		bundle.putString("aplow", String.valueOf(this.aplow));
		bundle.putString("apup", String.valueOf(this.apup));
		bundle.putString("rplow", String.valueOf(this.rplow));
		bundle.putString("rpup", String.valueOf(this.rpup));
		intent.putExtras(bundle);
		this.startActivity(intent);
		this.finish();
    }
    public void handleAdd(String name,double aplow,double apup,double rplow,double rpup)
    {
    	this.loadinfoDB.insert(name, aplow, apup, rplow, rpup);
    	this.cursor.requery();		//重新查询一下
    	this.listview_records.invalidateViews();
    	this.edittext_record.setText("");
    	this._id=0;
    }

    public void delete()
    {
		if(_id==0)			//表示没有选中
			return;
		this.loadinfoDB.delete(this._id);
		this.cursor.requery();
		this.listview_records.invalidateViews();
		this.edittext_record.setText("");
		_id=0;			//因为删除后任然在原来的页面所以需要把_id设置为0
    }
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==this.button_add)
		{
			Intent intent=new Intent();
			intent.setClass(this, ModifySQLDataActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("operateType", "add");
//			bundle.putString("id",this.cursor.getString(0));
//			bundle.putString("name", this.cursor.getString(1));
//			bundle.putString("aplow", String.valueOf(this.cursor.getDouble(2)));
//			bundle.putString("apup", String.valueOf(this.cursor.getDouble(3)));
//			bundle.putString("rplow", String.valueOf(this.cursor.getDouble(4)));
//			bundle.putString("rpup", String.valueOf(this.cursor.getDouble(5)));
//			intent.putExtras(bundle);
			intent.putExtras(bundle);
			this.startActivity(intent);
			this.finish();

		}
		else  if(v==this.button_delete)
		{
			this.delete();
			this.button_modify.setEnabled(false);
			this.button_delete.setEnabled(false);
		}
		else if(v==this.button_modify)
		{
			this.gotomodify();
		}
//		else if(v==this.button_ret)
//		{
//			Intent intent=new Intent();
//			intent.setClass(this, LoadCheckActivity.class);
//			this.startActivity(intent);
//			this.finish();
//		}
	}
}
