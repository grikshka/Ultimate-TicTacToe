/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.model;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ultimatetictactoe.bll.bot.DrunkenBot;
import ultimatetictactoe.bll.bot.GregBot;
import ultimatetictactoe.bll.bot.IBot;
import ultimatetictactoe.bll.bot.LocalPrioritisedListBot;
import ultimatetictactoe.bll.bot.MemeBot;
import ultimatetictactoe.bll.bot.RandomBot;
import ultimatetictactoe.bll.game.GameManager;
import ultimatetictactoe.bll.game.GameManager.GameMode;
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
    
    public ObservableList<IBot> getAllBots()
    {
        ObservableList<IBot> allBots = FXCollections.observableArrayList();
        allBots.add(new RandomBot());
        allBots.add(new MemeBot());
        allBots.add(new LocalPrioritisedListBot());
        allBots.add(new DrunkenBot());
        allBots.add(new GregBot());
        return allBots;
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
    
    public boolean performPlayerMove(int fieldXPosition, int fieldYPosition)
    {
        return game.updateGame(new Move(fieldXPosition, fieldYPosition));
    }
    
    public boolean performBotMove()
    {
        return game.updateGame();
    }
    
    public List<IMove> getAvailableMoves()
    {
        return gameState.getField().getAvailableMoves();
    }
    
    public boolean isMicroboardWon(int microboardXPosition, int microboardYPosition)
    {
        String microboardValue = gameState.getField().getMacroboard()[microboardXPosition][microboardYPosition];
        return microboardValue.equals(game.PLAYER_0_MARKER) || microboardValue.equals(game.PLAYER_1_MARKER);
    }
    
    public void restartGame()
    {
        game.restartGame();
    }
    
    public boolean isMacroboardWon()
    {
        return game.hasWinner();
    }
    
    public boolean isDraw()
    {
        return !game.hasWinner() && game.isGameOver();
    }
        
    public boolean isGameOver()
    {
        return game.isGameOver();
    }
    
    public int getCurrentPlayer()
    {
        return game.getCurrentPlayer();
    }
    
    public GameMode getGameMode()
    {
        return game.getGameMode();
    }
    
    public IMove getBotMove()
    {
        return game.getBotMove();
    }
}
