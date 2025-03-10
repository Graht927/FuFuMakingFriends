package cn.graht.test.sf.rec;

/**
 * @author GRAHT
 */

public class Fibonacci {
    //1 1 2 3 5 8
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println(fibonacci(i));
        }
    }
    public static int fibonacci(int n){
        if (n <= 2 ) return 1;
        return fibonacci(n-1) + fibonacci(n-2);
    }


}
