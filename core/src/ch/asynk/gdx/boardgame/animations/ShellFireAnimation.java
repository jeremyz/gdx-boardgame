package ch.asynk.gdx.boardgame.animations;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import ch.asynk.gdx.boardgame.FramedSprite;
import ch.asynk.gdx.boardgame.Piece;

public class ShellFireAnimation extends TimedAnimation implements Pool.Poolable
{
    private static class Config
    {
        public float maxFireDelay;
        public float maxShootScattering;
        public float shellSpeed;
        public float smokeDuration;
        public float explosionDuration;
        public FramedSprite shellSprites;
        public FramedSprite explosionSprites;
        public Config(float maxFireDelay, float maxShootScattering, float shellSpeed,
                float smokeDuration, float explosionDuration, FramedSprite shellSprites, FramedSprite explosionSprites)
        {
            this.maxFireDelay = maxFireDelay;
            this.maxShootScattering = maxShootScattering;
            this.shellSpeed = shellSpeed;
            this.smokeDuration = smokeDuration;
            this.explosionDuration = explosionDuration;
            this.shellSprites = shellSprites;
            this.explosionSprites = explosionSprites;
        }
    }

    private static Map<String, Config> configs = new Hashtable<String, Config>();

    public static void register(final String name,
            float maxFireDelay,
            float maxShootScattering,
            float shellSpeed,
            float smokeDuration,
            float explosionDuration,
            final Texture shellTexture, int shellC, int shellR,
            final Texture explosionTexture, int explosionC, int explosionR
            )
    {
        Config cfg = new Config(maxFireDelay, maxShootScattering, shellSpeed, smokeDuration, explosionDuration,
                new FramedSprite(shellTexture, shellC, shellR),
                new FramedSprite(explosionTexture, explosionC, explosionR)
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
        shooter.getShootingPoint(sv, target);
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

    private TextureRegion fireRegion;
    private float fireX;
    private float fireY;
    private float fireA;
    private float fireW;
    private float fireDx;
    private float fireDy;
    private float fireDw;

    private int smokeFrame;
    private float smokeDf;

    private int explosionStart;
    private int explosionFrame;
    private float explosionX;
    private float explosionY;
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
        x1 = (x1 + (random.nextFloat() * cfg.maxShootScattering) - (cfg.maxShootScattering / 2f));
        y1 = (y1 + (random.nextFloat() * cfg.maxShootScattering) -  (cfg.maxShootScattering / 2f));

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

        // fire vars
        this.fireX = x0;
        this.fireY = y0;
        this.fireA = a;
        this.fireW = 0f;
        this.fireDw = (w  / fireDuration);
        this.fireDx = (dx / fireDuration);
        this.fireDy = (dy / fireDuration);
        this.fireRegion = new TextureRegion(cfg.shellSprites.frames[0]);

        // smoke vars
        this.smokeFrame = 0;
        this.smokeDf = (cfg.shellSprites.rows / smokeDuration);

        // explosion vars
        this.explosionX = (x1 - (cfg.explosionSprites.width / 2.0f));
        this.explosionY = (y1 - (cfg.explosionSprites.height / 2.0f));
        this.explosionDf = (cfg.explosionSprites.cols / explosionDuration);
        this.explosionStart = (random.nextInt(cfg.explosionSprites.rows) * cfg.explosionSprites.cols);
        this.explosionFrame = this.explosionStart;

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
        }

        if (!hit && (elapsed < hitTime)) {
            fireW += (fireDw * delta);
            fireX += (fireDx * delta);
            fireY += (fireDy * delta);
            fireRegion.setRegionWidth((int) fireW);
            return;
        }

        if (!hit) {
            hit = true;
            drawExplosion = true;
        }

        float dt = (elapsed - hitTime);

        if (elapsed < smokeEndTime) {
            int frame = (int) (dt * smokeDf);
            if (frame != smokeFrame) {
                smokeFrame = frame;
                fireRegion.setRegion(cfg.shellSprites.frames[smokeFrame]);
                fireRegion.setRegionWidth((int) fireW);
            }
        } else {
            drawFire = false;
        }

        if (elapsed < explosionEndTime) {
            explosionFrame = (explosionStart + (int) (dt * explosionDf));
        } else {
            drawExplosion = false;
        }
    }

    @Override public void draw(Batch batch)
    {
        if (drawFire) {
            batch.draw(fireRegion, fireX, fireY, 0, 0, fireRegion.getRegionWidth(), fireRegion.getRegionHeight(), 1f, 1f, fireA);
        }

        if (drawExplosion) {
            batch.draw(cfg.explosionSprites.frames[explosionFrame], explosionX, explosionY);
        }
    }
}
