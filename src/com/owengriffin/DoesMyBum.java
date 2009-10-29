package com.owengriffin;

import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class DoesMyBum extends Activity implements SurfaceHolder.Callback, OnClickListener {

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private boolean mPreviewRunning;
	private Camera mCamera;
	private TextView mTextView;
	private TextView mResultTextView;
	private int step = 0;
	
	private String[] results = new String[] {
			"You look great!",
			"Wow. That's really you.",
			"It makes you look just like your mother",
			"Ahhh...",
			"Is it supposed to look like that?"
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startup);
		findViewById(R.id.startbutton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DoesMyBum.this.startAnalysis();	
			}
		});
	}
	
	private void startAnalysis() {
		this.step = 0;
		setContentView(R.layout.main);

		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mTextView = (TextView) findViewById(R.id.instruction);
		
		mSurfaceHolder = mSurfaceView.getHolder();

		mSurfaceHolder.addCallback(this);

		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		Button mButton = (Button) findViewById(R.id.takephoto);
		mButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		if (this.step <= 0) {
			this.mTextView.setText(R.string.instruction0);
//			this.mCamera.takePicture(null, null, new Camera.PictureCallback() {
//				
//				public void onPictureTaken(byte[] imageData, Camera c) {
//					Log.d(this.getClass().getName(), "Taking a photo.");
//				}
//			});
		} else if (this.step == 1) {
			this.mTextView.setText(R.string.instruction1);
		} else if (this.step == 2) {
			this.mTextView.setText(R.string.instruction2);
		} else {
			//Toast.makeText(DoesMyBum.this, "Analysing..", Toast.LENGTH_LONG).show();
			this.mCamera.stopPreview();
			
			try{ Thread.sleep(1000); }catch(InterruptedException e){ } 
			this.showResult();
		}
		this.incrementStep();
	}
	
	private void showResult() {
		this.setContentView(R.layout.result);
		
		Random random = new Random();
		int index = random.nextInt(this.results.length);
		Log.v("Index", index + "");
		this.mResultTextView = (TextView) findViewById(R.id.resulttext);
		this.mResultTextView.setText("\"" + this.results[index] + "\"");
		findViewById(R.id.resetbutton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DoesMyBum.this.startAnalysis();	
			}
		});
	}
	
	private void incrementStep() {
		this.step ++;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(width, height);
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		mPreviewRunning = true;

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		 mCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}
}