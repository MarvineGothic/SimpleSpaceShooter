package game;

import java.awt.*;

/**
 * Created by Sergiy Isakov on 13.04.2017.
 */
public class Bullet implements Entity{

    //FIELDS
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private Color color1;

    //CONSTRUCTOR

    public Bullet(double angle, int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        r = 2;

        rad = Math.toRadians(angle);

        dx = Math.cos(rad)*speed;
        dy = Math.sin(rad)*speed;


        color1 = Color.YELLOW;
    }


    //FUNCTIONS

    public double getx(){return x;}
    public double gety(){return y;}
    public double getr(){return r;}

    @Override
    public void hit() {

    }

    @Override
    public int getType() {
        return 0;
    }


    public boolean update() {
        x += dx;
        y += dy;
        if (x < -r || x > GamePanel.WIDTH + r ||
                y < -r || y > GamePanel.HEIGHT + r) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDead() {
        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(color1);
        g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

}
