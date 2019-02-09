package game;

import java.awt.*;

public interface Entity {
    boolean update();
    boolean isDead();
    void draw(Graphics2D g);

    double getx();

    double gety();

    double getr();

    void hit();

    int getType();
}
