package game;

import javax.swing.*;

/**
 * Created by Sergiy Isakov on 13.04.2017.
 * from awesome tutorials on youtube: https://www.youtube.com/user/ForeignGuyMike
 */
public class Game {
    public static void main(String[] args){
        JFrame window = new JFrame("Simple Shooter");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        window.setContentPane(new GamePanel());

        window.pack();
        window.setVisible(true);
    }
}
