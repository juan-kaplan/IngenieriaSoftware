package org.udesa.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceTest extends ModelServiceTest<UserVault, UserService> {

    protected UserVault newSample() {
        return EntityDrawer.someUser();
    }

    protected UserVault updateSample(UserVault sample) {
        sample.setName("Test_JohnPork");
        return sample;
    }

    private UserVault savedUser() {
        return service.save( newSample() );
    }

    @Test public void testFindByName(){
        UserVault user = savedUser();
        assertEquals(user, service.findByName( user.getName()));
    }
}
