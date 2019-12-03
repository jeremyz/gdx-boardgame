package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

import ch.asynk.gdx.boardgame.FramedSprite;
import ch.asynk.gdx.boardgame.Piece;

public class FireAnimation implements Animation, Pool.Poolable
{
    public static class Config
    {
        public int burstCount;
        public float burstDelay;
        public float maxFireDelay;
        public float maxFireScattering;
        public float shellSpeed;
        public float smokeDuration;
        public float explosionDuration;
        public FramedSprite shellSprites;
        public FramedSprite explosionSprites;
        public Sound shellFireSnd;
        public Sound explosionSnd;
        public Config(int burstCount, float burstDelay,
                float maxFireDelay, float maxFireScattering,
                float shellSpeed, float smokeDuration, float explosionDuration,
                FramedSprite shellSprites, FramedSprite explosionSprites,
                Sound shellFireSnd, Sound explosionSnd)
        {
            this.burstCount = burstCount;
            this.burstDelay = burstDelay;
            this.maxFireDelay = maxFireDelay;
            this.maxFireScattering = maxFireScattering;
            this.shellSpeed = shellSpeed;
            this.smokeDuration = smokeDuration;
            this.explosionDuration = explosionDuration;
            this.shellSprites = shellSprites;
            this.explosionSprites = explosionSprites;
            this.shellFireSnd = shellFireSnd;
            this.explosionSnd = explosionSnd;
        }
    }

    private static ObjectMap<String, Config> configs = new ObjectMap<String, Config>();

    public static void register(final String name,
            int burstCount,
            float burstDelay,
            float maxFireDelay,
            float maxFireScattering,
            float shellSpeed,
            float smokeDuration,
            float explosionDuration,
            final Texture shellTexture, int shellR, int shellC,
            final Texture explosionTexture, int explosionR, int explosionC,
            final Sound shellFireSnd,
            final Sound explosionSnd
            )
    {
        Config cfg = new Config(burstCount, burstDelay,
                maxFireDelay, maxFireScattering,
                shellSpeed, smokeDuration, (explosionTexture == null ? 0f : explosionDuration),
                (shellTexture == null ? null : new FramedSprite(shellTexture, shellR, shellC)),
                (explosionTexture == null ? null : new FramedSprite(explosionTexture, explosionR, explosionC)),
                shellFireSnd, explosionSnd
                );
        configs.put(name, cfg);
    }

    public static void free()
    {
        // for(String key : configs.keySet()) {
        //     Config cfg = configs.get(key);
        // }
        configs.clear();
    }

    private static final Pool<FireAnimation> shellFireAnimationPool = new Pool<FireAnimation>()
    {
        @Override protected FireAnimation newObject()
        {
            return new FireAnimation();
        }
    };

    public static Vector2 sv = new Vector2();
    public static Vector2 tv = new Vector2();
    public static FireAnimation obtain(final String configName, Piece shooter, Piece target)
    {
        shooter.getFireingPoint(sv, target);
        target.getImpactPoint(tv);
        return obtain(configName, sv.x, sv.y, tv.x, tv.y);
    }

    public static FireAnimation obtain(final String configName, float x0, float y0, float x1, float y1)
    {
        FireAnimation a = shellFireAnimationPool.obtain();

        Config cfg = configs.get(configName);
        if (cfg == null) {
            throw new RuntimeException("FireAnimation : no configuration named : '" + configName + "'");
        }
        a.compute(cfg, x0, y0, x1, y1);

        return a;
    }

    @Override public void reset()
    {
        this.shot = null;
        this.shots = null;
    }

    @Override public void dispose()
    {
        if (this.shot != null) {
            this.shot.dispose();
        }
        if (this.shots != null) {
            for (ShotAnimation shot : shots) {
                shot.dispose();
            }
        }
        shellFireAnimationPool.free(this);
    }

    private boolean single;
    private ShotAnimation shot;
    private ShotAnimation[] shots;

    private FireAnimation()
    {
    }

    private void compute(Config cfg, float x0, float y0, float x1, float y1)
    {
        this.single = (cfg.burstCount == 1);
        if (single) {
            this.shot = ShotAnimation.obtain();
            this.shot.compute(0, cfg, x0, y0, x1, y1);
        } else {
            this.shots = new ShotAnimation[cfg.burstCount];
            for (int i = 0; i < cfg.burstCount; i++) {
                this.shots[i] = ShotAnimation.obtain();
                this.shots[i].compute(i, cfg, x0, y0, x1, y1);
            }
        }
    }

    @Override public boolean completed()
    {
        if (single) {
            return this.shot.completed();
        } else {
            boolean completed = true;
            for (ShotAnimation shot : this.shots) {
                completed &= shot.completed();
            }
            return completed;
        }
    }

    @Override public boolean animate(float delta)
    {
        if (single) {
            return this.shot.animate(delta);
        } else {
            boolean completed = true;
            for (ShotAnimation shot : this.shots) {
                completed &= shot.animate(delta);
            }
            return completed;
        }
    }

    @Override public void draw(Batch batch)
    {
        if (single) {
            this.shot.draw(batch);
        } else {
            for (ShotAnimation shot : this.shots) {
                shot.draw(batch);
            }
        }
    }
}
