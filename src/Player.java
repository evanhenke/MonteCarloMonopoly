import static java.lang.Math.abs;
import static java.lang.Math.random;

public class Player {
    private MonteCarloMonopoly game = new MonteCarloMonopoly();

    private int playerID = 0;
    private int state = 0;
    private int previousState = 0;
    private int currentSideOfBoard = 1;
    private boolean isPlaying = true;
    private int money = 1200;
    private int favoredSide;
    private double probabilityToBuyOnSideOne;
    private double probabilityToBuyOnSideTwo;
    private double probabilityToBuyOnSideThree;
    private double probabilityToBuyOnSideFour;
    private int numPropertiesOwned = 0;
    private int numOfWins = 0;  //per round
    private double probabilityToBuyOfTheWinnerOnSideOne;
    private double probabilityToBuyOfTheWinnerOnSideTwo;
    private double probabilityToBuyOfTheWinnerOnSideThree;
    private double probabilityToBuyOfTheWinnerOnSideFour;
    private static double ai = 0.99;

    GameHelper gh = new GameHelper();
    double[][] transitionMatrix = gh.generateTransitionMatrix();

    public Player(){}

    public Player(int ID, int side, double buyingProbability){
        playerID = ID;
        favoredSide = side;
        probabilityToBuyOnSideOne = buyingProbability;
        probabilityToBuyOnSideTwo = buyingProbability;
        probabilityToBuyOnSideThree = buyingProbability;
        probabilityToBuyOnSideFour = buyingProbability;
    }

    public void setPlayerID(int newID){ playerID = newID; }
    public int getPlayerID(){ return playerID; }


    public void setState(int newState) { state = newState; }
    public void setPreviousState(int newPreviousState) { previousState = newPreviousState; }

    public int getState() { return state; }

    public int getPreviousState() { return previousState; }

    public int getMoney(){ return money; }

    public double getProbabilityToBuyOnSideOne(){ return probabilityToBuyOnSideOne; }
    public double getProbabilityToBuyOnSideTwo(){ return probabilityToBuyOnSideTwo; }
    public double getProbabilityToBuyOnSideThree(){ return probabilityToBuyOnSideThree; }
    public double getProbabilityToBuyOnSideFour(){ return probabilityToBuyOnSideFour; }

    public void randomizeProbabilityToBuyOnEachSide(){
        probabilityToBuyOnSideOne = Math.random();
        probabilityToBuyOnSideTwo = Math.random();
        probabilityToBuyOnSideThree = Math.random();
        probabilityToBuyOnSideFour = Math.random();
    }

    public void randomizeProbabilityToBuyOnEachSideInRange(double min, double max){
        probabilityToBuyOnSideOne = Math.random()*(max - min) + min;
        probabilityToBuyOnSideTwo = Math.random()*(max - min) + min;
        probabilityToBuyOnSideThree = Math.random()*(max - min) + min;
        probabilityToBuyOnSideFour = Math.random()*(max - min) + min;
    }

    public void randomizeProbabilityToBuyOnSpecificSideInRange(double min, double max, int sideOfBoard){
        double value = Math.random()*(max - min) + min;
        if(sideOfBoard == 1){
            probabilityToBuyOnSideOne = value;
        }else if(sideOfBoard == 2){
            probabilityToBuyOnSideTwo = value;
        }else if(sideOfBoard == 3){
            probabilityToBuyOnSideThree = value;
        }else if(sideOfBoard == 4){
            probabilityToBuyOnSideFour = value;
        }else{
            System.out.println("getting an invalid sideOfBoard in player.setProbabilityWindowValues");
        }
    }

    public boolean isPlaying(){ return isPlaying; }

    public boolean isInFavoredState(int indexOnBoard){
        if(favoredSide == 1 && indexOnBoard >= 1 && indexOnBoard <= 9){
            return true;
        }
        if(favoredSide == 2 && indexOnBoard >= 11 && indexOnBoard <= 19){
            return true;
        }
        if(favoredSide == 3 && indexOnBoard >= 21 && indexOnBoard <= 29){
            return true;
        }
        if(favoredSide == 4 && indexOnBoard >= 31 && indexOnBoard <= 39){
            return true;
        }
        return false;
    }

    public void move(){
        previousState = state;
        double sum = 0.0;
        double limit = Math.random();

        for (int j = 0; j < 40; j++) {
            sum = sum + transitionMatrix[state][j];
            if (limit <= sum) {
                state = j;
                break;
            }
        }
        setCurrentSideOfBoard();
    }

    public void loseAmount(int amountToLose){
        money = money - amountToLose;
        //System.out.println("Player " + playerID + " loses money: " + amountToLose + ", player new total: " + money);
        if(money <= 0){
            isPlaying = false;
            //System.out.println(playerID + " isPlaying has been set to false by loseAmount method");
        }
    }
    public void gainAmount(int amountToGain){
        if(!isPlaying){
            System.out.println(playerID + " has already lost, they should not be gaining money..");
            System.exit(0);
        }
        money = money + amountToGain;
        //System.out.println(playerID + " has just gained " + amountToGain + " and now has " + money + " from gainAmount method");
    }

    public boolean didPlayerPassGo(){
        if(state <= 11 && previousState >= 28 && previousState != 30){
            return true;
        }
        return false;
    }

    public void setIsPlaying(boolean bool){
        isPlaying = bool;
    }

    public void resetPlayer(){
        isPlaying = true;
        money = 1200;
        numPropertiesOwned = 0;
    }

    public void resetNumPlayerWins(){ numOfWins = 0; }

    public void increaseNumberOfOwnedProperties(){ numPropertiesOwned++; }
    public int getNumPropertiesOwned(){ return numPropertiesOwned; }

    public void increaseNumberOfWins(){ numOfWins++; }
    public int getNumOfWins(){ return numOfWins; }


    public void adjustProbabilityToBuy(){
        probabilityToBuyOfTheWinnerOnSideOne = game.getRoundWinner().getProbabilityToBuyOnSideOne();
        probabilityToBuyOfTheWinnerOnSideTwo = game.getRoundWinner().getProbabilityToBuyOnSideTwo();
        probabilityToBuyOfTheWinnerOnSideThree = game.getRoundWinner().getProbabilityToBuyOnSideThree();
        probabilityToBuyOfTheWinnerOnSideFour = game.getRoundWinner().getProbabilityToBuyOnSideFour();

        //double var = probabilityToBuy;
        probabilityToBuyOnSideOne = ai*probabilityToBuyOnSideOne + (1 - ai)*probabilityToBuyOfTheWinnerOnSideOne;
        probabilityToBuyOnSideTwo = ai*probabilityToBuyOnSideTwo + (1 - ai)*probabilityToBuyOfTheWinnerOnSideTwo;
        probabilityToBuyOnSideThree = ai*probabilityToBuyOnSideThree + (1 - ai)*probabilityToBuyOfTheWinnerOnSideThree;
        probabilityToBuyOnSideFour = ai*probabilityToBuyOnSideFour + (1 - ai)*probabilityToBuyOfTheWinnerOnSideFour;
        /*if(abs(probabilityToBuy - var)>0.005) {
            System.out.println("difference is larger than it should be?");
            //state.displayOwnedAndOwnableArrays();
            System.exit(0);
        }*/
    }

    public void setCurrentSideOfBoard(){
        if (state <= 10){
            currentSideOfBoard = 1;
        } else if (state >= 11 && state <= 20){
            currentSideOfBoard = 2;
        } else if (state >= 21 && state <= 30){
            currentSideOfBoard = 3;
        } else if (state >= 31 && state <= 39){
            currentSideOfBoard = 4;
        } else {
            System.out.println("setCurrentSideOfBoard is giving error, state is not a valid state, it is: " + state);
            System.exit(0);
        }
    }

    public int getCurrentSideOfBoard(){ return currentSideOfBoard; }


    public boolean canAffordProperty(){
        if(money > game.getStateObject().getAmountOfPropertyAtIndex(state)){
            //System.out.println("Player " + playerID + " can afford the property " + state + " from canAffordProperty method");
            return true;
        }
        //System.out.println("Player " + playerID + " cannot afford the property " + state + " from canAffordProperty method");
        return false;
    }
}
