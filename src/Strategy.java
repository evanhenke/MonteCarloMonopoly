import java.util.ArrayList;

/**
 * Created by UHENKEV on 10/26/2016.
 */
public class Strategy {
    private double random,compare;
    int currentState = 0;
    private MonteCarloMonopoly game = new MonteCarloMonopoly();
    private State state = new State();


    public Strategy(){
    }

    public boolean shouldPlayerBuy(Player player) {
        random = Math.random();
        currentState = game.getCurrentPlayer().getState();

        if (player.getCurrentSideOfBoard() == 1){
            compare = player.getProbabilityToBuyOnSideOne();
        } else if (player.getCurrentSideOfBoard() == 2){
            compare = player.getProbabilityToBuyOnSideTwo();
        } else if (player.getCurrentSideOfBoard() == 3){
            compare = player.getProbabilityToBuyOnSideThree();
        } else if (player.getCurrentSideOfBoard() == 4){
            compare = player.getProbabilityToBuyOnSideFour();
        } else {
            System.out.println("shouldPlayerBuy is giving error, currentState is not a valid state, it is: " + currentState);
            System.exit(0);
        }

        if (random <= compare) {
            return true;
        }
        return false;
    }
}
