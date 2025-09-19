package org.udesa.tp1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SystemFacadeTest {

    SystemFacade systemFacade;

    @BeforeEach
    public void beforeEach() { systemFacade = systemFacade();}

    @Test
    public void test01SystemAcceptsValidUser() {
        assertTrue(systemFacade.isTokenValid(systemFacade.login("John", "Jpass")));
    }

    @Test
    public void test02SystemRejectsInvalidUser() {
        assertThrowsLike( () -> systemFacade.login( "", ""), SystemFacade.InvalidLoginCredentialsError);
    }

    private SystemFacade systemFacade() {
        return new SystemFacade(Map.of( "John", "Jpass", "Paul", "Ppass" ),
                                List.of(new GiftCard(100), new GiftCard(250)));
    }

    private void assertThrowsLike(Executable executable, String message ) {
        assertEquals( message,
                assertThrows( Exception.class, executable )
                        .getMessage() );
    }
}
