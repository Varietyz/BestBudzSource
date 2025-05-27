package com.mayhem.rs2.content;

public class EloRatingSystem {
	// Score constants
    private final static double GWIN = 1.0;
    private final static double GDRAW = 0.5;
    private final static double GLOSS = 0.0;




    /** Default ELO starting rating for new users. */ 
    public static final int DEFAULT_ELO_START_RATING = 1200; 
    
    /** Default ELO k factor. */
    public static final double DEFAULT_ELO_K_FACTOR = 24.0;
    
    /** Player wins a game. */
    public static final int WIN = 1;


    /** Player losses a game. */
    public static final int LOSE = 2;


    /** Player draws with another player. */
    public static final int DRAW = 3;     
    
    /**
     * Convience overloaded version of getNewRating (int, int, double)
     * which takes a result type and 
     * 
     * @param rating
     * @param opponentRating
     * @param resultType
     * @return
     */
    public static int getNewRating (int rating, int opponentRating, int resultType) {
        switch (resultType) {
            case WIN:
                return getNewRating (rating, opponentRating, GWIN);
            case LOSE:
                return getNewRating (rating, opponentRating, GLOSS);
            case DRAW:
                return getNewRating (rating, opponentRating, GDRAW);                
        }
        return -1;        // no score this time.
    }
    
    /**
     * Get new rating.
     * 
     * @param rating
     *            Rating of either the current player or the average of the
     *            current team.
     * @param opponentRating
     *            Rating of either the opponent player or the average of the
     *            opponent team or teams.
     * @param score
     *            Score: 0=Loss 0.5=Draw 1.0=Win
     * @return the new rating
     */
    public static int getNewRating(int rating, int opponentRating, double score) {
        double kFactor       = getKFactor(rating);
        double expectedScore = getExpectedScore(rating, opponentRating);
        int    newRating     = calculateNewRating(rating, score, expectedScore, kFactor);
        
        return newRating;
    }    
    
    /**
     * Calculate the new rating based on the ELO standard formula.
     * newRating = oldRating + constant * (score - expectedScore)
     * 
     * @param oldRating     Old Rating
     * @param score            Score
     * @param expectedScore    Expected Score
     * @param constant        Constant
     * @return                the new rating of the player
     */
    private static int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }
    
    /**
     * This is the standard chess constant.  This constant can differ
     * based on different games.  The higher the constant the faster
     * the rating will grow.  That is why for this standard chess method,
     * the constant is higher for weaker players and lower for stronger
     * players.
     *  
     * @param rating        Rating
     * @return                Constant
     */
    private static double getKFactor (int rating) {
        // Return the correct k factor.
        return DEFAULT_ELO_K_FACTOR;
    }
    
    /**
     * Get expected score based on two players.  If more than two players
     * are competing, then opponentRating will be the average of all other
     * opponent's ratings.  If there is two teams against each other, rating
     * and opponentRating will be the average of those players.
     * 
     * @param rating            Rating
     * @param opponentRating    Opponent(s) rating
     * @return                    the expected score
     */
    private static double getExpectedScore (int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }
}