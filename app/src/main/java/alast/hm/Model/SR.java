package alast.hm.Model;

public class SR {
    private int score;
    private int l1, l2, l3;

    public SR() {
    }

    public SR(int score, int l1, int l2, int l3) {
        this.score = score;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getL1() {
        return l1;
    }

    public void setL1(int l1) {
        this.l1 = l1;
    }

    public int getL2() {
        return l2;
    }

    public void setL2(int l2) {
        this.l2 = l2;
    }

    public int getL3() {
        return l3;
    }

    public void setL3(int l3) {
        this.l3 = l3;
    }
}
