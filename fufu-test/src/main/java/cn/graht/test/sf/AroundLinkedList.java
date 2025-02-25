package cn.graht.test.sf;

/**
 * @author GRAHT
 */

public class AroundLinkedList {
    int size;
    ANode head;
    ANode tail;

    public void insertHead(int value) {
        ANode node = new ANode(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head = node;
            tail.next = head;
        }
    }

    public void insertPos(int value, int pos) {
        ANode node = new ANode(value);
        if (pos == 0) {
            insertHead(value);
        } else {
            ANode cur = head;
            for (int i = 0; i < pos - 1; i++) {
                cur = cur.next;
            }
            node.next = cur.next;
            cur.next = node;
            if (cur == tail) {
                tail = node;
                tail.next = head;
            }
        }
    }

    public void deleteHead() {
        if (head != null) {
            if (head == tail) {
                head = null;
                tail = null;
            }
            head = head.next;
            tail.next = head;
        }
    }

    public void deletePos(int pos) {
        if (pos == 0) {
            deleteHead();
        } else {
            ANode cur = head;
            for (int i = 0; i < pos - 1; i++) {
                cur = cur.next;
            }
            if (cur.next == tail) {
                tail = cur;
                tail.next = head;
            }else cur.next = cur.next.next;
        }
    }
}

class ANode {
    ANode next;
    int data;

    ANode(int data) {
        this.data = data;
        this.next = null;
    }

}