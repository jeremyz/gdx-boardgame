package ch.asynk.gdx.boardgame.animations;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

import ch.asynk.gdx.boardgame.FramedSprite;
import ch.asynk.gdx.boardgame.Piece;

public class ShellFireAnimation extends TimedAnimation implements Pool.Poolable
{
    private static class Config
    {
        public float maxFireDelay;
        public float maxFireScattering;
        public float shellSpeed;
        public float smokeDuration;
        public float explosionDuration;
        public FramedSprite shellSprites;
        public FramedSprite explosionSprites;
        public Sound shellFireSnd;
        public Sound explosionSnd;
        public Config(float maxFireDelay, float maxFireScattering, float shellSpeed,
                float smokeDuration, float explosionDuration,
                FramedSprite shellSprites, FramedSprite explosionSprites,
                Sound shellFireSnd, Sound explosionSnd)
        {
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

    private static Map<String, Config> configs = new Hashtable<String, Config>();

    public static void register(final String name,
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
        Config cfg = new Config(maxFireDelay, maxFireScattering, shellSpeed, smokeDuration, explosionDuration,
                new FramedSprite(shellTexture, shellR, shellC),
                new FramedSprite(explosionTexture, explosionR, explosionC),
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

    private static Random random = new Random();

    private static final Pool<ShellFireAnimation> shellFireAnimationPool = new Pool<ShellFireAnimation>()
    {
        @Override protected ShellFireAnimation newObject()
        {
            return new ShellFireAnimation();
        }
    };

    public static Vector2 sv = new Vector2();
    public static Vector2 tv = new Vector2();
    public static ShellFireAnimation obtain(final String configName, Piece shooter, Piece target)
    {
        shooter.getFireingPoint(sv, target);
        target.getImpactPoint(tv);
        return obtain(configName, sv.x, sv.y, tv.x, tv.y);
    }

    public static ShellFireAnimation obtain(final String configName, float x0, float y0, float x1, float y1)
    {
        ShellFireAnimation a = shellFireAnimationPool.obtain();

        Config cfg = configs.get(configName);
        if (cfg == null) {
            throw new RuntimeException(String.format("ShellFireAnimation : no configuration named : '%s'", configName));
        }
        a.cfg = cfg;
        a.compute(x0, y0, x1, y1);

        return a;
    }

    private Config cfg;

    private boolean fired;
    private boolean hit;
    private boolean drawFire;
    private boolean drawExplosion;
    private float fireTime;
    private float hitTime;
    private float smokeEndTime;
    private float explosionEndTime;

    private FramedSprite shellSprites;
    private float shellW;
    private float shellDx;
    private float shellDy;
    private float shellDw;

    private int smokeFrame;
    private float smokeDf;

    private FramedSprite explosionSprites;
    private int explosionFrame;
    private int explosionRow;
    private float explosionDf;

    private ShellFireAnimation()
    {
    }

    @Override public void dispose()
    {
        shellFireAnimationPool.free(this);
    }

    private void compute(float x0, float y0, float x1, float y1)
    {
        // scattering
        x1 = (x1 + (random.nextFloat() * cfg.maxFireScattering) - (cfg.maxFireScattering / 2f));
        y1 = (y1 + (random.nextFloat() * cfg.maxFireScattering) -  (cfg.maxFireScattering / 2f));

        // geometry
        float dx = (x1 - x0);
        float dy = (y1 - y0);
        float a = (float) (MathUtils.atan2(y0 - y1, x0 - x1)) * MathUtils.radiansToDegrees;
        float w = (float) Math.sqrt((dx * dx) + (dy * dy));

        // timing
        float fireDuration = (w / cfg.shellSpeed);
        float smokeDuration = cfg.smokeDuration;
        float explosionDuration = cfg.explosionDuration;
        this.fireTime = (random.nextFloat() * cfg.maxFireDelay);
        this.hitTime = this.fireTime + fireDuration;
        this.smokeEndTime = this.hitTime + smokeDuration;
        this.explosionEndTime = this.hitTime + explosionDuration;
        float endTime = (this.smokeEndTime > this.explosionEndTime ? this.smokeEndTime : this.explosionEndTime);

        // shell vars
        this.shellSprites = new FramedSprite(cfg.shellSprites);
        this.shellSprites.x = x0;
        this.shellSprites.y = y0;
        this.shellSprites.a = a;
        this.shellW = 0f;
        this.shellDw = (w  / fireDuration);
        this.shellDx = (dx / fireDuration);
        this.shellDy = (dy / fireDuration);

        // smoke vars
        this.smokeFrame = 0;
        this.smokeDf = ((shellSprites.rows - 1) / smokeDuration);

        // explosion vars
        this.explosionFrame = 0;
        this.explosionSprites = new FramedSprite(cfg.explosionSprites);
        this.explosionSprites.x = (x1 - (explosionSprites.getFrame().getRegionWidth() / 2.0f));
        this.explosionSprites.y = (y1 - (explosionSprites.getFrame().getRegionHeight() / 2.0f));
        this.explosionDf = (explosionSprites.cols / explosionDuration);
        this.explosionRow = random.nextInt(explosionSprites.rows);
        this.explosionSprites.setFrame(explosionRow, 0);

        this.fired = false;
        this.hit = false;
        this.drawFire = false;
        this.drawExplosion = false;
        setDuration(endTime);
    }

    @Override public void begin() { }
    @Override public void end() { }

    @Override public void update(float delta)
    {
        if (!fired && (elapsed < fireTime)) {
            return;
        }

        if (!fired) {
            fired = true;
            drawFire = true;
            if(cfg.shellFireSnd != null) {
                cfg.shellFireSnd.play();
            }
        }

        if (!hit && (elapsed < hitTime)) {
            this.shellW += (shellDw * delta);
            this.shellSprites.x += (shellDx * delta);
            this.shellSprites.y += (shellDy * delta);
            this.shellSprites.getFrame().setRegionWidth((int) shellW);
            return;
        }

        if (!hit) {
            hit = true;
            drawExplosion = true;
            if(cfg.explosionSnd != null) {
                cfg.explosionSnd.play();
            }
        }

        float dt = (elapsed - hitTime);

        if (elapsed < smokeEndTime) {
            int frame = (int) (dt * smokeDf) + 1;
            if (frame != smokeFrame) {
                smokeFrame = frame;
                this.shellSprites.setFrame(smokeFrame, 0);
                this.shellSprites.getFrame().setRegionWidth((int) shellW);
            }
        } else {
            drawFire = false;
        }

        if (elapsed < explosionEndTime) {
            int frame = (int) (dt * explosionDf);
            if (frame != explosionFrame) {
                explosionFrame = frame;
                explosionSprites.setFrame(explosionRow, explosionFrame);
            }
        } else {
            drawExplosion = false;
        }
    }

    @Override public void draw(Batch batch)
    {
        if (drawFire) {
            this.shellSprites.draw(batch);
        }

        if (drawExplosion) {
            this.explosionSprites.draw(batch);
        }
    }
}
