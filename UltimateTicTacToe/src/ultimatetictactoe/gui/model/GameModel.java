/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.gui.model;

import ultimatetictactoe.bll.bot.IBot;
import ultimatetictactoe.bll.game.GameManager;
import ultimatetictactoe.bll.game.GameState;

/**
 *
 * @author Acer
 */
public class GameModel {
    
    private GameManager game;
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
        game = new GameManager(new GameState());
    }
    
    public void newPlayerVsBotGame(IBot bot)
    {
        game = new GameManager(new GameState(), bot);
    }
    
    public void newBotVsBotGame(IBot bot1, IBot bot2)
    {
        game = new GameManager(new GameState(), bot1, bot2);
    }
    
    
    
}
