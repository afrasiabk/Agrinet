package alast.hm.Data;

import alast.hm.Model.Category;
import alast.hm.Model.Product;

public class DataSingleton {

    public static Category[] l1s;
    public static Category[][] l2s;
    public static Product[][][] products;
    public static Category[] l2s_only;
    public static Product[][] l2ps;

    private static DataSingleton dataSingleton;

    public static void getDataObject(){
        if (dataSingleton == null){
            dataSingleton = new DataSingleton();
        }
    }

    public static void dest(){
        l1s = null;
        l2s = null;
        products = null;
        l2s_only = null;
        l2ps = null;
        dataSingleton = null;
    }
}
