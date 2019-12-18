package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import ch.asynk.gdx.boardgame.utils.IterableArray;

public class Scissors
{
    private static IterableArray<Rectangle> array = new IterableArray<Rectangle>(5);

    private static Camera camera;
    private static Batch batch;

    public static void clear()
    {
        array.clear();
    }

    public static void invalidate()
    {
        for (int i = 0; i < array.size(); i++)
            array.replace(null, i);
    }

    public static void register(Camera _camera, Batch _batch)
    {
        camera = _camera;
        batch = _batch;
        invalidate();
    }

    public static int compute(int idx, Rectangle area)
    {
        if (idx < 0) {
            array.add(compute(area));
            return array.size() - 1;
        } else {
            array.replace(compute(area), idx);
            return idx;
        }
    }

    public static Rectangle get(int idx, Rectangle area)
    {
        Rectangle r = array.get(idx);
        if (r == null) {
            array.replace(compute(area), idx);
            r = array.get(idx);
        }
        return r;
    }

    public static Rectangle compute(Rectangle area)
    {
        Rectangle scissor = new Rectangle();
        compute(area, scissor);
        return scissor;
    }

    public static void compute(Rectangle area, Rectangle scissor)
    {
        ScissorStack.calculateScissors(camera, batch.getTransformMatrix(), area, scissor);
    }
}
