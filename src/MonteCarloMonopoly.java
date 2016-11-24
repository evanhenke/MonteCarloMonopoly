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
    static State state = new State();
    static Strategy strategy = new Strategy(playerOne,playerTwo,playerThree,playerFour);


    public static void main(String[] args) throws Exception {
        GameHelper gh = new GameHelper();

        int currentPlayerState;
        int numberOfGames = 1000;
        int numOfRounds = 10000;
        int numWinsByOne = 0;
        int previousRoundNumWinsByOne;
        int highestNumWinsByOne = 0;

        double[] probabilityArray = new double[numOfRounds*numberOfGames];

        java.util.Date dateStart = new Date();

        for (int roundNum = 1; roundNum <= numOfRounds; roundNum++) {
            //System.out.println("-------------------------  Round Number: " + roundNum + "  -------------------------");
            previousRoundNumWinsByOne = numWinsByOne;

            playerOne.resetNumPlayerWins();
            playerTwo.resetNumPlayerWins();
            playerThree.resetNumPlayerWins();
            playerFour.resetNumPlayerWins();
            playerTwo.setProbabilityToBuy(Math.random()*0.2 + 0.7);
            playerThree.setProbabilityToBuy(Math.random()*0.2 + 0.7);
            playerFour.setProbabilityToBuy(Math.random()*0.2 + 0.7);

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
                        //System.out.println("******Start of player " + player.getPlayerID() + "'s turn ******");
                        //System.out.println("Money: " + player.getMoney() + "  Current State: " + player.getState() + "  Num Properties owned: " + player.getNumPropertiesOwned());
                        player.move();
                        //System.out.println("Player moved to new state: " + player.getState());
                        if (player.didPlayerPassGo()) {
                            player.gainAmount(200);
                            //System.out.println("Player passed go, new state is: " + player.getState() + " and new money is: " + player.getMoney());
                        }
                        currentPlayerState = player.getState();

                        if (state.getIsStateOwnableIndexAt(currentPlayerState) == 1 && player.getMoney() > state.getAmount(currentPlayerState)) {
                            //System.out.println(player.getPlayerID() + " is on " + player.getState() + " and it is ownable and player has enough money to purchase it");
                            if (strategy.shouldPlayerBuy(player)) {
                                player.loseAmount(state.getAmount(currentPlayerState));
                                state.setStateToOwnedInArrays(currentPlayerState, player.getPlayerID());
                                player.increaseNumberOfOwnedProperties();
                                //System.out.println(player.getPlayerID() + " has purchased the property, new money: " + player.getMoney() + " new num prop owned: " + player.getNumPropertiesOwned());
                            }else {
                                //System.out.println("Player " + player.getPlayerID() + " decided not to buy with prob: " + player.getProbabilityToBuy());
                            }
                        } else if (state.getIsStateOwnedIndexAt(currentPlayerState) != 0) {
                            int ownerID = state.getIsStateOwnedIndexAt(currentPlayerState);
                            int moneyTransferred;
                            //System.out.println(player.getPlayerID() + " has landed on an owned state");
                            if (player.getPlayerID() == ownerID) {
                                moneyTransferred = 0;
                                //System.out.println("it is his own state");
                            } else if (ownerID == 1) {
                                moneyTransferred = state.getAmount(currentPlayerState) * playerOne.getNumPropertiesOwned();
                                //System.out.println("it is 1's state");
                            } else if (ownerID == 2) {
                                moneyTransferred = state.getAmount(currentPlayerState) * playerTwo.getNumPropertiesOwned();
                                //System.out.println("it is 2's state");
                            } else if (ownerID == 3) {
                                moneyTransferred = state.getAmount(currentPlayerState) * playerThree.getNumPropertiesOwned();
                                //System.out.println("it is 3's state");
                            } else {
                                moneyTransferred = state.getAmount(currentPlayerState) * playerFour.getNumPropertiesOwned();
                                //System.out.println("it is 4's state");
                            }
                            //System.out.println(player.getPlayerID() + " loses amount: " + moneyTransferred + " to " + ownerID);

                            player.loseAmount(moneyTransferred);
                            if (!player.isPlaying()) {
                                //System.out.println("This causes player " + player.getPlayerID() + " to lose, updating owned and ownable arrays:");
                                state.updateOwnedAndOwnableDueToPlayerLoss(player.getPlayerID());
                                //state.displayOwnedAndOwnableArrays();
                            } else {
                                if (state.getIsStateOwnedIndexAt(currentPlayerState) == 1) {
                                    playerOne.gainAmount(moneyTransferred);
                                }
                                if (state.getIsStateOwnedIndexAt(currentPlayerState) == 2) {
                                    playerTwo.gainAmount(moneyTransferred);
                                }
                                if (state.getIsStateOwnedIndexAt(currentPlayerState) == 3) {
                                    playerThree.gainAmount(moneyTransferred);
                                }
                                if (state.getIsStateOwnedIndexAt(currentPlayerState) == 4) {
                                    playerFour.gainAmount(moneyTransferred);
                                }
                            }
                        }
                    }
                    //System.out.println();
                    if (!playerOne.isPlaying() && PlayerArray.contains(playerOne)) {
                        PlayerArray.remove(playerOne);
                        //System.out.println("Player " + playerOne.getPlayerID() + " has been removed from the PlayerArray Array List");
                        //System.out.println("There are " + PlayerArray.size() + " players left");
                    }
                    if (!playerTwo.isPlaying() && PlayerArray.contains(playerTwo)) {
                        PlayerArray.remove(playerTwo);
                        //System.out.println("Player " + playerTwo.getPlayerID() + " has been removed from the PlayerArray Array List");
                        //System.out.println("There are " + PlayerArray.size() + " players left");
                    }
                    if (!playerThree.isPlaying() && PlayerArray.contains(playerThree)) {
                        PlayerArray.remove(playerThree);
                        //System.out.println("Player " + playerThree.getPlayerID() + " has been removed from the PlayerArray Array List");
                        //System.out.println("There are " + PlayerArray.size() + " players left");
                    }
                    if (!playerFour.isPlaying() && PlayerArray.contains(playerFour)) {
                        PlayerArray.remove(playerFour);
                        //System.out.println("Player " + playerFour.getPlayerID() + " has been removed from the PlayerArray Array List");
                        //System.out.println("There are " + PlayerArray.size() + " players left");
                    }

                    if (PlayerArray.size() == 1) {
                        if (PlayerArray.contains(playerOne)) {
                            playerOne.increaseNumberOfWins();
                        }
                        if (PlayerArray.contains(playerTwo)) {
                            playerTwo.increaseNumberOfWins();
                        }
                        if (PlayerArray.contains(playerThree)) {
                            playerThree.increaseNumberOfWins();
                        }
                        if (PlayerArray.contains(playerFour)) {
                            playerFour.increaseNumberOfWins();
                        }
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

            //System.out.println();
            System.out.println("P1 prob to buy: " + playerOne.getProbabilityToBuy() + "  P2 prob to buy: " + playerTwo.getProbabilityToBuy() + "  P3 prob to buy: " + playerThree.getProbabilityToBuy() + "  P4 prob to buy: " + playerFour.getProbabilityToBuy());
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



        BufferedWriter br = new BufferedWriter(new FileWriter("myfile.csv"));
        StringBuilder sb = new StringBuilder();
        for (double element : probabilityArray) {
            sb.append(element);
            sb.append(",");
        }

        br.write(sb.toString());
        br.close();
    }




    public State getStateObject(){ return state; }

    public ArrayList getPlayerArray(){ return PlayerArray; }

    public Player getPlayerByID(int ID){
        if(ID == 1){
            return playerOne;
        }else if(ID == 2){
            return playerTwo;
        }else if(ID == 3){
            return playerThree;
        }
        return playerFour;
    }

}
