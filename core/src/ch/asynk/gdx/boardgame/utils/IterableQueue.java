package ch.asynk.gdx.boardgame.utils;

public class IterableQueue<E> extends IterableArray<E>
{
    public IterableQueue(int n)
    {
        super(n);
    }

    public void enqueue(E e)
    {
        add(e);
    }

    public E dequeue()
    {
        return remove(0);
    }
}
