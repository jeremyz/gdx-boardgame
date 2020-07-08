package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.Piece;
import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage.TileProvider;
import ch.asynk.gdx.boardgame.utils.Collection;
import ch.asynk.gdx.boardgame.utils.IterableStack;

public class HexBoard implements Board
{
    private final float side;   // length of the side of the hex
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset
    private final boolean vertical;

    private final int cols;     // # colmuns
    private final int rows;     // # rows
    private final float w;      // side to side orthogonal distance
    private final float dw;     // half hex : w/2
    private final float dh;     // hex top : s/2
    private final float h;      // square height : s + dh
    private final float slope;  // dh / dw

    private int aOffset;        // to fix Orientation computation from adjacents
    private int searchCount;    // to differentiate move computations
    private IterableStack<Tile> stack;

    private final int tl;       // # tiles in 2 consecutive columns

    //  BoardOrientation.VERTICAL : 2 vertical sides : 2 vertices pointing up and down
    //  coordinates
    //  \
    //   \___
    //   cols are at   0°
    //   rows are at 120°
    //   bottom left is the bottom vertice of the most bottom-left vertical hex side of the map
    //
    //  BoardOrientation.HORIZONTAL : 2 horizontal sides : 2 vertices pointing left and right
    //  coordinates
    //  |
    //  |
    //   \
    //    \
    //   cols are at -60°
    //   rows are at   90°
    //   bottom left is the left vertice of the most bottom-left horizontal hex side of the map

    // [0] is 0° facing East
    // [8] is default
    private static final int [] vAngles = {  0, 60, -1, 120, 180, 240,  -1, 300, 60};
    private static final int [] hAngles = { -1, 30, 90, 150,  -1, 210, 270, 330, 30};

    private final Tile[] adjacents;
    private final TileProvider tileProvider;

    public HexBoard(int cols, int rows, float side, float x0, float y0, BoardFactory.BoardOrientation boardOrientation, TileProvider tileProvider)
    {
        this.side = side;
        this.vertical = (boardOrientation == BoardFactory.BoardOrientation.VERTICAL);
        this.tileProvider = tileProvider;

        this.w  = side * 1.73205f;
        this.dw = w / 2.0f;
        this.dh = side / 2.0f;
        this.h  = side + dh;

        this.searchCount = 0;
        this.stack = new IterableStack<Tile>(10);

        if (vertical) {
            this.x0 = x0;
            this.y0 = y0;
            this.cols = cols;
            this.rows = rows;
            this.aOffset = 0;
            this.slope = dh / dw;
        } else {
            this.x0 = y0;
            this.y0 = x0;
            this.cols = rows;
            this.rows = cols;
            this.aOffset = -60;
            this.slope = dw / dh;
        }
        this.tl = (2 * this.cols - 1);

        this.adjacents = new Tile[6];
        for (int i = 0; i < 6; i++)
            this.adjacents[i] = Tile.OffMap;
    }

    @Override public int size()
    {
        return (rows / 2) * tl + ((rows % 2) * cols);
    }

    @Override public Tile getTile(int x, int y)
    {
        return tileProvider.getTile(x, y, isOnBoard(x, y));
    }

    @Override public int[] getAngles()
    {
        if (vertical) {
            return vAngles;
        } else {
            return hAngles;
        }
    }

    @Override public Tile[] getAdjacents() { return adjacents; }

    @Override public void buildAdjacents(int x, int y)
    {
        // VERTICAL starts with E 0°, HORIZONTAL starts with SE -30°
        adjacents[0] = getTile(x + 1, y);
        adjacents[1] = getTile(x + 1, y + 1);
        adjacents[2] = getTile(x    , y + 1);
        adjacents[3] = getTile(x - 1, y);
        adjacents[4] = getTile(x - 1, y - 1);
        adjacents[5] = getTile(x    , y - 1);
    }

    @Override public int genKey(int x, int y)
    {
        if (!isOnBoard(x, y)) return -1;
        if (vertical) {
            return _genKey(x, y);
        } else {
            return _genKey(y, x);
        }
    }

    private int _genKey(int x, int y)
    {
        int n = y / 2;
        int i =  x - n + n * tl;
        if ((y % 2) != 0) {
            i += (cols - 1);
        }
        return i;
    }

    @Override public boolean isOnBoard(int x, int y)
    {
        if (vertical) {
            return _isOnBoard(x, y);
        } else {
            return _isOnBoard(y, x);
        }
    }

    private boolean _isOnBoard(int x, int y)
    {
        if ((y < 0) || (y >= rows)) return false;
        if ((x < ((y + 1) / 2)) || (x >= (cols + (y / 2)))) return false;
        return true;
    }

    @Override public void centerOf(int x, int y, Vector2 v)
    {
        if (vertical) {
            _centerOf(x, y, v, false);
        } else {
            _centerOf(y, x, v, true);
        }
    }

    private void _centerOf(int x, int y, Vector2 v, boolean swap)
    {
        float cx = this.x0 + (this.dw + (x * this.w) - (y * this.dw));
        float cy = this.y0 + (this.dh + (y * this.h));
        if (swap) {
            v.set(cy, cx);
        } else {
            v.set(cx, cy);
        }
    }

    @Override public void toBoard(float x, float y, Vector2 v)
    {
        if (vertical) {
            _toBoard(x, y, v, false);
        } else {
            _toBoard(y, x, v, true);
        }
    }

    private void _toBoard(float x, float y, Vector2 v, boolean swap)
    {
        int col = -1;
        int row = -1;

        // compute row
        float dy = y - this.y0;
        row = (int) (dy / this.h);
        if (dy < 0f) row -= 1;

        // compute col
        float dx = x - this.x0 + (row * this.dw);
        col = (int) (dx / this.w);
        if (dx < 0f) col -= 1;

        // upper rectangle or hex body
        if (dy > ((row * this.h) + this.side)) {
            dy -= ((row * this.h) + this.side);
            dx -= (col * this.w);
            // upper left or right rectangle
            if (dx < this.dw) {
                if (dy > (dx * this.slope)) {
                    // upper left hex
                    row += 1;
                }
            } else {
                // if (dy > ((2 * this.dh) - (dx * this.slope))) {
                if (dy > ((this.w - dx) * this.slope)) {
                    // upper right hex
                    row += 1;
                    col += 1;
                }
            }
        }

        if (swap) {
            v.set(row, col);
        } else {
            v.set(col, row);
        }
    }

    @Override public float distance(int x0, int y0, int x1, int y1, Geometry geometry)
    {
        if (geometry == Board.Geometry.EUCLIDEAN) {
            int dx = (x1 - x0);
            int dy = (y1 - y0);

            if (dx == 0) {
                return  Math.abs(dy);
            }
            else if (dy == 0 || dx == dy) {
                return  Math.abs(dx);
            }
            float fdx = dx - dy / 2f;
            float fdy = dy * 0.86602f;
            return (float)Math.sqrt((fdx * fdx) + (fdy * fdy));
        } else {
            int dx = Math.abs(x1 - x0);
            int dy = Math.abs(y1 - y0);
            int dz = Math.abs((x0 - y0) - (x1 - y1));

            if (dx > dy) {
                if (dx > dz)
                    return dx;
            } else {
                if (dy > dz)
                    return dy;
            }
            return dz;
        }
    }

    // http://zvold.blogspot.com/2010/01/bresenhams-line-drawing-algorithm-on_26.html
    // http://zvold.blogspot.com/2010/02/line-of-sight-on-hexagonal-grid.html
    @Override public boolean lineOfSight(int x0, int y0, int x1, int y1, Collection<Tile> tiles, Vector2 v)
    {
        tiles.clear();
        v.set(0, 0);

        // orthogonal projection
        float ox0 = x0 - ((y0 + 1) / 2f);
        float ox1 = x1 - ((y1 + 1) / 2f);
        int dy = y1 - y0;
        float dx = (ox1 - ox0);

        // quadrant I && III
        boolean q13 = (((dx >= 0) && (dy >= 0)) || ((dx < 0) && (dy < 0)));

        // is positive
        int xs = 1;
        int ys = 1;
        if (dx < 0) xs = -1;
        if (dy < 0) ys = -1;

        // dx counts half width
        dy = Math.abs(dy);
        dx = Math.abs(2 * dx);

        int dx3 = (int)(3 * dx);
        int dy3 = 3 * dy;

        if (dx == 0 || dx == dy3)
            return diagonalLineOfSight(x0, y0, x1, y1, (dx == 0), q13, tiles, v);

        // angle is less than 45°
        boolean flat = (dx > dy3);

        int x = x0;
        int y = y0;
        int e = (int)(-2 * dx);

        Tile from = getTile(x0, y0);
        Tile to = getTile(x1, y1);
        float d = distance(x0, y0, x1, y1, Board.Geometry.EUCLIDEAN);
        tiles.add(from);
        from.blocked = false;
        boolean contact = false;
        boolean losBlocked = false;
        while ((x != x1) || (y != y1)) {
            if (e > 0) {
                // quadrant I : up left
                e -= (dy3 + dx3);
                y += ys;
                if (!q13) {
                    x -= xs;
                }
            } else {
                e += dy3;
                if ((e > -dx) || (!flat && (e == -dx))) {
                    // quadrant I : up right
                    e -= dx3;
                    y += ys;
                    if (q13)
                        x += xs;
                } else if (e < -dx3) {
                    // quadrant I : down right
                    e += dx3;
                    y -= ys;
                    if (!q13)
                        x += xs;
                } else {
                    // quadrant I : right
                    e += dy3;
                    x += xs;
                }
            }
            final Tile t = getTile(x, y);
            if (losBlocked && !contact) {
                Orientation o = Orientation.fromTiles(tiles.get(tiles.size() - 1), t);
                computeContact(from, to, o, t, v, true);
                contact = true;
            }
            tiles.add(t);
            t.blocked = losBlocked;
            losBlocked = (losBlocked || t.blockLos(from, to, d, distance(x0, y0, x, y, Board.Geometry.EUCLIDEAN)));
        }

        return tiles.get(tiles.size() - 1).blocked;
    }

    private boolean diagonalLineOfSight(int x0, int y0, int x1, int y1, boolean flat, boolean q13, Collection<Tile> tiles, Vector2 v)
    {
        int dy = ( (y1 > y0) ? 1 : -1);
        int dx = ( (x1 > x0) ? 1 : -1);

        int x = x0;
        int y = y0;

        Tile from = getTile(x0, y0);
        Tile to = getTile(x1, y1);
        float d = distance(x0, y0, x1, y1, Board.Geometry.EUCLIDEAN);
        tiles.add(from);
        from.blocked = false;
        int blocked = 0;
        boolean contact = false;
        boolean losBlocked = false;
        while ((x != x1) || (y != y1)) {

            if (flat)
                y += dy; // up left
            else
                x += dx; // right
            Tile t = getTile(x, y);
            if (t.isOnBoard()) {
                tiles.add(t);
                t.blocked = losBlocked;
                if (t.blockLos(from, to, d, distance(x0, y0, x, y, Board.Geometry.EUCLIDEAN)))
                    blocked |= 0x01;
            } else {
                blocked |= 0x01;
            }

            if (flat)
                x += dx; // up right
            else {
                y += dy; // up right
                if (!q13) x -= dx;
            }
            t = getTile(x, y);
            if (t.isOnBoard()) {
                tiles.add(t);
                t.blocked = losBlocked;
                if (t.blockLos(from, to, d, distance(x0, y0, x, y, Board.Geometry.EUCLIDEAN)))
                    blocked |= 0x02;
            } else {
                blocked |= 0x02;
            }

            if (flat)
                y += dy; // up
            else
                x += dx; // diagonal
            t = getTile(x, y);
            tiles.add(t);
            t.blocked = (losBlocked || blocked == 0x03);
            if (t.blocked && !contact) {
                Orientation o = computeOrientation(dx, dy, flat);
                if (!losBlocked && blocked == 0x03)
                    computeContact(from, to, o, t, v, false);
                else
                    computeContact(from, to, o.opposite(), tiles.get(tiles.size() - 4), v, false);
                contact = true;
            }
            losBlocked = (t.blocked || t.blockLos(from, to, d, distance(x0, y0, x, y, Board.Geometry.EUCLIDEAN)));
        }

        return tiles.get(tiles.size() - 1).blocked;
    }

    private Orientation computeOrientation(int dx, int dy, boolean flat)
    {
        if (flat) {
            if (vertical)
                return (dy == 1 ? Orientation.N : Orientation.S);
            else
                return (dx == 1 ? Orientation.NE : Orientation.SW);
        }
        if (dx == 1) {
            if (dy == 1)
                return (vertical ? Orientation.NE : Orientation.E);
            else
                return Orientation.SE;
        } else {
            if (dy == 1)
                return Orientation.NW;
            else
                return (vertical ? Orientation.SW : Orientation.W);
        }
    }

    private void computeContact(Tile from, Tile to, Orientation o, Tile t, Vector2 v, boolean line)
    {
        float dx = to.cx - from.cx;
        float dy = to.cy - from.cy;
        float m = (dx == 0 ? Float.MAX_VALUE : dy / dx);
        float c = from.cy - (m * from.cx);
        if (vertical) {
            if (o == Orientation.N) {
                v.set(t.cx, t.cy - side);
            } else if (o == Orientation.S) {
                v.set(t.cx, t.cy + side);
            } else if (o == Orientation.E) {
                float x = t.cx - dw;
                v.set(x, from.cy + m * (x - from.cx));
            } else if (o == Orientation.W) {
                float x = t.cx + dw;
                v.set(x, from.cy + m * (x - from.cx));
            } else {
                if (line) {
                    float p = ((o == Orientation.SE || o == Orientation.NW) ? slope : -slope);
                    float k = t.cy - (p * t.cx);
                    if (o == Orientation.SE || o == Orientation.SW)
                        k += side;
                    else
                        k -= side;
                    float x = (k - c) / (m - p);
                    v.set(x, m * x + c);
                } else {
                    float x = t.cx + ((o == Orientation.NE || o == Orientation.SE) ? -dw : dw);
                    float y = t.cy + ((o == Orientation.SE || o == Orientation.SW) ? dh : -dh);
                    v.set(x, y);
                }
            }
        } else {
            if (o == Orientation.E) {
                v.set(t.cx - side, t.cy);
            } else if (o == Orientation.W) {
                v.set(t.cx + side, t.cy);
            } else if (o == Orientation.N) {
                float y = t.cy - dw;
                v.set(from.cx + (y - from.cy) / m, y);
            } else if (o == Orientation.S) {
                float y = t.cy + dw;
                v.set(from.cx + (y - from.cy) / m, y);
            } else {
                if (line) {
                    float k = 0;
                    float p = ((o == Orientation.SE || o == Orientation.NW) ? slope : -slope);
                    if (o == Orientation.SW || o == Orientation.NW)
                        k = t.cy - (p * (t.cx + side));
                    else
                        k = t.cy - (p * (t.cx - side));
                    float x = (k - c) / (m - p);
                    v.set(x, m * x + c);
                } else {
                    float x = t.cx + ((o == Orientation.NW || o == Orientation.SW) ? dh: -dh);
                    float y = t.cy + ((o == Orientation.SE || o == Orientation.SW) ? dw : -dw);
                    v.set(x, y);
                }
            }
        }
    }

    @Override public int possibleMoves(Piece piece, Tile from, Collection<Tile> tiles)
    {
        tiles.clear();
        searchCount += 1;

        from.acc = piece.getAvailableMP();
        from.parent = null;
        from.searchCount = searchCount;

        if (from.acc <= 0 || !from.isOnBoard())
            return tiles.size();

        int roadMarchBonus = piece.roadMarchBonus();
        from.roadMarch = roadMarchBonus > 0;
        stack.push(from);

        while(!stack.isEmpty()) {
            final Tile src = stack.pop();

            if ((src.acc + (src.roadMarch ? roadMarchBonus : 0)) <= 0) continue;

            buildAdjacents(src.x, src.y);
            for (int i = 0, j = 0; i < 6; i++, j++) {
                final Tile dst = adjacents[i];
                if (!dst.isOnBoard()) continue;

                if (getAngles()[j] == -1) j++;
                final Orientation o = Orientation.fromR(getAngles()[j] + aOffset);
                int cost = piece.moveCost(src, dst, o);
                if (cost == Integer.MAX_VALUE) continue;    // impracticable

                int r = src.acc - cost;
                boolean rm = src.roadMarch && src.hasRoad(o);
                // not enough MP even with RM, maybe first move allowed
                if ((r + (rm ? roadMarchBonus : 0)) < 0 && !(src == from && piece.atLeastOneTileMove())) continue;

                if (dst.searchCount != searchCount) {
                    dst.searchCount = searchCount;
                    dst.acc = r;
                    dst.parent = src;
                    dst.roadMarch = rm;
                    stack.push(dst);
                    tiles.add(dst);
                } else if (r > dst.acc || (rm && (r + roadMarchBonus > dst.acc + (dst.roadMarch ? roadMarchBonus : 0)))) {
                    dst.acc = r;
                    dst.parent = src;
                    dst.roadMarch = rm;
                    stack.push(dst);
                }
            }
        }

        return tiles.size();
    }

    @Override public int shortestPath(Piece piece, Tile from, Tile to, Collection<Tile> tiles)
    {
        tiles.clear();
        searchCount += 1;

        from.acc = 0;
        from.parent = null;
        from.searchCount = searchCount;

        if (from == to || !from.isOnBoard() || !to.isOnBoard())
            return tiles.size();

        int roadMarchBonus = piece.roadMarchBonus();
        from.roadMarch = roadMarchBonus > 0;
        stack.push(from);

        while(!stack.isEmpty()) {

            final Tile src = stack.pop();

            if (src == to) break;

            buildAdjacents(src.x, src.y);
            for (int i = 0, j = 0; i < 6; i++, j++) {
                final Tile dst = adjacents[i];
                if (!dst.isOnBoard()) continue;

                if (getAngles()[j] == -1) j++;
                final Orientation o = Orientation.fromR(getAngles()[j] + aOffset);
                int cost = piece.moveCost(src, dst, o);
                if (cost == Integer.MAX_VALUE) continue;    // impracticable
                cost += src.acc;
                float total = cost + distance(dst.x, dst.y, to.x, to.y, Board.Geometry.EUCLIDEAN);
                boolean rm = src.roadMarch && src.hasRoad(o);
                if (rm) total -= roadMarchBonus;

                boolean add = false;
                if (dst.searchCount != searchCount) {
                    dst.searchCount = searchCount;
                    add = true;
                } else if ((dst.f > total) || (rm && !dst.roadMarch && dst.f == total)) {
                    stack.remove(dst);
                    add = true;
                }

                if (add) {
                    dst.acc = cost;
                    dst.f = total;
                    dst.roadMarch = rm;
                    dst.parent = src;
                    int idx = Integer.MAX_VALUE;
                    for (int k = 0; k < stack.size(); k++) {
                        if (stack.get(k).f <= dst.f) {
                            idx = k;
                            break;
                        }
                    }
                    if (idx == Integer.MAX_VALUE)
                        stack.push(dst);
                    else
                        stack.insert(dst, idx);
                }
            }
        }

        if (to.searchCount == searchCount) {
            stack.clear();
            Tile t = to;
            while(t != from) {
                stack.add(t);
                t = t.parent;
            }
            tiles.add(from);
            while(!stack.isEmpty()) {
                tiles.add(stack.pop());
            }
        }

        return tiles.size();
    }
}
