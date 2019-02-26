/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Acer
 */
public class MainMenuViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // TODO
    }    

    @FXML
    private void clickPlayerVsPlayer(ActionEvent event) 
    {
        
    }

    @FXML
    private void clickPlayerVsAi(ActionEvent event) 
    {
        
    }

    @FXML
    private void clickAiVsAi(ActionEvent event) 
    {
        
    }

    @FXML
    private void clickExit(ActionEvent event) 
    {
        Platform.exit();
    }
    
}
