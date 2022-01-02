package projet;

import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class UtilitairesNim {
    static int[] heap=FXMLDocumentController.heap;
    static int[] heapInit=FXMLDocumentController.heapInit; //pour Nim
    static int[] heapInitCumul;
    static ArrayList<Sommet> lesSommetsNim = FXMLDocumentController.lesSommetsNim;
    //static int indNim=0;
    static int tailleHeap;
    //Pane pane=FXMLDocumentController.pane;

    static public int getIndexSommetNim(int indice) {
        if (indice<=heapInitCumul[0]) return 0;
        for (int i=1;i<tailleHeap;i++) {
            if (indice>heapInitCumul[i-1] && indice <heapInitCumul[i]) {
                return i;
            }
        }
        return -1;
    }

    static public int getSizeSommetNim(int indice) {
        int index=getIndexSommetNim(indice);
        if (index==0) return indice;
        return indice-heapInitCumul[index-1] -1;
    }

    static public int getIndiceSommetNim(Sommet s) {
        return lesSommetsNim.indexOf(s);
        //return -1;
    }

    static public int getIndiceSommetNimByIndex(int index, int size) {
        if (index==0) return size;
        return heapInitCumul[index-1] + size + 1;
        //return -1;
    }


    static public void changerHeapDepuisSommets(Sommet s0, Sommet s1, int[] heap) {
        int ind0=getIndiceSommetNim(s0);
        int ind1=getIndiceSommetNim(s1);
        int ind=ind1;
        if (ind0<ind1) {ind=ind0;}
        int index=getIndexSommetNim(ind);
        int size=getSizeSommetNim(ind);
        heap[index]=size;

    }

    static public void initierCumul(){
        tailleHeap=FXMLDocumentController.tailleHeap;
        heap=FXMLDocumentController.heap;
        heapInit=FXMLDocumentController.heapInit;
        System.out.println("taille= " + tailleHeap);
        heapInitCumul=new int[tailleHeap];
        heapInitCumul[0]=heapInit[0];
        for (int i=1;i<tailleHeap;i++) {
            System.out.println("heapInit:  "+i+" = "+heapInit[i]);
        }
        for (int i=1;i<tailleHeap;i++) {
            System.out.println("index:"+i);
            heapInitCumul[i]=heapInitCumul[i-1] + heapInit[i] + 1;
            System.out.println("  "+heapInitCumul[i]);
        }
      }

    static public void imprimerHeap(int[] heap) {
        for (int i=0;i<tailleHeap;i++) {
            System.out.println("index: "+i+" size: "+heap[i]);
        }
    }

    static public void changerSommetsNimDepuisCoup(Nim.Move coup) {

    }
}
