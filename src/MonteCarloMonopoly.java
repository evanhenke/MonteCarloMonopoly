import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MonteCarloMonopoly {
    static ArrayList<Player> PlayerArray = new ArrayList<>();

    static Player playerOne = new Player(1,1,0.5);
    static Player playerTwo = new Player(2,2,Math.random()*0.2 + 0.7);
    static Player playerThree = new Player(3,3,Math.random()*0.2 + 0.7);
    static Player playerFour = new Player(4,4,Math.random()*0.2 + 0.7);
    static Player currentPlayer = new Player();
    static Player roundWinner = new Player();
    static State state = new State();
    static Strategy strategy = new Strategy();
    static GameHelper gh = new GameHelper();

    static int numberOfGames = 1000;
    static int numOfRounds = 40000;
    static int startingComparisonValue = 3000;
    static double[] probabilityArrayOnSideOne = new double[numOfRounds];
    static double[] probabilityArrayOnSideTwo = new double[numOfRounds];
    static double[] probabilityArrayOnSideThree = new double[numOfRounds];
    static double[] probabilityArrayOnSideFour = new double[numOfRounds];
    static double[] averageArraySideOne = new double[numOfRounds];
    static double[] averageArraySideTwo = new double[numOfRounds];
    static double[] averageArraySideThree = new double[numOfRounds];
    static double[] averageArraySideFour = new double[numOfRounds];
    static double probSumSideOne=0,probSumSideTwo=0,probSumSideThree=0,probSumSideFour=0;
    static double averageSideOne,averageSideTwo,averageSideThree,averageSideFour;
    static double[] cutAverageArraySideOne = new double[numOfRounds];
    static double[] cutAverageArraySideTwo = new double[numOfRounds];
    static double[] cutAverageArraySideThree = new double[numOfRounds];
    static double[] cutAverageArraySideFour = new double[numOfRounds];


    public static void main(String[] args) throws Exception {
        int currentPlayerState;
        int numWinsByOne = 0;
        int ownerID;
        int moneyTransferred;
        int previousRoundNumWinsByOne;
        int highestNumWinsByOne = 0;

        double cutSumSideOne = 0;
        double cutSumSideTwo = 0;
        double cutSumSideThree = 0;
        double cutSumSideFour = 0;
        double cutAverageSideOne = 0;
        double cutAverageSideTwo = 0;
        double cutAverageSideThree = 0;
        double cutAverageSideFour = 0;


        java.util.Date dateStart = new Date();

        for (int roundNum = 1; roundNum <= numOfRounds; roundNum++) {
            //System.out.println("-------------------------  Round Number: " + roundNum + "  -------------------------");
            previousRoundNumWinsByOne = numWinsByOne;

            playerOne.resetNumPlayerWins();
            playerTwo.resetNumPlayerWins();
            playerThree.resetNumPlayerWins();
            playerFour.resetNumPlayerWins();
            playerTwo.randomizeProbabilityToBuyOnEachSide();
            playerThree.randomizeProbabilityToBuyOnEachSide();
            playerFour.randomizeProbabilityToBuyOnEachSide();

            for (int n = 1; n <= numberOfGames; n++) {
                //System.out.println("%%%%%%%%%%  Game Number " + n + " %%%%%%%%%%");
                PlayerArray.add(playerOne);
                PlayerArray.add(playerTwo);
                PlayerArray.add(playerThree);
                PlayerArray.add(playerFour);

                Collections.shuffle(PlayerArray);
                //gh.displayPlayerOrder(PlayerArray);

                state.resetOwnedAndOwnableArrays();
                playerOne.resetPlayer();
                playerTwo.resetPlayer();
                playerThree.resetPlayer();
                playerFour.resetPlayer();

                //state.displayOwnedAndOwnableArrays();

                while (PlayerArray.size() > 1) {
                    for (Player player : PlayerArray) {
                        currentPlayer = player;
                        //System.out.println("******Start of player " + player.getPlayerID() + "'s turn ******");
                        //System.out.println("Money: " + player.getMoney() + "  Current State: " + player.getState() + "  Num Properties owned: " + player.getNumPropertiesOwned());
                        player.move();
                        //System.out.println("Player moved to new state: " + player.getState());
                        gh.accountForPlayerPassingGo();
                        currentPlayerState = player.getState();

                        if (state.stateIsOwnableAtIndex(currentPlayerState) && player.canAffordProperty()) {
                            //System.out.println(player.getPlayerID() + " is on " + player.getState() + " and it is ownable and player has enough money to purchase it");
                            if (strategy.shouldPlayerBuy(player)) {
                                gh.propertyPurchasedFromBank(player);
                            }else {
                                //System.out.println("Player " + player.getPlayerID() + " decided not to buy");
                            }
                        } else if (state.stateIsOwnedAtIndex(currentPlayerState)) {
                            ownerID = state.getIsStateOwnedIndexAt(currentPlayerState);
                            //System.out.println(player.getPlayerID() + " has landed on an owned state");

                            moneyTransferred = gh.setAmountOfMoneyToBeTransferred();
                            player.loseAmount(moneyTransferred);

                            if (!player.isPlaying()) {
                                //System.out.println("This causes player " + player.getPlayerID() + " to lose, updating owned and ownable arrays:");
                                state.updateOwnedAndOwnableDueToPlayerLoss(player.getPlayerID());
                                //state.displayOwnedAndOwnableArrays();
                            }

                            gh.payPlayer(moneyTransferred);
                        }
                    }
                    //System.out.println();

                    PlayerArray = gh.adjustPlayerArrayInCaseOfPlayerLoss(PlayerArray);

                    if (PlayerArray.size() == 1) {
                        PlayerArray.get(0).increaseNumberOfWins();
                    }else if(PlayerArray.size() == 0){
                        System.out.println("WHAT THE FUCK HOW");
                        state.displayOwnedAndOwnableArrays();
                        System.exit(0);
                    }
                    //System.out.println("Each player has had a turn, new owned and ownable arrarys are: ");
                    //state.displayOwnedAndOwnableArrays();
                    //System.out.println();
                }

                PlayerArray.clear();
            }

            roundWinner = gh.getRoundWinner();

            //System.out.println();
            //System.out.println("P1 prob to buy: " + playerOne.getProbabilityToBuyOnSideOne() + "  P2 prob to buy: " + playerTwo.getProbabilityToBuyOnSideOne() + "  P3 prob to buy: " + playerThree.getProbabilityToBuyOnSideOne() + "  P4 prob to buy: " + playerFour.getProbabilityToBuyOnSideOne() + "  roundnum: "+ roundNum + "  roundWinner: player " + roundWinner.getPlayerID());

            System.out.println("roundNum: " + roundNum + "  roundWinner: "+ roundWinner.getPlayerID() + " p1 prob side 1: " + playerOne.getProbabilityToBuyOnSideOne() + " p1 prob side 2: " + playerOne.getProbabilityToBuyOnSideTwo() + " p1 prob side 3: " + playerOne.getProbabilityToBuyOnSideThree() + "  p1 prob side 4: " + playerOne.getProbabilityToBuyOnSideFour() + "  p2 prob side 1: " + playerTwo.getProbabilityToBuyOnSideOne() + " p2 prob side 2: " + playerTwo.getProbabilityToBuyOnSideTwo() + " p2 prob side 3: " + playerTwo.getProbabilityToBuyOnSideThree() + " p2 prob side 4: " + playerTwo.getProbabilityToBuyOnSideFour() + " p3 prob side 1: " + playerThree.getProbabilityToBuyOnSideOne() + " p3 prob side 2: " + playerThree.getProbabilityToBuyOnSideTwo() + " p3 prob side 3: " + playerThree.getProbabilityToBuyOnSideThree() + " p3 prob side 4: " + playerThree.getProbabilityToBuyOnSideFour() + " p4 prob side 1: " + playerFour.getProbabilityToBuyOnSideOne() + " p4 prob side 2: " + playerFour.getProbabilityToBuyOnSideTwo() + " p4 prob side 3: " + playerFour.getProbabilityToBuyOnSideThree() + " p4 prob side 4: " + playerFour.getProbabilityToBuyOnSideFour() + " num wins 1: " + playerOne.getNumOfWins() + " num wins 2: " + playerTwo.getNumOfWins() + " num wins 3: " + playerThree.getNumOfWins() + " num wins 4: " + playerFour.getNumOfWins());
            //System.out.println("P1 prob to buy: " + playerOne.getProbabilityToBuy() + "    roundNum: " + roundNum);

            //System.out.println("P1 wins = " + playerOne.getNumOfWins() + " P2 wins = " + playerTwo.getNumOfWins() + " P3 wins = " + playerThree.getNumOfWins() + " P4 wins = " + playerFour.getNumOfWins());

            playerOne.adjustProbabilityToBuy();

            numWinsByOne = playerOne.getNumOfWins();
            playerOne.adjustProbabilityToBuy();

            //System.out.print("roundNum: " + roundNum + "  sum of wins per round: " + (playerOne.getNumOfWins() + playerTwo.getNumOfWins() + playerThree.getNumOfWins() + playerFour.getNumOfWins()));
            //System.out.println();


            probSumSideOne = probSumSideOne + playerOne.getProbabilityToBuyOnSideOne();
            probSumSideTwo = probSumSideTwo + playerOne.getProbabilityToBuyOnSideOne();
            probSumSideThree = probSumSideThree + playerOne.getProbabilityToBuyOnSideThree();
            probSumSideFour = probSumSideFour + playerOne.getProbabilityToBuyOnSideFour();

            averageSideOne = probSumSideOne/roundNum;
            averageSideTwo = probSumSideTwo/roundNum;
            averageSideThree = probSumSideThree/roundNum;
            averageSideFour = probSumSideFour/roundNum;

            averageArraySideOne[roundNum-1] = averageSideOne;
            averageArraySideTwo[roundNum-1] = averageSideTwo;
            averageArraySideThree[roundNum-1] = averageSideThree;
            averageArraySideFour[roundNum-1] = averageSideFour;


            probabilityArrayOnSideOne[roundNum-1] = playerOne.getProbabilityToBuyOnSideOne();
            probabilityArrayOnSideTwo[roundNum-1] = playerOne.getProbabilityToBuyOnSideTwo();
            probabilityArrayOnSideThree[roundNum-1] = playerOne.getProbabilityToBuyOnSideThree();
            probabilityArrayOnSideFour[roundNum-1] = playerOne.getProbabilityToBuyOnSideFour();


            if(roundNum>startingComparisonValue){
                cutSumSideOne = cutSumSideOne + playerOne.getProbabilityToBuyOnSideOne();
                cutSumSideTwo = cutSumSideTwo + playerOne.getProbabilityToBuyOnSideTwo();
                cutSumSideThree = cutSumSideThree + playerOne.getProbabilityToBuyOnSideThree();
                cutSumSideFour = cutSumSideFour + playerOne.getProbabilityToBuyOnSideFour();

                cutAverageSideOne = cutSumSideOne/(roundNum-startingComparisonValue);
                cutAverageSideTwo = cutSumSideTwo/(roundNum-startingComparisonValue);
                cutAverageSideThree = cutSumSideThree/(roundNum-startingComparisonValue);
                cutAverageSideFour = cutSumSideFour/(roundNum-startingComparisonValue);


                cutAverageArraySideOne[roundNum-1] = cutAverageSideOne;
                cutAverageArraySideTwo[roundNum-1] = cutAverageSideTwo;
                cutAverageArraySideThree[roundNum-1] = cutAverageSideThree;
                cutAverageArraySideFour[roundNum-1] = cutAverageSideFour;
            }else{
                cutAverageArraySideOne[roundNum-1] = 0;
                cutAverageArraySideTwo[roundNum-1] = 0;
                cutAverageArraySideThree[roundNum-1] = 0;
                cutAverageArraySideFour[roundNum-1] = 0;
            }

            gh.adjustPlayerProbabilitiesToBuyImplementingFunnel(roundNum);

        }

        System.out.println("P1 averages = side1 : " + averageArraySideOne[numOfRounds-1] + " side 2: " + averageArraySideTwo[numOfRounds-1] + " side 3: " + averageArraySideThree[numOfRounds-1] + " side 4: " + averageArraySideFour[numOfRounds-1]);
        System.out.println("P1 cut averages = side1 : " + cutAverageArraySideOne[numOfRounds-1] + " side 2: " + cutAverageArraySideTwo[numOfRounds-1] + " side 3: " + cutAverageArraySideThree[numOfRounds-1] + " side 4: " + cutAverageArraySideFour[numOfRounds-1]);


        java.util.Date dateEnd = new Date();
        System.out.println("Start time: " + dateStart);
        System.out.println("End time: " + dateEnd);

        /*double[] averageValueProbabilityArrayOnSideOne = new double[numOfRounds];
        averageValueProbabilityArrayOnSideOne[0] = probabilityArrayOnSideOne[0];

        for(int index = 1; index <= probabilityArrayOnSideOne.length; index++){
            averageValueProbabilityArrayOnSideOne[index] = averageValueProbabilityArrayOnSideOne[index-1]/index;
        }*/

        gh.exportCSV(probabilityArrayOnSideOne);

        //gh.generateTransitionMatrix();
        //gh.displayTransitionMatrix();

        playerOne.

        gh.plotAllProbabilityArrays();
        gh.plotProbabilityArray(averageArraySideOne);
        gh.plotProbabilityArray(averageArraySideTwo);
        gh.plotProbabilityArray(averageArraySideThree);
        gh.plotProbabilityArray(averageArraySideFour);

    }



    public State getStateObject(){ return state; }

    public ArrayList<Player> getPlayerArray(){ return PlayerArray; }

    public Player getRoundWinner(){ return roundWinner; }

    public Player getPlayerByID(int ID){
        if(ID == 1){
            return playerOne;
        }else if(ID == 2){
            return playerTwo;
        }else if(ID == 3){
            return playerThree;
        }else if(ID == 4) {
            return playerFour;
        }else {
            System.out.println("That ain't no player, playa!");
            return null;
        }
    }

    public Player getCurrentPlayer(){ return currentPlayer; }

    public double[] getProbabilityArrayOnSideOne(){ return probabilityArrayOnSideOne; }
    public double[] getProbabilityArrayOnSideTwo(){ return probabilityArrayOnSideTwo; }
    public double[] getProbabilityArrayOnSideThree(){ return probabilityArrayOnSideThree; }
    public double[] getProbabilityArrayOnSideFour(){ return probabilityArrayOnSideFour; }

    public int getNumOfRounds(){ return numOfRounds; }

    public double[] getAverageArrayOnSpecificSide(int sideNum){
        if(sideNum == 1){
            return averageArraySideOne;
        }else if(sideNum == 2){
            return averageArraySideTwo;
        }else if(sideNum == 3){
            return averageArraySideThree;
        }else if(sideNum == 4){
            return averageArraySideFour;
        }else{
            System.out.println("invalid sideNum in getAverageArrayOnSpecificSide " + sideNum);
            System.exit(0);
        }
        return null;
    }

    public double[] getCutAverageArrayOnSpecificSide(int sideNum){
        if(sideNum == 1){
            return cutAverageArraySideOne;
        }else if(sideNum == 2){
            return cutAverageArraySideTwo;
        }else if(sideNum == 3){
            return cutAverageArraySideThree;
        }else if(sideNum == 4){
            return cutAverageArraySideFour;
        }else{
            System.out.println("invalid sideNum in getCutAverageArrayOnSpecificSide " + sideNum);
            System.exit(0);
        }
        return null;
    }
}
