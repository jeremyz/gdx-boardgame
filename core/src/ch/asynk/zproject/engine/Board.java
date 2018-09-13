package ch.asynk.gdx.board.engine;

import com.badlogic.gdx.math.Vector2;

public interface Board
{
    public void centerOf(int x, int y, Vector2 v);
    public void toBoard(float x, float y, Vector2 v);
}
