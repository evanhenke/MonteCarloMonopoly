import java.io.BufferedWriter;
import java.io.FileWriter;
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
    static State state = new State();
    static Strategy strategy = new Strategy();


    public static void main(String[] args) throws Exception {
        GameHelper gh = new GameHelper();

        int currentPlayerState;
        int numberOfGames = 1;
        int numOfRounds = 1;
        int numWinsByOne = 0;
        int ownerID;
        int moneyTransferred;
        int previousRoundNumWinsByOne;
        int highestNumWinsByOne = 0;

        double[] probabilityArray = new double[numOfRounds];

        java.util.Date dateStart = new Date();

        for (int roundNum = 1; roundNum <= numOfRounds; roundNum++) {
            System.out.println("-------------------------  Round Number: " + roundNum + "  -------------------------");
            previousRoundNumWinsByOne = numWinsByOne;

            playerOne.resetNumPlayerWins();
            playerTwo.resetNumPlayerWins();
            playerThree.resetNumPlayerWins();
            playerFour.resetNumPlayerWins();
            playerTwo.setProbabilityToBuy(Math.random()*0.2 + 0.7);
            playerThree.setProbabilityToBuy(Math.random()*0.2 + 0.7);
            playerFour.setProbabilityToBuy(Math.random()*0.2 + 0.7);

            for (int n = 1; n <= numberOfGames; n++) {
                System.out.println("%%%%%%%%%%  Game Number " + n + " %%%%%%%%%%");
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
                        System.out.println("******Start of player " + player.getPlayerID() + "'s turn ******");
                        System.out.println("Money: " + player.getMoney() + "  Current State: " + player.getState() + "  Num Properties owned: " + player.getNumPropertiesOwned());
                        player.move();
                        System.out.println("Player moved to new state: " + player.getState());
                        gh.accountForPlayerPassingGo();
                        currentPlayerState = player.getState();

                        if (state.stateIsOwnableAtIndex(currentPlayerState) && player.canAffordProperty()) {
                            System.out.println(player.getPlayerID() + " is on " + player.getState() + " and it is ownable and player has enough money to purchase it");
                            if (strategy.shouldPlayerBuy(player)) {
                                gh.propertyPurchasedFromBank(player);
                            }else {
                                System.out.println("Player " + player.getPlayerID() + " decided not to buy with prob: " + player.getProbabilityToBuy());
                            }
                        } else if (state.stateIsOwnedAtIndex(currentPlayerState)) {
                            ownerID = state.getIsStateOwnedIndexAt(currentPlayerState);
                            System.out.println(player.getPlayerID() + " has landed on an owned state");

                            moneyTransferred = gh.setAmountOfMoneyToBeTransferred();
                            player.loseAmount(moneyTransferred);

                            if (!player.isPlaying()) {
                                System.out.println("This causes player " + player.getPlayerID() + " to lose, updating owned and ownable arrays:");
                                state.updateOwnedAndOwnableDueToPlayerLoss(player.getPlayerID());
                                state.displayOwnedAndOwnableArrays();
                            }

                            gh.payPlayer(moneyTransferred);
                        }
                    }
                    System.out.println();

                    PlayerArray = gh.adjustPlayerArrayInCaseOfPlayerLoss(PlayerArray);

                    if (PlayerArray.size() == 1) {
                        PlayerArray.get(0).increaseNumberOfWins();
                    }else if(PlayerArray.size() == 0){
                        System.out.println("WHAT THE FUCK HOW");
                        state.displayOwnedAndOwnableArrays();
                        System.exit(0);
                    }
                    System.out.println("Each player has had a turn, new owned and ownable arrarys are: ");
                    state.displayOwnedAndOwnableArrays();
                    System.out.println();
                }

                PlayerArray.clear();
            }

            System.out.println();
            System.out.println("P1 prob to buy: " + playerOne.getProbabilityToBuy() + "  P2 prob to buy: " + playerTwo.getProbabilityToBuy() + "  P3 prob to buy: " + playerThree.getProbabilityToBuy() + "  P4 prob to buy: " + playerFour.getProbabilityToBuy() + "  roundnum: "+ roundNum);

            //System.out.println("P1 prob to buy: " + playerOne.getProbabilityToBuy() + "    roundNum: " + roundNum);

            //System.out.println("P1 wins = " + playerOne.getNumOfWins() + " P2 wins = " + playerTwo.getNumOfWins() + " P3 wins = " + playerThree.getNumOfWins() + " P4 wins = " + playerFour.getNumOfWins());

            playerOne.setProbabilityToBuyOfTheWinner(gh.getRoundWinnerProbabilityToBuy(playerOne,playerTwo,playerThree,playerFour));

            numWinsByOne = playerOne.getNumOfWins();
            playerOne.adjustProbabilityToBuy();

            //System.out.print("roundNum: " + roundNum + "  sum of wins: " + (playerOne.getNumOfWins() + playerTwo.getNumOfWins() + playerThree.getNumOfWins() + playerFour.getNumOfWins()));
            //System.out.println();

            probabilityArray[roundNum-1] = playerOne.getProbabilityToBuy();

        }

        java.util.Date dateEnd = new Date();
        System.out.println("Start time: " + dateStart);
        System.out.println("End time: " + dateEnd);

        gh.exportCSV(probabilityArray);
    }




    public State getStateObject(){ return state; }

    public ArrayList<Player> getPlayerArray(){ return PlayerArray; }

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

}
