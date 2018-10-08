package ch.asynk.gdx.boardgame.utils;

public class IterableSet<E> extends IterableArray<E>
{
    public IterableSet(int n)
    {
        super(n);
    }

    @Override public boolean add(E e)
    {
        if (contains(e)) return false;
        super.add(e);
        return true;
    }
}
