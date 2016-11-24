import java.util.ArrayList;

/**
 * Created by UHENKEV on 10/26/2016.
 */
public class Strategy {
    private double random,compare;
    int currentPlayerState = 0;
    private MonteCarloMonopoly game = new MonteCarloMonopoly();
    private State state = new State();


    public Strategy(Player playerOne, Player playerTwo, Player playerThree, Player playerFour){
    }

    public boolean shouldPlayerBuy(Player player) {
        random = Math.random();
        compare = player.getProbabilityToBuy();
        if (random <= compare) {
            return true;
        }
        return false;
    }
}
