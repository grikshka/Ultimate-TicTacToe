/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.bll.game;

import ultimatetictactoe.bll.field.Field;
import ultimatetictactoe.bll.field.IField;

/**
 *
 * @author Acer
 */
public class GameState implements IGameState {

    IField field = new Field();
    private int moveNumber;
    private int roundNumber;

    @Override
    public IField getField() {
        return field;
    }

    @Override
    public int getMoveNumber() {
        return moveNumber;
    }

    @Override
    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    @Override
    public int getRoundNumber() {
        return roundNumber;
    }

    @Override
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }
    /*
    //Rounds up the moves to the lowest. Each round is like moves / 2
    private void setRoundNumber(int moveNumber){
        roundNumber = (int) moveNumber/2;
    }
     */

}
