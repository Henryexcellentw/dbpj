package InterfaceImplementation;

import Entity.DetailedCommodity;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.*;

class CollectionInterfaceTest {
    @Test
    public void testGetCollectionDetailedCommodityByUserId(){
        ArrayList<DetailedCommodity>  result = CollectionInterface.getCollectionDetailedCommoditiesByUserId(10000510);
        System.out.println(result.size());
    }

}