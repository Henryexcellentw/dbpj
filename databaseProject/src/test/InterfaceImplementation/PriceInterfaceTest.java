package InterfaceImplementation;

import Entity.Price;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PriceInterfaceTest {
    @Test
    public void testGetPriceHistory(){
        try{
            ArrayList<Price> result = PriceInterface.getHistoryPrice(10001006, 0);
            for(Price price : result) {
                System.out.println(price.getTime());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}