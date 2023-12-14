package Entity;

public class Collection {
    private int u_id;
    private int c_id;
    private double floorPrice;
    private String name;
    private String shopName;
    private String platformName;

    public Collection(int u_id, int c_id, double floorPrice, String name, String shopName, String platformName) {
        this.u_id = u_id;
        this.c_id = c_id;
        this.floorPrice = floorPrice;
        this.name = name;
        this.shopName = shopName;
        this.platformName = platformName;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public int getC_id() {
        return c_id;
    }

    public void setC_id(int c_id) {
        this.c_id = c_id;
    }

    public double getFloorPrice() {
        return floorPrice;
    }

    public void setFloorPrice(double floorPrice) {
        this.floorPrice = floorPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
}
