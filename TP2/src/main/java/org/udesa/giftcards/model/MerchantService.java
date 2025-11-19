package org.udesa.giftcards.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantService extends ModelService< Merchant, MerchantRepository> {

    protected void updateData( Merchant existingObject, Merchant updatedObject ) {
        existingObject.setName( updatedObject.getName() );
    }

    @Transactional( readOnly = true )
    public Merchant findByName( String name ) {
        return repository.findByName( name )
                .orElseThrow( () ->new RuntimeException( GifCardFacade.InvalidMerchant ));
    }

}
