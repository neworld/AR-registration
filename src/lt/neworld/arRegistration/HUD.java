package lt.neworld.arRegistration;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HUD extends SurfaceView implements Runnable {

	private volatile boolean run = false;
	
	private int width;
	private int height;
	
	private Paint paintTarget = new Paint();
	private Paint paintFrameRate = new Paint();
	private Paint paintPickedColor = new Paint();
	private Paint paintFeatures = new Paint();
	
	private boolean initState = true;
	
	private float[] targetLines = new float[4*4];
	private static final int TARGET_LINES_LENGTH = 40;
	private static final int TARGET_SPACE_FROM_CENTER = 10;
	
	private int frameRate = 0;
	
	private int pickedUpColor = 0;
	private static final Rect pickedUpRect = new Rect(10, 10, 32, 32);
	
	private Bitmap drawBitmapOnCenter = null;
	private Bitmap drawBitmapOnScreen = null;
	
	private List<Feature> features = null;
	
	private CamView camView = null;
	
	public HUD(Context context) {
		super(context);
		
		init();
	}

	public HUD(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public HUD(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	private void init() {
		SurfaceHolder holder = getHolder();

		setZOrderMediaOverlay(true);
		holder.setFormat(PixelFormat.TRANSLUCENT);
		holder.addCallback(surfaceCallback);
		
		paintTarget.setStyle(Paint.Style.STROKE);
		
		paintFrameRate.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFrameRate.setTextSize(25);

		paintFrameRate.setStyle(Paint.Style.FILL);
		paintPickedColor.setTextSize(25);
		
		paintFeatures.setColor(Color.GREEN);
		paintFeatures.setStyle(Paint.Style.STROKE);
	}
	
	private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			run = false;
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (run)
				return;
			
			run = true;
			new Thread(HUD.this).start();
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			HUD.this.width = width;
			HUD.this.height = height;
			
			int middleX = width / 2;
			int middleY = height / 2;
			
			targetLines[0] = middleX - TARGET_SPACE_FROM_CENTER - TARGET_LINES_LENGTH;
			targetLines[2] = middleX - TARGET_SPACE_FROM_CENTER;
			targetLines[4] = middleX + TARGET_SPACE_FROM_CENTER + TARGET_LINES_LENGTH;
			targetLines[6] = middleX + TARGET_SPACE_FROM_CENTER;
			targetLines[1] = targetLines[3] = targetLines[5] = targetLines[7] = middleY;
			
			targetLines[8] = targetLines[10] = targetLines[12] = targetLines[14] = middleX;
			targetLines[9] = middleY - TARGET_SPACE_FROM_CENTER - TARGET_LINES_LENGTH;
			targetLines[11] = middleY - TARGET_SPACE_FROM_CENTER;
			targetLines[13] = middleY + TARGET_SPACE_FROM_CENTER + TARGET_LINES_LENGTH;
			targetLines[15] = middleY + TARGET_SPACE_FROM_CENTER;
		}
	};
	
	private void drawTarget(Canvas canvas) {
		paintTarget.setColor(Color.YELLOW);
		canvas.drawLines(targetLines, paintTarget);
		canvas.drawPoint(width / 2, height / 2, paintTarget);
		
		canvas.save();
		canvas.translate(1, 1);
		paintTarget.setColor(Color.BLACK);
		canvas.drawLines(targetLines, paintTarget);
		canvas.drawPoint(width / 2, height / 2, paintTarget);
		canvas.restore();
	}
	
	private void drawFrameRate(Canvas canvas) {
		canvas.save();
		canvas.translate(1, 1);
		paintFrameRate.setColor(Color.BLACK);
		canvas.drawText(String.format("FPS: %d", frameRate), width - 130, 30, paintFrameRate);
		canvas.restore();
		
		paintFrameRate.setColor(Color.YELLOW);
		canvas.drawText(String.format("FPS: %d", frameRate), width - 130, 30, paintFrameRate);
	}
	
	@Override
	protected final void onDraw(Canvas canvas) {
		synchronized (this) {
			canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

			drawTarget(canvas);
			
			if (!initState)
				drawPickedColor(canvas);
			
			drawFrameRate(canvas);
			
			if (drawBitmapOnCenter != null)
				canvas.drawBitmap(drawBitmapOnCenter, 
						(width - drawBitmapOnCenter.getWidth()) / 2, 
						(height - drawBitmapOnCenter.getHeight()) / 2,
						paintTarget);
			
			if (drawBitmapOnScreen != null) {
				Rect src = new Rect(0, 0, drawBitmapOnScreen.getWidth(), drawBitmapOnScreen.getHeight());
				Rect des = new Rect(0, 0, width, height);
				
				canvas.drawBitmap(drawBitmapOnScreen, src, des, paintTarget);
			}
			
			drawFeatures(canvas);
		}
	}

	private void drawFeatures(Canvas canvas) {
		float scaleX = camView.getScaleX();
		float scaleY = camView.getScaleY();
		
		if (features != null)
			for (Feature feature : features)
				canvas.drawRect(feature.getRect(scaleX, scaleY), paintFeatures);
	}

	private void drawPickedColor(Canvas canvas) {
		paintFrameRate.setColor(pickedUpColor);
		canvas.drawRect(pickedUpRect, paintFrameRate);
		paintFrameRate.setColor(Color.BLACK);
		paintFrameRate.setStyle(Paint.Style.STROKE);
		canvas.drawRect(pickedUpRect, paintFrameRate);
		paintFrameRate.setStyle(Paint.Style.FILL);
		
		canvas.save();
		canvas.translate(1, 1);
		canvas.drawText("Picked color", 40, 30, paintFrameRate);
		canvas.restore();
		
		paintFrameRate.setColor(Color.YELLOW);
		canvas.drawText("Picked color", 40, 30, paintFrameRate);
	}

	@Override
	public void run() {
		while (run) {
			Canvas canvas = null;
			canvas = getHolder().lockCanvas();
			
			if (canvas != null) {
				onDraw(canvas);
				getHolder().unlockCanvasAndPost(canvas);
			}
			
			try {
				Thread.sleep(1000 / 24);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public final void setFrameRate(int frameRate) {
		synchronized (this) {
			this.frameRate = frameRate;
		}
	}
	
	public Point getSize() {
		return new Point(width, height);
	}
	
	public void setPickedUpColor(int color) {
		synchronized (this) {
			pickedUpColor = color;
			initState = false;
		}
	}
	
	public void setDrawBitmapOnCenter(Bitmap bmp) {
		drawBitmapOnCenter = bmp;
	}

	public void pushFeatures(List<Feature> features) {
		this.features = features;
	}

	public Bitmap getDrawBitmapOnScreen() {
		return drawBitmapOnScreen;
	}

	public void setDrawBitmapOnScreen(Bitmap drawBitmapOnScreen) {
		this.drawBitmapOnScreen = drawBitmapOnScreen;
	}

	public void attachCamView(CamView camView) {
		this.camView = camView;
	}
}
