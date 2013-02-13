package lt.neworld.arRegistration;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamView extends SurfaceView {
	
	private volatile Camera camera;
	private volatile boolean inited = false;
	
	public CamView(Context context) {
		super(context);
		
		init();
	}

	public CamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public CamView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}
	
	private void init() {
		SurfaceHolder holder = getHolder();

		holder.setFormat(PixelFormat.OPAQUE);
		holder.addCallback(surfaceCallback);
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
		
		if (inited) {
			attachCamera();
		}
	}
	
	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			attachCamera();
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Camera.Parameters params = camera.getParameters();
			params.setPreviewSize(width, height);
			camera.setParameters(params);
		}
	}; 
	
	private void attachCamera() {
		if (camera == null || getHolder() == null)
			return;
		
		try {
			camera.setPreviewDisplay(getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
