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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIError;
import dji.sdk.api.GroundStation.DJIGroundStationFlyingInfo;
import dji.sdk.api.GroundStation.DJIGroundStationMissionPushInfo;
import dji.sdk.api.GroundStation.DJIGroundStationTask;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationFlightMode;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationResult;
import dji.sdk.api.GroundStation.DJIGroundStationTypeDef.GroundStationWayPointExecutionState;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.api.MainController.DJIMainControllerTypeDef.DJIMcErrorType;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIGroundStationExecuteCallBack;
import dji.sdk.interfaces.DJIGroundStationExecutionPushInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationFlyingInfoCallBack;
import dji.sdk.interfaces.DJIGroundStationMissionPushInfoCallBack;
import dji.sdk.interfaces.DJIMcuErrorCallBack;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;
import dji.sdk.interfaces.DJIReceivedVideoDataCallBack;
import dji.sdk.widget.DjiGLSurfaceView;

public class GroundStationActivity extends Activity {
	
	private TextView ShowState;
	private TextView GSstate;
	private TextView Error;
	private EditText pitchAngle; 
	private EditText rollAngle; 
	private EditText yawAngle; 
	private EditText exeTime; 
	private Button Go;
	private Button OpenGS;
	private Button UP;
	private Button DOWN;
	private Button OpenMotor;
	
	private static final String TAG="GroundStationActivity";
	private String rate = "";
	private FileOutputStream output;
	private Date date;
	private String OutputName;
	private String McStateString = "";
	private String McErrorString = "";
	private String GsInfoString = "";
    private String GsInfoString1 = "";
    
	private DJIMcuUpdateStateCallBack mMcuUpdateStateCallBack = null;
	private DJIMcuErrorCallBack mMcuErrorCallBack = null;
	private DjiGLSurfaceView mDjiGLSurfaceView;
    private DJIReceivedVideoDataCallBack mReceivedVideoDataCallBack = null;
    private DJIGroundStationFlyingInfoCallBack mGroundStationFlyingInfoCallBack = null;
    private DJIGroundStationMissionPushInfoCallBack mGroundStationMissionPushInfoCallBack = null;
    private DJIGroundStationExecutionPushInfoCallBack mGroundStationExecutionPushInfoCallBack = null;
    
    private GroundStationFlightMode flightMode;
    private DJIGroundStationTask mTask;
    
    private static final int NAVI_MODE_ATTITUDE = 0;
    private static final int NAVI_MODE_WAYPOINT = 1;
    private static final int EXECUTION_STATUS_UPLOAD_FINISH = 0;
    private static final int EXECUTION_STATUS_FINISH = 1;
    private static final int EXECUTION_STATUS_REACH_POINT = 2;
	
	private float pitch;
	private float roll;
	private float yaw;
	private float time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ground_station);
		
		Bundle bundle0 = this.getIntent().getExtras();
		rate = bundle0.getString("rate");
		date = new Date(System.currentTimeMillis());
		OutputName = "/sdcard/"+date.toString()+"_output.txt";
		
		ShowState = (TextView)findViewById(R.id.ShowState);
		GSstate = (TextView)findViewById(R.id.GSstate);
		Error = (TextView)findViewById(R.id.Error);
		
		pitchAngle = (EditText)findViewById(R.id.pitchAngle);
		rollAngle = (EditText)findViewById(R.id.rollAngle);
		yawAngle = (EditText)findViewById(R.id.yawAngle);
		exeTime = (EditText)findViewById(R.id.exeTime);
		Go = (Button)findViewById(R.id.go);
		OpenGS = (Button)findViewById(R.id.OpenGS);
		OpenMotor = (Button)findViewById(R.id.OpenMotor);
		UP = (Button)findViewById(R.id.UP);
		DOWN = (Button)findViewById(R.id.DOWN);
		
		Go.setOnClickListener(new Button.OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				pitch = Float.parseFloat(pitchAngle.getText().toString());
				roll = Float.parseFloat(rollAngle.getText().toString());
				yaw = Float.parseFloat(yawAngle.getText().toString());
				time = Float.parseFloat(exeTime.getText().toString());
				
			}
		});
		
		OpenGS.setOnClickListener(new Button.OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				DJIDrone.getDjiGroundStation().openGroundStation(new DJIGroundStationExecuteCallBack(){

                    @Override
                    public void onResult(GroundStationResult result) {
                        // TODO Auto-generated method stub
                        String ResultsString = "return code =" + result.toString();
                        Error.setText(ResultsString);
                        Log.e(TAG, ResultsString);
                    }
                    
                });
			}
		});
		
		OpenMotor.setOnClickListener(new Button.OnClickListener() {
			@Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
				/*
				DJIDrone.getDjiGroundStation().oneKeyFly(new DJIGroundStationExecuteCallBack(){
        			@Override
        			public void onResult(DJIGroundStationTypeDef.GroundStationResult result){
        				//Log.e(TAG, "Error = " + mErr.errorDescription);
        				Error.setText(result.toString());
        			}
        		});*/
				
				DJIDrone.getDjiMC().turnOnMotor(new DJIExecuteResultCallback(){
					@Override
					public void onResult(DJIError mErr){
						Error.setText(mErr.errorDescription);
					}
				});
				
			}
		});
		
		UP.setOnTouchListener(new Button.OnTouchListener(){
			 
			@Override
			public boolean onTouch(View v,MotionEvent event){
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){  //按下的時候
					DJIGroundStationExecuteCallBack mCallBack = new DJIGroundStationExecuteCallBack(){
						@Override
						public void onResult(DJIGroundStationTypeDef.GroundStationResult result){
							Log.e(TAG, result.toString());
							Error.setText(result.toString());
						}
					};
					DJIDrone.getDjiGroundStation().setAircraftThrottle( 1, mCallBack);
		        }  
		        if (event.getAction() == MotionEvent.ACTION_UP) {  //起來的時候
		        	DJIGroundStationExecuteCallBack mCallBack = new DJIGroundStationExecuteCallBack(){
						@Override
						public void onResult(DJIGroundStationTypeDef.GroundStationResult result){
							Log.e(TAG, result.toString());
							Error.setText(result.toString());
						}
					};
					DJIDrone.getDjiGroundStation().setAircraftThrottle( 0, mCallBack);
		        }
		        return false;
			}
		});
		
		DOWN.setOnTouchListener(new Button.OnTouchListener(){
			 
			@Override
			public boolean onTouch(View v,MotionEvent event){
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){  //按下的時候
					DJIGroundStationExecuteCallBack mCallBack = new DJIGroundStationExecuteCallBack(){
						@Override
						public void onResult(DJIGroundStationTypeDef.GroundStationResult result){
							Log.e(TAG, result.toString());
							Error.setText(result.toString());
						}
					};
					DJIDrone.getDjiGroundStation().setAircraftThrottle( 2, mCallBack);
		        }  
		        if (event.getAction() == MotionEvent.ACTION_UP) {  //起來的時候
		        	DJIGroundStationExecuteCallBack mCallBack = new DJIGroundStationExecuteCallBack(){
						@Override
						public void onResult(DJIGroundStationTypeDef.GroundStationResult result){
							Log.e(TAG, result.toString());
							Error.setText(result.toString());
						}
					};
					DJIDrone.getDjiGroundStation().setAircraftThrottle( 0, mCallBack);
		        }
		        return false;
			}
		});		
		
		mMcuUpdateStateCallBack = new DJIMcuUpdateStateCallBack(){
            @Override
            public void onResult(DJIMainControllerSystemState state) {
                // TODO Auto-generated method stub     
            	//Log.e(TAG, "mMcuUpdateStateCallBack");
            	//Log.e(TAG, "isFlying=" + state.isFlying);
            	//isFly = state.isFlying;
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
                sb.append("remainPower=").append(state.remainPower).append("\n");
                sb.append("remainFlyTime=").append(state.remainFlyTime).append("\n");
                sb.append("powerLevel=").append(state.powerLevel).append("\n");
                sb.append("isFlying=").append(state.isFlying).append("\n");
                sb.append("noFlyStatus=").append(state.noFlyStatus).append("\n");
                sb.append("noFlyZoneCenterLatitude=").append(state.noFlyZoneCenterLatitude).append("\n");
                sb.append("noFlyZoneCenterLongitude=").append(state.noFlyZoneCenterLongitude).append("\n");
                sb.append("noFlyZoneRadius=").append(state.noFlyZoneRadius);

                McStateString = sb.toString();
  
                GroundStationActivity.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() 
                    {   
                        //ShowState.setText(McStateString);
                        
						//Log.e(TAG , McStateString);
                        //if(isFly){
                        	/*try{                            	                           	
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
                            	
                            }*/
                        //}
                    }
                });
            }
		};
				
		DJIDrone.getDjiMC().setMcuUpdateStateCallBack(mMcuUpdateStateCallBack);
        //DJIDrone.getDjiMC().setMcuErrorCallBack(mMcuErrorCallBack);
        
        mGroundStationFlyingInfoCallBack = new DJIGroundStationFlyingInfoCallBack(){
			@Override
			public void onResult(DJIGroundStationFlyingInfo flyingInfo) {
				// TODO Auto-generated method stub
				String Info = "";
				Info = Info + "GPS Status:" + flyingInfo.gpsStatus + "\n";
				Info = Info + "altitude:" + flyingInfo.altitude + "\n";
				Info = Info + "pitch:" + flyingInfo.pitch + "\n";
				Info = Info + "roll:" + flyingInfo.roll + "\n";
				Info = Info + "yaw:" + flyingInfo.yaw + "\n";
				
				GsInfoString1 = Info;
				GroundStationActivity.this.runOnUiThread(new Runnable(){
					@Override
					public void run(){
						GSstate.setText(GsInfoString1);
					}
				});
				
				//Log.e(TAG,flyingInfo.gpsStatus);
				//Log.e(TAG, "DJIGroundStationFlyingInfo homeLocationLatitude " +flyingInfo.homeLocationLatitude);
            	//Log.e(TAG, "DJIGroundStationFlyingInfo homeLocationLongitude " +flyingInfo.homeLocationLongitude);	            	
			}
        };
        DJIDrone.getDjiGroundStation().setGroundStationFlyingInfoCallBack(mGroundStationFlyingInfoCallBack);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ground_station, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume(){
// TODO Auto-generated method stub
        DJIDrone.getDjiMC().startUpdateTimer(Integer.parseInt(rate));
        Log.e(TAG, "MainControllerVersion = " + DJIDrone.getDjiMC().getMcuVersion());
        
        DJIDrone.getDjiGroundStation().startUpdateTimer(Integer.parseInt(rate));
        super.onResume();
	}
	
	@Override
	protected void onPause(){
		DJIDrone.getDjiMC().stopUpdateTimer();
		DJIDrone.getDjiGroundStation().stopUpdateTimer(); 
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
