/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 *
 * @author zielonka
 */
public class Hackenbush extends Application {

    volatile static KeyCode c=KeyCode.RIGHT;

    public static KeyCode getKey() {
        return c;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            //System.out.println("scene touche enfoncée!"+e.getCode());
            //System.out.println("scene touche enfoncée!"+KeyCode.A);
            //if (e.getCode()==KeyCode.A) {
                //c=KeyCode.A;
                c=e.getCode();
                e.consume();
            //}
            //System.out.println("Hackenbush valeur de c "+c);
        });
        scene.setOnKeyReleased(e -> {
            //System.out.println("scene touche relachée!");
            c=KeyCode.RIGHT;
            e.consume();
            //System.out.println("Hackenbush valeur de c "+c);
        });
        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
