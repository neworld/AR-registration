package lt.neworld.arRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;

public class Process extends Thread {

	private CamView camView;
	private HUD hud;
	
	private volatile boolean run = true;
	
	private byte[] buffer;
	
	private final static int FRAMES_FOR_COUNT = 20;
	private long[] frames = new long[FRAMES_FOR_COUNT];
	private byte framesHead = 0;
	
	private static final int COLOR_PICKING_AREA = 16;
	private boolean needPickUpColor = false;
	private byte pickedUpColorU;
	private byte pickedUpColorV;
	private boolean pickedUp = false;
	
	private final static byte COLOR_TRESHOLD = 10;
	
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
			buffer = camView.getBitmap();
			
			if (needPickUpColor)
				pickUpColor(buffer);
			
			updateFrames();
			
			if (pickedUp) {
				process(buffer, buffer.length, hud.getSize().x, pickedUpColorU, pickedUpColorV);
				/*
				List<Feature> features = process(buffer, hud.getSize().x, pickedUpColorU, pickedUpColorV);
				hud.pushFeatures(features);
				*/
			}
			
			Thread.yield();
		}
	}
	
	private native int[] process(byte[] buffer, int size, int width, byte pickedUpColorU, byte pickedUpColorV);
	/*
	private List<Feature> process() {
		boolean[] checked = new boolean[buffer.length / 4];
		boolean[] good = new boolean[buffer.length / 4];
		
		Point size = hud.getSize();
		
		int minX, minY, maxX, maxY;
		
		Stack<Integer> steps = new Stack<Integer>();
		ArrayList<Feature> features = new ArrayList<Feature>();
		
		int founded = 0;
		
		for (int i = 0, index = buffer.length / 2; index < buffer.length; index += 2, i++)
			good[i] = Math.abs(buffer[i] - pickedUpColorU) <= COLOR_TRESHOLD && Math.abs(buffer[i + 1] - pickedUpColorV) <= COLOR_TRESHOLD;
		
		for (int index = 0; index < good.length; index += 2) {
			if (checked[index])
				continue;
			
			checked[index] = true;
			
			if (good[index]) {
				minX = maxX = index % size.x;
				minY = maxY = index / size.x;
				steps.add(index);
				while (steps.size() > 0) {
					int i = steps.pop();
					
					int ii = i - size.x;
					if (ii >= 0 && !checked[ii] && good[ii]) {
						checked[ii] = true;
						steps.add(ii);
						minY = Math.min(minY, ii / size.x);
					}
					
					ii = i - 1;
					if (ii >= 0 && !checked[ii] && good[ii]) {
						checked[ii] = true;
						steps.add(ii);
						minX = Math.min(minX, ii % size.x);
					}
					
					ii = i + 1;
					if (ii < checked.length && !checked[ii] && good[ii]) {
						checked[ii] = true;
						steps.add(ii);
						maxX = Math.max(maxX,  ii % size.x);
					}
					
					ii = i + size.x;
					if (ii < checked.length && !checked[ii] && good[ii]) {
						checked[ii] = true;
						steps.add(ii);
						maxY = Math.max(maxY, ii / size.x);
					}
				}
				
				features.add(new Feature(++founded, minX, minY, maxX, maxY));
			}
		}
		
		return features;
	}
	*/

	private void pickUpColor(byte[] buffer) {
		int cy = 0, 
			cu = 0,
			cv = 0;
		
		Point size = hud.getSize();
		
		int mainOffset = size.x * size.y;
		int blockOffsetY = (size.y - COLOR_PICKING_AREA) / 2 * size.x;
		int blockOffsetX = (size.x - COLOR_PICKING_AREA) / 2;
		
		if (blockOffsetY % 2 != 0)
			blockOffsetY++;
		
		if (blockOffsetX % 2 != 0)
			blockOffsetX++;
		
		int[] pixels = new int[COLOR_PICKING_AREA * COLOR_PICKING_AREA];
		
		if (buffer != null) {
			for (int y = 0; y < COLOR_PICKING_AREA; y+=2)
				for (int x = 0; x < COLOR_PICKING_AREA; x+=2) {
					int offsetY = blockOffsetY + size.x * y;
					int offsetX = blockOffsetX + x;
					
					cy += buffer[offsetY + offsetX] & 0xFF;
					
					cu += buffer[mainOffset + (offsetY >> 1) + offsetX + 1] & 0xFF;
					cv += buffer[mainOffset + (offsetY >> 1) + offsetX] & 0xFF;
					
					/*
					////////////////////////
					int i = offsetY + offsetX;
					int width = size.x;
					
			        int y1 = buffer[i  ]&0xff;
			        int y2 = buffer[i+1]&0xff;
			        int y3 = buffer[width+i  ]&0xff;
			        int y4 = buffer[width+i+1]&0xff;
			 
			        int u =  buffer[mainOffset + (offsetY >> 1) + offsetX + 1] & 0xFF;
			        int v = buffer[mainOffset + (offsetY >> 1) + offsetX] & 0xFF;
			        u = u-128;
			        v = v-128;
			        
			        int xx = y * COLOR_PICKING_AREA + x;
			        
			        pixels[xx] = convertYUVtoARGB(y1, u, v);
			        pixels[xx+1] = convertYUVtoARGB(y2, u, v);
			        pixels[COLOR_PICKING_AREA+xx  ] = convertYUVtoARGB(y3, u, v);
			        pixels[COLOR_PICKING_AREA+xx+1] = convertYUVtoARGB(y4, u, v);
			        ////////////////////////
			        */
			        
				}
		}
		
		int dalmuo = COLOR_PICKING_AREA * COLOR_PICKING_AREA / 4;
		
		cy /= dalmuo;
		cu /= dalmuo;
		cv /= dalmuo;
		
		cu -= 128;
		cv -= 128;
		
		pickedUpColorU = (byte) (cu & 0xFF);
		pickedUpColorV = (byte) (cv & 0xFF);
		
		int r, g, b;
		
		float Yf = 1.164f*((float)cy) - 16.0f;
		
	    r = (int) (Yf + 1.596f * cv);
	    g = (int) (Yf - (0.391f * cu + 0.813f * cv));
	    b = (int) (Yf + 2.018f * cu);
	    r = r > 255? 255 : r<0 ? 0 : r;
	    g = g > 255? 255 : g<0 ? 0 : g;
	    b = b > 255? 255 : b<0 ? 0 : b;
		
		int pickedUpColor = Color.rgb(r, g, b);
		needPickUpColor = false;
		pickedUp = true;
		
		hud.setPickedUpColor(pickedUpColor);
		//hud.setDrawBitmapOnCenter(Bitmap.createBitmap(pixels, COLOR_PICKING_AREA, COLOR_PICKING_AREA, Bitmap.Config.ARGB_8888));
		//hud.setOnClickListener(null);
	}
	/*
	private static int convertYUVtoARGB(int y, int u, int v) {
        float Yf = 1.164f*((float)y) - 16.0f;
        int R = (int)(Yf + 1.596f*v);
        int G = (int)(Yf - 0.813f*v - 0.391f*u);
        int B = (int)(Yf            + 2.018f*u);
        int alpha = 1; //unless transparent

        // Clip rgb values to 0-255
        R = R < 0 ? 0 : R > 255 ? 255 : R;
        G = G < 0 ? 0 : G > 255 ? 255 : G;
        B = B < 0 ? 0 : B > 255 ? 255 : B;
	    return 0xFF000000 | (R*65536 + G*256 + B);
	}
	*/
	private void updateFrames() {
		long last = frames[framesHead];
		long current = frames[framesHead] = System.nanoTime();
		
		if (++framesHead == FRAMES_FOR_COUNT)
			framesHead = 0;
		
		if (last > 0)
			hud.setFrameRate((int) (1000000000 / ((current - last) / FRAMES_FOR_COUNT)));
	}
	
	public void stopProcessing() {
		run = false;
		hud.setOnClickListener(null);
	}
}
