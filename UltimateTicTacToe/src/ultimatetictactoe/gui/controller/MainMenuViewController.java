/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ultimatetictactoe.bll.bot.IBot;
import ultimatetictactoe.gui.model.GameModel;
import ultimatetictactoe.gui.util.AnimationUtil;

/**
 * FXML Controller class
 *
 * @author Acer
 */
public class MainMenuViewController implements Initializable {

    private enum GameModes 
    {
    PVP,PVE,EVE
    }    
    @FXML
    private ToggleGroup GameMode;
    @FXML
    private Rectangle rctPlayerVsBot;
    @FXML
    private Rectangle rctBotVsBot;
    @FXML
    private Rectangle rctPlayerVsPlayer;
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
    private GameModes gameMode;
    
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
        setToggleButtons();
        gameMode = GameModes.PVP;
    }    
    
    private void setToggleButtons()
    {        
        btnPlayerVsPlayer.fire();
        GameMode.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
            {
                oldVal.setSelected(true);
            }
        });
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
        stcFirstOption.getChildren().add(txtPlayer1);
        stcSecondOption.getChildren().add(txtPlayer2);
    }
    
    private void initializeBotFields()
    {
        cmbBot1 = createBotField();
        cmbBot2 = createBotField();
    }

    @FXML
    private void clickPlayerVsPlayer(ActionEvent event) 
    {
        if (gameMode == GameModes.EVE)
        {
            gameMode = GameModes.PVP;
            clearOptions();
            clearRectangles();
            rctPlayerVsPlayer.setDisable(false);
            stcFirstOption.getChildren().add(cmbBot1);
            stcSecondOption.getChildren().add(cmbBot2);
            showAnimationSlideRightExit(stcFirstOption,stcSecondOption);
        }
        if (gameMode == GameModes.PVE)
        {
            gameMode = GameModes.PVP;
            clearOptions();
            clearRectangles();
            rctPlayerVsPlayer.setDisable(false);
            stcFirstOption.getChildren().add(txtPlayer1);
            stcSecondOption.getChildren().add(cmbBot2);
            showAnimationSlideLeftExit(stcFirstOption,stcSecondOption);
        }
    }

    @FXML
    private void clickPlayerVsBot(ActionEvent event) 
    {
        if (gameMode == GameModes.PVP)
        {
            gameMode = GameModes.PVE;
            clearOptions();
            clearRectangles();
            rctPlayerVsBot.setDisable(false);
            stcFirstOption.getChildren().add(txtPlayer1);
            stcSecondOption.getChildren().add(txtPlayer2);
            showAnimationSlideRightExit(stcFirstOption,stcSecondOption);
        }
        if (gameMode == GameModes.EVE)
        {
            gameMode = GameModes.PVE;
            clearOptions();
            clearRectangles();
            rctPlayerVsBot.setDisable(false);
            stcFirstOption.getChildren().add(cmbBot1);
            stcSecondOption.getChildren().add(cmbBot2);
            showAnimationSlideLeftExit(stcFirstOption,stcSecondOption);
        }
    }

    @FXML
    private void clickBotVsBot(ActionEvent event) 
    {
        if (gameMode == GameModes.PVE)
        {
            gameMode = GameModes.EVE;
            clearOptions();
            clearRectangles();
            rctBotVsBot.setDisable(false);
            stcFirstOption.getChildren().add(txtPlayer1);
            stcSecondOption.getChildren().add(cmbBot2);
            showAnimationSlideRightExit(stcFirstOption,stcSecondOption);
        }
        if (gameMode == GameModes.PVP)
        {
            gameMode = GameModes.EVE;
            clearOptions();
            clearRectangles();
            rctBotVsBot.setDisable(false);
            stcFirstOption.getChildren().add(txtPlayer1);
            stcSecondOption.getChildren().add(txtPlayer2);
            showAnimationSlideLeftExit(stcFirstOption,stcSecondOption);
        }    
    }

    @FXML
    private void clickExit(ActionEvent event) 
    {
        Platform.exit();
    }
    
    private void clearRectangles()
    {
        rctPlayerVsPlayer.setDisable(true);
        rctPlayerVsBot.setDisable(true);
        rctBotVsBot.setDisable(true);
    }
    
    private void clearOptions()
    {
        stcFirstOption.getChildren().clear();
        stcSecondOption.getChildren().clear();
    }
    
    private void showAnimationSlideRightExit(StackPane firstOption, StackPane secondOption) {
        List<Node> elements = new ArrayList();
        elements.add(firstOption);
        elements.add(secondOption);
        ParallelTransition transition = AnimationUtil.createHorizontalSlide(0, -800, elements);
        transition.setOnFinished(new EventHandler(){
            @Override
            public void handle(Event event) {  
            clearOptions();
            if (gameMode == GameModes.PVP)
                {
                stcFirstOption.getChildren().add(txtPlayer1);
                stcSecondOption.getChildren().add(txtPlayer2);
                showAnimationSlideRightEnter(stcFirstOption,stcSecondOption);
                }
            if (gameMode == GameModes.PVE)
                {
                stcFirstOption.getChildren().add(txtPlayer1);
                stcSecondOption.getChildren().add(cmbBot2);
                showAnimationSlideRightEnter(stcFirstOption,stcSecondOption);
                }
            if (gameMode == GameModes.EVE)
                {
                stcFirstOption.getChildren().add(cmbBot1);
                stcSecondOption.getChildren().add(cmbBot2);
                showAnimationSlideRightEnter(stcFirstOption,stcSecondOption);
                }
            }

        });
        transition.play();
    }

    private void showAnimationSlideRightEnter(StackPane firstOption, StackPane secondOption) {
        List<Node> elements = new ArrayList();
        elements.add(firstOption);
        elements.add(secondOption);
        ParallelTransition transition = AnimationUtil.createHorizontalSlide(800, 0, elements);
        transition.play();
    }

    private void showAnimationSlideLeftExit(StackPane firstOption, StackPane secondOption) {
        List<Node> elements = new ArrayList();
        elements.add(firstOption);
        elements.add(secondOption);
        ParallelTransition transition = AnimationUtil.createHorizontalSlide(0, 800, elements);
        transition.setOnFinished(new EventHandler(){
            @Override
            public void handle(Event event) {  
            clearOptions();
            if (gameMode == GameModes.PVP)
                {
                stcFirstOption.getChildren().add(txtPlayer1);
                stcSecondOption.getChildren().add(txtPlayer2);
                showAnimationSlideLeftEnter(stcFirstOption,stcSecondOption);
                }
            if (gameMode == GameModes.PVE)
                {
                stcFirstOption.getChildren().add(txtPlayer1);
                stcSecondOption.getChildren().add(cmbBot2);
                showAnimationSlideLeftEnter(stcFirstOption,stcSecondOption);
                }
            if (gameMode == GameModes.EVE)
                {
                stcFirstOption.getChildren().add(cmbBot1);
                stcSecondOption.getChildren().add(cmbBot2);
                showAnimationSlideLeftEnter(stcFirstOption,stcSecondOption);
                }
            }

        });
        transition.play();
    }   
    
    private void showAnimationSlideLeftEnter(StackPane firstOption, StackPane secondOption) {
        List<Node> elements = new ArrayList();
        elements.add(firstOption);
        elements.add(secondOption);
        ParallelTransition transition = AnimationUtil.createHorizontalSlide(-800, 0, elements);
        transition.play();
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
        stage.setMinWidth(1280);
        stage.setMinHeight(1024);
        stage.setScene(scene);
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
