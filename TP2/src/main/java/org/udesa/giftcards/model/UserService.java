package org.udesa.giftcards.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ModelService< UserVault, UserRepository> {

    protected void updateData( UserVault existingObject, UserVault updatedObject ) {
        existingObject.setName( updatedObject.getName() );
        existingObject.setPassword( updatedObject.getPassword() );
    }

    @Transactional( readOnly = true )
    public UserVault findByName( String name ) {
        return repository.findByName( name )
                .orElseThrow( () ->new RuntimeException( GifCardFacade.InvalidUser ));
    }
}
