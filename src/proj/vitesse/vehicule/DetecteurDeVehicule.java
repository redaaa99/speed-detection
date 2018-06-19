package proj.vitesse.vehicule;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class DetecteurDeVehicule implements Cloneable{
	private int mode;
	private int methode;
	private double minSurface;
	private double minWidth;
	private double minHeight;
	private double minRapportAspect;
	private double maxRapportAspect;
	private double minDiagonale;
	private List<MatOfPoint> contours;
	private List<MatOfPoint> convexHulls;
	private List<Vehicule> vehiculesCourants;
//===========================================================================
// Constructeurs
//===========================================================================
	public DetecteurDeVehicule() {
		super();
		this.contours = new ArrayList<MatOfPoint>();
		this.convexHulls = new ArrayList<MatOfPoint>();
	}
	public DetecteurDeVehicule(int mode, int methode, double minSurface, double minWidth, double minHeight,
			double minRapportAspect, double maxRapportAspect, double minDiagonale) {
		super();
		this.contours = new ArrayList<MatOfPoint>();
		this.convexHulls = new ArrayList<MatOfPoint>();
		this.vehiculesCourants = new ArrayList<Vehicule>();
		this.mode = mode;
		this.methode = methode;
		this.minSurface = minSurface;
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.minRapportAspect = minRapportAspect;
		this.maxRapportAspect = maxRapportAspect;
		this.minDiagonale = minDiagonale;
	}
//===========================================================================
// Méthodes
//===========================================================================
	public void determinerContours(Mat image) {
		Imgproc.findContours(image, this.contours, new Mat(), this.mode, this.methode);
	}
	public void determinerConvexHulls() {
		for(int i=0; i< contours.size();i++) {
        	MatOfInt hull = new MatOfInt();
        	Imgproc.convexHull(contours.get(i), hull,false);
        	MatOfPoint mopHull = new MatOfPoint();
    		mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
    		for (int j = 0; j < hull.size().height; j++) {
    			int index = (int) hull.get(j, 0)[0];
    			double[] point = new double[] { contours.get(i).get(index, 0)[0], contours.get(i).get(index, 0)[1] };
    			mopHull.put(j, 0, point);
    		}
    		convexHulls.add(mopHull);
        }
	}
	public void determinerVraisVehicules() {
		for (MatOfPoint convexHull : this.convexHulls) {
            Vehicule vehiculePossible = new Vehicule(convexHull);  
            if (vehiculePossible.getRectangleCourant().area() > this.minSurface &&
                vehiculePossible.getRapportAspectCourant() > this.minRapportAspect &&
                vehiculePossible.getRapportAspectCourant() < this.maxRapportAspect &&
                vehiculePossible.getRectangleCourant().width > this.minWidth &&
                vehiculePossible.getRectangleCourant().height > this.minHeight &&
                vehiculePossible.getDiagonaleCourante() > this.minDiagonale ) {
                this.vehiculesCourants.add(vehiculePossible);
            }
        }
	}
	/*public Mat dessinerContours(Size size, int type) {
		Mat imageResultat =  Mat.zeros(size,type);
		Imgproc.drawContours(imageResultat, contours, 2, new Scalar(255));
		return imageResultat;
	}
	public Mat dessinerConvexeHulls(Size size, int type) {
		Mat imageResultat =  Mat.zeros(size, type);
		Imgproc.drawContours(imageResultat, this.convexHulls, -1, new Scalar(255),-1);
		return imageResultat;
	}
	public Mat dessinerVraisVehicules(Size size, int type) {
		List<MatOfPoint> realConvexHulls = new ArrayList<MatOfPoint>();
        for (int i=0 ; i<vehiculesCourants.size() ; i++) {
        	realConvexHulls.add(vehiculesCourants.get(i).getContourCourant());
        }
        Mat imageResultat =  Mat.zeros(size,type);
        Imgproc.drawContours(imageResultat, realConvexHulls, -1, new Scalar(255),-1);
		return imageResultat;
	}*/
	
	@Override
	@SuppressWarnings("unused")
	protected Object clone() {
		if(this != null) {
			DetecteurDeVehicule detecteurDeVehicule = new DetecteurDeVehicule(this.mode, this.methode, this.minSurface, this.minWidth,
					this.minHeight, this.minRapportAspect, this.maxRapportAspect, this.minDiagonale);
			return detecteurDeVehicule;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return ("MinSurface : " + this.minSurface +" , Minwidth : " + this.minWidth +" , MinHeight : " +
					this.minHeight +" , minRapportAspect : " + this.minRapportAspect+" , maxRapportAspect : " + this.maxRapportAspect
					+" , minDiagonale : " +this.minDiagonale);
	}
//===========================================================================
// Getters & Setters
//===========================================================================
	/*public List<MatOfPoint> getContours() {
		return contours;
	}
	public void setContours(List<MatOfPoint> contours) {
		this.contours = contours;
	}
	public List<MatOfPoint> getConvexHulls() {
		return convexHulls;
	}
	public void setConvexHulls(List<MatOfPoint> convexHulls) {
		this.convexHulls = convexHulls;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getMethode() {
		return methode;
	}
	public void setMethode(int methode) {
		this.methode = methode;
	}
	public double getMinSurface() {
		return minSurface;
	}
	public void setMinSurface(int minSurface) {
		this.minSurface = minSurface;
	}
	public double getMinWidth() {
		return minWidth;
	}
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}
	public double getMinHeight() {
		return minHeight;
	}
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}
	public double getMinRapportAspect() {
		return minRapportAspect;
	}
	public void setMinRapportAspect(double minRapportAspect) {
		this.minRapportAspect = minRapportAspect;
	}
	public double getMaxRapportAspect() {
		return maxRapportAspect;
	}
	public void setMaxRapportAspect(double maxRapportAspect) {
		this.maxRapportAspect = maxRapportAspect;
	}
	public double getMinDiagonale() {
		return minDiagonale;
	}
	public void setMinDiagonale(double minDiagonale) {
		this.minDiagonale = minDiagonale;
	}*/
	public List<Vehicule> getVehiculesCourants() {
		return vehiculesCourants;
	}
	/*public void setVehiculesCourants(List<Vehicule> vehiculesCourants) {
		this.vehiculesCourants = vehiculesCourants;
	}*/
}
