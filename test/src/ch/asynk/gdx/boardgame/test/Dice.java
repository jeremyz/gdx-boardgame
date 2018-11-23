package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.Texture;

import ch.asynk.gdx.boardgame.FramedSprite;
import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.animations.Animation;

public class Dice extends FramedSprite implements Drawable, Animation
{
    private static int[] sides = new int[] { 64, 68, 128, 0, 76, 72 };
    private static int[][] rolls = new int[][]{
        {  25,  40,  55,  70,  85, 100, 115,  99,  83,  67,  51,  36,  37,  52,  67,  66,  65,  64 },
        {  58,  74,  59,  60,  45,  62,  78,  94, 109, 108, 123, 106,  89,  71,  70,  69,  68 },
        { 106, 121, 120, 103,  86,  70,  54,  37,  20,  19,  18,  34,  50,  51,  52,  69,  86, 103, 119, 128 },
        {  95,  79,  93,  92,  91,  90, 104, 103, 102,  85,  84,  67,  66,  65,  49,  32,  16,   0 },
        {  22,  39,  56,  73,  90, 107, 124, 128, 113,  98,  83,  68,  53,  38,  23,   0,  25,  42,  59,  76 },
        {  79,  78,  61,  76,  91, 106, 121, 120, 119, 102, 101,  84,  68,  52,  37,  38,  39,  40,  41,  58,  75,  74,  73,  72 },
    };
    private int[] roll;
    private int x;
    private int last;
    private float dt;
    private float elapsed;

    public Dice(Texture texture, int rows, int cols, float dt)
    {
        super(texture, rows, cols);
        this.roll = null;
        this.dt = dt;
    }

    public void setSide(int i)
    {
        setFrame(sides[i - 1]);
    }

    public void setFrame(int frame)
    {
        int row = (frame / cols);
        setFrame(row, frame - (row * cols));
    }

    public void rollTo(int n)
    {
        roll = rolls[n - 1];
        x = 0;
        last = roll.length - 1;
        elapsed = 0f;
        setFrame(roll[x]);
    }

    @Override public void dispose() { }

    @Override public boolean completed()
    {
        return (roll == null);
    }

    @Override public boolean animate(float delta)
    {
        if (roll != null) {
            elapsed += delta;
            if (elapsed >= dt) {
                elapsed -= dt;
                if (x == last) {
                    roll = null;
                } else {
                    x += 1;
                    setFrame(roll[x]);
                }
            }
        }
        return completed();
    }
}
