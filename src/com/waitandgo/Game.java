package com.waitandgo;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import javax.swing.JFrame;

public class Game extends Canvas {
    public static final int WIDTH = 120;
    public static final int HEIGHT = WIDTH / 16 * 9;
    public static final int SCALE = 3;

    private int xOffset = 0;
    private int yOffset = 0;
    private Thread thread;
    private boolean running = false;
    private JFrame frame = new JFrame();
    private BufferStrategy bufferStrategy = null;
    private BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
    private GameRender renderer = new GameRender(WIDTH, HEIGHT, SCALE, pixels);

    public Game() {
        Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        setPreferredSize(size);
    }

    public synchronized void start() {
        running = true;
        init();
        thread = new Thread(() -> {
            double frameTime = 1000 * 1000 * 1000 / 60.;
            double delta = 0.;
            long previous = System.nanoTime();
            long time = System.currentTimeMillis();
            int updates = 0;
            int frames = 0;
            while (running) {
                long now = System.nanoTime();
                delta += (now - previous);
                previous = now;
                if (delta >= frameTime) {
                    update();
                    ++updates;
                    delta = 0.;
                }
                render();
                ++frames;
                if (System.currentTimeMillis() - time > 1000) {
                    time += 1000;
                    frame.setTitle("Java Game " + "updates(" + updates + ") frames(" + frames + ")");
                    updates = 0;
                    frames = 0;
                    //renderer.fill();
                }
            }
        });
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void render() {
        if (bufferStrategy == null) {
            createBufferStrategy(3);
            bufferStrategy = getBufferStrategy();
        }
        renderer.clear();
        renderer.render(xOffset, yOffset);
        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), null);
        graphics.dispose();
        bufferStrategy.show();
    }

    private void update() {
        if (GameKeyListener.isKeyPressed(KeyEvent.VK_UP)) {
            --yOffset;
        }
        if (GameKeyListener.isKeyPressed(KeyEvent.VK_DOWN)) {
            ++yOffset;
        }
        if (GameKeyListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            ++xOffset;
        }
        if (GameKeyListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            --xOffset;
        }
    }

    private void init() {
        frame.addKeyListener(new GameKeyListener());
        frame.setResizable(false);
        frame.setTitle("Java Game");
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Game().start();
    }
}
