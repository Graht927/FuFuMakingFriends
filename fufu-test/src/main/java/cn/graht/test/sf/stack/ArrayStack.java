package cn.graht.test.sf.stack;

/**
 * @author GRAHT
 */

public class ArrayStack<T> implements MyStack<T> {
    private T[] items = (T[]) new Object[10]; //最开始设置大小
    private int N = 0; //初始元素个数
    public ArrayStack(int cap) {
        this.items = (T[]) new Object[cap];
    }
    @Override
    public void push(T item) {
        judgeSize();
        items[N++] = item;
    }
    private void judgeSize(){
        if (items.length <= N){
            resize(items.length*2);
        }else if (N > 0 && N < items.length/2){
            resize(items.length / 2);
        }
    }
    private void  resize(int size){
        T[] temp = (T[]) new Object[size];
        for (int i = 0; i < N; i++){
            temp[i] = items[i];
        }
        items = temp;

    }

    @Override
    public T pop() {
        if (isEmpty()) return null;
        T item = items[--N];
        items[N] = null;
        resize(items.length/2);
        return item;
    }

    @Override
    public int size() {
        return N;
    }

    @Override
    public boolean isEmpty() {
        return N == 0;
    }
}
