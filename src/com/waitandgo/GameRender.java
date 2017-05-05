package com.waitandgo;

import java.util.Random;

public class GameRender {
    private int width = 0, height = 0, scale = 0;
    private int[] pixels = null;
    private static final Random random = new Random();
    private static final int MAP_SIZE = 4;
    private static final int MAP_SIZE_MASK = MAP_SIZE - 1;
    private int[] tiles = new int [MAP_SIZE * MAP_SIZE];

    public GameRender(int width, int height, int scale, int[] pixels) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.pixels = pixels;
        fill();
    }

    public void fill() {
        for (int i = 0; i < MAP_SIZE * MAP_SIZE; ++i) {
            tiles[i] = random.nextInt(0xffffff);
            tiles[0] = 0x000000;
        }

    }

    public void clear() {
        for (int i = 0; i < pixels.length; ++i) {
            pixels[i] = 0x000000;
        }
    }

    public void render(int xOffset, int yOffset) {
        for (int y = 0; y < height; ++y) {
            int yShifted = y + yOffset;
            for (int x = 0; x < width; ++x) {
                int xShifted = x + xOffset;
                int tileIndex = ((xShifted >> scale) & MAP_SIZE_MASK) + ((yShifted >> scale) & MAP_SIZE_MASK) * MAP_SIZE;
                pixels[x + (y * width)] = tiles[tileIndex];
            }
        }
    }

}
