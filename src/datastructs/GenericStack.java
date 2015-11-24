package datastructs;
@SuppressWarnings("serial")
/**
* Generic stack used to hold queries of different priorities.
*/
public class GenericStack<T> extends java.util.ArrayList<T> {
    
    public GenericStack() {}

    /**
    * Clones a generic stack to current generic stack
    * @param clone
    */
    public GenericStack(GenericStack<T> clone) {
        for (T element : clone) {
            push(element);
        }
    }

    /**
    * Pushes an object to the top of the stack
    * @param obj
    */
    public void push(T obj) {
        add(0, obj);
    }

    /**
    * Pops an object from the top of the stack
    * @return obj  
    */
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
