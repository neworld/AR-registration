package lt.neworld.arRegistration.prediction;

import java.util.List;

import lt.neworld.arRegistration.Cluster;

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

	public void predict(Cluster cluster, double T) {
		Filter filter = filters.get(cluster.id);
		if (filter == null) {
			MyProcessModel processModel = new MyProcessModel();
			processModel.setInit(cluster.getX(), cluster.getY());
			MyMeasurementModel measuredModel = new MyMeasurementModel();
			
			filter = new Filter(processModel, measuredModel, cluster);
			filters.append(cluster.id, filter);
		} else {
			filter.updateAndPredict(cluster, T);
		}
	}

	@Override
	public void predict(List<Cluster> features, double T) {
		for (Cluster feature : features)
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
	
	public Filter(MyProcessModel processModel, MyMeasurementModel measurementModel, Cluster cluster) {
		super(processModel, measurementModel);
		
		this.processModel = processModel;
		this.measurementModel = measurementModel;
		
		prevX = cluster.getX();
		prevY = cluster.getY();
	}
	
	public void updateAndPredict(Cluster cluster, double T) {
		int x = cluster.getX();
		int y = cluster.getY();
		
		double velocityX = (x - prevX) / T;
		double velocityY = (y - prevY) / T;
		
		double ax = (velocityX - prevVelocityX) / T;
		double ay = (velocityY - prevVelocityY) / T;
		
		double a = Math.sqrt(ax * ax + ay * ay);

		processModel.setT(T * a);
		measurementModel.setT(T * a);
		
		predict(new ArrayRealVector(new double[] { a }));
		correct(new ArrayRealVector(new double[] { x, y }));
		
		double[] pos = getStateEstimation();
		cluster.correct((int)pos[0], (int)pos[1]);
	}
}

class MyProcessModel implements ProcessModel {
	private double T;
	private int x;
	private int y;
	
	private static final double accelNoise = 0.5d;

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
		
		return tmp.scalarMultiply(accelNoise);
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
	
	@Override
	public RealMatrix getMeasurementMatrix() {
		return new Array2DRowRealMatrix(new double[][] {
				{1, 0, 0, 0},
				{0, 1, 0, 0}
		});
	}

	@Override
	public RealMatrix getMeasurementNoise() {
		double t4 = Math.pow(T, 4) / 4;
		
		return new Array2DRowRealMatrix(new double[][] { 
				{t4, 0},
				{0, t4}
		}).scalarMultiply(measurementNoise);
	}
	
	public void setT(double T) {
		this.T = T;
	}
}