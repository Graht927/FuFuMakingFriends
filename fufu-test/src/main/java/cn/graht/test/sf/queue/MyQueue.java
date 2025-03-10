package cn.graht.test.sf.queue;

import java.util.Date;

/**
 * @author GRAHT
 */

public class MyQueue {

}

class ArrayQueue<T> {
    private int head;
    private int tail;
    private int n;
    private int[] items;

    public ArrayQueue(int cap) {
        items = new int[cap];
        n = cap;
    }

    public void push(int value) {
        //判断队列是否已满
        if (judge()) { //最好O(1) 最坏O(n)
            for (int i = 0, j = head; i < tail - head && j < tail; i++, j++) {
                items[i] = items[j];
            }
            tail -= head;
            head = 0;
            push(value);
            return;
        }
        items[tail] = value;
        tail++;
    }

    public boolean judge() {
        return tail == n;
    }

    public int pop() {
        if (isEmpty()) return -1;
        int temp = items[head];
        items[head] = 0;
        head++;
        return temp;
    }

    public int pop2() {  //O(n)
        if (isEmpty()) return -1;
        int temp = items[head];
        tail--;
        for (int i = 0; i < tail; i++) {
            items[i] = items[i + 1];
        }
        return temp;
    }

    public boolean isEmpty() {
        return head == tail;
    }

}

class CircleQueue<T> {
    private int head;
    private int tail;
    private int n;
    private int[] items;

    public CircleQueue(int cap) {
        items = new int[cap];
        n = cap;
    }

    public void push(int value) {
        //判断队列是否已满
        if (judge()) { //最好O(1) 最坏O(n)
            return;
        }
        items[tail] = value;
        tail = (tail + 1) % n; //循环队列
    }

    public boolean judge() {
        return (tail + 1) % n == head;
    }

    public int pop() {
        if (isEmpty()) return -1;
        int temp = items[head];
        head = (head + 1) % n;
        return temp;
    }

    public int pop2() {  //O(n)
        if (isEmpty()) return -1;
        int temp = items[head];
        tail--;
        for (int i = 0; i < tail; i++) {
            items[i] = items[i + 1];
        }
        return temp;
    }

    public boolean isEmpty() {
        return head == tail;
    }

}

class LinkedQueue<T> {
    private int n;
    private LinkedT items;

    public LinkedQueue(int cap) {
        items = new LinkedT();
        n = cap;
    }

    public void push(int value) {
        //判断队列是否已满
        if (judge()) { //最好O(1) 最坏O(n)
            return;
        }
        items.insertTail(value);
    }

    public boolean judge() {
        return items.size == n;
    }

    public Node pop() {
        if (isEmpty()) return null;
        Node node = items.deleteHead();
        return node;
    }

    public boolean isEmpty() {
        return items.size == 0;
    }
}

class LinkedT {
    int size;
    Node head;
    Node tail;

    public void insertHead(int value) {
        Node node = new Node();
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head = node;
        }
        size++;
    }

    public void insertPos(int value, int pos) {
        Node node = new Node();
        if (pos == 0) {
            insertHead(value);
        } else {
            Node cur = head;
            for (int i = 0; i < pos - 1; i++) {
                cur = cur.next;
            }
            if (cur == tail) {
                tail = node;
            }
            node.next = cur.next;
            cur.next = node;
        }

    }
    public void insertTail(int value){
        Node node = new Node();
        if (tail == null) {
            tail = node;
            head = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public Node deleteHead() {
        if (head == tail) {
            tail = null;
        }
        Node cur = head;
        head = head.next;
        return cur;
    }

    public Node deletePos(int pos) {
        if (pos == 0) {
         return    deleteHead();
        } else {
            Node cur = head;
            for (int i = 0; i < pos - 1; i++) {
                cur = cur.next;
            }
            if (cur == tail) {
                return null;
            } else cur.next = cur.next.next;
            return cur;
        }
    }

    public int size() {
        return size;
    }
}

class Node {
    int data;
    Node next;

    Node() {
        data = 0;
        next = null;
    }
}

