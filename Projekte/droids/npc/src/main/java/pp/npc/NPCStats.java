package pp.npc;

import pp.util.Position;

public class NPCStats {
    private int lives;
    private int coins;
    private boolean hasFlag;
    private float x;
    private float y;

    public NPCStats(int lives, int coins, boolean hasFlag) {
        this.lives = lives;
        this.coins = coins;
        this.hasFlag = hasFlag;
        this.x = 0;
        this.y = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoin() {
        this.coins += 1;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }
}
