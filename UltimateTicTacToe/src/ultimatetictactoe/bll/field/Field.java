/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.bll.field;

import java.util.List;
import ultimatetictactoe.bll.move.IMove;

/**
 *
 * @author Acer
 */
public class Field implements IField {
    
    private String[][] board;
    private String[][] macroboard;
    
    public Field()
    {
        clearBoard();
    }

    @Override
    public void clearBoard() {
        clearMicroboard();
        clearMacroboard();        
    }
    
    private void clearMicroboard()
    {
        for(int i = 0; i < board.length; i++)
        {
            for(int j = 0; j < board[i].length; j++)
            {
                board[i][j] = AVAILABLE_FIELD;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
