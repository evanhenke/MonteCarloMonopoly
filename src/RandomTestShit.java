import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by UHENKEV on 11/9/2016.
 */
public class RandomTestShit {
    static Player playerOne = new Player(1,1,0.7);
    static Player playerTwo = new Player(2,2,0.7);
    static Player playerThree = new Player(3,3,0.7);
    static Player playerFour = new Player(4,4,0.7);
    static ArrayList<Player> PlayerArray = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println(Math.random() * .2 + .7);
        System.out.println(Math.random());
        System.out.println(Math.random());
        System.out.println(Math.random());


        int randomNum = 1 + (int)(Math.random() * (4));
        PlayerArray.add(playerOne);
        PlayerArray.add(playerTwo);
        PlayerArray.add(playerThree);
        PlayerArray.add(playerFour);
        Collections.shuffle(PlayerArray, new Random(randomNum));

        for(Player player : PlayerArray){
            System.out.print(player.getPlayerID());
        }
    }

}
