/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.bll.move;

/**
 *
 * @author Acer
 */
public class Move implements IMove{
    
    private int x;
    private int y;
    
    public Move(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
    
}
