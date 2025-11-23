package org.udesa.giftcards.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserVault, Long> {
    Optional<UserVault> findByName(String name );
    void deleteByNameStartingWith( String prefix );
}
