import java.lang.reflect.Array;
import java.util.ArrayList;

public class Solution {
    ArrayList<int[]> swaps;
    int generation;

    Solution(int size) {
        swaps = new ArrayList<int[]>(size);
        generation = 1;
    }
}
