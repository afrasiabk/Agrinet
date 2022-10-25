package alast.hm.Model;

public class location {
    private String address;
    private double lati;
    private double longi;

    public location(String address, double lati, double longi) {
        this.address = address;
        this.lati = lati;
        this.longi = longi;
    }

    public location() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
