package cn.graht.test.sf.xiaomi;

import java.util.List;

/**
 * @author GRAHT
 */

public class Test1 {

    public static void main(String[] args) {
        List<int[]> operations = List.of(new int[]{0, 0, 1}, new int[]{1, 2, 2}, new int[]{0, 2, 3}, new int[]{1, 0, 4});
        System.out.println(test1(3, operations));
    }

    private static int test1(int n, List<int[]> operations) { //O(n^2)
        int[][] matrix = new int[n][n];
        for (int[] operation : operations) {
            int type = operation[0];
            int index = operation[1];
            int val = operation[2];
            if (type == 0) {
                for (int i = 0; i < n; i++) {
                    matrix[index][i] = val;
                }
            } else if (type == 1) {
                for (int i = 0; i < n; i++) {
                    matrix[i][index] = val;
                }
            }
        }
        int sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum += matrix[i][j];
            }
        }
        return sum;
    }
}
