import com.panayotis.gnuplot.JavaPlot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.abs;

public class GameHelper {

    double[][] transitionMatrix = new double[40][40];
    double[] diceRollProbability = new double[12];


    private int amount;
    private int currentPlayerStateIndex;
    private int ownerID;

    private MonteCarloMonopoly game = new MonteCarloMonopoly();
    private JavaPlot plot = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");


    public ArrayList randomizeOrderPlayerArray(ArrayList arrayList){
        Collections.shuffle(arrayList);
        return arrayList;
    }

    public void displayPlayerOrder(ArrayList<Player> arrayList){
        System.out.print("Player Order: ");
        for(Player player : arrayList){
            System.out.print(player.getPlayerID() + " ");
        }
        System.out.println();
    }

    public void accountForPlayerPassingGo(){
        if (game.getCurrentPlayer().didPlayerPassGo()) {
            game.getCurrentPlayer().gainAmount(200);
            //System.out.println("Player " + game.getCurrentPlayer().getPlayerID() + " passed go, new state is: " + game.getCurrentPlayer().getState() + " and new money is: " + game.getCurrentPlayer().getMoney() + " from accountForPlayerPassingGo method");
        }
    }

    public void propertyPurchasedFromBank(Player playerWhoBought){
        playerWhoBought.loseAmount(game.getStateObject().getAmountOfPropertyAtIndex(playerWhoBought.getState()));
        game.getStateObject().setStateToOwnedInArrays(playerWhoBought.getState(), playerWhoBought.getPlayerID());
        playerWhoBought.increaseNumberOfOwnedProperties();
        //System.out.println(playerWhoBought.getPlayerID() + " has purchased the property, new money: " + playerWhoBought.getMoney() + " new num prop owned: " + playerWhoBought.getNumPropertiesOwned());
    }

    public int setAmountOfMoneyToBeTransferred(){
        currentPlayerStateIndex = game.getCurrentPlayer().getState();
        //System.out.println("testing shit this is currentPlayerStateIndex: " + currentPlayerStateIndex);
        ownerID = game.getStateObject().getIsStateOwnedIndexAt(currentPlayerStateIndex);
        //System.out.println("testing more shit this is ownerID in setAmountTransferred method: " + ownerID);

        if (game.getCurrentPlayer().getPlayerID() == ownerID) {
            amount = 0;
            //System.out.println("it is his own state");
        } else {
            amount = game.getStateObject().getAmountOfPropertyAtIndex(currentPlayerStateIndex)*game.getPlayerByID(ownerID).getNumPropertiesOwned();
            //System.out.println("State " + currentPlayerStateIndex + " is owned by player " + ownerID + " and the amount owed is " + amount + " from setAmountOfMoneyToBeTransferred method");
        }
        return amount;
    }

    public void payPlayer(int amount){
        currentPlayerStateIndex = game.getCurrentPlayer().getState();
        ownerID = game.getStateObject().getIsStateOwnedIndexAt(currentPlayerStateIndex);
        game.getPlayerByID(ownerID).gainAmount(amount);
        //System.out.println("Player " + game.getPlayerByID(ownerID).getPlayerID() + " has gained " + amount + " and new total money is " + game.getPlayerByID(ownerID).getMoney());
    }

    public ArrayList<Player> adjustPlayerArrayInCaseOfPlayerLoss(ArrayList<Player> PlayerArray){
        if (!game.getPlayerByID(1).isPlaying() && PlayerArray.contains(game.getPlayerByID(1))) {
            PlayerArray.remove(game.getPlayerByID(1));
            //System.out.println("Player " + game.getPlayerByID(1).getPlayerID() + " has been removed from the PlayerArray Array List");
            //System.out.println("There are " + PlayerArray.size() + " players left");
        }
        if (!game.getPlayerByID(2).isPlaying() && PlayerArray.contains(game.getPlayerByID(2))) {
            PlayerArray.remove(game.getPlayerByID(2));
            //System.out.println("Player " + game.getPlayerByID(2).getPlayerID() + " has been removed from the PlayerArray Array List");
            //System.out.println("There are " + PlayerArray.size() + " players left");
        }
        if (!game.getPlayerByID(3).isPlaying() && PlayerArray.contains(game.getPlayerByID(3))) {
            PlayerArray.remove(game.getPlayerByID(3));
            //System.out.println("Player " + game.getPlayerByID(3).getPlayerID() + " has been removed from the PlayerArray Array List");
            //System.out.println("There are " + PlayerArray.size() + " players left");
        }
        if (!game.getPlayerByID(4).isPlaying() && PlayerArray.contains(game.getPlayerByID(4))) {
            PlayerArray.remove(game.getPlayerByID(4));
            //System.out.println("Player " + game.getPlayerByID(4).getPlayerID() + " has been removed from the PlayerArray Array List");
            //System.out.println("There are " + PlayerArray.size() + " players left");
        }

        return PlayerArray;
    }

    public void adjustTotalPlayerWinsDueToGameEnding(){
        game.getPlayerArray().get(0).increaseNumberOfWins();
    }

    public double getRoundWinnerProbabilityToBuy(Player p1, Player p2, Player p3, Player p4){
        if(p1.getNumOfWins() > p2.getNumOfWins() && p1.getNumOfWins() > p3.getNumOfWins() && p1.getNumOfWins() > p4.getNumOfWins()){
            return p1.getProbabilityToBuy();
        }else if(p2.getNumOfWins() > p1.getNumOfWins() && p2.getNumOfWins() > p3.getNumOfWins() && p2.getNumOfWins() > p4.getNumOfWins()){
            return p2.getProbabilityToBuy();
        }else if(p3.getNumOfWins() > p1.getNumOfWins() && p3.getNumOfWins() > p2.getNumOfWins() && p3.getNumOfWins() > p4.getNumOfWins()){
            return p3.getProbabilityToBuy();
        }
        return p4.getProbabilityToBuy();
    }

    public double[][] generateTransitionMatrix(){
        diceRollProbability[0] = 0;
        diceRollProbability[1] = 1/36.0;
        diceRollProbability[2] = 2/36.0;
        diceRollProbability[3] = 3/36.0;
        diceRollProbability[4] = 4/36.0;
        diceRollProbability[5] = 5/36.0;
        diceRollProbability[6] = 6/36.0;
        diceRollProbability[11] = 1/36.0;
        diceRollProbability[10] = 2/36.0;
        diceRollProbability[9] = 3/36.0;
        diceRollProbability[8] = 4/36.0;
        diceRollProbability[7] = 5/36.0;
        int i = 0,j = 0;

        while(i<40) {
            for (int count = 0; count < 12; count++) {
                int colCount = count + j + 1;
                if(colCount > 39){
                    colCount = abs(40 - colCount);
                }
                if(colCount==30){ //this accounts for landing on 'go to jail' space
                    transitionMatrix[i][10] = diceRollProbability[count];
                    transitionMatrix[i][colCount] = 0.0;
                } else {
                    transitionMatrix[i][colCount] = diceRollProbability[count];
                }
            }
            i++;
            j++;
        }

        return transitionMatrix;
    }

    public void displayTransitionMatrix(){
        int i,j;
        for(i = 0; i < 40; i++){
            System.out.println();
            for(j = 0; j < 40; j++){
                System.out.print(Math.round(transitionMatrix[i][j]*1000)/1000.0 + " ");
            }
        }
    }

    public void exportCSV(double[] probabilityArray) throws Exception {
        BufferedWriter br = new BufferedWriter(new FileWriter("myfile.csv"));
        StringBuilder sb = new StringBuilder();
        for (double element : probabilityArray) {
            sb.append(element);
            sb.append("\n");
        }

        br.write(sb.toString());
        br.close();
    }

    public void plotProbabilityArray(){
        int arraySize = game.getProbabilityArray().length;
        double[][] array = new double[arraySize][1];

        for(int count = 0; count < arraySize; count++){
            array[count][0] = game.getProbabilityArray()[count];
        }

        plot.addPlot(array);
        plot.plot();
    }

    public void plotProbabilityArrayInRange(int startPointByRoundNumber, int endPointByRoundNumber){
        int arraySize = game.getProbabilityArray().length - startPointByRoundNumber - (game.getProbabilityArray().length - endPointByRoundNumber);
        System.out.println("Starting point: " + startPointByRoundNumber + " Ending Point: " + endPointByRoundNumber + " and arraySize: " + arraySize);
        double[][] array = new double[arraySize][1];


        for(int count = 0; count < arraySize; count++){
            array[count][0] = game.getProbabilityArray()[count + startPointByRoundNumber];
        }

        plot.addPlot(array);
        plot.plot();
    }
}
