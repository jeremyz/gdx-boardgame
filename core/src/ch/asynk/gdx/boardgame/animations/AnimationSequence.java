package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;

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

    public static AnimationSequence obtain(int capacity)
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
            Animation animation = animations.get(0);
            if (animation.animate(delta)) {
                animations.remove(0);
                animation.dispose();
            }
        }

        return completed();
    }

    @Override public void draw(Batch batch)
    {
        if (!completed()) {
            animations.get(0).draw(batch);
        }
    }
}
