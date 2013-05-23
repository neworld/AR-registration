package lt.neworld.arRegistration.prediction;

import java.util.List;

import lt.neworld.arRegistration.Feature;

import org.apache.commons.math3.filter.KalmanFilter;
import org.apache.commons.math3.filter.MeasurementModel;
import org.apache.commons.math3.filter.ProcessModel;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import android.util.SparseArray;

public class KalmanFilterPredictor implements Predictor {
	
	private SparseArray<Filter> filters = new SparseArray<Filter>();

	public void predict(Feature feature, double T) {
		Filter filter = filters.get(feature.id);
		if (filter == null) {
			MyProcessModel processModel = new MyProcessModel();
			processModel.setInit(feature.getX(), feature.getY());
			MyMeasurementModel measuredModel = new MyMeasurementModel();
			
			filter = new Filter(processModel, measuredModel, feature);
			filters.append(feature.id, filter);
		} else {
			filter.updateAndPredict(feature, T);
		}
	}

	@Override
	public void predict(List<Feature> features, double T) {
		for (Feature feature : features)
			predict(feature, T);
	}
}

class Filter extends KalmanFilter {
	private MyProcessModel processModel;
	private MyMeasurementModel measurementModel;
	
	int prevX;
	int prevY;
	
	double prevVelocityX;
	double prevVelocityY;
	
	public Filter(MyProcessModel processModel, MyMeasurementModel measurementModel, Feature feature) {
		super(processModel, measurementModel);
		
		this.processModel = processModel;
		this.measurementModel = measurementModel;
		
		prevX = feature.getX();
		prevY = feature.getY();
	}
	
	public void updateAndPredict(Feature feature, double T) {
		processModel.setT(T);
		measurementModel.setT(T);
		
		int x = feature.getX();
		int y = feature.getY();
		
		double velocityX = (x - prevX) / T;
		double velocityY = (y - prevY) / T;
		
		double ax = (velocityX - prevVelocityX) / T;
		double ay = (velocityY - prevVelocityY) / T;
		
		double a = Math.sqrt(ax * ax + ay * ay);
		
		predict(new ArrayRealVector(new double[] { a }));
		correct(new ArrayRealVector(new double[] { x, y }));
		
		double[] pos = getStateEstimation();
		feature.correct((int)pos[0], (int)pos[1]);
	}
}

class MyProcessModel implements ProcessModel {
	private double T;
	private int x;
	private int y;
	
	private static final double accelNoise = 2d;

	@Override
	public RealMatrix getControlMatrix() {
		double tt = Math.pow(T, 2) / 2;
		
		return new Array2DRowRealMatrix(new double[][] {{tt}, {tt}, {T}, {T}});
	}

	@Override
	public RealMatrix getInitialErrorCovariance() {
		return null;
	}

	@Override
	public RealVector getInitialStateEstimate() {
		return new ArrayRealVector(new double[] { x, y, 0, 0});
	}

	@Override
	public RealMatrix getProcessNoise() {
		double t4 = Math.pow(T, 4) / 4;
		double t3 = Math.pow(T, 3) / 2;
		
		RealMatrix tmp = new Array2DRowRealMatrix(new double[][] {
				{t4, 0, t3, 0},
				{0, t4, 0, t3},
				{t3, 0, T, 0},
				{0, t3, 0, T}
		});
		
		return tmp.scalarMultiply(Math.pow(accelNoise, 2));
	}

	@Override
	public RealMatrix getStateTransitionMatrix() {
		return new Array2DRowRealMatrix(new double[][] {
				{1, 0, T, 0},
				{0, 1, 0, T},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
		});
	}
	
	public void setT(double T) {
		this.T = T = 1;
	}
	
	public void setInit(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class MyMeasurementModel implements MeasurementModel {
	
	private double T;
	
	private static final double measurementNoise = 0.2d;
	
	private static final RealMatrix R = new Array2DRowRealMatrix(new double[][] { 
			{Math.pow(measurementNoise, 2), 0},
			{0, Math.pow(measurementNoise, 2)}
	});

	@Override
	public RealMatrix getMeasurementMatrix() {
		return new Array2DRowRealMatrix(new double[][] {
				{1, 0, 0, 0},
				{0, 1, 0, 0}
		});
	}

	@Override
	public RealMatrix getMeasurementNoise() {
		return R;
	}
	
	public void setT(double T) {
		this.T = T;
	}
}