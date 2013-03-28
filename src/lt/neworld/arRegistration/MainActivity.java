package lt.neworld.arRegistration;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	
	private CamView camView;
	private Camera camera;
	private HUD hud;
	private Process process;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FrameLayout root = new FrameLayout(this);
		
		camView = new CamView(this);
		hud = new HUD(this);
		hud.attachCamView(camView);
		
		root.addView(camView);
		root.addView(hud);
		
		setContentView(root);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		camera.startPreview();
		
		process = new Process(camView, hud);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		process.stopProcessing();
		
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
