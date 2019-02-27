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
    private int currentPlayer = 0; //player0 == 0 && player1 == 1
    private GameMode mode = GameMode.HumanVsHuman;
    private IBot bot = null;
    private IBot bot2 = null;

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
    
    /**
     * User input driven Update
     * @param move The next user move
     * @return Returns true if the update was successful, false otherwise.
     */
    public Boolean updateGame(IMove move)
    {
        //Verify the new move
        if(!verifyMoveLegality(move)) 
        { 
            return false; 
        }
        
        //Update the currentState
        updateBoard(move);
        updateMacroboard(move);
        
        //Update currentPlayer
        currentPlayer = (currentPlayer + 1) % 2;
        
        return true;
    }
    
    /**
     * Non-User driven input, e.g. an update for playing a bot move.
     * @return Returns true if the update was successful, false otherwise.
     */
    public Boolean updateGame()
    {
        //Check game mode is set to one of the bot modes.
        assert(mode != GameMode.HumanVsHuman);
        
        //Check if player is bot, if so, get bot input and update the state based on that.
        if(mode == GameMode.HumanVsBot && currentPlayer == 1)
        {
            //Check bot is not equal to null, and throw an exception if it is.
            assert(bot != null);
            
            IMove botMove = bot.doMove(currentState);
            
            //Be aware that your bots might perform illegal moves.
            return updateGame(botMove);
        }
        
        //Check bot is not equal to null, and throw an exception if it is.
        assert(bot != null);
        assert(bot2 != null);
        
        //TODO: Implement a bot vs bot Update.
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    private Boolean verifyMoveLegality(IMove move)
    {
        if(currentState.getField().isInActiveMicroboard(move.getX(), move.getY()))
        {
            for(IMove availableMove : currentState.getField().getAvailableMoves())
            {
                if(move.getX() == availableMove.getX() && move.getY() == availableMove.getY())
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void updateBoard(IMove move)
    {
       String[][] board = currentState.getField().getBoard();
       board[move.getX()][move.getY()] = currentPlayer + "";
    }
    
    private void updateMacroboard(IMove move)
    {
       int activeMicroboardX = move.getX()%3;
       int activeMicroboardY = move.getY()%3;
       String[][] macroboard = currentState.getField().getMacroboard();
       if((macroboard[activeMicroboardX][activeMicroboardY] == IField.AVAILABLE_FIELD 
               || macroboard[activeMicroboardX][activeMicroboardY] == IField.EMPTY_FIELD))
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
        for(int i = 0; i < 3; i++)
           {
               for(int j = 0; j < 3; j++)
               {
                   if(i == activeMicroboardX && j == activeMicroboardY)
                   {
                       macroboard[i][j] = IField.AVAILABLE_FIELD;
                   }
                   else if(macroboard[i][j] == IField.AVAILABLE_FIELD)
                   {
                       macroboard[i][j] = IField.EMPTY_FIELD;
                   }
               }
           }
    }
    
    private void setAllMicroboardsAvailable()
    {
        String[][] macroboard = currentState.getField().getMacroboard();
        for(int i = 0; i < 3; i++)
           {
               for(int j = 0; j < 3; j++)
               {
                   if(macroboard[i][j] == IField.EMPTY_FIELD)
                   {
                       macroboard[i][j] = IField.AVAILABLE_FIELD;
                   }
               }
           }
    }
    
    public int getCurrentPlayer()
    {
        return currentPlayer;
    }
}
