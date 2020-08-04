package ds.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class CalculatePermutations {
    public CalculatePermutations() {
    }

    public static List<List<Integer>> calculate(List<Integer> inputSeq) {
        List<List<Integer>> result = new ArrayList<>();
        ArrayList<CalculatePermutations.DirectedInt> directedSeq = new ArrayList<>();

        for (Integer input : inputSeq) {
            directedSeq.add(new CalculatePermutations.DirectedInt(input));
        }

        ArrayList<Integer> perm = new ArrayList<>();

        for (DirectedInt di : directedSeq) {
            perm.add(di.value);
        }

        result.add(perm);

        for(int largeMobile = getPosLargestMobile(directedSeq); largeMobile != -1; largeMobile = getPosLargestMobile(directedSeq)) {
            CalculatePermutations.DirectedInt di = (CalculatePermutations.DirectedInt)directedSeq.remove(largeMobile);
            if (di.faceLeft) {
                directedSeq.add(largeMobile - 1, di);
            } else {
                directedSeq.add(largeMobile + 1, di);
            }

            perm = new ArrayList<>();

            CalculatePermutations.DirectedInt d;
            for(Iterator var7 = directedSeq.iterator(); var7.hasNext(); perm.add(d.value)) {
                d = (CalculatePermutations.DirectedInt)var7.next();
                if (d.value > di.value) {
                    d.faceLeft = !d.faceLeft;
                }
            }

            result.add(perm);
        }

        return result;
    }

    private static int getPosLargestMobile(List<CalculatePermutations.DirectedInt> sequence) {
        int largestMobile = -1;
        int largestMobileIdx = -1;

        for(int i = 0; i < sequence.size(); ++i) {
            CalculatePermutations.DirectedInt di = (CalculatePermutations.DirectedInt)sequence.get(i);
         //   int adjacentIdx = false;
            int adjacentIdx;
            if (di.faceLeft) {
                if (i <= 0) {
                    continue;
                }

                adjacentIdx = i - 1;
            } else {
                if (i >= sequence.size() - 1) {
                    continue;
                }

                adjacentIdx = i + 1;
            }

            if (di.value > ((CalculatePermutations.DirectedInt)sequence.get(adjacentIdx)).value && di.value > largestMobile) {
                largestMobile = di.value;
                largestMobileIdx = i;
            }
        }

        return largestMobileIdx;
    }

    private static class DirectedInt {
        Integer value;
        boolean faceLeft = true;

        public DirectedInt(Integer value) {
            this.value = value;
        }
    }
}
