package cn.graht.test.sf;

/**
 * @author GRAHT
 */

public class LinkedTest {
    DNode head;

    LinkedTest() {
        this.head = null;
    }

    void insertHead(int data) {
        DNode node = new DNode(data);
        node.next = head;
        head = node;
    }

    void insertPos(int data, int pos) {
        if (pos == 0) {
            insertHead(data);
        } else {
            DNode cur = head;
            for (int i = 1; i < pos; i++) {
                cur = cur.next;
            }
            DNode node = new DNode(data);
            node.next = cur.next;
            cur.next = node;
        }
    }

    void deleteHead() {
        if (head != null) {
            head = head.next;
        }
    }

    void deletePos(int pos) {
        if (pos == 0) {
            deleteHead();
        } else {
            DNode cur = head;
            for (int i = 1; i < pos; i++) {
                cur = cur.next;
            }
            cur.next = cur.next.next;
        }
    }
    void print() {
        DNode cur = head;
        while (cur != null) {
            System.out.println(cur.data);
            cur = cur.next;
        }
    }
    DNode findPos(int pos) {
        DNode cur = head;
        for (int i = 1; i < pos; i++) {
            cur = cur.next;
        }
        return cur;
    }
}

class Node {
    int data;
    DNode next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class MyLinked {
    public static void main(String[] args) {
        LinkedTest linkedTest = new LinkedTest();
        linkedTest.insertHead(3);
        linkedTest.insertHead(2);
        linkedTest.insertHead(1);
        linkedTest.insertPos(4, 3);
        linkedTest.insertPos(5, 4);
        linkedTest.print();
        linkedTest.deleteHead();
        linkedTest.deletePos(3);
        linkedTest.print();

    }
}