package ds.util;

public class HeapSort {


    public HeapSort() {
    }

    public static void execute(Comparable<?>[] a) {
        int i;
        for (i = a.length / 2; i >= 0; --i) {
            percDown(a, i, a.length);
        }

        for (i = a.length - 1; i > 0; --i) {
            swapReferences(a, 0, i);
            percDown(a, 0, i);
        }

    }

    private static int leftChild(int i) {
        return 2 * i + 1;
    }

    private static void percDown(Comparable[] a, int i, int n) {
        int child;
        Comparable tmp;
        for (tmp = a[i]; leftChild(i) < n; i = child) {
            child = leftChild(i);
            if (child != n - 1 && a[child].compareTo(a[child + 1]) < 0) {
                ++child;
            }

            if (tmp.compareTo(a[child]) >= 0) {
                break;
            }

            a[i] = a[child];
        }

        a[i] = tmp;
    }

    public static final void swapReferences(Object[] a, int index1, int index2) {
        Object tmp = a[index1];
        a[index1] = a[index2];
        a[index2] = tmp;
    }

    public static void main(String[] args) {
        Integer[] a = new Integer[]{9, 8, 7, 6, 5, 4, 3, 2, 1};
        Integer[] var5 = a;
        int var4 = a.length;

        Integer i;
        int var3;
        for (var3 = 0; var3 < var4; ++var3) {
            i = var5[var3];
            System.out.println(i);
        }

        execute(a);
        var5 = a;
        var4 = a.length;

        for (var3 = 0; var3 < var4; ++var3) {
            i = var5[var3];
            System.out.println(i);
        }

    }
}