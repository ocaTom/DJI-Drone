package com.example.dji_motioncontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.DJIDroneTypeDef.DJIDroneType;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGeneralListener;

public class MainActivity extends Activity {
	private static final String TAG="djiTestActivity";
	private static boolean isStarted = false;
	
	Button DisConnect;
	Button Connect;
	Button MCState;
	Button enter;
	Button GS;
	RadioButton TwoVP;
	RadioButton Three;
	RadioButton M100;
	EditText SampleRate;
	
	TextView ShowState;
	TextView Rate;
	
	private int type = -1;
	//String McStateString = "";
	
	//DJIDrone Drone = new DJIDrone();
	
	/*private DJIMcuUpdateStateCallBack mMcuUpdateStateCallBack = null;
	DJIMainController DJIMC;
	DJIRemoteController DJIRC;
	*/
	private DJIExecuteResultCallback mDJIExecuteResultCallback = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {        
		super.onCreate(savedInstanceState);
		        
		//Intent intent= getIntent();
		Intent intent= getIntent();
		type = intent.getIntExtra("DroneType", 3);
		
		setContentView(R.layout.activity_main);
		Connect = (Button)findViewById(R.id.Connect);
		DisConnect = (Button)findViewById(R.id.Disconnect);
		//MCState = (Button)findViewById(R.id.MCU);
		GS = (Button)findViewById(R.id.GS);
		ShowState = (TextView)findViewById(R.id.ShowState);
		TwoVP = (RadioButton)findViewById(R.id.TwoVP);
		Three = (RadioButton)findViewById(R.id.Three);
		M100 = (RadioButton)findViewById(R.id.M100);
		SampleRate = (EditText)findViewById(R.id.SampleRate);
		enter = (Button)findViewById(R.id.enterSampleRate);
		Rate = (TextView)findViewById(R.id.Rate);
		/*
		mMcuUpdateStateCallBack = new DJIMcuUpdateStateCallBack(){
			public void onResult(DJIMainControllerSystemState state){
				ShowState.setText("state = " + state);
			}
		};*/
		mDJIExecuteResultCallback = new DJIExecuteResultCallback(){
			public void onResult(DJIError mErr){
				Log.e(TAG, "Error = " + mErr.errorDescription);					
			}
		};
		
		Connect.setOnClickListener(new Button.OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				onInitSDK(type);
				SampleRate.requestFocus();
			}
		});
		
		DisConnect.setOnClickListener(new Button.OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				onUnInitSDK();
			}
		});
		
		enter.setOnClickListener(new Button.OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				Rate.setText(SampleRate.getText());
			}
		});
		
		/*MCState.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				String rate = SampleRate.getText().toString();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MainControllerStateActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("rate", rate);;
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});*/
		
		GS.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(type == -1){
					ShowState.setText("Please choose drone type...");
				}
				else{
					String rate = SampleRate.getText().toString();
					String machineType = "" + type;
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, GroundStationActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("rate", rate);
					bundle.putString("type", machineType);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
				
		TwoVP.setOnClickListener(new RadioButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				type = 0;
				ShowState.setText("Choose Phantom 2V+ ....");
			}
		});
		
		Three.setOnClickListener(new RadioButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				type = 2;
				ShowState.setText("Choose Phantom 3 ....");
			}
		});
		
		M100.setOnClickListener(new RadioButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				type = 3;
				ShowState.setText("Choose Phantom M100 ....");
			}
		});
		
		//boolean activation_succ = false;
        new Thread(){
            public void run() {
                try {
                		DJIDrone.checkPermission(getApplicationContext(), new DJIGeneralListener() {                        
                        @Override
                        public void onGetPermissionResult(int result) {
                            // TODO Auto-generated method stub
                            Log.e(TAG, "onGetPermissionResult = "+result);
                            Log.e(TAG, "onGetPermissionResultDescription = "+DJIError.getCheckPermissionErrorDescription(result));
                        }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();        
        
	}
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	@Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        onUnInitSDK();
        super.onDestroy();
    }	    
	
	private void onInitSDK(int type){
		ShowState = (TextView)findViewById(R.id.ShowState);
		
		switch(type){
			case -1:{
				ShowState.setText("Please choose drone type...");
			}
	        case 0 : {
	            DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_Vision);
	            break;
	        }
	        case 1 : {
	            DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_Inspire1);
	            break;
	        }
	        case 2 : {
	            DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_Phantom3_Advanced);
	            break;
	        }
	        case 3 : {
	            DJIDrone.initWithType(this.getApplicationContext(),DJIDroneType.DJIDrone_M100);
	            break;
	        }
	        default : {
	            break;
	        }
		}
        
		//ShowState.setText("Connecting...");
        Boolean state = DJIDrone.connectToDrone();
        
        ShowState.append("\nConnectToDrone :" + state);
    }
    
    private void onUnInitSDK(){
        ShowState = (TextView)findViewById(R.id.ShowState);
        ShowState.setText("Disconnecting...");
        Boolean state = DJIDrone.disconnectToDrone();
        ShowState.setText("DisConnectToDrone :" + state);
    }
}
