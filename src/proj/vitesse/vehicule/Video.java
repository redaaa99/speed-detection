package proj.vitesse.vehicule;


import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import proj.sample.Controller;

public class Video {
	
	private SoustracteurDeFond soustracteurDeFond;
	private DetecteurDeVehicule detecteurDeVehicule;
	private SuiveurDeVehicule suiveurDeVehicule ;
	private Frame frame;
	private VideoCapture maVideo;
	private Mat frameCourant;
	private boolean firstFrame;
	private int compteur;
//===========================================================================
// Constructeurs
//===========================================================================
	public Video() {
		this.frameCourant = new Mat();
		this.firstFrame = true;
		this.compteur = 0;
		this.frame = new Frame();
	}
	public Video(String chemin) {
		this();
		this.soustracteurDeFond = new SoustracteurDeFond(org.opencv.video.Video.createBackgroundSubtractorMOG2(),
				0.01, 200, 255, Imgproc.THRESH_BINARY, 5);
		//this.detecteurDeVehicule = new DetecteurDeVehicule(Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE,
		//		200, 30, 30, 0.2, 4.0, 60);
		this.detecteurDeVehicule = new DetecteurDeVehicule();
		this.suiveurDeVehicule = new SuiveurDeVehicule();
		this.maVideo = new VideoCapture(chemin);
		
	}
	public Video(BackgroundSubtractorMOG2 backgroundSubtractorMOG2, double learningRate, double tresh,double maxVal, int type, int kernelSize,
			int mode, int methode, double minSurface, double minWidth, double minHeight, double minRapportAspect, double maxRapportAspect, double minDiagonale,
			String cheminVideo) {
		this();
		this.soustracteurDeFond = new SoustracteurDeFond(backgroundSubtractorMOG2,
				learningRate, tresh, maxVal, type, kernelSize);
		this.detecteurDeVehicule = new DetecteurDeVehicule(mode, methode,
				minSurface, minWidth, minHeight, minRapportAspect, maxRapportAspect, minDiagonale);
		this.maVideo = new VideoCapture(cheminVideo);
		this.suiveurDeVehicule = new SuiveurDeVehicule();
	}
//===========================================================================
// Méthodes
//===========================================================================
	public void vehiculesEntrants() {
		for (ConvertisseurCoordonnees convertisseurCoordonnees1 : this.frame.getConvertisseursCoordonnees()) {
			convertisseurCoordonnees1.setContientVehicule(false);
			for (Vehicule vehicule : suiveurDeVehicule.getVehiculesExistants()) {
				Point dernierCentre = vehicule.getCentre(vehicule.getCentres().size()-1);
				if((vehicule.isDedans() == false) && convertisseurCoordonnees1.getPolygoneImage().aLinterieur(dernierCentre)) {
					vehicule.setConvertisseurCoordonnees(convertisseurCoordonnees1);
					vehicule.setDedans(true);
					vehicule.setPointDentree(dernierCentre);
					convertisseurCoordonnees1.setContientVehicule(true);
					//if(Controller.vitesseMax>vehicule.getVitesse())
					this.compteur++;
					//System.out.println(convertisseurCoordonnees1 + " " + dernierCentre);
					
				}
			}
		}
	}
	public void vehiculesSortants() {
		for (Vehicule vehicule : this.suiveurDeVehicule.getVehiculesExistants()) {
			Point dernierCentre = vehicule.getCentre(vehicule.getCentres().size()-1);
			if((vehicule.isDedans() == true) && !vehicule.getConvertisseurCoordonnees().getPolygoneImage().aLinterieur(dernierCentre) 
					&& (vehicule.getPointDeSortie() == null)) {
				//System.out.println(dernierCentre + " " + ensa.proj.vitesse.vehicule.getConvertisseurCoordonnees().getPolygoneImage().aLinterieur(dernierCentre));
				vehicule.setPointDeSortie(vehicule.getCentre(vehicule.getCentres().size()-2));
				if(Controller.vitesseMax>vehicule.getVitesse()){
					compteur--;
				}
			}
		}
	}
	public boolean process() {
		if(maVideo.read(frameCourant)) {
			frame.setFrameCourant(frameCourant);
			Imgproc.resize(frameCourant, frameCourant, new Size(1071, 597), 0, 0, Imgproc.INTER_CUBIC);
			DetecteurDeVehicule detecteurDeVehicule = (DetecteurDeVehicule) this.detecteurDeVehicule.clone();
			Mat drawing = soustracteurDeFond.appliquer(frameCourant);
			detecteurDeVehicule.determinerContours(drawing);
			detecteurDeVehicule.determinerConvexHulls();
			detecteurDeVehicule.determinerVraisVehicules();
			//Mat draw = detecteurDeVehicule.dessinerVraisVehicules(image.size()	, CvType.CV_8UC1);
			//Mat draw = detecteurDeVehicule.dessinerConvexeHulls(image.size(),CvType.CV_8UC1);
			//Mat draw =  detecteurDeVehicule.dessinerContours(image.size(), CvType.CV_8UC1);
			//Imgproc.drawContours(draw, detecteurDeVehicule.getConvexHulls(), -1, new Scalar(255),-1);
			if (this.firstFrame == true) {
	            suiveurDeVehicule.setVehiculesExistants(detecteurDeVehicule.getVehiculesCourants());
	            this.firstFrame = false;
	        } else {
	            suiveurDeVehicule.setVehiculesCourants(detecteurDeVehicule.getVehiculesCourants());
	            suiveurDeVehicule.comparaisonVehiculesCourantsVehiculesExistans();
	        }
			 // Etape 4
			vehiculesEntrants();
			//Etape 5
			vehiculesSortants();
			//Etape 6
			frame.dessinerVehiculesInfo(this.compteur , this.suiveurDeVehicule.getVehiculesExistants());
			frame.dessinerReferences();
			//frame.dessinerCompteurDeVoiture(compteur);
			return true;
		} 
		return false;
	}
	public void ajouterReference(Point p1, Point p2, Point p3, Point p4, double w, double h) {
		Polygone imagecoordinates = new Polygone();
		imagecoordinates.ajouterSommet(p1);
		imagecoordinates.ajouterSommet(p2);
		imagecoordinates.ajouterSommet(p3);
		imagecoordinates.ajouterSommet(p4);
		
		Polygone realwordcoordinates = new Polygone();
		realwordcoordinates.ajouterSommet(new Point(0,0));
		realwordcoordinates.ajouterSommet(new Point(0,w));
		realwordcoordinates.ajouterSommet(new Point(h,w));
		realwordcoordinates.ajouterSommet(new Point(h,0));
		
		ConvertisseurCoordonnees convertisseurCoordonnees = new ConvertisseurCoordonnees(imagecoordinates, realwordcoordinates);
		this.frame.ajouterConvertisseur(convertisseurCoordonnees);
	}
//===========================================================================
// Getters & Setters
//===========================================================================
	public SoustracteurDeFond getSoustracteurDeFond() {
		return soustracteurDeFond;
	}
	public void setSoustracteurDeFond(SoustracteurDeFond soustracteurDeFond) {
		this.soustracteurDeFond = soustracteurDeFond;
	}
	public DetecteurDeVehicule getDetecteurDeVehicule() {
		return detecteurDeVehicule;
	}
	public void setDetecteurDeVehicule(DetecteurDeVehicule detecteurDeVehicule) {
		this.detecteurDeVehicule = detecteurDeVehicule;
	}
	public SuiveurDeVehicule getSuiveurDeVehicule() {
		return suiveurDeVehicule;
	}
	public void setSuiveurDeVehicule(SuiveurDeVehicule suiveurDeVehicule) {
		this.suiveurDeVehicule = suiveurDeVehicule;
	}
	public Frame getFrame() {
		return frame;
	}
	/*public void setFrame(Frame frame) {
		this.frame = frame;
	}
	public boolean isFirstFrame() {
		return firstFrame;
	}
	public void setFirstFrame(boolean firstFrame) {
		this.firstFrame = firstFrame;
	}

	public VideoCapture getMaVideo() {
		return maVideo;
	}
	public void setMaVideo(VideoCapture maVideo) {
		this.maVideo = maVideo;
	}
	public Mat getFrameCourant() {
		return frameCourant;
	}
	public void setFrameCourant(Mat frameCourant) {
		this.frameCourant = frameCourant;
	}*/
	public int getCompteur() {
		return compteur;
	}
	public void setCompteur(int compteur) {
		this.compteur = compteur;
	}
	public double getFps() {
		return maVideo.get(Videoio.CAP_PROP_FPS);
	}
}
