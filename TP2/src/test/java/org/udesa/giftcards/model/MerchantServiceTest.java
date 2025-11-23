package org.udesa.giftcards.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MerchantServiceTest extends ModelServiceTest<Merchant, MerchantService>{

    @AfterAll
    public void cleanUp(){
        service.deleteByNameStartingWith( EntityDrawer.MerchantPrefix );
    }

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

    @Test public void testDeleteByNameStartingWith(){
        Merchant merchant1 = savedMerchant();
        Merchant merchant2 = savedMerchant();

        service.deleteByNameStartingWith( EntityDrawer.MerchantPrefix );
        assertThrows( RuntimeException.class, () -> service.getById( merchant1.getId() ) );
        assertThrows( RuntimeException.class, () -> service.getById( merchant2.getId() ) );
    }
}
