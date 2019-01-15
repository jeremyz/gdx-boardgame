package ch.asynk.gdx.boardgame.animations;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;

import ch.asynk.gdx.boardgame.FramedSprite;
import ch.asynk.gdx.boardgame.Drawable;

public class ShotAnimation extends TimedAnimation implements Drawable, Pool.Poolable
{
    private static final Pool<ShotAnimation> shotAnimationPool = new Pool<ShotAnimation>()
    {
        @Override protected ShotAnimation newObject()
        {
            return new ShotAnimation();
        }
    };

    public static ShotAnimation obtain()
    {
        return shotAnimationPool.obtain();
    }

    @Override public void dispose()
    {
        shotAnimationPool.free(this);
    }

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
    private Sound fireSnd;

    private int smokeFrame;
    private float smokeDf;

    private FramedSprite explosionSprites;
    private int explosionFrame;
    private int explosionRow;
    private float explosionDf;
    private Sound explosionSnd;

    private static Random random = new Random();

    public void compute(int i, FireAnimation.Config cfg, float x0, float y0, float x1, float y1)
    {
        // scattering
        x1 = (x1 + (random.nextFloat() * cfg.maxFireScattering) - (cfg.maxFireScattering / 2f));
        y1 = (y1 + (random.nextFloat() * cfg.maxFireScattering) -  (cfg.maxFireScattering / 2f));

        // geometry
        float dx = (x1 - x0);
        float dy = (y1 - y0);
        float r = (float) (MathUtils.atan2(y0 - y1, x0 - x1)) * MathUtils.radiansToDegrees;
        float w = (float) Math.sqrt((dx * dx) + (dy * dy));

        // timing
        float fireDuration = (w / cfg.shellSpeed);
        float smokeDuration = cfg.smokeDuration;
        float explosionDuration = cfg.explosionDuration;
        this.fireTime = (random.nextFloat() * cfg.maxFireDelay) + (i * cfg.burstDelay);
        this.hitTime = this.fireTime + fireDuration;
        this.smokeEndTime = this.hitTime + smokeDuration;
        this.explosionEndTime = this.hitTime + explosionDuration;
        float endTime = (this.smokeEndTime > this.explosionEndTime ? this.smokeEndTime : this.explosionEndTime);

        // shell vars
        this.shellSprites = new FramedSprite(cfg.shellSprites);
        this.shellSprites.x = x0;
        this.shellSprites.y = y0;
        this.shellSprites.r = r;
        this.shellW = 0f;
        this.shellDw = (w  / fireDuration);
        this.shellDx = (dx / fireDuration);
        this.shellDy = (dy / fireDuration);
        this.fireSnd = (i == 0 ? cfg.shellFireSnd : null);

        // smoke vars
        this.smokeFrame = 0;
        this.smokeDf = ((shellSprites.rows - 1) / smokeDuration);

        // explosion vars
        if (cfg.explosionSprites == null) {
            this.explosionSnd = null;
            this.explosionSprites = null;
        } else {
            this.explosionFrame = 0;
            this.explosionSprites = new FramedSprite(cfg.explosionSprites);
            this.explosionSprites.x = (x1 - (explosionSprites.getFrame().getRegionWidth() / 2.0f));
            this.explosionSprites.y = (y1 - (explosionSprites.getFrame().getRegionHeight() / 2.0f));
            this.explosionDf = (explosionSprites.cols / explosionDuration);
            this.explosionRow = random.nextInt(explosionSprites.rows);
            this.explosionSprites.setFrame(explosionRow, 0);
            this.explosionSnd = cfg.explosionSnd;
        }

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
            if(fireSnd != null) {
                fireSnd.play();
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
            if(explosionSnd != null) {
                explosionSnd.play();
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
