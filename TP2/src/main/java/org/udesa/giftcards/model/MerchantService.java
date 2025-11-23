package org.udesa.giftcards.model;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MerchantService extends ModelService< Merchant, MerchantRepository> {

    protected MerchantService(MerchantRepository repository) {
        super(repository);
    }

    @Transactional( readOnly = true )
    public boolean existsByName( String name ) {
        return repository.existsByName(name);
    }

    @Transactional
    public void deleteByNameStartingWith( String prefix ) {
        repository.deleteByNameStartingWith(prefix);
    }

}
