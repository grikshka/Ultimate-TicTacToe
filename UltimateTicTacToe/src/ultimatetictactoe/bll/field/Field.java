/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.bll.field;

import java.util.ArrayList;
import java.util.List;
import ultimatetictactoe.bll.move.IMove;
import ultimatetictactoe.bll.move.Move;

/**
 *
 * @author Acer
 */
public class Field implements IField {
    
    private String[][] board = new String[9][9];
    private String[][] macroboard = new String[3][3];
    
    public Field()
    {
        clearBoard();
    }

    @Override
    public void clearBoard() {
        clearMicroboards();
        clearMacroboard();        
    }
    
    private void clearMicroboards()
    {
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                board[i][j] = EMPTY_FIELD;
            }
        }
    }
    
    private void clearMacroboard()
    {
        for(int i = 0; i < macroboard.length; i++)
        {
            for(int j = 0; j < macroboard[i].length; j++)
            {
                macroboard[i][j] = AVAILABLE_FIELD;
            }
        }
    }

    @Override
    public List<IMove> getAvailableMoves() {
        List<IMove> availableMoves = new ArrayList();
        for(int i = 0; i < macroboard.length; i++)
        {
            for(int j = 0; j < macroboard[i].length; j++)
            {
                if(macroboard[i][j] == AVAILABLE_FIELD)
                {
                    availableMoves.addAll(getAvailableMovesFromMicroboard(i, j));
                }
            }
        }
        return availableMoves;
    }
    
    private List<IMove> getAvailableMovesFromMicroboard(int microboardXPosition, int microboardYPosition)
    {
        List<IMove> availableMoves = new ArrayList();
        int startingXPosition = microboardXPosition*3;
        int endingXPosition = startingXPosition + 2;
        int startingYPosition = microboardYPosition*3;
        int endingYPosition = startingYPosition + 2;
        for(int i = startingXPosition; i <= endingXPosition; i++)
        {
            for(int j = startingYPosition; j <= endingYPosition; j++)
            {
                if(board[i][j] == EMPTY_FIELD)
                {
                    availableMoves.add(new Move(i, j));
                }
            }
        }
        return availableMoves;
    }

    @Override
    public String getPlayerId(int column, int row) {
        return board[column][row];
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isInActiveMicroboard(int x, int y) {
        int macroboardXPosition = x/3;
        int macroboardYPosition = y/3;
        if(macroboard[macroboardXPosition][macroboardYPosition] == AVAILABLE_FIELD)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String[][] getBoard() {
        return board;
    }

    @Override
    public String[][] getMacroboard() {
        return macroboard;
    }

    @Override
    public void setBoard(String[][] board) {
        this.board = board;
    }

    @Override
    public void setMacroboard(String[][] macroboard) {
        this.macroboard = macroboard;
    }
    
}
