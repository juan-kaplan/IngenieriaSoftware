package org.udesa.giftcards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Merchant extends ModelEntity{
    @Column(unique=true) private String name;

    public Merchant() {
    }

    public Merchant(String name){
        this.name = name;
    }
}
