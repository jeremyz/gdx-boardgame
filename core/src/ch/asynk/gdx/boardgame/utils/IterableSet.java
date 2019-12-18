package ch.asynk.gdx.boardgame.utils;

public class IterableSet<E> extends IterableArray<E>
{
    public IterableSet(int n)
    {
        super(n);
    }

    public IterableSet(IterableSet<E> set)
    {
        super(set);
    }

    @Override public boolean add(E e)
    {
        if (contains(e)) return false;
        super.add(e);
        return true;
    }
}
