package lt.neworld.arRegistration;

import java.io.IOException;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamView extends SurfaceView {
	
	private Camera camera;
	private boolean inited = false;
	
	private Object lockCameraBuffer = new Object();
	private byte[] cameraBuffer = null;
	
	private static final int SCALE_FACTOR = 1;
	
	public int width, height;
	
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
		//setDrawingCacheEnabled(true);
		//setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		SurfaceHolder holder = getHolder();

		//holder.setFormat(PixelFormat.OPAQUE);
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
		public void surfaceChanged(SurfaceHolder holder, int format, int _width, int _height) {
			width = _width / SCALE_FACTOR;
			height = _height / SCALE_FACTOR;
			
			width = 640;
			height = 480;
			
			Camera.Parameters params = camera.getParameters();
			params.setPreviewFormat(ImageFormat.NV21);
			params.setPreviewSize(width, height);
			camera.setParameters(params);
			synchronized (lockCameraBuffer) {
				cameraBuffer = new byte[width * height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];	
			}
		}
	};
	
	private PreviewCallback cameraPreviewCallback = new PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			//Log.d("camera", "previewFrame");
			synchronized (lockCameraBuffer) {
				lockCameraBuffer.notify();
			}
		}
	}; 
	
	private void attachCamera() {
		if (camera == null || getHolder() == null)
			return;
		
		try {
			camera.setPreviewDisplay(getHolder());
			camera.setPreviewCallbackWithBuffer(cameraPreviewCallback);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public byte[] getBitmap() {
		synchronized (lockCameraBuffer) {
			if (cameraBuffer == null)
				return null;
			
			camera.addCallbackBuffer(cameraBuffer);
			try {
				lockCameraBuffer.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return cameraBuffer;
		}
	}
	
	public Point getSize() {
		return new Point(width, height);
	}
	
	public float getScaleX() {
		return (float) getWidth() / width;
	}
	
	public float getScaleY() {
		return (float) getHeight() / height;
	}
}
