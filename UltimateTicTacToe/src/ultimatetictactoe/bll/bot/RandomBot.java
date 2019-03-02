/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.bll.bot;

import java.util.List;
import java.util.Random;
import ultimatetictactoe.bll.game.IGameState;
import ultimatetictactoe.bll.move.IMove;

/**
 *
 * @author Acer
 */
public class RandomBot implements IBot{

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        Random randomGenerator = new Random();
        int randomMoveIndex = randomGenerator.nextInt(availableMoves.size());
        return availableMoves.get(randomMoveIndex);
    }
    
    @Override
    public String toString()
    {
        return "Random Bot";
    }
    
}
