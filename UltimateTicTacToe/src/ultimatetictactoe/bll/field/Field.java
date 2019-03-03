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
    
    private String[][] board;
    private String[][] macroboard;
    
    public Field()
    {
        board = new String[9][9];
        macroboard = new String[3][3];
        clearBoard();
    }

    @Override
    public void clearBoard() {
        clearMicroboards();
        clearMacroboard();        
    }
    
    private void clearMicroboards()
    {
        for(int x = 0; x < board.length; x++)
        {
            for(int y = 0; y < board[x].length; y++)
            {
                board[x][y] = EMPTY_FIELD;
            }
        }
    }
    
    private void clearMacroboard()
    {
        for(int x = 0; x < macroboard.length; x++)
        {
            for(int y = 0; y < macroboard[x].length; y++)
            {
                macroboard[x][y] = AVAILABLE_FIELD;
            }
        }
    }

    @Override
    public List<IMove> getAvailableMoves() {
        List<IMove> availableMoves = new ArrayList();
        for(int x = 0; x < macroboard.length; x++)
        {
            for(int y = 0; y < macroboard[x].length; y++)
            {
                if(macroboard[x][y].equals(AVAILABLE_FIELD))
                {
                    availableMoves.addAll(getAvailableMovesFromMicroboard(x, y));
                }
            }
        }
        return availableMoves;
    }
    
    private List<IMove> getAvailableMovesFromMicroboard(int microboardXPosition, int microboardYPosition)
    {
        List<IMove> availableMoves = new ArrayList();
        int startingXPosition = microboardXPosition*3;
        int startingYPosition = microboardYPosition*3;
        for(int x = startingXPosition; x < startingXPosition+3; x++)
        {
            for(int y = startingYPosition; y < startingYPosition+3; y++)
            {
                if(board[x][y].equals(EMPTY_FIELD))
                {
                    availableMoves.add(new Move(x, y));
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
        for(int x = 0; x < board.length; x++)
        {
            for(int y = 0; y < board[x].length; y++)
            {
                if(!board[x][y].equals(EMPTY_FIELD))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isFull() {
        for(int x = 0; x < macroboard.length; x++)
        {
            for(int y = 0; y < macroboard[x].length; y++)
            {
                if(macroboard[x][y].equals(EMPTY_FIELD) || macroboard[x][y].equals(AVAILABLE_FIELD))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Boolean isInActiveMicroboard(int x, int y) {
        int macroboardXPosition = x/3;
        int macroboardYPosition = y/3;
        return macroboard[macroboardXPosition][macroboardYPosition].equals(AVAILABLE_FIELD);
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
