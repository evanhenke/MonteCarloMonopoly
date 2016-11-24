import static java.lang.Math.abs;

public class Player {
    private MonteCarloMonopoly game = new MonteCarloMonopoly();

    private int playerID = 0;
    private int state = 0;
    private int previousState = 0;
    private boolean isPlaying = true;
    private int money = 1200;
    private int favoredSide;
    private double probabilityToBuy;
    private int numPropertiesOwned = 0;
    private int numOfWins = 0;  //per round
    private double probabilityToBuyOfTheWinner;
    private static double ai = 0.99;

    GameHelper gh = new GameHelper();
    double[][] transitionMatrix = gh.generateTransitionMatrix();

    public Player(){}

    public Player(int ID, int side, double buyingProbability){
        playerID = ID;
        favoredSide = side;
        probabilityToBuy = buyingProbability;
    }

    public void setPlayerID(int newID){ playerID = newID; }
    public int getPlayerID(){ return playerID; }


    public void setState(int newState) { state = newState; }
    public void setPreviousState(int newPreviousState) { previousState = newPreviousState; }

    public int getState() { return state; }
    public int getPreviousState() { return previousState; }
    public int getMoney(){ return money; }
    public double getProbabilityToBuy(){ return probabilityToBuy; }


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

    public void setProbabilityToBuyOfTheWinner(double prob){
        probabilityToBuyOfTheWinner = prob;
    }

    public void setProbabilityToBuy(double prob){ probabilityToBuy = prob; }

    public void adjustProbabilityToBuy(){
        double var = probabilityToBuy;
        probabilityToBuy = ai*probabilityToBuy + (1 - ai)*probabilityToBuyOfTheWinner;
        if(abs(probabilityToBuy - var)>0.005) {
            System.out.println("difference is larger than it should be?");
            //state.displayOwnedAndOwnableArrays();
            System.exit(0);
        }
    }


    public double getProbabilityToBuyOfTheWinner(){ return probabilityToBuyOfTheWinner; }

    public boolean canAffordProperty(){
        if(money > game.getStateObject().getAmountOfPropertyAtIndex(state)){
            System.out.println("Player " + playerID + " can afford the property " + state + " from canAffordProperty method");
            return true;
        }
        System.out.println("Player " + playerID + " cannot afford the property " + state + " from canAffordProperty method");
        return false;
    }
}
