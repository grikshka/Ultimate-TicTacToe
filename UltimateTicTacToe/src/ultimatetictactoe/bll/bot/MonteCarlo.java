/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimatetictactoe.bll.bot;

import ultimatetictactoe.bll.field.IField;
import ultimatetictactoe.bll.game.GameState;
import ultimatetictactoe.bll.game.IGameState;
import ultimatetictactoe.bll.move.IMove;
import ultimatetictactoe.bll.move.Move;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Acer
 */
public class MonteCarlo implements IBot{
    
    private Random rand;
    private int opponent;
    private UCT uct;
    
    public MonteCarlo()
    {
        rand = new Random();
        uct = new UCT();
    }

    @Override
    public IMove doMove(IGameState state) {
        
        //Establishing for how long alghoritm should work
        long endTime = System.currentTimeMillis() + 1000;
        
        //Creating rootNode with current game state
        Node rootNode = new Node(state);
        
        //Sets opponent player number
        opponent = (state.getMoveNumber()+1)%2;
        
        while(System.currentTimeMillis() < endTime)
        {
            
            //Step 1 - selecting promising node
            Node promisingNode = selectPromisingNode(rootNode);
            
            //Step 2 - expanding node
            if(!isGameOver(promisingNode.getState()))
            {
                expandNode(promisingNode);
            }
            
            //Step 3 - select node to explore
            Node nodeToExplore = promisingNode;
            if(!nodeToExplore.getChildren().isEmpty())
            {
                nodeToExplore = promisingNode.getRandomChild();
            }
            
            int rolloutResult = performRollout(nodeToExplore);
            backPropagation(nodeToExplore, rolloutResult);
        }
        Node winnerNode = rootNode.getChildWithMaxScore();
        return getMove(rootNode, winnerNode);
    }
    
    private Node selectPromisingNode(Node rootNode)
    {
        Node promisingNode = rootNode;
        while(!promisingNode.getChildren().isEmpty())
        {
            promisingNode = uct.findBestNodeWithUCT(promisingNode);
        }
        return promisingNode;
    }
    
    private void expandNode(Node promisingNode)
    {
        List<IMove> availableMoves = promisingNode.getState().getField().getAvailableMoves();
        for(IMove move : availableMoves)
        {
            
            //creating child node with current state and connecting it to parent
            Node childNode = new Node(promisingNode.getState());
            childNode.setParent(promisingNode);
            promisingNode.getChildren().add(childNode);
            
            //performing move on child node state
            performMove(childNode.getState(), move.getX(), move.getY());
        }
    }
    
    private int performRollout(Node nodeToExplore)
    {
        Node tempNode = new Node(nodeToExplore);
        IGameState tempState = tempNode.getState();
        if(isGameOver(tempState) && (tempState.getMoveNumber()+1)%2 == opponent)
        {
            tempNode.getParent().setScore(Integer.MIN_VALUE);
            return opponent;
        }
        while(!isGameOver(tempState))
        {
            randomPlay(tempState);
        }
        return (tempState.getMoveNumber()+1)%2;  
//        Node tempNode = new Node(nodeToExplore);
//        IGameState tempState = tempNode.getState();
//        while(!isGameOver(tempState))
//        {
//            randomPlay(tempState);
//        }
//        if(isWin(tempState) &&  (tempState.getMoveNumber()+1)%2 == opponent)
//        {
//            return 0;
//        }
//        else if(isWin(tempState) && (tempState.getMoveNumber()+1)%2 == (opponent+1)%2)
//        {
//            return 50;
//        }
//        else
//        {
//            return 15;
//        } 
    }
    
    private void backPropagation(Node node, int value)
    {
        Node tempNode = node;
        while(tempNode != null)
        {
            tempNode.incrementVisit();
            if((tempNode.getState().getMoveNumber()+1)%2 == value)
            {
                tempNode.addScore(10);
            }
            tempNode = tempNode.getParent();
        }
//        Node tempNode = node;
//        while(tempNode != null)
//        {
//            tempNode.incrementVisit();
//            tempNode.addScore(value);
//            tempNode = tempNode.getParent();
//        }
    }
    
    private void randomPlay(IGameState state)
    {
        List<IMove> availableMoves = state.getField().getAvailableMoves();
        IMove randomMove = availableMoves.get(rand.nextInt(availableMoves.size()));
        performMove(state, randomMove.getX(), randomMove.getY());
    }
    
    private class UCT {
        
        public double uctValue(int totalVisit, double nodeWinScore, int nodeVisit) 
        {
            if (nodeVisit == 0) 
            {
                return Integer.MAX_VALUE;
            }
            return (nodeWinScore / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }
        
        public Node findBestNodeWithUCT(Node node) 
        {
            int parentVisit = node.getNumberOfVisits();
            return Collections.max(
                node.getChildren(),
                Comparator.comparing(child -> uctValue(parentVisit, 
                    child.getScore(), child.getNumberOfVisits())));
        }
        
    }
    
    private class Node {
        
        private Node parent;
        
        private IGameState state;
        private int score;
        private int numberOfVisits;
        
        private List<Node> children;
        
        public Node(Node node)
        {
            //copying game state
            this(node.getState());
            
            //connecting to parent
            this.children = new ArrayList();
            if(node.getParent() != null)
            {
                this.parent = node.getParent();
            }
            
            //copying children
            List<Node> childArray = node.getChildren();
            for(Node child : childArray)
            {
                this.children.add(new Node(child));
            }
            
            //setting score and numberOfVisits
            score = node.getScore();
            numberOfVisits = node.getNumberOfVisits();
        }
        
        public Node(IGameState state)
        {
            this.state = new GameState();
            // copy boards
            String[][] board = new String[9][9];
            String[][] macroboard = new String[3][3];
            for(int i = 0; i < board.length; i++)
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    board[i][j] = state.getField().getBoard()[i][j];
                }
            }
            for(int i = 0; i < macroboard.length; i++)
            {
                for(int j = 0; j < macroboard[i].length; j++)
                {
                    macroboard[i][j] = state.getField().getMacroboard()[i][j];
                }
            }
            
            //add boards
            this.state.getField().setBoard(board);
            this.state.getField().setMacroboard(macroboard);
            
            //copy move and round numbers
            this.state.setMoveNumber(state.getMoveNumber());
            this.state.setRoundNumber(state.getRoundNumber());
            
            //create empty children list
            this.children = new ArrayList();
            
            //sets score and numberOfVisits to default
            this.score = 0;
            this.numberOfVisits = 0;
        }
        
        public List<Node> getChildren()
        {
            return children;
        }
        
        public Node getChildWithMaxScore()
        {
            return Collections.max(this.children, Comparator.comparing(c -> {
                return c.getNumberOfVisits();
            }));
        }
        
        public Node getRandomChild()
        {
            return children.get(rand.nextInt(children.size()));
        }
        
        public Node getParent()
        {
            return parent;
        }
        
        public void setParent(Node parent)
        {
            this.parent = parent;
        }
        
        public IGameState getState()
        {
            return state;
        }
        
        public void setScore(int score)
        {
            this.score = score;
        }
        
        public void addScore(int score)
        {
            this.score += score;
        }
        
        public int getScore()
        {
            return score;
        }
        
        public void incrementVisit()
        {
            numberOfVisits++;
        }
        
        public int getNumberOfVisits()
        {
            return numberOfVisits;
        }
        
    }
    
    private IMove getMove(Node parentNode, Node childNode)
    {
        String[][] parentBoard = parentNode.getState().getField().getBoard();
        String[][] childBoard = childNode.getState().getField().getBoard();
        for(int i = 0; i < parentBoard.length; i++)
        {
            for(int j = 0; j < parentBoard[i].length; j++)
            {
                if(!parentBoard[i][j].equals(childBoard[i][j]))
                {
                    return new Move(i,j);
                }
            }
        }
        return null;
    }
    
    private void performMove(IGameState state, int moveX, int moveY)
    {
        String[][] board = state.getField().getBoard();
        board[moveX][moveY] = state.getMoveNumber()%2 + "";
        state.getField().setBoard(board);
        updateMacroboard(state, moveX, moveY);
        state.setMoveNumber(state.getMoveNumber()+1);
    }
    
    private void updateMacroboard(IGameState state, int moveX, int moveY)
    {
       updateMicroboardState(state, moveX, moveY);
       updateMicroboardsAvailability(state, moveX, moveY);
    }
    
    private void updateMicroboardState(IGameState state, int moveX, int moveY)
    {
        String[][] macroboard = state.getField().getMacroboard();
        int startingXPosition = (moveX/3)*3;
        int startingYPosition = (moveY/3)*3;
        if(isWinOnMicroboard(state, startingXPosition, startingYPosition))
        {
            macroboard[moveX/3][moveY/3] = state.getMoveNumber()%2+"";
        }
        else if(isDrawOnMicroboard(state, startingXPosition, startingYPosition))
        {
            macroboard[moveX/3][moveY/3] = "-";
        }
        state.getField().setMacroboard(macroboard);
    }
    
    private void updateMicroboardsAvailability(IGameState state, int moveX, int moveY)
    {
       int activeMicroboardX = moveX%3;
       int activeMicroboardY = moveY%3;
       String[][] macroboard = state.getField().getMacroboard();
       if(macroboard[activeMicroboardX][activeMicroboardY].equals(IField.AVAILABLE_FIELD) 
               || macroboard[activeMicroboardX][activeMicroboardY].equals(IField.EMPTY_FIELD))
       {
           setAvailableMicroboard(state, activeMicroboardX, activeMicroboardY);
       }
       else
       {
           setAllMicroboardsAvailable(state);
       }
    }
    
    private void setAvailableMicroboard(IGameState state, int activeMicroboardX, int activeMicroboardY)
    {
        String[][] macroboard = state.getField().getMacroboard();
        for(int x = 0; x < macroboard.length; x++)
           {
               for(int y = 0; y < macroboard[x].length; y++)
               {
                   if(x == activeMicroboardX && y == activeMicroboardY)
                   {
                       macroboard[x][y] = IField.AVAILABLE_FIELD;
                   }
                   else if(macroboard[x][y].equals(IField.AVAILABLE_FIELD))
                   {
                       macroboard[x][y] = IField.EMPTY_FIELD;
                   }
               }
           }
        state.getField().setMacroboard(macroboard);
    }
    
    private void setAllMicroboardsAvailable(IGameState state)
    {
        String[][] macroboard = state.getField().getMacroboard();
        for(int x = 0; x < 3; x++)
           {
               for(int y = 0; y < 3; y++)
               {
                   if(macroboard[x][y].equals(IField.EMPTY_FIELD))
                   {
                       macroboard[x][y] = IField.AVAILABLE_FIELD;
                   }
               }
           }
    }
    
    private boolean isWinOnMicroboard(IGameState state, int startingX, int startingY)
    {
        String[][] board = state.getField().getBoard();
        return isWinOnBoard(board, startingX, startingY);
    }
    
    private boolean isDrawOnMicroboard(IGameState state, int startingX, int startingY)
    {
        boolean isDraw = true;
        String[][] board = state.getField().getBoard();
        for(int x = startingX; x < startingX+3; x++)
        {
            for(int y = startingY; y < startingY+3; y++)
            {
                if(board[x][y].equals(IField.EMPTY_FIELD))
                {
                    isDraw = false;
                }
            }
        }
        return isDraw;
    }
    
    private boolean isGameOver(IGameState state)
    {
        String[][] macroboard = state.getField().getMacroboard();
        return isWinOnBoard(macroboard, 0, 0) || isDraw(state);
    }
    
    private boolean isWin(IGameState state)
    {
        String[][] macroboard = state.getField().getMacroboard();
        return isWinOnBoard(macroboard, 0, 0);
    }
    
    private boolean isDraw(IGameState state)
    {
        String[][] macroboard = state.getField().getMacroboard();
        for(int x = 0; x < macroboard.length; x++)
        {
            for(int y = 0; y < macroboard[x].length; y++)
            {
                if(macroboard[x][y].equals(IField.EMPTY_FIELD) || macroboard[x][y].equals(IField.AVAILABLE_FIELD))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isWinOnBoard(String[][] board, int startingX, int startingY)
    {
        for(int x = startingX; x < startingX+3; x++)
        {
            if(isHorizontalWin(board, x, startingY))
            {
                return true;
            }
            for(int y = startingY; y < startingY+3; y++)
            {
                
                if(isVerticalWin(board, startingX, y))
                {
                    return true;
                }
            }
        }
        return isDiagonalWin(board, startingX, startingY);
    }
    
    private boolean isHorizontalWin(String[][] board, int startingX, int startingY)
    {
        return ((board[startingX][startingY].equals("0") || board[startingX][startingY].equals("1"))
                    && board[startingX][startingY].equals(board[startingX][startingY+1]) 
                    && board[startingX][startingY+1].equals(board[startingX][startingY+2]));
    }
    
    private boolean isVerticalWin(String[][] board, int startingX, int startingY)
    {
        return ((board[startingX][startingY].equals("0") || board[startingX][startingY].equals("1"))
                    && board[startingX][startingY].equals(board[startingX+1][startingY]) 
                    && board[startingX+1][startingY].equals(board[startingX+2][startingY]));
    }
    
    private boolean isDiagonalWin(String[][] board, int startingX, int startingY)
    {
        if((board[startingX][startingY].equals("0") || board[startingX][startingY].equals("1"))
                && board[startingX][startingY].equals(board[startingX+1][startingY+1])
                && board[startingX+1][startingY+1].equals(board[startingX+2][startingY+2]))
        {
            return true;
        }
        else if((board[startingX][startingY+2].equals("0") || board[startingX][startingY+2].equals("1"))
                && board[startingX][startingY+2].equals(board[startingX+1][startingY+1])
                && board[startingX+1][startingY+1].equals(board[startingX+2][startingY]))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "Greg AI";
    }
    
}
