/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ultimatetictactoe.bll.bot.IBot;
import ultimatetictactoe.gui.model.GameModel;

/**
 * FXML Controller class
 *
 * @author Acer
 */
public class MainMenuViewController implements Initializable {

    @FXML
    private ToggleGroup GameMode;
    @FXML
    private ToggleButton btnPlayerVsPlayer;
    @FXML
    private ToggleButton btnPlayerVsBot;
    @FXML
    private ToggleButton btnBotVsBot;
    @FXML
    private StackPane stcFirstOption;
    @FXML
    private StackPane stcSecondOption;
    
    private GameModel model;
    private TextField txtPlayer1;
    private TextField txtPlayer2;
    private ComboBox cmbBot1;
    private ComboBox cmbBot2;
    @FXML
    private VBox vbxOptions;
    
    public MainMenuViewController()
    {
        model = GameModel.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        initializeUserAndBotFields();
        btnPlayerVsPlayer.fire();
    }    
    
    private void initializeUserAndBotFields()
    {
        initializeUserFields();
        initializeBotFields();
    }
    
    private void initializeUserFields()
    {
        txtPlayer1 = createUserField();
        txtPlayer2 = createUserField();
        txtPlayer1.setPromptText("Player 1");
        txtPlayer2.setPromptText("Player 2");
    }
    
    private void initializeBotFields()
    {
        cmbBot1 = createBotField();
        cmbBot2 = createBotField();
    }

    @FXML
    private void clickPlayerVsPlayer(ActionEvent event) 
    {
        clearOptions();
        stcFirstOption.getChildren().add(txtPlayer1);
        stcSecondOption.getChildren().add(txtPlayer2);
    }

    @FXML
    private void clickPlayerVsBot(ActionEvent event) 
    {
        clearOptions();
        stcFirstOption.getChildren().add(txtPlayer1);
        stcSecondOption.getChildren().add(cmbBot2);
    }

    @FXML
    private void clickBotVsBot(ActionEvent event) 
    {
        clearOptions();
        stcFirstOption.getChildren().add(cmbBot1);
        stcSecondOption.getChildren().add(cmbBot2);
        
    }

    @FXML
    private void clickExit(ActionEvent event) 
    {
        Platform.exit();
    }
    
    private void clearOptions()
    {
        stcFirstOption.getChildren().clear();
        stcSecondOption.getChildren().clear();
    }
    
    private TextField createUserField()
    {
        TextField txt = new TextField();
        txt.setMaxHeight(50);
        txt.setMaxWidth(400);
        txt.textProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue.length() >= 10) 
            {
                ((StringProperty)observable).setValue(oldValue);
            }
        });
        return txt;
    }
    
    private ComboBox createBotField()
    {
        ComboBox cmb = new ComboBox();
        cmb.setMaxHeight(50);
        cmb.setMaxWidth(400);
        cmb.setItems(model.getAllBots());
        cmb.getSelectionModel().selectFirst();
        return cmb;
    }

    @FXML
    private void clickStart(ActionEvent event) throws IOException 
    {
        setNewGame();
        showGameView();
    }
    
    private void setNewGame()
    {
        IBot firstBot = (IBot) cmbBot1.getSelectionModel().getSelectedItem();
        IBot secondBot = (IBot) cmbBot2.getSelectionModel().getSelectedItem();
        if(btnPlayerVsPlayer.isSelected())
        {
            model.newPlayerVsPlayerGame();
        }
        else if(btnPlayerVsBot.isSelected())
        {
            model.newPlayerVsBotGame(secondBot);
        }
        else
        {
            model.newBotVsBotGame(firstBot, secondBot);
        }
    }
    
    private void showGameView() throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ultimatetictactoe/gui/view/GameView.fxml"));
        Parent root = fxmlLoader.load();   
        GameViewController controller = fxmlLoader.getController();
        Stage stage = (Stage) btnPlayerVsPlayer.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.hide();
        setGameView(stage,scene,controller);
        stage.show();
    }
    
    private void setGameView(Stage stage, Scene scene, GameViewController controller)
    {
        setStageForGameView(stage, scene);
        setPlayerLabelsOnGameView(controller);
    }
    
    private void setStageForGameView(Stage stage, Scene scene)
    {
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(1280);
        stage.setMinHeight(1024);
        stage.centerOnScreen();
    }
    
    private void setPlayerLabelsOnGameView(GameViewController controller)
    {
        String player1 = "Player 1";
        String player2 = "Player 2";
        if(btnPlayerVsPlayer.isSelected())
        {
            if(!txtPlayer1.getText().isEmpty())
            {
                player1 = txtPlayer1.getText();
            }
            if(!txtPlayer2.getText().isEmpty())
            {
                player2 = txtPlayer2.getText();
            }
        }
        else if(btnPlayerVsBot.isSelected())
        {
            if(!txtPlayer1.getText().isEmpty())
            {
                player1 = txtPlayer1.getText();
            }
            player2 = cmbBot2.getSelectionModel().getSelectedItem().toString();
        }
        else
        {
            player1 = cmbBot1.getSelectionModel().getSelectedItem().toString();
            player2 = cmbBot2.getSelectionModel().getSelectedItem().toString();
        }
        controller.setPlayerLabels(player1, player2);
    }
    
}
