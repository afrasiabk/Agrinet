package alast.hm.Model;

public class L1L2P {
    private Category l1;
    private L2P[] l2PS;

    public L1L2P() {
    }

    public L1L2P(Category l1, L2P[] l2PS) {
        this.l1 = l1;
        this.l2PS = l2PS;
    }

    public Category getL1() {
        return l1;
    }

    public void setL1(Category l1) {
        this.l1 = l1;
    }

    public L2P[] getL2PS() {
        return l2PS;
    }

    public void setL2PS(L2P[] l2PS) {
        this.l2PS = l2PS;
    }
}
