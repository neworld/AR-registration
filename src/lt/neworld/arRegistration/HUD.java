package lt.neworld.arRegistration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HUD extends SurfaceView implements Runnable {
	
	private int width;
	private int height;
	
	private Paint center = new Paint();
	
	private volatile boolean run = false;
	
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
		
		center.setColor(Color.RED);
		center.setStyle(Paint.Style.FILL);
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
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		canvas.drawRect(new Rect(width / 2 - 5, height / 2 - 5,  5, 5), center);
	}

	@Override
	public void run() {
		while (run) {
			Canvas canvas = null;
			canvas = getHolder().lockCanvas();
			onDraw(canvas);
			getHolder().unlockCanvasAndPost(canvas);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
