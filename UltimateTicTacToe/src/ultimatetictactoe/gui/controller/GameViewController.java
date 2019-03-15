/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ultimatetictactoe.bll.game.GameManager.GameMode;
import ultimatetictactoe.bll.move.IMove;
import ultimatetictactoe.gui.model.GameModel;
import ultimatetictactoe.gui.util.AnimationUtil;

/**
 * FXML Controller class
 *
 * @author Acer
 */
public class GameViewController implements Initializable {

    private GameModel model;
    private GameMode mode;
    private HashMap<Integer, HashMap<Integer, Button>> board = new HashMap();
    private Thread botThread = new Thread();
    private int activePointer = 0;
    
    @FXML
    private GridPane grdGameboard;
    @FXML
    private Rectangle imgCrossPartOne;
    @FXML
    private Rectangle imgCrossPartTwo;
    @FXML
    private Circle imgCirclePartOne;
    @FXML
    private Circle imgCirclePartTwo;
    @FXML
    private Label lblPlayer1;
    @FXML
    private Label lblPlayer2;
    @FXML
    private Button btnNewGame;
    @FXML
    private Button btnMainMenu;
    @FXML
    private Label lblPlayer1Score;
    @FXML
    private Label lblPlayer2Score;
    @FXML
    private Rectangle rctGameOver;
    @FXML
    private Label lblWinner;
    @FXML
    private Label lblDraw;
    @FXML
    private ImageView imgWinner;
    @FXML
    private ImageView imgDraw;
    @FXML
    private StackPane stcGameOver;

    
    public GameViewController()
    {
        model = GameModel.getInstance();
        mode = model.getGameMode();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        initializeBoard();
        checkForSimulationMode();
    }    
    
    private void initializeBoard()
    {
       for(Node microboard : grdGameboard.getChildren())
       {
           Integer microboardXPosition = GridPane.getRowIndex(microboard);
           Integer microboardYPosition = GridPane.getColumnIndex(microboard);
           int microboardX = (microboardXPosition == null) ? 0 : microboardXPosition;
           int microboardY = (microboardYPosition == null) ? 0 : microboardYPosition;
           for(Node field : ((GridPane) ((StackPane) microboard).getChildren().get(0)).getChildren())
           {
               Integer row = GridPane.getRowIndex(field);
               Integer col = GridPane.getColumnIndex(field);
               int fieldXPosition = (row == null) ? microboardX*3+0 : microboardX*3 + row;
               int fieldYPosition = (col == null) ? microboardY*3+0 : microboardY*3 + col;
               Button button = ((Button) ((StackPane) field).getChildren().get(0));
               addToBoard(fieldXPosition , fieldYPosition, button);
           }
       }
    }
    
    private void addToBoard(int fieldXPosition, int fieldYPosition, Button field)
    {
        if(board.get(fieldXPosition) == null)
        {
            HashMap<Integer, Button> map = new HashMap();
            map.put(fieldYPosition, field);
            board.put(fieldXPosition, map);
        }
        else
        {
            board.get(fieldXPosition).put(fieldYPosition, field);
        }
    }

    private void checkForSimulationMode()
    {
        if(mode == GameMode.BotVsBot)
        {
            performBotVsBotSimulation();
        }
    }
    
    private void performBotVsBotSimulation()
    {
        grdGameboard.setDisable(true);
        botThread = new Thread(new Runnable() {
            @Override
            public void run() 
            {
                try 
                {
                    while(!model.isGameOver())
                    {
                        Thread.sleep(150);
                        Platform.runLater(() -> performBotMove());
                    }
                    Platform.runLater(() -> btnNewGame.fire());
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        botThread.start();
    }
    
    @FXML
    private void clickOnField(ActionEvent event)
    {
        if(performPlayerMove(event))
        {
            if(mode == GameMode.HumanVsBot)
            {
                imitateBotMove();          
            }
        }
    }
    
    private void imitateBotMove()
    {
        grdGameboard.setDisable(true);
        botThread = new Thread(new Runnable() {
            @Override
            public void run() 
            {
                try 
                {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        if(performBotMove())
                        {
                            grdGameboard.setDisable(false);
                        }
                    });
                } 
                catch (InterruptedException ex) 
                {
                    Logger.getLogger(GameViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        botThread.start();
    }
    
    private boolean performBotMove()
    {
        int currentPlayer = model.getCurrentPlayer();
        IMove botMove = null;
        if(model.performBotMove())
        {
            botMove = model.getBotMove();
            updateGameboard(currentPlayer, botMove.getX(), botMove.getY());
            return true;
        }
        return false;
    }
    
    private boolean performPlayerMove(ActionEvent event)
    {
        int currentPlayer = model.getCurrentPlayer();
        int[] fieldPosition = getFieldPosition(event);
        if(model.performPlayerMove(fieldPosition[0], fieldPosition[1]))
        {
            updateGameboard(currentPlayer, fieldPosition[0], fieldPosition[1]);
            return true;
        }
        return false;
    }
    
    private void updateGameboard(int currentPlayer, int fieldXPosition, int fieldYPosition)
    {
        updateBoard(currentPlayer, fieldXPosition, fieldYPosition);          
        updateMacroboard(currentPlayer, fieldXPosition, fieldYPosition);
        updateGameState(currentPlayer, fieldXPosition, fieldYPosition);
        if(!model.isGameOver())
        {
            setPlayerPointer((currentPlayer+1)%2);
        }
    }
    
        private void updateBoard(int player, int fieldXPosition, int fieldYPosition)
    {
        Button field = board.get(fieldXPosition).get(fieldYPosition);
        ImageView imageView = new ImageView(getPlayerMarker(player));
        field.setGraphic(imageView);
        ParallelTransition transition = AnimationUtil.createFieldAnimation((ImageView) field.getGraphic());
        transition.play();
    }
    
    private void updateMacroboard(int currentPlayer, int fieldXPosition, int fieldYPosition)
    {
        updateMicroboardState(currentPlayer, fieldXPosition, fieldYPosition);
        updateMicroboardsAvailability();
    }
    
    private void updateMicroboardState(int currentPlayer, int fieldXPosition, int fieldYPosition)
    {
        if(model.isMicroboardWon(fieldXPosition/3, fieldYPosition/3))
        {
            GridPane microboard = (GridPane) board.get(fieldXPosition).get(fieldYPosition).getParent().getParent();
            setMicroboardToWon(currentPlayer, microboard);
        }
    }
    
    private void updateMicroboardsAvailability()
    {
        List<IMove> availableMoves = model.getAvailableMoves();
        for(int x = 0; x < 9; x++)
        {
            for(int y = 0; y < 9; y++)
            {
                board.get(x).get(y).setDisable(true);
                for(IMove move : availableMoves)
                {
                    if(move.getX() == x && move.getY() == y)
                    {
                        board.get(x).get(y).setDisable(false);
                    }
                }
            }
        }
    }
    
    private void updateGameState(int currentPlayer, int fieldXPosition, int fieldYPosition)
    {
        if(model.isMacroboardWon())
        {
            setGameOver(currentPlayer);
        }
        else if(model.isDraw())
        {
            setDraw();
        }     
    }
    
    private void setMicroboardToWon(int microboardWinner, GridPane microboard)
    {
        StackPane microboardField = (StackPane) microboard.getParent();
        microboard.setVisible(false);
        microboardField.setMouseTransparent(true);
        ImageView imageView = new ImageView(getPlayerMarkerMicroboard(microboardWinner));
        microboardField.getChildren().add(imageView);
        ParallelTransition transition = AnimationUtil.createMicroboardAnimation(imageView);
        transition.play();
    }
    
    private void setGameOver(int winner)
    {
        grdGameboard.setDisable(true);
        incrementPlayerScore(winner);
        if(model.getGameMode() != GameMode.BotVsBot)
        {
            showGameOverAnimation(getPlayerMarker(winner));
            showSlideOptionButtons();
        }
    }
    
    private void showGameOverAnimation(Image imageWinner)
    {
        stcGameOver.setVisible(true);
        imgDraw.setVisible(false);
        lblDraw.setVisible(false);
        imgWinner.setImage(imageWinner);
        ParallelTransition transition = AnimationUtil.createGameOverAnimation(rctGameOver);
        transition.play();
        List<Node> elements = new ArrayList();
        elements.add(imgWinner);
        elements.add(lblWinner);
        ParallelTransition fade = AnimationUtil.createFadingInAnimation(elements);
        fade.play();
    }        
    
    private void setDraw()
    {
        btnNewGame.setVisible(true);
        grdGameboard.setDisable(true);
        if(model.getGameMode() != GameMode.BotVsBot)
        {
            showGameOverAnimation();
            showSlideOptionButtons();
        }
    }
    
    private void showGameOverAnimation()
    {
        stcGameOver.setVisible(true);
        imgWinner.setVisible(false);
        lblWinner.setVisible(false);
        ParallelTransition transition = AnimationUtil.createGameOverAnimation(rctGameOver);
        transition.play();
        List<Node> elements = new ArrayList();
        elements.add(imgWinner);
        elements.add(lblWinner);
        ParallelTransition fade = AnimationUtil.createFadingInAnimation(elements);
        fade.play();
    }    
    
    private void showSlideOptionButtons()
    {
        List<Node> nodes = new ArrayList();
        nodes.add(btnMainMenu);
        ParallelTransition transition = AnimationUtil.createHorizontalSlide(0, -250, nodes);
        transition.setOnFinished(e -> {
            btnNewGame.setVisible(true);
            List<Node> elements = new ArrayList();
            elements.add(btnNewGame);
            ParallelTransition trans = AnimationUtil.createHorizontalSlide(0, 250, elements);
            ParallelTransition fade = AnimationUtil.createFadingInAnimation(elements);
            fade.getChildren().add(trans);
            fade.play();
            
        });
        transition.play();
    }
    
    private void setPlayerPointer(int currentPlayer)
    {
        ParallelTransition transition = new ParallelTransition();
        
        List<Node> cross = new ArrayList();
        cross.add(imgCrossPartOne);
        cross.add(imgCrossPartTwo);
        
        if(currentPlayer == 0 && activePointer==1)
        {
            ParallelTransition showTransition = AnimationUtil.createShowCrossAnimation(cross);
            ParallelTransition hideTransition = AnimationUtil.createHideCircleAnimation(imgCirclePartOne, imgCirclePartTwo);
            transition.getChildren().addAll(showTransition, hideTransition);
            activePointer = 0;
        }
        else if(currentPlayer == 1 && activePointer==0)
        {
            ParallelTransition hideTransition = AnimationUtil.createHideCrossAnimation(cross);
            ParallelTransition showTransition = AnimationUtil.createShowCircleAnimation(imgCirclePartOne, imgCirclePartTwo);
            transition.getChildren().addAll(showTransition, hideTransition);
            activePointer = 1;
        }
        transition.play();
    }
    
    private int[] getFieldPosition(ActionEvent event)
    {
        int[] macroboardPosition = getPositionOnMacroboard(event);
        int[] microboardPosition = getPositionOnMicroboard(event);
        int fieldXPosition = macroboardPosition[0] * 3 + microboardPosition[0];
        int fieldYPosition = macroboardPosition[1] * 3 + microboardPosition[1];
        int[] fieldPosition = {fieldXPosition, fieldYPosition};
        return fieldPosition;
    }
    
    private int[] getPositionOnMacroboard(ActionEvent event)
    {
        Integer macroboardXPosition = GridPane.getRowIndex(((Node) event.getSource()).getParent().getParent().getParent());
        Integer macroboardYPosition = GridPane.getColumnIndex(((Node) event.getSource()).getParent().getParent().getParent());
        macroboardXPosition = (macroboardXPosition == null) ? 0 : macroboardXPosition;
        macroboardYPosition = (macroboardYPosition == null) ? 0 : macroboardYPosition;
        int[] macroboardPosition = {macroboardXPosition, macroboardYPosition};
        return macroboardPosition;
    }
    
    private int[] getPositionOnMicroboard(ActionEvent event)
    {
        Integer microboardXPosition = GridPane.getRowIndex(((Node) event.getSource()).getParent());
        Integer microboardYPosition = GridPane.getColumnIndex(((Node) event.getSource()).getParent());
        microboardXPosition = (microboardXPosition == null) ? 0 : microboardXPosition;
        microboardYPosition = (microboardYPosition == null) ? 0 : microboardYPosition;
        int[] microboardPosition = {microboardXPosition, microboardYPosition};
        return microboardPosition;
    }
    
    private Image getPlayerMarker(int playerNumber)
    {
        Image image;
        if(playerNumber == 0)
        {
            image = new Image("/ultimatetictactoe/gui/images/PlayerOne.png");
            return image;
        }
        else
        {
            image = new Image("/ultimatetictactoe/gui/images/PlayerTwo.png");
            return image;
        }
    }
    
    private Image getPlayerMarkerMicroboard(int playerNumber)
    {
        Image image;
        if(playerNumber == 0)
        {
            image = new Image("/ultimatetictactoe/gui/images/PlayerOneMicroboard.png");
            return image;
        }
        else
        {
            image = new Image("/ultimatetictactoe/gui/images/PlayerTwoMicroboard.PNG");
            return image;
        }
    }
    
    private void incrementPlayerScore(int playerNumber)
    {
        if(playerNumber == 0)
        {
            int updatedScore = Integer.parseInt(lblPlayer1Score.getText())+1;
            lblPlayer1Score.setText(updatedScore + "");
            lblWinner.setText(lblPlayer1.getText() + " won!");
        }
        else
        {
            int updatedScore = Integer.parseInt(lblPlayer2Score.getText())+1;
            lblPlayer2Score.setText(updatedScore + "");
            lblWinner.setText(lblPlayer2.getText() + " won!");
        }
    }
    
    public void setPlayerLabels(String player1, String player2)
    {
        lblPlayer1.setText(player1);
        lblPlayer2.setText(player2);
    }

    @FXML
    private void clickNewGame(ActionEvent event) 
    {
        model.restartGame();
        hideGameOverElements();       
        clearGameboard();
        setPlayerPointer(model.getCurrentPlayer());
        checkForSimulationMode();
        checkForBotMove();
    }
    
    private void checkForBotMove()
    {
        if(model.getGameMode() == GameMode.HumanVsBot && model.getCurrentPlayer() == 1)
        {
            imitateBotMove();
        }
    }
    
    private void hideGameOverElements()
    {
        if(model.getGameMode() != GameMode.BotVsBot)
        {
            stcGameOver.setVisible(false); 
            btnNewGame.setVisible(false);
            List<Node> elements = new ArrayList();
            elements.add(btnMainMenu);
            ParallelTransition transition = AnimationUtil.createHorizontalSlide(-250, 0, elements);
            transition.play();
        }
    }
    
    private void clearGameboard()
    {
        for(Node microboardField : grdGameboard.getChildren())
        {
            Iterator<Node> iterator = ((StackPane)microboardField).getChildren().iterator();
            while(iterator.hasNext())
            {
                Node node = iterator.next();
                if(node instanceof GridPane)
                {
                    GridPane microboard = (GridPane) node;
                    for(Node buttonField : microboard.getChildren())
                    {
                        Button field = (Button)((StackPane)buttonField).getChildren().get(0);
                        field.setDisable(false);
                        field.setGraphic(null);
                    }
                    microboard.setVisible(true);
                    microboardField.setMouseTransparent(false);
                }
                else
                {
                    iterator.remove();
                }
            }
        }
        grdGameboard.setDisable(false);
    }

    @FXML
    private void clickMainMenu(ActionEvent event) throws IOException 
    {
        botThread.stop();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ultimatetictactoe/gui/view/MainMenuView.fxml"));
        Parent root = fxmlLoader.load();   
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.hide();
        setMainMenuView(stage, scene);
        stage.show();
    }
    
    private void setMainMenuView(Stage stage, Scene scene)
    {
        stage.setMaximized(false);
        stage.setMinWidth(800);
        stage.setMinHeight(630);
        stage.setScene(scene);
        stage.centerOnScreen();
    }
    
}

