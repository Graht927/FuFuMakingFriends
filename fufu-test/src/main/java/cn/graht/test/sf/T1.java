package cn.graht.test.sf;

/**
 * @author GRAHT
 */

public class T1 {
    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            boolean r = isPowerOfTwo(i);
            if (r) {
                System.out.println(i);
            }
        }
        System.out.println("============================");
        for (int i = 0; i < 10000; i++) {
            if (i>1 && (i & (i - 1))==0) {
                System.out.println(i);
            }
        }
    }
    private static boolean isPowerOfTwo(int param) {
        if (param <=1) {
            return false;
        }
        int n = param;
        while (n > 1) {
            if (n%2==0){
                n = n/2;
            }else {
                return false;
            }
        }
        return true;
    }
}
