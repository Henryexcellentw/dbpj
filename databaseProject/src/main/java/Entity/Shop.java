package Entity;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-02
 **/
public class Shop {
    private Integer id;
    private String name;
    private String address;
    private Integer ownerId;
    public Shop(Integer id, String name, String address, Integer ownerId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.ownerId = ownerId;
    }
    public Shop() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
}
