package proj.vitesse.vehicule;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;



public class 	ConvertisseurCoordonnees extends Video {
	private boolean contientVehicule;
	private Polygone polygoneImage;
	private Polygone polygoneMondeReel;
	private Mat image2World;
	private Mat world2Image;
//===========================================================================
// Constructeurs
//===========================================================================
	public ConvertisseurCoordonnees() {
		super();
		this.contientVehicule = false;
	}
	public ConvertisseurCoordonnees(Polygone polygoneImage, Polygone polygoneMondeReel) {
		super();
		this.contientVehicule = false;
		this.polygoneImage = polygoneImage;
		this.polygoneMondeReel = polygoneMondeReel;
		this.calculerMatricesConversion();
	}
	
//===========================================================================
// Methodes
//===========================================================================
	public void calculerMatricesConversion() {
		MatOfPoint2f coordonneesImage = new MatOfPoint2f(
				polygoneImage.getSommet(0),
				polygoneImage.getSommet(1), 
			    polygoneImage.getSommet(2), 
			    polygoneImage.getSommet(3)
		);
		Mat coordonneesMondeReel = new MatOfPoint2f(
				polygoneMondeReel.getSommet(0),
				polygoneMondeReel.getSommet(1), 
				polygoneMondeReel.getSommet(2), 
				polygoneMondeReel.getSommet(3)
		);
		//matrice de transformation des coordonnées de l'image en coordonnées du monde réel
		image2World = Imgproc.getPerspectiveTransform(coordonneesImage,coordonneesMondeReel);
		//l'inverse est la transformation du monde à l'image
		world2Image = image2World.inv();
	}
	public Point conversionImageMondeReel(Point pointImage) {
		Point coordonneesMondeReel = new Point();
		coordonneesMondeReel.x = (int) (pointImage.x * image2World.get(0, 0)[0] + pointImage.y * image2World.get(0, 1)[0] + image2World.get(0, 2)[0]);
		coordonneesMondeReel.y = (int) (pointImage.x * image2World.get(1, 0)[0] + pointImage.y * image2World.get(1,1)[0] + image2World.get(1,2)[0]);
	    double z = pointImage.x * image2World.get(2,0)[0] + pointImage.y * image2World.get(2, 1)[0] + image2World.get(2,2)[0];
	    coordonneesMondeReel.x /= z;
	    coordonneesMondeReel.y /= z;
	    return coordonneesMondeReel;
	}
	public Point conversionMondeReelImage(Point pointImage) {
		Point coordonneesImage = new Point();
		coordonneesImage.x = (int) (pointImage.x * world2Image.get(0, 0)[0] + pointImage.y * world2Image.get(0, 1)[0] + world2Image.get(0, 2)[0]);
		coordonneesImage.y = (int) (pointImage.x * world2Image.get(1, 0)[0] + pointImage.y * world2Image.get(1,1)[0] + world2Image.get(1,2)[0]);
	    double z = pointImage.x * world2Image.get(2,0)[0] + pointImage.y * world2Image.get(2, 1)[0] + world2Image.get(2,2)[0];
	    coordonneesImage.x /= z;
	    coordonneesImage.y /= z;
	    return coordonneesImage;
	}
//===========================================================================
// Getters & Setters
//===========================================================================
	public Polygone getPolygoneImage() {
		return polygoneImage;
	}
	public void setPolygoneImage(Polygone polygoneImage) {
		this.polygoneImage = polygoneImage;
	}
	public Polygone getPolygoneMondeReel() {
		return polygoneMondeReel;
	}
	public void setPolygoneMondeReel(Polygone polygoneMondeReel) {
		this.polygoneMondeReel = polygoneMondeReel;
	}
	public Mat getImage2World() {
		return image2World;
	}
	public void setImage2World(Mat image2World) {
		this.image2World = image2World;
	}
	public Mat getWorld2Image() {
		return world2Image;
	}
	public void setWorld2Image(Mat world2Image) {
		this.world2Image = world2Image;
	}
	public boolean isContientVehicule() {
		return contientVehicule;
	}
	public void setContientVehicule(boolean contientVehicule) {
		this.contientVehicule = contientVehicule;
	}
	
}
