package lt.neworld.arRegistration;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;

public class Process extends Thread {

	private CamView camView;
	private HUD hud;
	
	private volatile boolean run = true;
	
	private Bitmap bitmap;
	
	private final static int FRAMES_FOR_COUNT = 20;
	private long[] frames = new long[FRAMES_FOR_COUNT];
	private byte framesHead = 0;
	
	private static final int COLOR_PICKING_AREA = 50;
	private boolean needPickUpColor = false;
	private int pickedUpColor;
	
	private OnClickListener onColorPickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			needPickUpColor = true;
		}
	};
	
	public Process(CamView camView, HUD hud) {
		this.camView = camView;
		this.hud = hud;
		
		hud.setOnClickListener(onColorPickListener );
		
		start();
	}

	@Override
	public void run() {
		while (run) {
			bitmap = camView.getBitmap();
			
			if (needPickUpColor)
				pickUpColor(bitmap);
			
			updateFrames();
			Thread.yield();
		}
	}
	
	private void pickUpColor(Bitmap bitmap) {
		int red = 0, 
			green = 0, 
			blue = 0; 
		
		Point size = hud.getSize();
		
		int[] pixels = new int[COLOR_PICKING_AREA * COLOR_PICKING_AREA];
		
		if (bitmap != null)
			bitmap.getPixels(pixels, 0, 0, (size.x - COLOR_PICKING_AREA) / 2, (size.y - COLOR_PICKING_AREA) / 2, COLOR_PICKING_AREA, COLOR_PICKING_AREA);
		
		for (int pixel : pixels) {
			red += Color.red(pixel);
			green += Color.green(pixel);
			blue += Color.blue(pixel);
		}
		
		red /= pixels.length;
		green /= pixels.length;
		blue /= pixels.length;
		
		pickedUpColor = Color.rgb(red, green, blue);
		needPickUpColor = false;
		
		hud.setPickedUpColor(pickedUpColor);
		hud.setOnClickListener(null);
	}

	private void updateFrames() {
		long last = frames[framesHead];
		long current = frames[framesHead] = System.nanoTime();
		
		if (++framesHead == FRAMES_FOR_COUNT)
			framesHead = 0;
		
		if (last > 0)
			hud.setFrameRate((int) (1000000000 / (current - last)));
	}
	
	public void stopProcessing() {
		run = false;
		hud.setOnClickListener(null);
	}
}
