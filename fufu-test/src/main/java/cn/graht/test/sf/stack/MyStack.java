package cn.graht.test.sf.stack;

/**
 * @author GRAHT
 */


public interface MyStack<T> {
    void push(T t);

    T pop();

    int size();

    boolean isEmpty();
}
