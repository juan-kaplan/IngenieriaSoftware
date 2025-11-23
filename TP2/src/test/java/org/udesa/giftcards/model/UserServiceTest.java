package org.udesa.giftcards.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest extends ModelServiceTest<UserVault, UserService> {

    @AfterAll
    public void cleanUp(){
        service.deleteByNameStartingWith( EntityDrawer.UserPrefix );
    }

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

    @Test public void testDeleteByNameStartingWith(){
        UserVault user1 = savedUser();
        UserVault user2 = savedUser();

        service.deleteByNameStartingWith( EntityDrawer.UserPrefix );
        assertThrows( RuntimeException.class, () -> service.getById( user1.getId() ) );
        assertThrows( RuntimeException.class, () -> service.getById( user2.getId() ) );
    }
}
