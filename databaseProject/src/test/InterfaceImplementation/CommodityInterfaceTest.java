package InterfaceImplementation;

import Entity.DetailedCommodity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CommodityInterfaceTest {
    @Test
    public void testGetAllCommoditiesByShopId(){
        ArrayList<DetailedCommodity> result = CommodityInterface.getAllCommoditiesByShopId(10000261);
        for(DetailedCommodity commodity : result){
            System.out.println(commodity.getName());
        }
    }
    @Test
    public void testGetAllCommodity(){
        ArrayList<DetailedCommodity> result = CommodityInterface.getAllCommodity();
        System.out.println(result.size());
    }
    @Test
    public void testGetAllCommoditiesByPlatformId(){
        ArrayList<DetailedCommodity> result = CommodityInterface.getAllCommoditiesByPlatformId(50);
        System.out.println(result.size());
    }
    @Test
    public void testGetCommodityByName(){
        ArrayList<DetailedCommodity> result = CommodityInterface.getCommodityByName("直接");
        System.out.println(result.size());
    }
}