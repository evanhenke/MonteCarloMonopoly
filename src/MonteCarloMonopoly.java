import com.panayotis.gnuplot.JavaPlot;

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

    static int numberOfGames = 1000;
    static int numOfRounds = 10000;
    static double[] probabilityArrayOnSideOne = new double[numOfRounds];
    static double[] probabilityArrayOnSideTwo = new double[numOfRounds];
    static double[] probabilityArrayOnSideThree = new double[numOfRounds];
    static double[] probabilityArrayOnSideFour = new double[numOfRounds];


    public static void main(String[] args) throws Exception {
        GameHelper gh = new GameHelper();

        int currentPlayerState;
        int numWinsByOne = 0;
        int ownerID;
        int moneyTransferred;
        int previousRoundNumWinsByOne;
        int highestNumWinsByOne = 0;

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

            System.out.println("P1 prob side 1: " + playerOne.getProbabilityToBuyOnSideOne() + "  P1 prob side 2: " + playerOne.getProbabilityToBuyOnSideTwo() + " P1 prob side 3: " + playerOne.getProbabilityToBuyOnSideThree() + "  p1 prob side 4: " + playerOne.getProbabilityToBuyOnSideFour() + "  roundNum: " + roundNum + "  roundWinner: "+ roundWinner.getPlayerID());
            //System.out.println("P1 prob to buy: " + playerOne.getProbabilityToBuy() + "    roundNum: " + roundNum);

            //System.out.println("P1 wins = " + playerOne.getNumOfWins() + " P2 wins = " + playerTwo.getNumOfWins() + " P3 wins = " + playerThree.getNumOfWins() + " P4 wins = " + playerFour.getNumOfWins());

            playerOne.adjustProbabilityToBuy();

            numWinsByOne = playerOne.getNumOfWins();
            playerOne.adjustProbabilityToBuy();

            //System.out.print("roundNum: " + roundNum + "  sum of wins per round: " + (playerOne.getNumOfWins() + playerTwo.getNumOfWins() + playerThree.getNumOfWins() + playerFour.getNumOfWins()));
            //System.out.println();

            probabilityArrayOnSideOne[roundNum-1] = playerOne.getProbabilityToBuyOnSideOne();
            probabilityArrayOnSideTwo[roundNum-1] = playerOne.getProbabilityToBuyOnSideTwo();
            probabilityArrayOnSideThree[roundNum-1] = playerOne.getProbabilityToBuyOnSideThree();
            probabilityArrayOnSideFour[roundNum-1] = playerOne.getProbabilityToBuyOnSideFour();

        }

        java.util.Date dateEnd = new Date();
        System.out.println("Start time: " + dateStart);
        System.out.println("End time: " + dateEnd);

        /*double[] averageValueProbabilityArrayOnSideOne = new double[numOfRounds];
        averageValueProbabilityArrayOnSideOne[0] = probabilityArrayOnSideOne[0];

        for(int index = 1; index <= probabilityArrayOnSideOne.length; index++){
            averageValueProbabilityArrayOnSideOne[index] = averageValueProbabilityArrayOnSideOne[index-1]/index;
        }*/

        gh.exportCSV(probabilityArrayOnSideOne);
        gh.plotProbabilityArray(probabilityArrayOnSideOne);
        gh.plotProbabilityArray(probabilityArrayOnSideTwo);
        gh.plotProbabilityArray(probabilityArrayOnSideThree);
        gh.plotProbabilityArray(probabilityArrayOnSideFour);
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

}
