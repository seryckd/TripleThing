package ds.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created on 2020-08-03.
 */
public class CalculatePermutationsTest {

    @Test
    public void calculate() {
        List<Integer> t1 = new ArrayList<>();
        t1.add(1);
        t1.add(2);

        List<List<Integer>> result = CalculatePermutations.calculate(t1);
        System.out.println(result);
        t1.add(3);
        result = CalculatePermutations.calculate(t1);

        System.out.println(result);
        List<Integer> t2 = new ArrayList<>();
        t2.add(4);
        t2.add(4);
        t2.add(7);
        result = CalculatePermutations.calculate(t2);
        System.out.println(result);

    }

}