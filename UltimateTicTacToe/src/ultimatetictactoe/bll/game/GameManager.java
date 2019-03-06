package ultimatetictactoe.bll.game;

import ultimatetictactoe.bll.bot.IBot;
import ultimatetictactoe.bll.field.IField;
import ultimatetictactoe.bll.move.IMove;

/**
 * This is a proposed GameManager for Ultimate Tic-Tac-Toe,
 * the implementation of which is up to whoever uses this interface.
 * Note that initializing a game through the constructors means
 * that you have to create a new instance of the game manager 
 * for every new game of a different type (e.g. Human vs Human, Human vs Bot or Bot vs Bot),
 * which may not be ideal for your solution, so you could consider refactoring
 * that into an (re-)initialize method instead.
 * @author mjl
 */
public class GameManager {
    
    /**
     * Three different game modes.
     */
    
    public enum GameMode{
        HumanVsHuman,
        HumanVsBot,
        BotVsBot
    }
    
    private final IGameState currentState;
    private int startingPlayer = 0;
    private int currentPlayer = startingPlayer; //player0 == 0 && player1 == 1
    public final String PLAYER_0_MARKER = "0";
    public final String PLAYER_1_MARKER = "1";
    public final String DRAW_MARKER = "-";
    private GameMode mode = GameMode.HumanVsHuman;
    private boolean isGameOver = false;
    private boolean hasWinner = false;
    private IBot bot = null;
    private IBot bot2 = null;
    private IMove botMove;
    
    /**
     * Set's the currentState so the game can begin.
     * Game expected to be played Human vs Human
     * @param currentState Current game state, usually an empty board, 
     * but could load a saved game.
     */
    public GameManager(IGameState currentState) {
        this.currentState = currentState;
        mode = GameMode.HumanVsHuman;
    }
    
    /**
     * Set's the currentState so the game can begin.
     * Game expected to be played Human vs Bot
     * @param currentState Current game state, usually an empty board, 
     * but could load a saved game.
     * @param bot The bot to play against in vsBot mode.
     */
    public GameManager(IGameState currentState, IBot bot) {
        this.currentState = currentState;
        mode = GameMode.HumanVsBot;
        this.bot = bot;
    }
    
    /**
     * Set's the currentState so the game can begin.
     * Game expected to be played Bot vs Bot
     * @param currentState Current game state, usually an empty board, 
     * but could load a saved game.
     * @param bot The first bot to play.
     * @param bot2 The second bot to play.
     */
    public GameManager(IGameState currentState, IBot bot, IBot bot2) {
        this.currentState = currentState;
        mode = GameMode.BotVsBot;
        this.bot = bot;
        this.bot2 = bot2;
    }
    
    public void restartGame()
    {
        isGameOver = false;
        hasWinner = false;
        currentState.getField().clearBoard();
        startingPlayer = (startingPlayer + 1) % 2;
        currentPlayer = startingPlayer;
    }
    
    /**
     * User input driven Update
     * @param move The next user move
     * @return Returns true if the update was successful, false otherwise.
     */
    public Boolean updateGame(IMove move)
    {
        //Verify the new move and check if game is not finished
        if(isGameOver || !verifyMoveLegality(move)) 
        { 
            return false; 
        }
        
        //Update the currentState
        updateBoard(move);
        updateMacroboard(move);
        
        //Updates status
        updateGameStatus();
        
        //Switch currentPlayer if game is not finished
        if(!isGameOver)
        {
            currentPlayer = (currentPlayer + 1) % 2;
        }
        
        return true;
    }
    
    /**
     * Non-User driven input, e.g. an update for playing a bot move.
     * @return Returns true if the update was successful, false otherwise.
     */
    public Boolean updateGame()
    {
        //Verify the new move and check if game is not finished
        if(isGameOver)
        {
            return false;
        }
        //Check game mode is set to one of the bot modes.
        assert(mode != GameMode.HumanVsHuman);
        
        //Check if player is bot, if so, get bot input and update the state based on that.
        if(mode == GameMode.HumanVsBot && currentPlayer == 1)
        {
            //Check bot is not equal to null, and throw an exception if it is.
            assert(bot != null);
            
            botMove = bot.doMove(currentState);
        }
        
        else if(mode == GameMode.BotVsBot)
        {
            //Check bot is not equal to null, and throw an exception if it is.
            assert(bot != null);
            assert(bot2 != null);
            
            if(currentPlayer == 0)
            {
                botMove = bot.doMove(currentState);
            }
            else
            {
                botMove = bot2.doMove(currentState);
            }
        }
            
        //Be aware that your bots might perform illegal moves.
        return updateGame(botMove); 
    }
    
    private Boolean verifyMoveLegality(IMove move)
    {
        boolean isInActiveMicroboard = currentState.getField().isInActiveMicroboard(move.getX(), move.getY());
        boolean isEmpty = currentState.getField().getBoard()[move.getX()][move.getY()].equals(IField.EMPTY_FIELD);
        return isInActiveMicroboard && isEmpty;
    }
    
    private void updateBoard(IMove move)
    {
       String[][] board = currentState.getField().getBoard();
       board[move.getX()][move.getY()] = getPlayerMarker(currentPlayer);
    }
    
    private void updateMacroboard(IMove move)
    {
       updateMicroboardState(move);
       updateMicroboardsAvailability(move);
    }
    
    private void updateGameStatus()
    {
        currentState.setMoveNumber(currentState.getMoveNumber()+1);
        if(currentState.getMoveNumber() % 2 == 0) { currentState.setRoundNumber(currentState.getRoundNumber() + 1); }
        if(isWinOnMacroboard())
        {
            isGameOver = true;
            hasWinner = true;
        }
        else if(currentState.getField().isFull())
        {
            isGameOver = true;
        }
    }
    
    private void updateMicroboardState(IMove move)
    {
        String[][] macroboard = currentState.getField().getMacroboard();
        int startingXPosition = (move.getX()/3)*3;
        int startingYPosition = (move.getY()/3)*3;
        if(isWinOnMicroboard(startingXPosition, startingYPosition))
        {
            macroboard[move.getX()/3][move.getY()/3] = getPlayerMarker(currentPlayer);
        }
        else if(isDrawOnMicroboard(startingXPosition, startingYPosition))
        {
            macroboard[move.getX()/3][move.getY()/3] = DRAW_MARKER;
        }
    }
    
    private boolean isWinOnMicroboard(int startingX, int startingY)
    {
        String[][] board = currentState.getField().getBoard();
        return isWinOnBoard(board, startingX, startingY);
    }
    
    private boolean isWinOnMacroboard()
    {
        String[][] macroboard = currentState.getField().getMacroboard();
        return isWinOnBoard(macroboard, 0, 0);
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
        return ((board[startingX][startingY].equals(PLAYER_0_MARKER) || board[startingX][startingY].equals(PLAYER_1_MARKER))
                    && board[startingX][startingY].equals(board[startingX][startingY+1]) 
                    && board[startingX][startingY+1].equals(board[startingX][startingY+2]));
    }
    
    private boolean isVerticalWin(String[][] board, int startingX, int startingY)
    {
        return ((board[startingX][startingY].equals(PLAYER_0_MARKER) || board[startingX][startingY].equals(PLAYER_1_MARKER))
                    && board[startingX][startingY].equals(board[startingX+1][startingY]) 
                    && board[startingX+1][startingY].equals(board[startingX+2][startingY]));
    }
    
    private boolean isDiagonalWin(String[][] board, int startingX, int startingY)
    {
        if((board[startingX][startingY].equals(PLAYER_0_MARKER) || board[startingX][startingY].equals(PLAYER_1_MARKER))
                && board[startingX][startingY].equals(board[startingX+1][startingY+1])
                && board[startingX+1][startingY+1].equals(board[startingX+2][startingY+2]))
        {
            return true;
        }
        else if((board[startingX][startingY+2].equals(PLAYER_0_MARKER) || board[startingX][startingY+2].equals(PLAYER_1_MARKER))
                && board[startingX][startingY+2].equals(board[startingX+1][startingY+1])
                && board[startingX+1][startingY+1].equals(board[startingX+2][startingY]))
        {
            return true;
        }
        return false;
    }
    
    private boolean isDrawOnMicroboard(int startingX, int startingY)
    {
        boolean isDraw = true;
        String[][] board = currentState.getField().getBoard();
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
    
    private void updateMicroboardsAvailability(IMove move)
    {
       int activeMicroboardX = move.getX()%3;
       int activeMicroboardY = move.getY()%3;
       String[][] macroboard = currentState.getField().getMacroboard();
       if(macroboard[activeMicroboardX][activeMicroboardY].equals(IField.AVAILABLE_FIELD) 
               || macroboard[activeMicroboardX][activeMicroboardY].equals(IField.EMPTY_FIELD))
       {
           setAvailableMicroboard(activeMicroboardX, activeMicroboardY);
       }
       else
       {
           setAllMicroboardsAvailable();
       }
    }
    
    private void setAvailableMicroboard(int activeMicroboardX, int activeMicroboardY)
    {
        String[][] macroboard = currentState.getField().getMacroboard();
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
    }
    
    private void setAllMicroboardsAvailable()
    {
        String[][] macroboard = currentState.getField().getMacroboard();
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
    
    private String getPlayerMarker(int player)
    {
        if(player == 0)
        {
            return PLAYER_0_MARKER;
        }
        else
        {
            return PLAYER_1_MARKER;
        }
    }
    
    public int getCurrentPlayer()
    {
        return currentPlayer;
    }
    
    public GameMode getGameMode()
    {
        return mode;
    }
    
    public IMove getBotMove()
    {
        return botMove;
    }
    
    public boolean isGameOver()
    {
        return isGameOver;
    }
    
    public boolean hasWinner()
    {
        return hasWinner;
    }
}
