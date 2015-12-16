package com.example.dji_motioncontrol;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.GroundStation.DJIGroundStationTask;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationFlightMode;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.api.MainController.DJIMainControllerTypeDef.DJIMcErrorType;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGroundStationExecutionPushInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationFlyingInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationMissionPushInfoCallBack;
import dji.sdk.interfaces.DJIMcuErrorCallBack;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;

public class MainControllerStateActivity extends Activity {

	private String McStateString = "";
	private String McErrorString = "";
	private static final String TAG="MainControllerStateActivity";
	private FileOutputStream output;
	private Date date;
	private String OutputName;
	private String rate = "";
	public boolean isFly = false;
	
	private TextView SampleRate;
	private TextView mcState;
	
	private DJIMcuUpdateStateCallBack mMcuUpdateStateCallBack = null;
	private DJIMcuErrorCallBack mMcuErrorCallBack = null;
	private DjiGLSurfaceView mDjiGLSurfaceView;
    private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;
    private DJIGroundStationFlyingInfoCallBack mGroundStationFlyingInfoCallBack = null;
    private DJIGroundStationMissionPushInfoCallBack mGroundStationMissionPushInfoCallBack = null;
    private DJIGroundStationExecutionPushInfoCallBack mGroundStationExecutionPushInfoCallBack = null;
    
    private GroundStationFlightMode flightMode;
    private ScrollView mGroundStationInfoScrollView;
    private DJIGroundStationTask mTask;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maincontrollerstate);
		Bundle bundle0 = this.getIntent().getExtras();
		rate = bundle0.getString("rate");
		SampleRate = (TextView)findViewById(R.id.SampleRate);
		SampleRate.setText("Current Sampling rate = "+rate);
		
		date = new Date(System.currentTimeMillis());
		OutputName = "/sdcard/"+date.toString()+"_output.txt";
		
		mcState = (TextView)findViewById(R.id.mcState);
		
		mcState.setText("runing...");
		Log.e(TAG, "MainControllerState");
		
		mMcuUpdateStateCallBack = new DJIMcuUpdateStateCallBack(){
            @Override
            public void onResult(DJIMainControllerSystemState state) {
                // TODO Auto-generated method stub     
            	//Log.e(TAG, "mMcuUpdateStateCallBack");
            	//Log.e(TAG, "isFlying=" + state.isFlying);
            	isFly = state.isFlying;
                StringBuffer sb = new StringBuffer();   
                //sb.append(getString(R.string.mcstate)).append("\n");
                sb.append("satelliteCount=").append(state.satelliteCount).append("\n");
                sb.append("homeLocationLatitude=").append(state.homeLocationLatitude).append("\n");
                sb.append("homeLocationLongitude=").append(state.homeLocationLongitude).append("\n");
                sb.append("droneLocationLatitude=").append(state.droneLocationLatitude).append("\n");
                sb.append("droneLocationLongitude=").append(state.droneLocationLongitude).append("\n");
                sb.append("velocityX=").append(state.velocityX).append("\n");
                sb.append("velocityY=").append(state.velocityY).append("\n");
                sb.append("velocityZ=").append(state.velocityZ).append("\n");
                sb.append("speed=").append(state.speed).append("\n");      
                sb.append("altitude=").append(state.altitude).append("\n");
                sb.append("pitch=").append(state.pitch).append("\n");
                sb.append("roll=").append(state.roll).append("\n");
                sb.append("yaw=").append(state.yaw).append("\n");
                //sb.append("remainPower=").append(state.remainPower).append("\n");
                //sb.append("remainFlyTime=").append(state.remainFlyTime).append("\n");
                //sb.append("powerLevel=").append(state.powerLevel).append("\n");
                //sb.append("isFlying=").append(state.isFlying).append("\n");
                //sb.append("noFlyStatus=").append(state.noFlyStatus).append("\n");
                //sb.append("noFlyZoneCenterLatitude=").append(state.noFlyZoneCenterLatitude).append("\n");
                //sb.append("noFlyZoneCenterLongitude=").append(state.noFlyZoneCenterLongitude).append("\n");
                //sb.append("noFlyZoneRadius=").append(state.noFlyZoneRadius);

                McStateString = sb.toString();
  
                MainControllerStateActivity.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() 
                    {   
                        mcState.setText(McStateString);
                        
						//Log.e(TAG , McStateString);
                        //if(isFly){
                        	try{                            	                           	
                            	output = new FileOutputStream(OutputName, true);
                            	String currentTime = String.valueOf(System.currentTimeMillis());
                            	output.write("currentTime=".getBytes());
                            	output.write(currentTime.getBytes());
                            	output.write("\n".getBytes());
                            	output.write(McStateString.getBytes());    
                            	output.write("end".getBytes());
                            	output.write("\n".getBytes());
                            }
                            catch(IOException e){
                            	
                            }
                        //}
                    }
                });
            }
		};
		
		mMcuErrorCallBack = new DJIMcuErrorCallBack(){
            @Override
            public void onError(DJIMcErrorType error) {
                // TODO Auto-generated method stub
            	//Log.e(TAG, "mMcuErrorCallBack ");
                StringBuffer sb = new StringBuffer();   
                sb.append("main_controller_error").append("\n");
                sb.append(error.toString());

                McErrorString = sb.toString();
  
                MainControllerStateActivity.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() 
                    {   
                        //mMainControllerErrorBtn.setText(McErrorString);
                        Log.e(TAG, McErrorString);
                    }
                });
            }
            
        };
                        	
		DJIDrone.getDjiMC().setMcuUpdateStateCallBack(mMcuUpdateStateCallBack);
        DJIDrone.getDjiMC().setMcuErrorCallBack(mMcuErrorCallBack);
	}

	@Override
	protected void onResume(){
// TODO Auto-generated method stub
        DJIDrone.getDjiMC().startUpdateTimer(Integer.parseInt(rate));
        Log.e(TAG, "MainControllerVersion = " + DJIDrone.getDjiMC().getMcuVersion());
        super.onResume();
	}
	
	@Override
	protected void onPause(){
		DJIDrone.getDjiMC().stopUpdateTimer();
		try{
			date = new Date(System.currentTimeMillis());
			output.write(date.toString().getBytes());
			output.close();
		}
		catch(IOException e){
			
		}
		super.onPause();
	}
	
	@Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
		DJIDrone.getDjiMC().stopUpdateTimer();
		try{
			date = new Date(System.currentTimeMillis());
			output.write(date.toString().getBytes());
			output.close();
		}
		catch(IOException e){
			
		}
        super.onDestroy();
    }	    
	
}
