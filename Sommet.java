package projet;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.HashSet;

public class Sommet extends Circle{

    private static final double RADIUS = 7.0;

    private boolean auSol=false;
    private boolean marque=false;

    static ArrayList<Sommet> pile=new ArrayList<>();
    //static HashSet<Shape> ensemble=new HashSet<>();

    ArrayList<Sommet> adjacents=new ArrayList<>();

    static void pilePush(Sommet s){
        pile.add(s);

    }

    static Sommet pilePop() {
        if (pile.isEmpty()) return null;
        Sommet temp=pile.get(pile.size()-1);
        pile.remove(pile.size()-1);
        return temp;
    }

    public Sommet(Double x,Double y, boolean auSol) {
        this.setCenterX(x);
        this.setCenterY(y);
        this.auSol=auSol;
        this.setRadius(RADIUS);
        if(auSol) {
            this.setFill(Color.RED);
        }else {
            this.setFill(Color.GREEN);
            //this.setFill(FXMLDocumentController.couleurArete);
        }
    }

    public void ajouterAdjacent(Sommet autreSommet){
        this.adjacents.add(autreSommet);
    }

    public void supprimerAdjacent(Sommet autreSommet){
        this.adjacents.remove(autreSommet);
    }

    public boolean estAuSol(){
        return auSol;
    }

    public void setMarque() {
        this.marque=true;
    }

    public void marquerSommetsZero() {
        for (Sommet s: FXMLDocumentController.lesSommets) {
            s.marque=false;
        }
    }

    public boolean estRelieAuSol(){
        //parcours en largeur
        //if (this.auSol) return true;
        pile.clear();
        marquerSommetsZero();
        pilePush(this);
        this.setMarque();
        while(!pile.isEmpty()) {
            Sommet s=pilePop();
            if (s.auSol) return true;
            for (Sommet y: s.adjacents) {
                if (!y.marque) {
                    pilePush(y);
                    y.setMarque();
                }
            }
        }
        return false;
    }

    public HashSet<Sommet> leGraphe() {
        HashSet<Sommet> res =new HashSet<>();
        pile.clear();
        marquerSommetsZero();
        pilePush(this);res.add(this);
        this.setMarque();
        while(!pile.isEmpty()) {
            Sommet s=pilePop();
            for (Sommet y: s.adjacents) {
                if (!y.marque) {
                    pilePush(y);res.add(y);
                    y.setMarque();
                }
            }
        }
    return res;
    }
}
