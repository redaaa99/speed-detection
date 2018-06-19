package proj.vitesse.vehicule;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import proj.sample.Controller;

import java.util.ArrayList;
import java.util.List;

public class Vehicule {
	private MatOfPoint contourCourant;
	private Rect rectangleCourant;
	private List<Point> centres = new ArrayList<Point>();
	private double diagonaleCourante;
	private double rapportAspectCourant;
	private int compteurNonCorrespondance;
	public Point prochainePositionPredite = new Point();
	public Point pointDentree ;
	public Point pointDeSortie;
	private double deltaFrame;
	private double vitesse;
	private boolean correspondance;
	private boolean nouveauVehicule; 
	private boolean dedans;
	private boolean finalist;
	private boolean capture;
	private double speed;
	private ConvertisseurCoordonnees convertisseurCoordonnees;
//===========================================================================
// Constructeurs
//===========================================================================
	public Vehicule() {
		super();
	}
	public Vehicule(MatOfPoint contour) {
		this.contourCourant = contour;
		this.rectangleCourant = Imgproc.boundingRect(this.contourCourant);
		Point centreCourant = new Point();
		centreCourant.x = (this.rectangleCourant.x + (this.rectangleCourant.width / 2) );
		centreCourant.y = (this.rectangleCourant.y + (this.rectangleCourant.height / 2) );
		this.centres.add(centreCourant);
		this.diagonaleCourante = Math
				.sqrt(Math.pow(this.rectangleCourant.width, 2) + Math.pow(this.rectangleCourant.height, 2));
		this.rapportAspectCourant = (float) this.rectangleCourant.width / (float) this.rectangleCourant.height;
		this.compteurNonCorrespondance = 0;
		this.nouveauVehicule = true;
		this.correspondance = false;
		this.capture = false;
		this.dedans = false;
		this.deltaFrame = 0;
		
		finalist = false;
	}
//===========================================================================
// Méthodes
//===========================================================================
	public void calculProchainePositionPredite() {
		int nombreDeCentres = (int)centres.size();
	    
	    if (nombreDeCentres == 1) {
	        prochainePositionPredite.x = centres.get(0).x;
	        prochainePositionPredite.y = centres.get(0).y;
	        
	    } else if (nombreDeCentres == 2) {
	        int deltaX = (int) (centres.get(1).x - centres.get(0).x);
	        int deltaY = (int) (centres.get(1).y - centres.get(0).y);
	        
	        prochainePositionPredite.x = centres.get(1).x + deltaX;
	        prochainePositionPredite.y = centres.get(1).y + deltaY;
	        
	    } else if (nombreDeCentres == 3) {
	        int sommeXChangements = (int) (((centres.get(2).x - centres.get(1).x) * 2) +
	        ((centres.get(1).x - centres.get(0).x) * 1));
	        int deltaX = (int) Math.round((float)sommeXChangements / 3.0);
	        
	        int sommeYChangements = (int) (((centres.get(2).y - centres.get(1).y) * 2) +
	        ((centres.get(1).y - centres.get(0).y) * 1));
	        int deltaY = (int) Math.round((float)sommeYChangements / 3.0);
	        
	        prochainePositionPredite.x = centres.get(2).x + deltaX;
	        prochainePositionPredite.y = centres.get(2).y + deltaY;
	        
	    } else if (nombreDeCentres == 4) {
	        
	        int sommeXChangements = (int) (((centres.get(3).x - centres.get(2).x) * 3) +
	        ((centres.get(2).x - centres.get(1).x) * 2) +
	        ((centres.get(1).x - centres.get(0).x) * 1));
	        int deltaX = (int) Math.round((float)sommeXChangements / 6.0);
	        
	        int sommeYChangements = (int) (((centres.get(3).y - centres.get(2).y) * 3) +
	        ((centres.get(2).y - centres.get(1).y) * 2) +
	        ((centres.get(1).y - centres.get(0).y) * 1));
	        int deltaY = (int) Math.round((float)sommeYChangements / 6.0);
	        
	        prochainePositionPredite.x = centres.get(3).x + deltaX;
	        prochainePositionPredite.y = centres.get(3).y + deltaY;
	        
	    } else if (nombreDeCentres >= 5) {
	        
	        int sommeXChangements = (int) (((centres.get(nombreDeCentres - 1).x - centres.get(nombreDeCentres - 2).x) * 4) +
	        ((centres.get(nombreDeCentres - 2).x - centres.get(nombreDeCentres - 3).x) * 3) +
	        ((centres.get(nombreDeCentres - 3).x - centres.get(nombreDeCentres - 4).x) * 2) +
	        ((centres.get(nombreDeCentres - 4).x - centres.get(nombreDeCentres - 5).x) * 1));
	        int deltaX = (int) Math.round((float)sommeXChangements / 10.0);
	        
	        int sommeYChangements = (int) (((centres.get(nombreDeCentres - 1).y - centres.get(nombreDeCentres - 2).y) * 4) +
	        ((centres.get(nombreDeCentres - 2).y - centres.get(nombreDeCentres - 3).y) * 3) +
	        ((centres.get(nombreDeCentres - 3).y - centres.get(nombreDeCentres - 4).y) * 2) +
	        ((centres.get(nombreDeCentres - 4).y - centres.get(nombreDeCentres - 5).y) * 1));
	        int deltaY = (int) Math.round((float)sommeYChangements / 10.0);
	        
	        prochainePositionPredite.x = centres.get(nombreDeCentres - 1).x + deltaX;
	        prochainePositionPredite.y = centres.get(nombreDeCentres - 1).y + deltaY;
	        
	    } else {
	        
	    }
	}
	public void calculerVitesse() {
		//System.out.println(pointDeSortie);
		if(this.pointDeSortie != null && !finalist) {
			//System.out.println(deltaFrame + " " + pointDentree + " " + pointDeSortie);
			double deltaT = deltaFrame/ Controller.fps;
			this.vitesse = (distance2Points(pointDeSortie, pointDentree) / deltaT)*3.6;
			//System.out.println(pointDeSortie+ "  " + pointDentree + " " + deltaFrame);
			finalist = true;
		}
		else if(this.dedans == true && !finalist) {
			Point positionCourante = this.getCentre(this.centres.size()-1);
			positionCourante = this.convertisseurCoordonnees.conversionImageMondeReel(positionCourante);			
			double deltaT = this.deltaFrame/ Controller.fps;
			this.vitesse = (distance2Points(positionCourante, pointDentree) / deltaT)*3.6;
		}
	}
	public double distance2Points(Point aPoint, Point bPoint) {
		int deltaX = (int) Math.abs(aPoint.x - bPoint.x);
		int deltaY = (int) Math.abs(aPoint.y - bPoint.y);
		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
//===========================================================================
// Getters & Setters
//===========================================================================
	public MatOfPoint getContourCourant() {
		return contourCourant;
	}
	public void setContourCourant(MatOfPoint contourCourant) {
		this.contourCourant = contourCourant;
	}
	public Rect getRectangleCourant() {
		return rectangleCourant;
	}
	public void setRectangleCourant(Rect rectangleCourant) {
		this.rectangleCourant = rectangleCourant;
	}
	public List<Point> getCentres() {
		return centres;
	}
	public void setCentres(List<Point> centres) {
		this.centres = centres;
	}
	public double getDiagonaleCourante() {
		return diagonaleCourante;
	}
	public void setDiagonaleCourante(double diagonaleCourante) {
		this.diagonaleCourante = diagonaleCourante;
	}
	public double getRapportAspectCourant() {
		return rapportAspectCourant;
	}
	public void setRapportAspectCourant(double rapportAspectCourant) {
		this.rapportAspectCourant = rapportAspectCourant;
	}
	public boolean isCorrespondance() {
		return correspondance;
	}
	public void setCorrespondance(boolean correspondance) {
		this.correspondance = correspondance;
	}
	public boolean isNouveauVehicule() {
		return nouveauVehicule;
	}
	public void setNouveauVehicule(boolean nouveauVehicule) {
		this.nouveauVehicule = nouveauVehicule;
	}
	public int getCompteurNonCorrespondance() {
		return compteurNonCorrespondance;
	}
	public void setCompteurNonCorrespondance(int compteurNonCorrespondance) {
		this.compteurNonCorrespondance = compteurNonCorrespondance;
	}
	public Point getProchainePositionPredite() {
		return prochainePositionPredite;
	}
	public void setProchainePositionPredite(Point prochainePositionPredite) {
		this.prochainePositionPredite = prochainePositionPredite;
	}
	public double getDeltaFrame() {
		return deltaFrame;
	}
	public void setDeltaFrame(double deltaFrame) {
		this.deltaFrame = deltaFrame;
	}
	public boolean isFinalist() {
		return finalist;
	}
	public void setFinalist(boolean finalist) {
		this.finalist = finalist;
	}
	public double getVitesse() {
		return vitesse;
	}
	public void setVitesse(double vitesse) {
		this.vitesse = vitesse;
	}
	public boolean isDedans() {
		return dedans;
	}
	public void setDedans(boolean dedans) {
		this.dedans = dedans;
	}
	public Point getCentre(int index) {
		return this.centres.get(index);
	}
	public void ajouterNouveauCentre(Point centre) {
		this.centres.add(centre);
	}
	public double getSpeed() {
		return speed;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public ConvertisseurCoordonnees getConvertisseurCoordonnees() {
		return convertisseurCoordonnees;
	}
	public void setConvertisseurCoordonnees(ConvertisseurCoordonnees convertisseurCoordonnees) {
		this.convertisseurCoordonnees = convertisseurCoordonnees;
	}
	public Point getPointDentree() {
		return pointDentree;
	}
	public void setPointDentree(Point pointDentree) {
		this.pointDentree = this.convertisseurCoordonnees.conversionImageMondeReel(pointDentree);
	}
	public Point getPointDeSortie() {
		return pointDeSortie;
	}
	public void setPointDeSortie(Point pointDeSortie) {
		this.pointDeSortie = this.convertisseurCoordonnees.conversionImageMondeReel(pointDeSortie);
	}
	public boolean isCapture() {
		return capture;
	}
	public void setCapture(boolean capture) {
		this.capture = capture;
	}
}
