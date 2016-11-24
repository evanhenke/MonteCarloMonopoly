import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.abs;

public class GameHelper {

    double[][] transitionMatrix = new double[40][40];
    double[] diceRollProbability = new double[12];


    static Player playerOne = new Player(1,1,0.7);
    static Player playerTwo = new Player(2,2,0.7);
    static Player playerThree = new Player(3,3,0.7);
    static Player playerFour = new Player(4,4,0.7);
    static ArrayList<Player> PlayerArray = new ArrayList<>();

    private MonteCarloMonopoly game = new MonteCarloMonopoly();


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
}
