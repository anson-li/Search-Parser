package datastructs;
@SuppressWarnings("serial")
public class GenericStack<T> extends java.util.ArrayList<T> {
    
    public GenericStack() {}

    public GenericStack(GenericStack<T> clone) {
        for (T element : clone) {
            push(element);
        }
    }

    public void push(T obj) {
        add(0, obj);
    }

    public T pop() {
        if (isEmpty())
            return null;

        T obj = get(0);
        remove(0);
        return obj;
    }

    public boolean isEmpty() {
        if(size() == 0)
            return true;
        else
            return false;
    }
}
