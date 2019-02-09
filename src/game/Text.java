package game;

import java.awt.*;

/**
 * Created by Sergiy Isakov on 15.04.2017.
 */
public class Text implements Entity{

    //FIELDS
    private double x;
    private double y;
    private long time;
    private String s;

    private long start;

    //CONSTRUCTOR
    public Text(double x, double y, long time, String s) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.s = s;
        start = System.nanoTime();
    }

    public boolean update() {
        long elapsed = (System.nanoTime() - start) / 1000000;
        if (elapsed > time) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDead() {
        return false;
    }

    public void draw(Graphics2D g) {
        g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        long elapsed = (System.nanoTime() - start) / 1000000;

        int alpha = (int) (255 * Math.sin(3.14 * elapsed / time));
        if (alpha > 255) alpha = 255;
        g.setColor(new Color(255, 255, 255, alpha));
        int length = (int)g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (int) (x - (length/2)), (int) y);
    }

    @Override
    public double getx() {
        return 0;
    }

    @Override
    public double gety() {
        return 0;
    }

    @Override
    public double getr() {
        return 0;
    }

    @Override
    public void hit() {

    }

    @Override
    public int getType() {
        return 0;
    }
}
