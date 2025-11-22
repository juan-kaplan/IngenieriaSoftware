package org.udesa.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MerchantServiceTest extends ModelServiceTest<Merchant, MerchantService>{

    protected Merchant newSample() {
        return EntityDrawer.someMerchant();
    }

    protected Merchant updateSample(Merchant sample) {
        sample.setName("Test_Merchant");
        return sample;
    }

    private Merchant savedMerchant() { return service.save( newSample());}

    @Test public void testExistsByName() {
        Merchant merchant = savedMerchant();
        assertTrue(service.existsByName(merchant.getName()));
    }
}
