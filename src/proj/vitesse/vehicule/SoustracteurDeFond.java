package proj.vitesse.vehicule;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

public class SoustracteurDeFond {
	private BackgroundSubtractorMOG2 backgroundSubtractorMOG2;
	private double learningRate;
	private double tresh;
	private double maxVal;
	private int type;
	private int kernelSize;
	
//===========================================================================
// Constructeurs
//===========================================================================
	public SoustracteurDeFond() {
		super();
	}
	public SoustracteurDeFond(BackgroundSubtractorMOG2 backgroundSubtractorMOG2, double learningRate, double tresh,
			double maxVal, int type, int kernelSize) {
		this.backgroundSubtractorMOG2 = backgroundSubtractorMOG2;
		this.learningRate = learningRate;
		this.tresh = tresh;
		this.maxVal = maxVal;
		this.type = type;
		this.kernelSize = kernelSize;
	}
//===========================================================================
// Methodes
//===========================================================================
	public Mat appliquer(Mat inputImage) {
		Mat imageResultat = new Mat();
		this.backgroundSubtractorMOG2.apply(inputImage,imageResultat,learningRate);
		Imgproc.threshold(imageResultat, imageResultat, this.tresh, this.maxVal, this.type);
		Imgproc.medianBlur(imageResultat, imageResultat, this.kernelSize);
		return imageResultat;
	}
//===========================================================================
// Getters & Setters
//===========================================================================
	public BackgroundSubtractorMOG2 getBackgroundSubtractorMOG2() {
		return backgroundSubtractorMOG2;
	}
	public void setBackgroundSubtractorMOG2(BackgroundSubtractorMOG2 backgroundSubtractorMOG2) {
		this.backgroundSubtractorMOG2 = backgroundSubtractorMOG2;
	}
	public double getLearningRate() {
		return learningRate;
	}
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	public double getTresh() {
		return tresh;
	}
	public void setTresh(double tresh) {
		this.tresh = tresh;
	}
	public double getMaxVal() {
		return maxVal;
	}
	public void setMaxVal(double maxVal) {
		this.maxVal = maxVal;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getKernelSize() {
		return kernelSize;
	}
	public void setKernelSize(int kernelSize) {
		this.kernelSize = kernelSize;
	}
}
