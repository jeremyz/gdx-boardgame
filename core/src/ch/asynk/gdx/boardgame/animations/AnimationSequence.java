package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.utils.IterableArray;

public class AnimationSequence implements Animation, Pool.Poolable
{
    private static final Pool<AnimationSequence> animationSequencePool = new Pool<AnimationSequence>()
    {
        @Override protected AnimationSequence newObject()
        {
            return new AnimationSequence();
        }
    };

    public static AnimationSequence get(int capacity)
    {
        AnimationSequence seq = animationSequencePool.obtain();
        if (seq.animations == null) {
            seq.animations = new IterableArray<Animation>(capacity);
        } else {
            seq.animations.ensureCapacity(capacity);
        }

        return seq;
    }

    private IterableArray<Animation> animations;

    private AnimationSequence()
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
        animationSequencePool.free(this);
    }

    public void addAnimation(Animation animation)
    {
        animations.add(animation);
    }

    @Override public boolean completed()
    {
        return (animations.size() == 0);
    }

    @Override public boolean animate(float delta)
    {
        if (animations.isEmpty()) return true;

        Animation animation = animations.get(0);
        if (animation.animate(delta)) {
            animations.remove(0);
            animation.dispose();
        }

        return (animations.isEmpty());
    }

    @Override public void draw(Batch batch)
    {
        animations.get(0).draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
        animations.get(0).drawDebug(debugShapes);
    }
}
