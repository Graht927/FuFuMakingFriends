package cn.graht.test.sf;

import org.apache.logging.log4j.core.util.JsonUtils;

/**
 * @author GRAHT
 */

public class DoubleLinkedTest {
    DNode head;
    DNode tail;
    void insertHead(int data){
        DNode node = new DNode(data);
        if (head != null) {
            head.pre = node;
        }
        node.next = head;
        head = node;
    }
    void insertPos(int data,int pos) {
        if (pos == 0) {
            insertHead(data);
        }else{
            DNode node = new DNode(data);
            DNode cur = head;
            for (int i = 1; i < pos; i++) {
                cur = cur.next;
            }
            node.pre = cur;
            cur.next = node;
        }
    }
    void deleteHead(){
        head = head.next;
        head.pre = null;
    }
    void deletePos(int pos) {
        if (pos == 0) {
            deleteHead();
        }else {
            DNode cur = head;
            for (int i = 1; i < pos; i++) {
                cur = cur.next;
            }
            if (cur.next.next != null){
                cur.next.next.pre = cur;
            }
            cur.next = cur.next.next;
        }
    }
    void print(){
        DNode cur = head;
        while (cur != null) {
            System.out.print(cur.data+" ");
            cur = cur.next;
        }
        System.out.println();
    }
    DNode find(int data) {
        DNode cur = head;
        while (cur != null) {
            if (cur.data == data) {
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

}
class DNode {
    int data;
    DNode next;
    DNode pre;
    public DNode(int data) {
        this.data = data;
        this.next = null;
        this.pre = null;
    }
}

class  DoubleLinked {
    public static void main(String[] args) {
        DoubleLinkedTest dlt = new DoubleLinkedTest();
        dlt.insertHead(1);
        dlt.insertHead(2);
        dlt.insertHead(3);
        dlt.print();
        dlt.insertPos(4,3);
        dlt.print();
        dlt.deletePos(3);
        dlt.print();
        dlt.deleteHead();
        dlt.print();
    }
}
