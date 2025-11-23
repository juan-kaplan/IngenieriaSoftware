package org.udesa.giftcards.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends ModelService< UserVault, UserRepository > {

    public UserService(UserRepository repository) {
        super(repository);
    }

    @Transactional( readOnly = true )
    public UserVault findByName( String name ) {
        return repository.findByName( name )
                .orElseThrow( () ->new RuntimeException( GiftCardFacade.InvalidUser ));
    }

    @Transactional
    public void deleteByNameStartingWith( String prefix ) {
        repository.deleteByNameStartingWith( prefix );
    }


}
