package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.utils.IterableArray;

public class AnimationBatch implements Animation, Pool.Poolable
{
    private static final Pool<AnimationBatch> animationBatchPool = new Pool<AnimationBatch>()
    {
        @Override protected AnimationBatch newObject()
        {
            return new AnimationBatch();
        }
    };

    public static AnimationBatch obtain(int capacity)
    {
        AnimationBatch batch = animationBatchPool.obtain();
        if (batch.animations == null) {
            batch.animations = new IterableArray<Animation>(capacity);
        } else {
            batch.animations.ensureCapacity(capacity);
        }

        return batch;
    }

    private IterableArray<Animation> animations;

    private AnimationBatch()
    {
    }

    @Override public void reset()
    {
        for (Animation a : animations) {
            a.dispose();
        }
        animations.clear();
    }

    @Override public void dispose()
    {
        animationBatchPool.free(this);
    }

    public void add(Animation animation)
    {
        animations.add(animation);
    }

    @Override public boolean completed()
    {
        return animations.isEmpty();
    }

    @Override public boolean animate(float delta)
    {
        if (!completed()) {
            for (Animation animation : animations) {
                if (animation.animate(delta)) {
                    animations.remove(animation);
                    animation.dispose();
                }
            }
        }

        return completed();
    }

    @Override public void draw(Batch batch)
    {
        if (!completed()) {
            for (Animation animation : animations) {
                animation.draw(batch);
            }
        }
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        if (!completed()) {
            for (Animation animation : animations) {
                animation.drawDebug(shapeRenderer);
            }
        }
    }
}
