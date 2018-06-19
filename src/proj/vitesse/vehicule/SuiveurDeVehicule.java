package proj.vitesse.vehicule;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class SuiveurDeVehicule {
	private List<Vehicule> vehiculesCourants;
	private List<Vehicule> vehiculesExistants;
//===========================================================================
// Constructeurs
//===========================================================================
	public SuiveurDeVehicule() {
		super();
		this.vehiculesCourants = new ArrayList<Vehicule>();
		this.vehiculesExistants = new ArrayList<Vehicule>();
	}
	public SuiveurDeVehicule(List<Vehicule> vehiculesCourants, List<Vehicule> vehiculesExistants) {
		super();
		this.vehiculesCourants = vehiculesCourants;
		this.vehiculesExistants = vehiculesExistants;
	}

//===========================================================================
// Methodes
//===========================================================================
	public void comparaisonVehiculesCourantsVehiculesExistans() {

		for (Vehicule vehiculeExistant : this.vehiculesExistants) {
			vehiculeExistant.setCorrespondance(false);
			vehiculeExistant.setNouveauVehicule(false);
			vehiculeExistant.calculProchainePositionPredite();
		}
		for (Vehicule vehiculeCourant : vehiculesCourants) {
				
			int indiceCourteDistance = 0;
			double courteDistance = 100000;
			
			for (int i = 0; i < vehiculesExistants.size(); i++) {
				int index = vehiculeCourant.getCentres().size()-1;
				Point aPoint = vehiculeCourant.getCentre(index);
				Point bPoint = vehiculesExistants.get(i).getProchainePositionPredite();
				double distance = distance2Points(aPoint,bPoint);
				if (distance < courteDistance) {
					courteDistance = distance;
					indiceCourteDistance = i;
			    }
		    }
			if (courteDistance < vehiculeCourant.getDiagonaleCourante() * 0.5) {
				this.modifierVehiculeDansVehiculesExistants(vehiculeCourant, indiceCourteDistance);
			}
			else {
				ajouterNouveauVehicule(vehiculeCourant);
			}
		}
		for (int i =0 ; i<vehiculesExistants.size() ; i++) {
			Vehicule vehiculeExistant = vehiculesExistants.get(i);
			if ((vehiculeExistant.isCorrespondance() == false) && (vehiculeExistant.isNouveauVehicule() == false)) {
				int n = vehiculeExistant.getCompteurNonCorrespondance() + 1;
				vehiculeExistant.setCompteurNonCorrespondance(n);
			}
			if (vehiculeExistant.getCompteurNonCorrespondance() >= 5) {
				vehiculesExistants.remove(i);
			}
		}
	}
	public void modifierVehiculeDansVehiculesExistants(Vehicule vehiculeCourant, int indice) {
		this.vehiculesExistants.get(indice).setContourCourant(vehiculeCourant.getContourCourant());
		this.vehiculesExistants.get(indice).setRectangleCourant(vehiculeCourant.getRectangleCourant());
		Point centre = vehiculeCourant.getCentre(0);
		this.vehiculesExistants.get(indice).ajouterNouveauCentre(centre);
		this.vehiculesExistants.get(indice).setDiagonaleCourante(vehiculeCourant.getDiagonaleCourante());
		this.vehiculesExistants.get(indice).setRapportAspectCourant(vehiculeCourant.getRapportAspectCourant());
		this.vehiculesExistants.get(indice).setCorrespondance(true);
	}
	public void ajouterNouveauVehicule(Vehicule vehiculeCourant) {
		vehiculeCourant.setNouveauVehicule(true);
		this.vehiculesExistants.add(vehiculeCourant);
	}
	public double distance2Points(Point aPoint, Point bPoint) {
		int deltaX = (int) Math.abs(aPoint.x - bPoint.x);
		int deltaY = (int) Math.abs(aPoint.y - bPoint.y);
		return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
	}
//===========================================================================
// Getters & Setters
//===========================================================================
	public List<Vehicule> getVehiculesExistants() {
		return vehiculesExistants;
	}
	public void setVehiculesExistants(List<Vehicule> vehiculesExistants) {
		this.vehiculesExistants = vehiculesExistants;
	}
	public List<Vehicule> getVehiculesCourants() {
		return vehiculesCourants;
	}
	public void setVehiculesCourants(List<Vehicule> vehiculesCourants) {
		this.vehiculesCourants = vehiculesCourants;
	}
	
}
