package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;

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
    private Tile from;
    private Tile to;

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
        this.from = null;
        this.to = null;
        this.finalOrientation = null;
    }

    @Override public void dispose()
    {
        clear();
        pathPool.free(this);
    }

    @Override public String toString()
    {
        StringBuilder s = new StringBuilder(" o:").append(finalOrientation).append("\n");
        for (Tile t : this)
            s.append("  ").append(t).append("\n");
        return s.toString();
    }

    public Tile from()
    {
        return from;
    }

    public Tile to()
    {
        return to;
    }

    public void show(int i, int j)
    {
        Tile prev = null;
        for (int n = 0; n < size(); n++) {
            final Tile tile = get(n);
            if (prev != null) {
                final Orientation o = Orientation.fromTiles(prev, tile);
                prev.enableOverlay(i, o);
                tile.enableOverlay(j, o.opposite());
            }
            prev = tile;
        }
    }

    public boolean nextPosition(Piece piece, Vector3 v)
    {
        if (hasNext()) {
            to = current();
            if (piece.isOn(to)) {
                // rotation ...
                from = to;
                next();
                final Orientation o = (hasNext() ? Orientation.fromTiles(from, current()) : finalOrientation);
                // if already facing, transform into a straight move
                if (piece.isFacing(o)) {
                    to = current();
                    if (to == null) {
                        return true;
                    }
                }
                piece.getPosOn(to, o, v);
            } else {
                // rotation finished, regular move
                piece.getPosOn(to, Orientation.fromR(v.z), v);
            }
            return false;
        }
        to = null;
        return true;
    }
}
