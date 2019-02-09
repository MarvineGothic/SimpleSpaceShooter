package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sergiy Isakov on 13.04.2017.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    //FIELDS
    public static int WIDTH = 600;
    public static int HEIGHT = 500;
    public static Player player;
    public static ArrayList<Entity> bullets;
    public static ArrayList<Entity> enemies;
    public static ArrayList<Entity> powerUps;
    public static ArrayList<Entity> explosions;
    public static ArrayList<Entity> texts;
    static boolean slow1;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS = 30;
    private double averageFPS;
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;
    private long slowDownTimer;
    private long getSlowDownTimerDiff;
    private int slowDownLength = 6000;

    //CONSTRUCTOR
    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        JukeBox.init();
        JukeBox.load("/SFX/menuoption.wav", "shot");
        JukeBox.load("/SFX/collect.wav", "collect");
        JukeBox.load("/SFX/splash.wav", "boom");
        JukeBox.load("/SFX/tilechange.wav", "impact");
    }

    //FUNCTIONS
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);

    }

    @Override
    public void run() {

        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        player = new Player();
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        explosions = new ArrayList<>();
        texts = new ArrayList<>();

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;

        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000 / FPS;

        // GAME lOOP
        while (running) {

            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;

            waitTime = targetTime - URDTimeMillis;

            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {

            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 16));
        String s = "G A M E  O V E R";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);
        s = "H I  S C O R E: " + player.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2 + 30);
        gameDraw();
    }

    private void entitiesUpdate(ArrayList<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            boolean remove = entities.get(i).update();
            if (remove) {
                entities.remove(i);
                i--;
            }
        }
    }

    private void collision(Object object, Entity e1, List<Entity> entities, Method toDo, Integer i) {
        double bx = e1.getx();
        double by = e1.gety();
        double br = e1.getr();

        for (int j = 0; j < entities.size(); j++) {
            Entity e = entities.get(j);
            double ex = e.getx();
            double ey = e.gety();
            double er = e.getr();

            double dx = bx - ex;
            double dy = by - ey;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < br + er) {
                // some method
                try {
                    if (toDo.getName().equals("bulletEnemyToDo")) {
                        toDo.invoke(object, e, i);
                        break;
                    }
                    if (toDo.getName().equals("playerEnemyToDo"))
                        toDo.invoke(object);
                    if (toDo.getName().equals("playerPowerUpToDo")) {
                        toDo.invoke(object, e, j);
                        powerUps.remove(j);
                        JukeBox.play("collect");
                        j--;
                    }


                } catch (IllegalAccessException | InvocationTargetException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public void bulletEnemyToDo(Entity e, Integer i) {
        e.hit();
        bullets.remove(i);
        i--;
    }

    public void playerEnemyToDo() {
        player.loseLife();
        player.losePower();
        JukeBox.play("impact");
    }

    public void playerPowerUpToDo(Entity e, Integer i) {
        int type = e.getType();
        if (type == 1) {
            player.gainLife();
            texts.add(new Text(player.getx(), player.gety(), 2000, "Life UP"));
        }
        if (type == 2) {
            player.increasePower(1);
            texts.add(new Text(player.getx(), player.gety(), 2000, "Power UP"));
        }
        if (type == 3) {
            player.increasePower(2);
            texts.add(new Text(player.getx(), player.gety(), 2000, "Double Power"));
        }
        if (type == 4) {
            slowDownTimer = System.nanoTime();
            slow1 = true;       // instead of a loop I use a static boolean slow1 that is imported to game.Enemy
                   /* for (int j = 0; j < enemies.size(); ) {
                        enemies.get(j).setSlow(true);j++;
                    }*/
            texts.add(new Text(player.getx(), player.gety(), 2000, "Slow Down"));
        }
    }

    private void gameUpdate() {

        Method bE = null;
        Method pE = null;
        Method pP = null;
        try {
            bE = GamePanel.class.getMethod("bulletEnemyToDo", Entity.class, Integer.class);
            pE = GamePanel.class.getMethod("playerEnemyToDo");
            pP = GamePanel.class.getMethod("playerPowerUpToDo", Entity.class, Integer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //new wave
        if (waveStartTimer == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        } else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if (waveStartTimerDiff > waveDelay) {
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }

        //create enemies
        if (waveStart && enemies.size() == 0) {
            createNewEnemies();
        }

        //player update
        player.update("shot");
        //enemy update
        for (Entity enemy : enemies)
            enemy.update();


        //bullet update
        entitiesUpdate(bullets);
        //powerup update
        entitiesUpdate(powerUps);
        //text update
        entitiesUpdate(texts);
        //explosion update
        entitiesUpdate(explosions);

        // bullet-enemy collision
        for (int i = 0; i < bullets.size(); i++)
            collision(this, bullets.get(i), enemies, bE, i);


        //check dead enemies
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                Enemy e = (Enemy) enemies.get(i);

                // chance for powerup
                double rand = Math.random();
                if (rand < 0.050) powerUps.add(new PowerUp(1, e.getx(), e.gety()));
                else if (rand < 0.100) powerUps.add(new PowerUp(3, e.getx(), e.gety()));
                else if (rand < 0.180) powerUps.add(new PowerUp(2, e.getx(), e.gety()));
                else if (rand < 0.130) powerUps.add(new PowerUp(4, e.getx(), e.gety()));

                player.addScore(e.getType() + e.getRank());
                enemies.remove(i);
                JukeBox.play("boom");
                i--;

                e.explode();
                explosions.add(new Explosion(e.getx(), e.gety(), e.getr(), (int) (e.getr() + 15)));
            }
        }

        // check dead player
        if (player.isDead()) {
            running = false;
        }

        //player-enemy collision
        if (!player.isRecovering())
            collision(this, player, enemies, pE, 0);


        //player-powerup collision
        collision(this, player, powerUps, pP, 0);


        //slowdown update
        if (slowDownTimer != 0) {
            getSlowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
            if (getSlowDownTimerDiff > slowDownLength) {
                slowDownTimer = 0;
                slow1 = false;         // instead of a loop I use a static boolean slow1 that is imported to game.Enemy
              /*  for (int j = 0; j < enemies.size(); j++) {
                    enemies.get(j).setSlow(false);
                }*/
            }
        }
    }

    private void gameRender() {

        //draw background
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        //draw slowdown screen
        if (slowDownTimer != 0) {
            g.setColor(new Color(255, 255, 255, 64));
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }


        //draw player
        player.draw(g);

        //draw bullet
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }
        //draw enemy
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
        //draw powerups
        for (int i = 0; i < powerUps.size(); i++) {
            powerUps.get(i).draw(g);
        }
        //draw explosions
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }
        //draw text
        for (int i = 0; i < texts.size(); i++) {
            texts.get(i).draw(g);
        }

        //draw wave number
        if (waveStartTimer != 0) {
            g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
            String s = "- W A V E  " + waveNumber + "  -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if (alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
        }
        //draw wave statistic
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Wave: " + waveNumber, WIDTH - 200, 30);

        //draw player lives
        for (int i = 0; i < player.getLives(); i++) {
            g.setColor(Color.white);
            g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
            g.drawString("LIFE:", 20, 30);
            g.fillOval(80 + (20 * i), 20, (int) player.getr() * 2, (int) player.getr() * 2);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.white.darker());
            g.drawOval(80 + (20 * i), 20, (int) player.getr() * 2, (int) player.getr() * 2);
            g.setStroke(new BasicStroke(1));
        }
        //draw player power
        g.setColor(Color.white);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("POWER:", 20, 50);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Level: " + player.getPowerLevel(), 20, 80);
        //g.drawString("power: " + player.getPower(), 20, 100);
        g.setColor(Color.YELLOW);
        g.drawRect(80, 40, 8, 8);
        g.fillRect(80, 40, player.getPower() * 8, 8);
        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(2));
        //if (player.getPower() < player.getRequiredPower()) {

          /*  for (int i = 0; i < player.getPowerLevel(); i++) {
                g.drawRect(80 + 8 * i, 40, 8, 8);
            }*/
        if (player.getPowerLevel() >= 1) g.drawRect(80 + 8, 40, 8, 8);
        if (player.getPowerLevel() >= 2) g.drawRect(80 + 8 * 2, 40, 8, 8);
        if (player.getPowerLevel() >= 3) g.drawRect(80 + 8 * 3, 40, 8, 8);
        if (player.getPowerLevel() >= 4) g.drawRect(80 + 8 * 4, 40, 8, 8);
        if (player.getPowerLevel() == 5) g.drawRect(80 + 8 * 4, 40, 8, 8);


        // }
        g.setStroke(new BasicStroke(1));

        //draw player score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);

        //draw slowdown meter
        if (slowDownTimer != 0) {
            g.setColor(Color.WHITE);
            g.drawRect(20, 60, 100, 8);
            g.fillRect(20, 60, (int) (100 - 100.0 * getSlowDownTimerDiff / slowDownLength), 8);
        }
    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    private void createNewEnemies() {
        enemies.clear();
        Enemy e;

       /* for (int j = 1;j>0;j++) {

            if (waveNumber == j) {
                for (int i = 0; i < 4*j; i++) {
                    enemies.add(new Enemy(1, 1));
                }

            }
        }*/

        if (waveNumber == 1) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }
        if (waveNumber == 2) {
            for (int i = 0; i < 8; i++) {
                enemies.add(new Enemy(1, 1));
            }
            enemies.add(new Enemy(1, 2));
            enemies.add(new Enemy(1, 2));
        }
        if (waveNumber == 3) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
            enemies.add(new Enemy(1, 2));
            enemies.add(new Enemy(1, 2));
            // enemies.add(new Enemy(2,1));
        }
        if (waveNumber == 4) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(2, 1));
            }
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 4));
        }
        if (waveNumber == 5) {
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
        }
        if (waveNumber == 6) {
            enemies.add(new Enemy(1, 3));
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(2, 1));
                enemies.add(new Enemy(3, 1));
            }
        }
        if (waveNumber == 7) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
            enemies.add(new Enemy(3, 3));
        }
        if (waveNumber == 8) {
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(2, 4));
            enemies.add(new Enemy(3, 4));
        }
        if (waveNumber == 9) {
            running = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent key) {

    }

    @Override
    public void keyPressed(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(true);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(true);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(true);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(true);
        }
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(true);
        }

    }

    @Override
    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(false);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(false);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(false);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(false);
        }
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(false);
        }
    }
}
