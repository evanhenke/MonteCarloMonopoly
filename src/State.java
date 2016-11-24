public class State {
    private GameHelper gh = new GameHelper();
    private MonteCarloMonopoly game = new MonteCarloMonopoly();

    private int[] isStateOwnableArray = new int[40];
    private int[] isStateOwnedArray = new int[40];


    private double[][] transitionMatrix = gh.generateTransitionMatrix();

    public State(){
        for(int i = 0; i < 40; i++){
            isStateOwnedArray[i] = 0;
        }
        for(int index = 0; index < 40; index++){
            if(index == 1 ||index == 3 ||index == 5 ||index == 7 ||index == 8 ||index == 11 ||index == 12 ||index == 14 ||index == 16 ||index == 18 ||index == 19 ||index == 21 ||index == 22 ||index == 24 ||index == 26 ||index == 27 ||index == 28 ||index == 31 ||index == 32 ||index == 34 ||index == 37 ||index == 39){
                isStateOwnableArray[index] = 1;
            }
        }
    }

    public void resetOwnedAndOwnableArrays(){
        for(int i = 0; i < 40; i++){
            isStateOwnedArray[i] = 0;
        }
        for(int index = 0; index < 40; index++){
            if(index == 1 ||index == 3 ||index == 5 ||index == 7 ||index == 8 ||index == 11 ||index == 12 ||index == 14 ||index == 16 ||index == 18 ||index == 19 ||index == 21 ||index == 22 ||index == 24 ||index == 26 ||index == 27 ||index == 28 ||index == 31 ||index == 32 ||index == 34 ||index == 37 ||index == 39){
                isStateOwnableArray[index] = 1;
            }
        }
    }
    public int[] getIsStateOwnableArray(){ return isStateOwnableArray; }
    public int getIsStateOwnableIndexAt(int index){ return isStateOwnableArray[index]; }
    public int[] getIsStateOwnedArray(){ return isStateOwnedArray; }
    public int getIsStateOwnedIndexAt(int index){ return isStateOwnedArray[index]; }

    public void setStateToOwnedInArrays(int index,int playerNum){
        isStateOwnedArray[index] = playerNum;
        isStateOwnableArray[index] = 0;
    }

    public int getAmountOfPropertyAtIndex(int index){
        if (index >= 1 && index <= 9){
            return 100;
        }if (index >= 11 && index <= 19){
            return 200;
        }if (index >= 21 && index <= 29){
            return 300;
        }if (index >= 31 && index <= 39){
            return 400;
        }
        return 0;
    }

    public boolean stateIsOwnableAtIndex(int stateIndex){
        if (isStateOwnableArray[stateIndex] == 1){
            //System.out.println("State " + stateIndex + " is ownable(==" + isStateOwnableArray[stateIndex] + ") and will return true from stateIsOwnableAtIndex method");
            return true;
        }
        //System.out.println("State " + stateIndex + " is not ownable(==" + isStateOwnableArray[stateIndex] + ") and will return false from stateIsOwnableAtIndex method");
        return false;
    }

    public boolean stateIsOwnedAtIndex(int stateIndex){
        if(isStateOwnedArray[stateIndex]!=0){
            //System.out.println("State is owned by: " + isStateOwnedArray[stateIndex] + " from stateIsOwnedAtIndex method");
            return true;
        }
        //System.out.println("State is not owned");
        return false;
    }

    public void updateOwnedAndOwnableDueToPlayerLoss(int playerID){
        for(int index = 0; index < 40; index++){
            if(isStateOwnedArray[index] == playerID){
                isStateOwnedArray[index] = 0;
                isStateOwnableArray[index] = 1;
            }
        }
    }

    public void displayOwnedAndOwnableArrays(){
        System.out.print("OwnedArray:   [");
        for (int i = 0; i < 40; i++) {
            System.out.print(isStateOwnedArray[i] + " ");
        }
        System.out.print("]");
        System.out.println();
        System.out.print("OwnableArray: [");
        for (int i = 0; i < 40; i++) {
            System.out.print(isStateOwnableArray[i] + " ");
        }
        System.out.print("]");
        System.out.println();
    }

    public double[][] getTransitionMatrix(){ return transitionMatrix; }
}
