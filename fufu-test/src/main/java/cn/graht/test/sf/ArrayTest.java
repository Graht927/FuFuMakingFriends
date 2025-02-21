package cn.graht.test.sf;

/**
 * @author GRAHT
 */

public class ArrayTest {
    int size;
    int index;
    int[] data;
    public ArrayTest(int size) {
        this.size = size; //数组总长度
        data = new int[size]; //数组
        index = 0; //数组当前元素个数
    }
    public void add(int value,int index) {
        if (index < 0 || index > size) {
            throw new ArrayIndexOutOfBoundsException("越界");
        }

        if (this.index + 1 > this.size) {
            //扩容
            int[] newData = new int[this.size * 2];
            for (int i = 0; i < this.data.length; i++) {
                newData[i] = data[i];
            }
            this.data = newData;
            this.size = this.size * 2;
        }

        for (int i = this.size - 1; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = value;
        this.index++;
    }
    public void remove(int index) {
        if (index < 0 || index > size) {
            throw new ArrayIndexOutOfBoundsException("越界");
        }
        for (int i = index; i < this.size - 1; i++) {
            data[i] = data[i + 1];
        }
    }
    public void print() {
        for (int i = 0; i < this.index; i++) {
            System.out.println(data[i]);
        }
        System.out.println(this.size);
    }
    public static void main(String[] args) {
        ArrayTest arrayTest = new ArrayTest(10);
        for (int i = 0; i < 10; i++) {
            arrayTest.add(i, i);
        }
        arrayTest.add(62,5);
        arrayTest.print();
    }
}
