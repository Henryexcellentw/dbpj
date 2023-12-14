package Entity;

import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-02
 **/
public class Price {
    private Integer cId;
    private double price;
    private String time;
    public Price(Integer cId, double price, String time) {
        this.cId = cId;
        this.price = price;
        this.time = time;
    }


    public static double getMaxPrice(ArrayList<Price> priceList){
        double maxPrice = 0;
        for (Price price : priceList) {
            if (price.getPrice() > maxPrice) {
                maxPrice = price.getPrice();
            }
        }
        return maxPrice;
    }

    public static double getMinPrice(ArrayList<Price> priceList){
        double minPrice = 1000000000;
        for (Price price : priceList) {
            if (price.getPrice() < minPrice) {
                minPrice = price.getPrice();
            }
        }
        return minPrice;
    }

    public static double getDiffPrice(ArrayList<Price> priceList){
        double maxPrice = Price.getMaxPrice(priceList);
        double minPrice = Price.getMinPrice(priceList);
        return maxPrice - minPrice;
    }

    public Integer getCId() {
        return cId;
    }

    public void setCId(Integer cId) {
        this.cId = cId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
