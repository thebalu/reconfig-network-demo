package demo;

import static java.lang.Integer.parseInt;

public class Demands {
    public int[][] demands;

    public Demands(int s) {
        demands = new int[s][s];

        for (int i = 0; i < s; i++) {
            for (int j = 0; j < s; j++) {
                if (i == j) {
                    demands[i][j] = 0;
                } else {
                    demands[i][j] = (i + 1) * (j + 1);
                }
            }
        }
    }

    public int getDemand(String v1, String v2) {
        if (v1.equals(Util.CENTER) || v2.equals(Util.CENTER)) {
            return 0;
        }
        int from = Integer.parseInt(v1.substring(1));
        int to = Integer.parseInt(v2.substring(1));
        return getDemand(from, to);
    }

    public int getDemand(int v1, int v2) {
        return demands[v1][v2];
    }

}
