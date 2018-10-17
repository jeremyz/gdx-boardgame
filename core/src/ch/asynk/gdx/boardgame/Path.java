package ch.asynk.gdx.boardgame;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.gdx.boardgame.pieces.Piece;
import ch.asynk.gdx.boardgame.utils.IterableArray;

public class Path extends IterableArray<Tile> implements Disposable, Pool.Poolable
{
    public static int defaultCapacity = 8;

    private static final Pool<Path> pathPool = new Pool<Path>()
    {
        @Override protected Path newObject()
        {
            return new Path();
        }
    };

    public static Path obtain()
    {
        return pathPool.obtain();
    }

    private Orientation finalOrientation;

    private Path()
    {
        super(defaultCapacity);
    }

    public void setFinalOrientation(Orientation orientation)
    {
        this.finalOrientation = orientation;
    }

    @Override public void reset()
    {
        clear();
        this.finalOrientation = null;
    }

    @Override public void dispose()
    {
        clear();
        pathPool.free(this);
    }

    @Override public String toString()
    {
        String s = String.format(" o:%s\n", finalOrientation);
        for (Tile t : this)
            s += String.format("  %s\n", t.toString());
        return s;
    }

    public boolean nextVector(Piece piece, Vector3 v)
    {
        if (hasNext()) {
            Tile cur = current();
            if (piece.isOn(cur)) {
                // rotation ...
                next();
                Orientation o = (hasNext() ? Orientation.fromTiles(cur, current()) : finalOrientation);
                // if already facing, transform into a move
                if (piece.isFacing(o)) {
                    cur = current();
                    if (cur == null) {
                        return true;
                    }
                }
                piece.getPosOn(cur, o, v);
            } else {
                // regular move, no rotation
                piece.getPosOn(cur, Orientation.fromR(v.z), v);
            }
            return false;
        }
        return true;
    }
}
