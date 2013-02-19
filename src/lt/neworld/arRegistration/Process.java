package lt.neworld.arRegistration;

import android.graphics.Bitmap;

public class Process extends Thread {

	private CamView camView;
	private HUD hud;
	
	private volatile boolean run = true;
	
	private Bitmap bitmap;
	
	public Process(CamView camView, HUD hud) {
		this.camView = camView;
		this.hud = hud;
		start();
	}

	@Override
	public void run() {
		while (run) {
			bitmap = camView.getBitmap();
			Thread.yield();
		}
	}
	
	public void stopProcessing() {
		run = false;
	}
}
