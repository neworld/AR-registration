package lt.neworld.arRegistration;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	
	private CamView camView;
	private Camera camera;
	private HUD hud;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FrameLayout root = new FrameLayout(this);
		
		camView = new CamView(this);
		hud = new HUD(this);
		
		root.addView(camView);
		root.addView(hud);
		
		setContentView(root);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		camera.startPreview();
		
		//start capture frames
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					camView.getBitmap();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		camera.stopPreview();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		camera = Camera.open();
		camView.setCamera(camera);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		camera.release();
		camView.setCamera(camera = null);
	}
}
