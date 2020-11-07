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
                    demands[i][j] = (i + 1) * (j + 2);
                }
            }
        }
    }

    public int getSumDemandFrom(String v) {
        int from = Integer.parseInt(v.substring(1));
        return getSumDemandFrom(from);
    }

    public int getSumDemandTo(String v) {
        int to = Integer.parseInt(v.substring(1));
        return getSumDemandTo(to);
    }
    public int getSumDemandFrom(int v) {
        int sumDemand = 0;
        for(int i=0; i<demands.length; i++) {
            sumDemand += demands[v][i];
        }
        return sumDemand;
    }


    public int getSumDemandTo(int v) {
        int sumDemand = 0;
        for(int i=0; i<demands.length; i++) {
            sumDemand += demands[i][v];
        }
        return sumDemand;
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
