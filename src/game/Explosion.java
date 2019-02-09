package game;

import java.awt.*;

/**
 * Created by Sergiy Isakov on 15.04.2017.
 */
public class Explosion implements Entity {
    //FIELDS
    private double x;
    private double y;
    private double r;
    private int maxRadius;

    //CONSTRUCTOR
    public Explosion(double x, double y, double r, int max) {
        this.x = x;
        this.y = y;
        this.r = r;
        maxRadius = max;
    }

    public boolean update() {
        r += 2;
        if (r >= maxRadius) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isDead() {
        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 128));
        g.setStroke(new BasicStroke(3));
        g.drawOval((int) (x - r), (int) (y - r), 2 * (int)r, 2 * (int) r);
        g.setStroke(new BasicStroke(1));

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
