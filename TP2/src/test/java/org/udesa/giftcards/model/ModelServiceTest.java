package org.udesa.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class ModelServiceTest<M extends ModelEntity, S extends ModelService<M, ? extends JpaRepository<M, Long>>>{
    @Autowired protected S service;

    protected abstract M newSample() ;
    protected abstract M updateSample( M sample );
    protected M savedSample() {
        return service.save( newSample() );
    }

    @Test public void testEntitySave() {
        M model = newSample();
        M retrieved = service.save( model );
        assertNotNull( retrieved.getId() );
        assertNotNull( model.getId() );
        assertEquals( retrieved, model );
    }

    @Test public void testEntityUpdate() {
        M model = newSample();

        updateSample( model );
        service.save( model );
        M retrieved = service.getById( model.getId() );
        assertEquals( model, retrieved );
    }

    @Test public void testDeletionByObject() {
        M model = savedSample();

        service.delete( model );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testDeletionById() {
        M model = savedSample();

        service.delete( model.getId() );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testDeletionByProxy() throws Exception {
        M model = savedSample();
        M proxy = service.getModelClass().getConstructor().newInstance();
        proxy.setId( model.getId() );

        service.delete( proxy );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );
    }

    @Test public void testFindAll() {
        M model = savedSample();
        List list = service.findAll();
        assertFalse( list.isEmpty() );
        assertTrue( list.contains( model ) );
    }
}
