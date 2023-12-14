package Entity;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-02
 **/
public class Platform {
    private Integer id;
    private String name;
    public Platform(Integer id, String name) {
        this.id = id;
        this.name = name;
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
}
