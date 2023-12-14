package Entity;

public class DetailedCommodity extends Commodity {
    //    private int id;
//    private String name;
//    private int price;
//    private String shop;
//
//    private String platform;
    private String category;
    private String description;
    private String productionDate;
    private String shopAddress;


    public DetailedCommodity(int id, String commodityName, double price, String shopName, String platformName, String origin, String category, String description, String produceDate, String address) {
        super(id,commodityName,price,shopName,platformName,origin);
        this.category = category;
        this.description = description;
        this.productionDate = produceDate;
        this.shopAddress = address;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getProductionDate() {
        return productionDate;
    }


    public String getShopAddress() {
        return shopAddress;
    }


}
