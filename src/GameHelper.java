import com.panayotis.gnuplot.JavaPlot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class GameHelper {

    double[][] transitionMatrix = new double[40][40];
    double[] diceRollProbability = new double[12];


    private int amount;
    private int currentPlayerStateIndex;
    private int ownerID;
    private double windowMin;
    private double windowMax;

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

    public Player getRoundWinner(){
        Player p1 = game.getPlayerByID(1);
        Player p2 = game.getPlayerByID(2);
        Player p3 = game.getPlayerByID(3);
        Player p4 = game.getPlayerByID(4);

        if(p1.getNumOfWins() > p2.getNumOfWins() && p1.getNumOfWins() > p3.getNumOfWins() && p1.getNumOfWins() > p4.getNumOfWins()){
            return p1;
        }else if(p2.getNumOfWins() > p1.getNumOfWins() && p2.getNumOfWins() > p3.getNumOfWins() && p2.getNumOfWins() > p4.getNumOfWins()){
            return p2;
        }else if(p3.getNumOfWins() > p1.getNumOfWins() && p3.getNumOfWins() > p2.getNumOfWins() && p3.getNumOfWins() > p4.getNumOfWins()){
            return p3;
        }
        return p4;
    }

    /*public void adjustPlayerProbabilitiesToBuyImplementingFunnel(int roundNum){
        int numOfRounds = game.getNumOfRounds();
        if((double)roundNum < (double)(numOfRounds/10)) {
            adjustProbabilitiesBeforeFunnel();
        }else if((double)roundNum >= (double)(numOfRounds/10) && (double)roundNum <= (double)(numOfRounds/5)){
            adjustProbabilitiesWithFunnelOfSize(.4,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds/5) && (double)roundNum <= (double)(numOfRounds*3/10)) {
            adjustProbabilitiesWithFunnelOfSize(.3,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds*3/10) && (double)roundNum <= (double)(numOfRounds*2/5)){
            adjustProbabilitiesWithFunnelOfSize(.2,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds*2/5) && (double)roundNum <= (double)(numOfRounds/2)){
            adjustProbabilitiesWithFunnelOfSize(.1,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds/2) && (double)roundNum <= (double)(numOfRounds*3/5)) {
            adjustProbabilitiesWithFunnelOfSize(.075,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds*3/5) && (double)roundNum <= (double)(numOfRounds*7/10)){
            adjustProbabilitiesWithFunnelOfSize(.05,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds*7/10) && (double)roundNum <= (double)(numOfRounds*4/5)) {
            adjustProbabilitiesWithFunnelOfSize(.025,roundNum);
        }else if((double)roundNum >= (double)(numOfRounds*4/5) && (double)roundNum <= (double)(numOfRounds*9/10)){
            adjustProbabilitiesWithFunnelOfSize(.01,roundNum);
        }else{
            adjustProbabilitiesWithFunnelOfSize(.005,roundNum);
        }
    }*/

    public void adjustPlayerProbabilitiesToBuyImplementingFunnel(int roundNum){
        int numOfRounds = game.getNumOfRounds();
        if(roundNum < 1000) {
            adjustProbabilitiesBeforeFunnel();
        }else if(roundNum >= 3000 && roundNum < 4000){
            adjustProbabilitiesWithFunnelOfSize(.4,roundNum);
        }else if(roundNum >= 4000 && roundNum < 5000){
            adjustProbabilitiesWithFunnelOfSize(.3,roundNum);
        }else if(roundNum >= 5000 && roundNum < 6000){
            adjustProbabilitiesWithFunnelOfSize(.2,roundNum);
        }else if(roundNum >= 6000 && roundNum < 7000){
            adjustProbabilitiesWithFunnelOfSize(.1,roundNum);
        }else if(roundNum >= 7000 && roundNum < 8000){
            adjustProbabilitiesWithFunnelOfSize(.075,roundNum);
        }else if(roundNum >= 8000 && roundNum < 9000){
            adjustProbabilitiesWithFunnelOfSize(.05,roundNum);
        }else if(roundNum >= 9000 && roundNum < 10000){
            adjustProbabilitiesWithFunnelOfSize(.025,roundNum);
        }else if(roundNum >= 10000 && roundNum < 11000){
            adjustProbabilitiesWithFunnelOfSize(.01,roundNum);
        }else if(roundNum >= 11000 && roundNum < 12000){
            adjustProbabilitiesWithFunnelOfSize(.005,roundNum);
        }else if(roundNum >= 12000 && roundNum < 13000){
            adjustProbabilitiesWithFunnelOfSize(.001,roundNum);
        }else{
            adjustProbabilitiesWithFunnelOfSize(.0005,roundNum);
        }
    }

    public void adjustProbabilitiesBeforeFunnel(){
        game.getPlayerByID(2).randomizeProbabilityToBuyOnEachSide();
        game.getPlayerByID(3).randomizeProbabilityToBuyOnEachSide();
        game.getPlayerByID(4).randomizeProbabilityToBuyOnEachSide();
    }

    public void adjustProbabilitiesWithFunnelOfSize(double windowSize, int roundNum){
        double oneMin = game.getCutAverageArrayOnSpecificSide(1)[roundNum-1] - (windowSize/2);
        double oneMax = game.getCutAverageArrayOnSpecificSide(1)[roundNum-1] + (windowSize/2);
        double twoMin = game.getCutAverageArrayOnSpecificSide(2)[roundNum-1] - (windowSize/2);
        double twoMax = game.getCutAverageArrayOnSpecificSide(2)[roundNum-1] + (windowSize/2);
        double threeMin = game.getCutAverageArrayOnSpecificSide(3)[roundNum-1] - (windowSize/2);
        double threeMax = game.getCutAverageArrayOnSpecificSide(3)[roundNum-1] + (windowSize/2);
        double fourMin = game.getCutAverageArrayOnSpecificSide(4)[roundNum-1] - (windowSize/2);
        double fourMax = game.getCutAverageArrayOnSpecificSide(4)[roundNum-1] + (windowSize/2);

        game.getPlayerByID(2).randomizeProbabilityToBuyOnSpecificSideInRange(oneMin,oneMax,1);
        game.getPlayerByID(2).randomizeProbabilityToBuyOnSpecificSideInRange(twoMin,twoMax,2);
        game.getPlayerByID(2).randomizeProbabilityToBuyOnSpecificSideInRange(threeMin,threeMax,3);
        game.getPlayerByID(2).randomizeProbabilityToBuyOnSpecificSideInRange(fourMin,fourMax,4);
        game.getPlayerByID(3).randomizeProbabilityToBuyOnSpecificSideInRange(oneMin,oneMax,1);
        game.getPlayerByID(3).randomizeProbabilityToBuyOnSpecificSideInRange(twoMin,twoMax,2);
        game.getPlayerByID(3).randomizeProbabilityToBuyOnSpecificSideInRange(threeMin,threeMax,3);
        game.getPlayerByID(3).randomizeProbabilityToBuyOnSpecificSideInRange(fourMin,fourMax,4);
        game.getPlayerByID(4).randomizeProbabilityToBuyOnSpecificSideInRange(oneMin,oneMax,1);
        game.getPlayerByID(4).randomizeProbabilityToBuyOnSpecificSideInRange(twoMin,twoMax,2);
        game.getPlayerByID(4).randomizeProbabilityToBuyOnSpecificSideInRange(threeMin,threeMax,3);
        game.getPlayerByID(4).randomizeProbabilityToBuyOnSpecificSideInRange(fourMin,fourMax,4);
        if(roundNum>9000){
            System.out.println("oneMin: " + oneMin + " and oneMax: " + oneMax + " twoMin: " + twoMin + " twoMax: " + twoMax + " threeMin: " + threeMin + " threeMax: " + threeMax + " fourMin: " + fourMin + " fourMax: " + fourMax);
            System.out.println("Player 2 prob to buy on side 1: " + game.getPlayerByID(2).getProbabilityToBuyOnSideOne() + " side 2: " + game.getPlayerByID(2).getProbabilityToBuyOnSideTwo() + " side 3: " + game.getPlayerByID(2).getProbabilityToBuyOnSideThree() + " side 4: " + game.getPlayerByID(2).getProbabilityToBuyOnSideFour());
        }
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
        sb.append("Probability of Player One to buy on side one");
        sb.append("\n");
        for (double element : probabilityArray) {
            sb.append(element);
            sb.append("\n");
        }

        br.write(sb.toString());
        br.close();
    }

    public void plotProbabilityArrayOnSideOne(){
        JavaPlot plot = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        int arraySize = game.getProbabilityArrayOnSideOne().length;
        double[][] array = new double[arraySize][1];

        for(int count = 0; count < arraySize; count++){
            array[count][0] = game.getProbabilityArrayOnSideOne()[count];
        }

        plot.addPlot(array);
        plot.plot();
    }

    public double[] cutArrayOne(int numToCutOff){
        double[] array = new double[game.getProbabilityArrayOnSideOne().length];
        for(int i = 0; i < array.length; i++){
            if(i>numToCutOff){
                array[i] = game.getProbabilityArrayOnSideOne()[i];
            }
        }
        return array;
    }

    public double[] cutArrayTwo(int numToCutOff){
        double[] array = new double[game.getProbabilityArrayOnSideOne().length];
        for(int i = 0; i < array.length; i++){
            if(i>numToCutOff){
                array[i] = game.getProbabilityArrayOnSideTwo()[i];
            }
        }
        return array;
    }

    public double[] cutArrayThree(int numToCutOff){
        double[] array = new double[game.getProbabilityArrayOnSideOne().length];
        for(int i = 0; i < array.length; i++){
            if(i>numToCutOff){
                array[i] = game.getProbabilityArrayOnSideThree()[i];
            }
        }
        return array;
    }

    public double[] cutArrayFour(int numToCutOff){
        double[] array = new double[game.getProbabilityArrayOnSideOne().length];
        for(int i = 0; i < array.length; i++){
            if(i>numToCutOff){
                array[i] = game.getProbabilityArrayOnSideFour()[i];
            }
        }
        return array;
    }

    public double[] createAverageArraySideOneStartingAt(int startingIndex){
        double[] array = cutArrayOne(startingIndex);
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]+array[i-1];
        }
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]/(i-startingIndex);
        }
        return array;
    }

    public double[] createAverageArraySideTwoStartingAt(int startingIndex){
        double[] array = cutArrayTwo(startingIndex);
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]+array[i-1];
        }
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]/(i-startingIndex);
        }
        return array;
    }

    public double[] createAverageArraySideThreeStartingAt(int startingIndex){
        double[] array = cutArrayThree(startingIndex);
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]+array[i-1];
        }
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]/(i-startingIndex);
        }
        return array;
    }

    public double[] createAverageArraySideFourStartingAt(int startingIndex){
        double[] array = cutArrayFour(startingIndex);
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]+array[i-1];
        }
        for(int i = startingIndex+1; i < array.length; i++){
            array[i] = array[i]/(i-startingIndex);
        }
        return array;
    }

    public void plotAllProbabilityArrays(){
        JavaPlot plot1 = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        JavaPlot plot2 = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        JavaPlot plot3 = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        JavaPlot plot4 = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        int arraySize = game.getProbabilityArrayOnSideOne().length;
        double[][] array1 = new double[arraySize][1];
        double[][] array2 = new double[arraySize][1];
        double[][] array3 = new double[arraySize][1];
        double[][] array4 = new double[arraySize][1];
        double[][] array5 = new double[arraySize][1];
        double[][] array6 = new double[arraySize][1];
        double[][] array7 = new double[arraySize][1];
        double[][] array8 = new double[arraySize][1];

        for(int count = 0; count < arraySize; count++){
            array1[count][0] = game.getProbabilityArrayOnSideOne()[count];
            array5[count][0] = game.getCutAverageArrayOnSpecificSide(1)[count];
        }for(int count = 0; count < arraySize; count++){
            array2[count][0] = game.getProbabilityArrayOnSideTwo()[count];
            array6[count][0] = game.getCutAverageArrayOnSpecificSide(2)[count];
        }for(int count = 0; count < arraySize; count++){
            array3[count][0] = game.getProbabilityArrayOnSideThree()[count];
            array7[count][0] = game.getCutAverageArrayOnSpecificSide(3)[count];
        }for(int count = 0; count < arraySize; count++){
            array4[count][0] = game.getProbabilityArrayOnSideFour()[count];
            array8[count][0] = game.getCutAverageArrayOnSpecificSide(4)[count];
        }

        plot1.addPlot(array1);
        plot1.addPlot(array5);
        plot1.plot();
        plot2.addPlot(array2);
        plot2.addPlot(array6);
        plot2.plot();
        plot3.addPlot(array3);
        plot3.addPlot(array7);
        plot3.plot();
        plot4.addPlot(array4);
        plot4.addPlot(array8);
        plot4.plot();
    }

    public void plotProbabilityArray(double[] arrayGiven){
        JavaPlot plot = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        int arraySize = arrayGiven.length;
        double[][] array = new double[arraySize][1];

        for(int count = 0; count < arraySize; count++){
            array[count][0] = arrayGiven[count];
        }

        plot.addPlot(array);
        plot.plot();
    }

    public void plotProbabilityArrayInRange(int startPointByRoundNumber, int endPointByRoundNumber){
        JavaPlot plot = new JavaPlot("C:\\Program Files (x86)\\gnuplot\\bin\\gnuplot.exe");
        int arraySize = game.getProbabilityArrayOnSideOne().length - startPointByRoundNumber - (game.getProbabilityArrayOnSideOne().length - endPointByRoundNumber);
        //System.out.println("Starting point: " + startPointByRoundNumber + " Ending Point: " + endPointByRoundNumber + " and arraySize: " + arraySize);
        double[][] array = new double[arraySize][1];


        for(int count = 0; count < arraySize; count++){
            array[count][0] = game.getProbabilityArrayOnSideOne()[count + startPointByRoundNumber];
        }

        plot.addPlot(array);
        plot.plot();
    }
}
