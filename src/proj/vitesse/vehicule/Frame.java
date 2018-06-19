package proj.vitesse.vehicule;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import proj.sample.Controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.FONT_HERSHEY_PLAIN;
import static org.opencv.imgproc.Imgproc.getTextSize;

public class Frame {
	private Mat frameCourant;
	private static DecimalFormat df2 = new DecimalFormat(".#");
	private List<ConvertisseurCoordonnees> convertisseursCoordonnees;
	private List<Vehicule> vehicules;
	//===========================================================================
// Constructeurs
//===========================================================================
	public Frame() {
		super();
		this.convertisseursCoordonnees = new ArrayList<ConvertisseurCoordonnees>();
	}
	public Frame(List<ConvertisseurCoordonnees> convertisseursCoordonnees) {
		super();
		this.convertisseursCoordonnees = convertisseursCoordonnees;
	}
	public Frame(Mat frameCourant, List<Vehicule> vehicules) {
		super();
		this.convertisseursCoordonnees = new ArrayList<ConvertisseurCoordonnees>();
		this.frameCourant = frameCourant;
		this.vehicules = vehicules;
	}
	public Frame(Mat frameCourant, List<Vehicule> vehicules , List<ConvertisseurCoordonnees> convertisseursCoordonnees) {
		super();
		this.frameCourant = frameCourant;
		this.vehicules = vehicules;
		this.convertisseursCoordonnees = convertisseursCoordonnees;
	}
	//===========================================================================
// Methodes
//===========================================================================
	public void dessinerReferences() {
		for (ConvertisseurCoordonnees convertisseurCoordonnees : convertisseursCoordonnees) {
			Polygone polygone = convertisseurCoordonnees.getPolygoneImage();
			//System.out.println(polygone.getPolygone().size());
			Scalar couleur;
			if(convertisseurCoordonnees.isContientVehicule()) couleur = new Scalar(0,0,250);
			else couleur = new Scalar(0,250,0);
			Imgproc.line(this.frameCourant, polygone.getSommet(0), polygone.getSommet(1), couleur, 2);
			Imgproc.line(this.frameCourant, polygone.getSommet(1), polygone.getSommet(2), couleur, 2);
			Imgproc.line(this.frameCourant, polygone.getSommet(2), polygone.getSommet(3), couleur, 2);
			Imgproc.line(this.frameCourant, polygone.getSommet(3), polygone.getSommet(0), couleur, 2);
		}
	}
	public void dessinerCompteurDeVoiture(int compteurDeVoiture) {
		int intFontFace = Core.FONT_HERSHEY_SIMPLEX;
		double dblFontScale = (frameCourant.rows() * frameCourant.cols()) / 300000.0;
		int intFontThickness = (int) Math.round(dblFontScale*0.5);
		Size textSize = Imgproc.getTextSize(""+ compteurDeVoiture, intFontFace, dblFontScale, intFontThickness, new int[] {0});
		Point ptTextBottomLeftPosition = new Point();
		ptTextBottomLeftPosition.x = frameCourant.cols() - 1 - (int)((double)textSize.width * 1.25);
		ptTextBottomLeftPosition.y = (int)((double)textSize.height * 1.25);
		Imgproc.putText(frameCourant, "" + compteurDeVoiture, ptTextBottomLeftPosition, intFontFace, dblFontScale, new Scalar(0,255,0), intFontThickness);
	}


	public void dessinerVehiculesInfo(int compteur, List<Vehicule> vehiculesExistants) {

		for (Vehicule vehicule : vehiculesExistants) {
			Rect  rec = vehicule.getRectangleCourant();
			if(vehicule.isDedans()) {
				vehicule.setDeltaFrame(vehicule.getDeltaFrame()+1);
				vehicule.calculerVitesse();
				Size text = getTextSize(df2.format(vehicule.getVitesse()),2,2,2,new int[] {0});
				if(vehicule.pointDeSortie != null && !vehicule.isCapture()) {
					if(Controller.vitesseMax<=vehicule.getVitesse()){
						prendCapture(compteur , rec,vehicule.getVitesse() );
						vehicule.setCapture(true);
					}
				}
				if(Controller.vitesseMax<=vehicule.getVitesse()){
					Imgproc.rectangle(this.frameCourant, new Point(rec.x, rec.y- text.height),new Point(rec.x + rec.width+50,rec.y),new Scalar(0,0,255),-1);
				} else {
					Imgproc.rectangle(this.frameCourant, new Point(rec.x, rec.y- text.height),new Point(rec.x + rec.width+50,rec.y),new Scalar(0,255,0),-1);
				}

				Imgproc.putText(this.frameCourant,""+df2.format(vehicule.getVitesse()), new Point(rec.x+10, rec.y-5), FONT_HERSHEY_PLAIN, 2, new Scalar(0, 0, 0, 255));
			}
			Imgproc.rectangle(this.frameCourant, new Point(rec.x,rec.y), new Point(rec.x+rec.width, rec.y+rec.height), new Scalar(0,0,255), 1);
			for(int j=Math.max(vehicule.getCentres().size()-4,0) ;j<vehicule.getCentres().size() ; j++) {
				Imgproc.circle(this.frameCourant, vehicule.getCentre(j), 1, new Scalar(255,0,0), -1);
				Imgproc.circle(this.frameCourant, new Point(vehicule.getCentre(j).x +1, vehicule.getCentre(j).y), 1, new Scalar(255,255,0), -1);
			}
		}
	}
	public void prendCapture(Integer compteur,Rect rec, double speed) {
		Mat vehicule = new Mat(this.frameCourant,rec);
		Imgcodecs.imwrite(Controller.pathForResult + "/" + compteur + "_" + speed + ".jpeg", vehicule);
	}
	//===========================================================================
// Getters & Setters
//===========================================================================
	public Mat getFrameCourant() {
		return frameCourant;
	}

	public void setFrameCourant(Mat frameCourant) {
		this.frameCourant = frameCourant;
	}
	public List<ConvertisseurCoordonnees> getConvertisseursCoordonnees() {
		return convertisseursCoordonnees;
	}
	public void setConvertisseursCoordonnees(List<ConvertisseurCoordonnees> convertisseursCoordonnees) {
		this.convertisseursCoordonnees = convertisseursCoordonnees;
	}
	public void ajouterConvertisseur(ConvertisseurCoordonnees convertisseurCoordonnees) {
		this.convertisseursCoordonnees.add(convertisseurCoordonnees);
	}
	public List<Vehicule> getVehicules() {
		return vehicules;
	}
	public void setVehicules(List<Vehicule> vehicules) {
		this.vehicules = vehicules;
	}
}