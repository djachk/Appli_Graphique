/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projet;

import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_TYPED;


/**
 * @author zielonka
 */
public class FXMLDocumentController {

    //@FXML
    //ColorPicker picker;
    @FXML
    ToggleButton sommetButton;
    @FXML
    ToggleButton areteButton;
    @FXML
    ToggleButton lineButton;
    @FXML
    ToggleButton cubicButton;
    @FXML
    ToggleButton quadButton;
    @FXML
    ToggleButton selectButton;
    @FXML
    ToggleButton resetButton;
    @FXML
    ToggleButton supprimerButton;
    @FXML
    ToggleButton dupliquerButton;
    @FXML
    ToggleButton rougeButton;
    @FXML
    ToggleButton bleuButton;
    @FXML
    ToggleButton vertButton;
    @FXML
    BorderPane borderPane;
    @FXML
    Pane pane;
    @FXML
    ToggleGroup toggleSommetArete;
    @FXML
    ToggleGroup toggle;
    @FXML
    ToggleGroup toggleSelect;
    @FXML
    ToggleGroup toggleCouleur;
    @FXML
    ToggleGroup toggleEditionJouer;
    @FXML
    RadioMenuItem edition;
    @FXML
    CheckMenuItem nim;
    @FXML
    RadioMenuItem jouer;
    @FXML
    ToggleGroup toggleMode;
    @FXML
    RadioMenuItem normal;
    @FXML
    RadioMenuItem misere;
    @FXML
    ToggleGroup toggleHumainMachine;
    @FXML
    RadioMenuItem humainHumain;
    @FXML
    RadioMenuItem humainMachine;
    @FXML
    RadioMenuItem machineMachine;



    Line sol;
    static ArrayList<Sommet> lesSommets = new ArrayList<>();
    static ArrayList<Arete> lesAretes = new ArrayList<>();

    static Color couleurArete = Color.GREEN;
    static ArrayList<Shape> laSelection = new ArrayList<>();
    double hauteurSol;
    Sommet sommet0, sommet1;
    volatile int numSommet = 0;
    private AudioClip audioClip,audioClip1;
    boolean modeEdition = true;
    boolean modeNim = false;
    boolean modeJeu = false;
    boolean modeNormal = true;
    boolean modeMisere = false;
    //static KeyCode car= Hackenbush.c;

    /*nombre de sommets dans le dernier PathElement*/
    volatile private int nb = 0;
    //private CubicCurve cubicCurve;
    //private QuadCurve quadCurve;

    /* le rayon de circles */
    private static double RADIUS = 5;

    public Paint joueur=Color.BLUE;

    /* tableau de sommets pour les courbes */
    Circle[] petitsSommets = new Circle[4];

    static int[] heap;
    static int[] heapInit; //pour Nim
    static ArrayList<Sommet> lesSommetsNim=new ArrayList<>();
    static int indNim;
    static int tailleHeap;
    static Nim jeuNim;
    static boolean typeNim;



    private void creerSol() {
        hauteurSol = borderPane.getPrefHeight() - 100.0;
        sol = new Line();
        sol.setStartX(20.0);
        sol.setStartY(borderPane.getPrefHeight() - 100.0);
        sol.setEndX(borderPane.getPrefWidth() - 20.0);
        sol.setEndY(hauteurSol);
        sol.setStrokeWidth(4.0);
        sol.setStroke(Color.BLACK);
        //sol.setOnMouseClicked(sommetSolHandler);
    }

    public void creerPaneHandlers() {
        pane.onMouseClickedProperty()
                .bind(Bindings.when(sommetButton.selectedProperty())
                        .then(sommetHandler)
                        .otherwise(
                                (MouseEvent e) -> {
                                })

                        );
    }

    public void creerPaneHandlersPourCourbe() {
        pane.onMouseClickedProperty()
                .bind(Bindings.when(cubicButton.selectedProperty())
                        .then(cubicHandler)
                        .otherwise(Bindings.when(quadButton.selectedProperty())
                                .then(quadHandler)
                                .otherwise(
                                        (MouseEvent e) -> {

                                        }
                                )));
    }

    @FXML
    public void initialize() {
        System.out.println("Height= " + borderPane.getPrefHeight());
        System.out.println("Width= " + borderPane.getPrefWidth());

        audioClip = new AudioClip(getClass()
                .getResource("1967.wav").toString());
        audioClip1 = new AudioClip(getClass()
                .getResource("bruit1.wav").toString());
        creerSol();
        pane.getChildren().add(sol);
        //pane.setOnMouseClicked(sommetHandler);

        creerPaneHandlers();

        //pane.onMouseClickedProperty().unbind();

        sol.onMouseClickedProperty()
                .bind(Bindings.when(sommetButton.selectedProperty())
                        .then(sommetSolHandler)
                        .otherwise(passerHandler));


        toggleCouleur.selectedToggleProperty()
                .addListener((observable, old, neuf) -> {
                    if (rougeButton.isSelected()) {
                        couleurArete = Color.RED;
                    } else if (bleuButton.isSelected()) {
                        couleurArete = Color.BLUE;
                    } else if (vertButton.isSelected()) {
                        couleurArete = Color.GREEN;
                    }
                });

        toggle.selectedToggleProperty()
                .addListener((observable, old, neuf) -> {
                    /* neuf - le nouveau bouton sélectionné ou null
                     * si aucun bouton sélectionné
                     * old - ancien bouton sélectionné.
                     */

                    if (old != null) {
                        /* Supprimer les sommets du dernier PathElement
                         *  non terminé. */
                        for (int i = nb - 1; i >= 0; i--) {
                            pane.getChildren().remove(petitsSommets[i]);
                        }

                    }
                    /* reinitialiser nb à la fin de la construction de Path */
                    //if (neuf == null) { /* si aucun bouton séléctionné */
                    nb = 0;
                    numSommet = 0;
                    //}
                });
        //sommetButton.setDisable(true);
        //pane.setFocusTraversable(true);

        /*pane.setOnKeyTyped(e -> {
            System.out.println("touche enfoncée!");
            c=KeyCode.F;
        });
        pane.setOnKeyReleased(e -> {
            System.out.println("touche relachée!");
            c=KeyCode.RIGHT;
        });*/

    }


    @FXML
    void quitter(ActionEvent e) {
        Platform.exit();
    }

    @FXML
    void toggleModeEdition(ActionEvent e) {
        if (nim.isSelected()) {
            if (lesSommets.size() != 0) {
                boolean rep = ouiNonAlerte("Pour mode Nim, tout effacer?");
                if (rep) {
                    toutEffacer();
                } else {
                    edition.setSelected(false);
                    return;
                }
            }
            String dims=inputAlerte("taille des fils Nim?");
            System.out.println(dims);
            toutpositionner(true); //disable true
            dessinerNim(dims);
        } else {
            //modeEdition = !modeEdition;
            toutpositionner(false); //disable false
        }
    }

    @FXML
    void toggleModeNim(ActionEvent e) {
        modeNim = !modeNim;
        edition.setSelected(false);
        jouer.setSelected(false);
    }

    @FXML
    void toggleModeJeu(ActionEvent e) {
        System.out.println("je toggle mode jeu");
        modeJeu = !modeJeu;
        toutpositionner(true);
        Slider slider = new Slider(0, 250, 0);
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.setPrefSize(200,40);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().setPrefSize(300,100);
        HBox box=new HBox();
        box.setPrefSize(250,50);
        box.getChildren().add(slider);
        alert.getDialogPane().getChildren().add(box);

        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText("Joueur Bleu commence");
        joueur=Color.BLUE;

        alert.showAndWait();
        //System.out.println(dims.getText());
    }

    @FXML
    void toggleModeNormal(ActionEvent e) {
        modeNormal = !modeNormal;
        modeMisere = !modeNormal;
    }

    @FXML
    void toggleModeMisere(ActionEvent e) {
        modeMisere = !modeMisere;
        modeNormal = !modeMisere;
    }

    @FXML
    void effacer(ActionEvent e) {
        toutEffacer();
    }

    public void toutEffacer() {
        for (Sommet sommet : lesSommets) {
            pane.getChildren().remove(sommet);
        }
        for (Arete arete : lesAretes) {
            if (arete.petitsCercles != null) {
                for (Circle petitcercle : arete.petitsCercles) {
                    pane.getChildren().remove(petitcercle);
                }
            }
            pane.getChildren().remove(arete.ligne);
        }
        for (int i = 0; i <= nb; i++) {
            pane.getChildren().remove(petitsSommets[i]);
        }
        lesSommets.clear();
        lesAretes.clear();
    }

    void toutpositionner(boolean d) {
        sommetButton.setSelected(false);sommetButton.setDisable(d);
        areteButton.setSelected(false);areteButton.setDisable(d);
        selectButton.setSelected(false);selectButton.setDisable(d);
        lineButton.setSelected(false);lineButton.setDisable(d);
        cubicButton.setSelected(false);cubicButton.setDisable(d);
        quadButton.setSelected(false);quadButton.setDisable(d);
        resetButton.setSelected(false);resetButton.setDisable(d);
        supprimerButton.setSelected(false);supprimerButton.setDisable(d);
        dupliquerButton.setSelected(false);dupliquerButton.setDisable(d);
        rougeButton.setSelected(false);rougeButton.setDisable(d);
        vertButton.setSelected(false);vertButton.setDisable(d);
        bleuButton.setSelected(false);bleuButton.setDisable(d);
    }

    public boolean existeArete(Sommet s0, Sommet s1) {
        for (Arete arete : lesAretes) {
            if (arete.sommet0.equals(s0) && arete.sommet1.equals(s1)) {
                //System.out.println("arete existe deja!");
                return true;
            }
        }
        return false;
    }

    public boolean existeAreteBi(Sommet s0, Sommet s1) {
        for (Arete arete : lesAretes) {
            if (arete.sommet0.equals(s0) && arete.sommet1.equals(s1)) {
                //System.out.println("arete existe deja!");
                return true;
            }
            if (arete.sommet0.equals(s1) && arete.sommet1.equals(s0)) {
                //System.out.println("arete existe deja!");
                return true;
            }
        }
        return false;
    }
    public Arete getArete(Sommet s0, Sommet s1) {
        for (Arete arete : lesAretes) {
            if (arete.sommet0.equals(s0) && arete.sommet1.equals(s1)) {
                //System.out.println("arete existe en effet!");
                return arete;
            }
        }
        return null;
    }

    public Arete getAreteByLigne(Shape ligne) {
        for (Arete arete : lesAretes) {
            if (arete.ligne.equals(ligne)) {
                //System.out.println("arete existe en effet!");
                return arete;
            }
        }
        return null;
    }

    public void changerCouleur(Sommet s0, Sommet s1) {
        for (Arete arete: lesAretes) {
            if (arete.getSommet0().equals(s0) && arete.getSommet1().equals(s1)) {
                arete.ligne.setStroke(couleurArete);
            }
            if (arete.getSommet0().equals(s1) && arete.getSommet1().equals(s0)) {
                arete.ligne.setStroke(couleurArete);
            }
        }
    }

    private void creerArete() {
        System.out.println("valeur de c"+Hackenbush.c);
        if ((Hackenbush.c==KeyCode.A)&&(edition.isSelected())) {
            System.out.println("OK F detecte");
            supprimerLesAretes(sommet0,sommet1);
            //supprimerArete(sommet0,sommet1);
            //supprimerArete(sommet1,sommet0);
            return;
        }

        if (sommet0.estAuSol() && sommet1.estAuSol()) return;
        //if (!sommet0.equals(sommet1) && existeArete(sommet0, sommet1)) {
        //    getArete(sommet0, sommet1).ligne.setStroke(couleurArete);
        //    return;
        //}
        if ((Hackenbush.c==KeyCode.C)&& (edition.isSelected())){
            //getArete(sommet1, sommet0).ligne.setStroke(couleurArete);
            changerCouleur(sommet0,sommet1);
            return;
        }
        if (cubicButton.isSelected()) {
            pane.onMouseClickedProperty().unbind();
            creerPaneHandlersPourCourbe();

        } else if (quadButton.isSelected()) {
            if (sommet0.equals(sommet1)) return;
            pane.onMouseClickedProperty().unbind();
            creerPaneHandlersPourCourbe();

        } else if (lineButton.isSelected()) {
            if (sommet0.equals(sommet1)) return;
            Line ligne = new Line(sommet0.getCenterX(), sommet0.getCenterY(), sommet1.getCenterX(), sommet1.getCenterY());
            ligne.setStroke(couleurArete);
            ligne.setStrokeWidth(2.0);
            ligne.startXProperty().bind(sommet0.centerXProperty());
            ligne.startYProperty().bind(sommet0.centerYProperty());
            ligne.endXProperty().bind(sommet1.centerXProperty());
            ligne.endYProperty().bind(sommet1.centerYProperty());
            ligne.setOnMouseClicked(areteSeuleHandler);
            Arete newArete = new Arete(sommet0, sommet1, ligne, null);
            //lesAretes.add(newArete);
            if (!sommet0.equals((sommet1))) {
                if (!existeAreteBi(sommet0,sommet1)) {
                    sommet0.ajouterAdjacent((sommet1));
                    sommet1.ajouterAdjacent(sommet0);
                }
            }
            lesAretes.add(newArete);

            pane.getChildren().add(ligne);
            /*ligne.setOnMouseClicked((MouseEvent ev) -> {
                audioClip.play();
                if (areteButton.isSelected()) {
                    System.out.println("je dois changer couleur arete...");
                    ligne.setStroke(couleurArete);
                    ev.consume();
                }
            });*/
        }
    }

    public void supprimerAreteSeuleEdition(Shape ligne) {
        //if ((Hackenbush.getKey()!=KeyCode.R)) return;
        Arete arete=getAreteByLigne(ligne);
        Sommet s0=arete.getSommet0();
        Sommet s1=arete.getSommet1();
        if (arete.petitsCercles != null) {
            for (Circle petitcercle : arete.petitsCercles) {
                pane.getChildren().remove(petitcercle);
            }
        }
        pane.getChildren().remove(arete.ligne);
        lesAretes.remove(arete);
        if (!existeAreteBi(s0,s1)) {
            s0.supprimerAdjacent(s1);
            s1.supprimerAdjacent(s0);
        }
    }



    public void supprimerArete(Sommet s0, Sommet s1) {
        if (existeArete(s0, s1)) {
            Arete arete = getArete(s0, s1);
            if (arete.petitsCercles != null) {
                for (Circle petitcercle : arete.petitsCercles) {
                    pane.getChildren().remove(petitcercle);
                }
            }
            pane.getChildren().remove(arete.ligne);
            lesAretes.remove(arete);
            if (!existeAreteBi(s0,s1)) {
                s0.supprimerAdjacent(s1);
                s1.supprimerAdjacent(s0);
            }
        }
        return;
    }

    public void supprimerLesAretes(Sommet s0, Sommet s1) {
        Arete arete=getArete(s0,s1);
        while(arete!=null) {
            supprimerArete(s0,s1);
            arete=getArete(s0,s1);
        }
        arete=getArete(s1,s0);
        while(arete!=null) {
            supprimerArete(s1,s0);
            arete=getArete(s1,s0);
        }
    }

    /*public static FadeTransition makeFadeTransition(Node node, int duree) {
        double startOpacity = 1.0;
        double endOpacity = 0.05;
        FadeTransition ft = new FadeTransition();
        ft.setNode(node);
        ft.setDuration(Duration.seconds(duree));
        ft.setAutoReverse(false);
        ft.setFromValue(startOpacity);
        ft.setToValue(endOpacity);
        return ft;
    }


    public void faireTransition(Sommet s0, Sommet s1) {
        for (Arete arete : lesAretes) {
            if (arete.sommet0.equals(s0) && arete.sommet1.equals(s1)) {
                Shape ligne = arete.ligne;
                Transition fadeTransition = makeFadeTransition(ligne, 4);
                fadeTransition.play();
                System.out.println("j ai lance un transition play");
            }
        }
    }*/


    public boolean estRelieArete(Sommet s) {
        //if (lesAretes.isEmpty()) return false;
        for (Arete arete: lesAretes) {
            if(arete.sommet0.equals(s)||arete.sommet1.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void supprimerSommet(Sommet s) {
        if (!estRelieArete(s)) {
            pane.getChildren().remove(s);
            lesSommets.remove(s);
        }
        return;
    }

    public boolean memecouleur () {
        Paint coul=joueur,coulcour;
        boolean res=true;
        for (Arete arete: lesAretes) {
            if((arete.sommet0.equals(sommet0) && arete.sommet1.equals(sommet1))||
                    (arete.sommet0.equals(sommet1) && arete.sommet1.equals(sommet0)) ) {
                coulcour=arete.ligne.getStroke();
                if ((coulcour!=Color.GREEN) && (coulcour!=joueur)) {
                    res=false;
                }
            }
        }
        return res;
    }

    public void supprimerAreteSeuleJouer(Shape ligne) {
        //if ((Hackenbush.getKey()!=KeyCode.R)) return;
        Paint coul=joueur,coulcour;
        if (coul!=ligne.getStroke() && ligne.getStroke()!=Color.GREEN) {
            direAlerte("pas la bonne couleur...");
            return;
        }
        Arete arete=getAreteByLigne(ligne);
        Sommet som0=arete.getSommet0();
        Sommet som1=arete.getSommet1();
        supprimerAreteSeuleEdition(ligne);
        if (!existeAreteBi(som0,som1)) {
            supprimerGraphe(som0,som1);
        }
        if (joueur==Color.BLUE) {
            if (lesAretes.isEmpty()) { direAlerte("joueur Bleu a gagné!"); return;}
            joueur=Color.RED;
            direAlerte("au joueur rouge de jouer");
        }else if (joueur==Color.RED) {
            if (lesAretes.isEmpty()) { direAlerte("joueur Rouge a gagné!"); return;}
            joueur=Color.BLUE;
            direAlerte("au joueur bleu de jouer");
        }
    }

    public void jouerArete() {
        if (!existeAreteBi(sommet0,sommet1)) return;
        if (!memecouleur()) {
            direAlerte("pas la bonne couleur...");
            return;
        }
        if (nim.isSelected()) {
            jouerAreteNim();
            System.out.println("joueur a joué");
            UtilitairesNim.imprimerHeap(heap);
            if (!humainMachine.isSelected()) {
                if (joueur == Color.BLUE) {
                    if (lesAretes.isEmpty()) { direAlerte("joueur Bleu a gagné!"); return;}
                    joueur = Color.RED;
                    direAlerte("au joueur rouge de jouer");
                } else if (joueur == Color.RED) {
                    if (lesAretes.isEmpty()) { direAlerte("joueur Rouge a gagné!"); return;}
                    joueur = Color.BLUE;
                    direAlerte("au joueur bleu de jouer");
                }
            } else {
                    direAlerte("machine joue");
                    //machine joue
                    Nim.Move coup;
                    coup=jeuNim.nextMove(heap);
                    if (coup==Nim.Move.EMPTY) {
                        direAlerte("machine a perdu!");
                        return;
                    } else {
                        try{Nim.applyMove(coup,heap);}
                        catch (Exception e) {e.printStackTrace();direAlerte("machine n' a pas pu jouer");return;}
                        int index=coup.getIndex();
                        int size=coup.getSize();
                        System.out.println("machine a joué: index= "+index+" , size= "+size);
                        UtilitairesNim.imprimerHeap(heap);
                        int ind0=UtilitairesNim.getIndiceSommetNimByIndex(index,size);
                        int ind1=UtilitairesNim.getIndiceSommetNimByIndex(index,size+1);
                        Sommet s0=lesSommetsNim.get(ind0);
                        Sommet s1=lesSommetsNim.get(ind1);
                        supprimerLesAretes(s0,s1);
                        supprimerGraphe(s0,s1);
                        if (lesAretes.isEmpty()) {
                            direAlerte("machine a gagné!");
                            return;
                        }
                    }
                    direAlerte("au joueur bleu de jouer");
            }
        }else {
            jouerAreteNormal();
            if (joueur==Color.BLUE) {
                if (lesAretes.isEmpty()) { direAlerte("joueur Bleu a gagné!"); return;}
                joueur=Color.RED;
                direAlerte("au joueur rouge de jouer");
            }else if (joueur==Color.RED) {
                if (lesAretes.isEmpty()) { direAlerte("joueur Rouge a gagné!"); return;}
                joueur=Color.BLUE;
                direAlerte("au joueur bleu de jouer");
            }
        }
    }

    public void supprimerGraphe(Sommet som0, Sommet som1) {
        if (!som0.estRelieAuSol()) {
            System.out.println("le sommet 0 n'est pas relie au sol!");
            HashSet<Sommet> leGraphe=som0.leGraphe();
            /*for (Sommet s0: leGraphe) {
                for (Sommet s1: leGraphe) {
                    System.out.println("je fais transition");
                    faireTransition(s0,s1);
                }
            }
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            for (Sommet s0: leGraphe) {
                for (Sommet s1: leGraphe) {
                    supprimerLesAretes(s0,s1);
                }
            }
            for (Sommet s: leGraphe) {
                supprimerSommet(s);
            }
            //sommet0.effacerGraphe();
        }
        if (!som1.estRelieAuSol()) {
            //sommet1.effacerGraphe();
            System.out.println("le sommet 1 n'est pas relie au sol!");
            HashSet<Sommet> leGraphe=som1.leGraphe();
            /*for (Sommet s0: leGraphe) {
                for (Sommet s1: leGraphe) {
                    System.out.println("je fais transition");
                    faireTransition(s0,s1);
                }
            }
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            for (Sommet s0: leGraphe) {
                for (Sommet s1: leGraphe) {
                    supprimerLesAretes(s0,s1);
                }
            }
            for (Sommet s: leGraphe) {
                supprimerSommet(s);
            }
        }
    }

    public void jouerAreteNim() {
        supprimerLesAretes(sommet0,sommet1);
        supprimerGraphe(sommet0,sommet1);
        UtilitairesNim.changerHeapDepuisSommets(sommet0, sommet1, heap);
    }


    public void jouerAreteNormal() {
        //if (!memecouleur()) {
        //    direAlerte("pas la bonne couleur...");
        //    return;
        //}
        supprimerLesAretes(sommet0,sommet1);
        supprimerGraphe(sommet0,sommet1);
     }

    public void creerTige(Double x, int nbBranches) {
        Sommet s0,s1;
        s0=creerUnSommet(x,hauteurSol,true);
        lesSommetsNim.add(s0); indNim++;
        Double longu=(pane.getHeight()/(float) nbBranches);
        if (longu>pane.getHeight()-40.0) {longu-=40.0;}
        Double hCourant=hauteurSol;
        for (int i=0; i<nbBranches;i++) {
            s1=creerUnSommet(x,hCourant-longu,false);
            lesSommetsNim.add(s1); indNim++;
            Line ligne = new Line(s0.getCenterX(), s0.getCenterY(), s1.getCenterX(), s1.getCenterY());
            ligne.setStroke(Color.GREEN);
            ligne.startXProperty().bind(s0.centerXProperty());
            ligne.startYProperty().bind(s0.centerYProperty());
            ligne.endXProperty().bind(s1.centerXProperty());
            ligne.endYProperty().bind(s1.centerYProperty());
            Arete newArete = new Arete(s0, s1, ligne, null);
            lesAretes.add(newArete);
            s0.ajouterAdjacent((s1));
            s1.ajouterAdjacent(s0);
            pane.getChildren().add(ligne);
            s0=s1;
            hCourant-=longu;
            longu*=4.0/5.0;
        }
    }

    public Sommet creerSommet(MouseEvent e,boolean auSol) {
        return creerUnSommet(e.getX(),e.getY(),auSol);
    }

    public Sommet creerUnSommet(Double x, Double y, boolean auSol) {
        Sommet newSommet = new Sommet(x, y , auSol);
        lesSommets.add(newSommet);
        System.out.println("de sommetHandler, size= " + lesSommets.size());
        pane.getChildren().add(newSommet);
        newSommet.setOnMouseClicked(ev -> {
            audioClip.play();
            System.out.println("dans sommet, c= "+Hackenbush.c);
            if ((Hackenbush.c==KeyCode.S)&&(edition.isSelected())) {
                System.out.println("je vais supprimer ce sommet");
                supprimerSommet(newSommet);
                return;
            }
            if (selectButton.isSelected()) {
                laSelection.add(newSommet);
            } else {
                if (numSommet == 0) {
                    numSommet++;
                    sommet0 = newSommet;

                } else if (numSommet == 1) {
                    sommet1 = newSommet;
                    numSommet = 0;
                    if (areteButton.isSelected()) {
                        System.out.println("arete est bien selectionné");
                        creerArete();
                    } else if (jouer.isSelected()) {
                        jouerArete();
                    }
                }
            }
            int ind = lesSommets.indexOf(newSommet);
            System.out.println("index= " + ind);
            ev.consume();

        });
        if (!auSol) {
            newSommet.setOnMouseDragged((MouseEvent z) -> {
                newSommet.setCenterX(z.getX());
                newSommet.setCenterY(z.getY());
                z.consume();
            });
        }
        return newSommet;
    }

    private EventHandler<MouseEvent> sommetHandler
            = (MouseEvent e) -> {
        if (e.getY() <= hauteurSol) {
            creerSommet(e, false);
        }
    };

    private EventHandler<MouseEvent> sommetSolHandler
            = (MouseEvent e) -> {
        creerSommet(e, true);
        e.consume();
    };

    private EventHandler<MouseEvent> passerHandler
            = (MouseEvent e) -> {
    };

    private EventHandler<MouseEvent> areteSeuleHandler
            = (MouseEvent e) -> {
                    //if ((Hackenbush.c==KeyCode.R)&&(edition.isSelected())) {
        //if ((Hackenbush.getKey()==KeyCode.R))
            System.out.println("keycode = "+Hackenbush.c);
            if (edition.isSelected()) {
                if ((Hackenbush.getKey()==KeyCode.R)) {
                    audioClip1.play();
                    Shape ligne = (Shape) e.getSource();
                    supprimerAreteSeuleEdition(ligne);
                }
            } else if (jouer.isSelected()) {
                audioClip1.play();
                Shape ligne = (Shape) e.getSource();
                supprimerAreteSeuleJouer(ligne);
            }

        e.consume();
    };

    /* définir Hadler pour la construction de CubicCurveTo */
    private EventHandler<MouseEvent> cubicHandler
            = (MouseEvent e) -> {
        petitsSommets[nb] = createVertex(e);
        pane.getChildren().add(petitsSommets[nb]);


        switch (nb) {
            case 0:
                petitsSommets[nb++].setFill(Color.BLACK);
                return;
            case 1:
                /* construire CubicCurveTo */
                petitsSommets[nb++].setFill(Color.BLACK);
                CubicCurve cubicCurve = new CubicCurve();
                cubicCurve.setFill(null);
                cubicCurve.setStroke(couleurArete);
                cubicCurve.setStrokeWidth(2.0);
                cubicCurve.startXProperty()
                        .bind(sommet0.centerXProperty());
                cubicCurve.startYProperty()
                        .bind(sommet0.centerYProperty());
                cubicCurve.controlX1Property()
                        .bind(petitsSommets[0].centerXProperty());
                cubicCurve.controlY1Property()
                        .bind(petitsSommets[0].centerYProperty());
                cubicCurve.controlX2Property()
                        .bind(petitsSommets[1].centerXProperty());
                cubicCurve.controlY2Property()
                        .bind(petitsSommets[1].centerYProperty());
                cubicCurve.endXProperty()
                        .bind(sommet1.centerXProperty());
                cubicCurve.endYProperty()
                        .bind(sommet1.centerYProperty());
                cubicCurve.setOnMouseClicked(areteSeuleHandler);

                //path.getElements().add(cubicCurve);
                ArrayList<Circle> laliste = new ArrayList<>();
                laliste.add(petitsSommets[0]);
                laliste.add(petitsSommets[1]);
                nb = 0;
                Arete newArete = new Arete(sommet0, sommet1, cubicCurve, laliste);
                if (!sommet0.equals((sommet1))) {
                    if (!existeAreteBi(sommet0,sommet1)) {
                        sommet0.ajouterAdjacent((sommet1));
                        sommet1.ajouterAdjacent(sommet0);
                    }
                }
                lesAretes.add(newArete);
                pane.getChildren().add(cubicCurve);

                pane.onMouseClickedProperty().unbind();
                creerPaneHandlers();
                /*cubicCurve.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
                    audioClip.play();
                    if (areteButton.isSelected()) {
                        cubicCurve.setStroke(couleurArete);
                        ev.consume();
                    }
                 });*/
                e.consume();
        }
    };

    /* définir Hadler pour la construction de QuadCurveTo */
    private EventHandler<MouseEvent> quadHandler
            = (MouseEvent e) -> {
        petitsSommets[nb] = createVertex(e);
        pane.getChildren().add(petitsSommets[nb]);
        petitsSommets[nb++].setFill(Color.BLACK);

        QuadCurve quadCurve = new QuadCurve();
        quadCurve.setFill(null);
        quadCurve.setStroke(couleurArete);
        quadCurve.setStrokeWidth(2.0);
        quadCurve.startXProperty().bind(sommet0.centerXProperty());
        quadCurve.startYProperty().bind(sommet0.centerYProperty());
        quadCurve.controlXProperty().bind(petitsSommets[0].centerXProperty());
        quadCurve.controlYProperty().bind(petitsSommets[0].centerYProperty());
        quadCurve.endXProperty().bind(sommet1.centerXProperty());
        quadCurve.endYProperty().bind(sommet1.centerYProperty());
        quadCurve.setOnMouseClicked(areteSeuleHandler);
        //path.getElements().add(quadCurve);
        ArrayList<Circle> laliste = new ArrayList<>();
        laliste.add(petitsSommets[0]);
        nb = 0;
        Arete newArete = new Arete(sommet0, sommet1, quadCurve, laliste);
        if (!sommet0.equals((sommet1))) {
            if (!existeAreteBi(sommet0,sommet1)) {
                sommet0.ajouterAdjacent((sommet1));
                sommet1.ajouterAdjacent(sommet0);
            }
        }
        lesAretes.add(newArete);
        pane.getChildren().add(quadCurve);

        pane.onMouseClickedProperty().unbind();
        creerPaneHandlers();
        /*quadCurve.setOnMouseClicked((MouseEvent ev) -> {
            audioClip.play();
            if (areteButton.isSelected()) {
                quadCurve.setStroke(couleurArete);
                ev.consume();
            }
        });*/
        e.consume();
    };

    private Circle createVertex(MouseEvent e) {
        Circle vertex = new Circle();
        vertex.setCenterX(e.getX());
        vertex.setCenterY(e.getY());
        vertex.setRadius(RADIUS);

        vertex.setOnMouseDragged((MouseEvent z) -> {
            vertex.setCenterX(z.getX());
            vertex.setCenterY(z.getY());
        });

        return vertex;
    }

    public void direAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.getDialogPane().setPrefSize(300, 100);
        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean ouiNonAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Question Dialog");
        alert.getDialogPane().setPrefSize(300, 100);
        alert.setResizable(true);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result=alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {return false;}
    }

    public String inputAlerte(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Input Dialog");
        alert.getDialogPane().setPrefSize(300,100);

        GridPane params=new GridPane();
        Label label = new Label(message);
        TextField resField=new TextField();

        params.add(label,0,0);
        params.add(resField,0,1);

        alert.getDialogPane().setContent(params);

        alert.setResizable(true);
        alert.setHeaderText(null);

        alert.showAndWait();
        return resField.getText();
    }

    public void dessinerNim(String dims) {
        String[] lesDims=dims.split(",");
        tailleHeap=lesDims.length;
        heap=new int[tailleHeap];
        heapInit=new int[tailleHeap];
        lesSommetsNim.clear();
        indNim=0;
        jeuNim=new Nim(true);
        try {
            int rang=0;
            int n=lesDims.length;
            double delta=pane.getWidth()/(float)(n+1);
            for (String s : lesDims) {
                int i = Integer.parseInt(s);
                heap[rang]=i;
                heapInit[rang]=i;
                //System.out.println("entier= " + i);
                creerTige(delta*(rang+1),i);
                rang++;
            }
        }catch(Exception e) {
            e.printStackTrace();
            direAlerte("probleme dans les dimensions");
            edition.setSelected(false);
            return;
        }
        System.out.println("taille dans controller= " + tailleHeap);
        UtilitairesNim.initierCumul();
    }

}
