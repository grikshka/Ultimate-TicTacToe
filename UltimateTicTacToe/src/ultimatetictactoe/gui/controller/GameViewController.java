/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import ultimatetictactoe.bll.move.IMove;
import ultimatetictactoe.gui.model.GameModel;

/**
 * FXML Controller class
 *
 * @author Acer
 */
public class GameViewController implements Initializable {
    
    @FXML
    private GridPane grdGameboard;

    private GameModel model;
    private HashMap<Integer, HashMap<Integer, Button>> board = new HashMap();
    
    public GameViewController()
    {
        model = GameModel.getInstance();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        initializeBoard();
    }    
    
    private void initializeBoard()
    {
       for(Node microboard : grdGameboard.getChildren())
       {
           Integer microboardXPosition = GridPane.getRowIndex(microboard);
           Integer microboardYPosition = GridPane.getColumnIndex(microboard);
           int microboardX = (microboardXPosition == null) ? 0 : microboardXPosition;
           int microboardY = (microboardYPosition == null) ? 0 : microboardYPosition;
           for(Node field : ((GridPane) microboard).getChildren())
           {
               Integer row = GridPane.getRowIndex(field);
               Integer col = GridPane.getColumnIndex(field);
               int r = (row == null) ? microboardX*3+0 : microboardX*3 + row;
               int c = (col == null) ? microboardY*3+0 : microboardY*3 + col;
               Button button = ((Button) ((StackPane) field).getChildren().get(0));
               addToBoard(r , c, button);
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

    @FXML
    private void clickOnField(ActionEvent event)
    {
        int[] fieldPosition = getFieldPosition(event);
        performPlayerMove(fieldPosition[0], fieldPosition[1]);
        setAvailableFields();
    }
    
    private int[] getFieldPosition(ActionEvent event)
    {
        int[] macroboardPosition = getMacroboardPosition(event);
        int[] microboardPosition = getMicroboardPosition(event);
        int fieldXPosition = macroboardPosition[0] * 3 + microboardPosition[0];
        int fieldYPosition = macroboardPosition[1] * 3 + microboardPosition[1];
        int[] fieldPosition = {fieldXPosition, fieldYPosition};
        return fieldPosition;
    }
    
    private int[] getMacroboardPosition(ActionEvent event)
    {
        Integer macroboardXPosition = GridPane.getRowIndex(((Node) event.getSource()).getParent().getParent());
        Integer macroboardYPosition = GridPane.getColumnIndex(((Node) event.getSource()).getParent().getParent());
        macroboardXPosition = (macroboardXPosition == null) ? 0 : macroboardXPosition;
        macroboardYPosition = (macroboardYPosition == null) ? 0 : macroboardYPosition;
        int[] macroboardPosition = {macroboardXPosition, macroboardYPosition};
        return macroboardPosition;
    }
    
    private int[] getMicroboardPosition(ActionEvent event)
    {
        Integer microboardXPosition = GridPane.getRowIndex(((Node) event.getSource()).getParent());
        Integer microboardYPosition = GridPane.getColumnIndex(((Node) event.getSource()).getParent());
        microboardXPosition = (microboardXPosition == null) ? 0 : microboardXPosition;
        microboardYPosition = (microboardYPosition == null) ? 0 : microboardYPosition;
        int[] microboardPosition = {microboardXPosition, microboardYPosition};
        return microboardPosition;
    }
    
    private void setAvailableFields()
    {
        List<IMove> availableMoves = model.getAvailableMoves();
        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                board.get(i).get(j).setDisable(true);
                for(IMove move : availableMoves)
                {
                    if(move.getX() == i && move.getY() == j)
                    {
                        board.get(i).get(j).setDisable(false);
                    }

                }
            }
        }
    }
    
    private void performPlayerMove(int fieldXPosition, int fieldYPosition)
    {
        int currentPlayer = model.getCurrentPlayer();
        if(model.performPlayerMove(fieldXPosition, fieldYPosition))
        {
            Button field = board.get(fieldXPosition).get(fieldYPosition);
            if(currentPlayer == 0)
            {
                field.setText("X");
            }
            else
            {
                field.setText("O");
            }
            field.setDisable(true);
        }
    }
    
}
