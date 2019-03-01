/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.model;

import java.util.List;
import ultimatetictactoe.bll.bot.IBot;
import ultimatetictactoe.bll.game.GameManager;
import ultimatetictactoe.bll.game.GameState;
import ultimatetictactoe.bll.game.IGameState;
import ultimatetictactoe.bll.move.IMove;
import ultimatetictactoe.bll.move.Move;

/**
 *
 * @author Acer
 */
public class GameModel {
    
    private GameManager game;
    private IGameState gameState;
    private static GameModel instance;
    
    private GameModel()
    {
        
    }
    
    public static GameModel getInstance()
    {
        if(instance == null)
        {
            instance = new GameModel();
        }
        return instance;
    }
    
    public void newPlayerVsPlayerGame()
    {
        gameState = new GameState();
        game = new GameManager(gameState);
    }
    
    public void newPlayerVsBotGame(IBot bot)
    {
        gameState = new GameState();
        game = new GameManager(gameState, bot);
    }
    
    public void newBotVsBotGame(IBot bot1, IBot bot2)
    {
        gameState = new GameState();
        game = new GameManager(gameState, bot1, bot2);
    }
    
    public List<IMove> getAvailableMoves()
    {
        return gameState.getField().getAvailableMoves();
    }
    
    public boolean isMicroboardWon(int microboardXPosition, int microboardYPosition)
    {
        return game.isMicroboardWon(microboardXPosition, microboardYPosition);
    }
    
    public boolean isGameOver()
    {
        return game.isGameOver();
    }
    
    public boolean isDraw()
    {
        return false;
    }
        
    
    public int getCurrentPlayer()
    {
        return game.getCurrentPlayer();
    }
    
    public boolean performPlayerMove(int fieldXPosition, int fieldYPosition)
    {
        return game.updateGame(new Move(fieldXPosition, fieldYPosition));
    }
    
}
