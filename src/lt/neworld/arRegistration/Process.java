package lt.neworld.arRegistration;

import android.graphics.Bitmap;

public class Process extends Thread {

	private CamView camView;
	private HUD hud;
	
	private volatile boolean run = true;
	
	private Bitmap bitmap;
	
	private final static int FRAMES_FOR_COUNT = 20;
	private long[] frames = new long[FRAMES_FOR_COUNT];
	private byte framesHead = 0;
	
	public Process(CamView camView, HUD hud) {
		this.camView = camView;
		this.hud = hud;
		start();
	}

	@Override
	public void run() {
		while (run) {
			bitmap = camView.getBitmap();
			
			updateFrames();
			Thread.yield();
		}
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
	}
}
