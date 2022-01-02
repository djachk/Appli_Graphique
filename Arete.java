package projet;

import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;


public class Arete {

    Sommet sommet0,sommet1;
    Shape ligne;
    ArrayList<Circle> petitsCercles;

    public Arete(Sommet sommet0, Sommet sommet1, Shape ligne, ArrayList<Circle> laliste) {
        this.sommet0=sommet0;
        this.sommet1=sommet1;
        this.ligne=ligne;
        this.petitsCercles=laliste;
  /*      this.ligne.setOnMouseClicked((MouseEvent ev) -> {
            if (areteButton.isSelected()) {
                this.ligne.setStroke(couleurArete);
            }
        });*/
    }

    public Sommet getSommet0(){
        return sommet0;
    }

    public Sommet getSommet1(){
        return sommet1;
    }


}
