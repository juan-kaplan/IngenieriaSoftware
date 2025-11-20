package org.udesa.giftcards.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter @Setter
public class Charge extends ModelEntity {

    @Column(nullable = false) // al parecer esto se puede, creo q no hace falta aclarar el name xq defaultea al nombre de la var, igual q @Table
    private int amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private String description;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "giftcard_id", nullable = false)
    private GiftCard giftCard;

    public Charge() {
    }

    public Charge(int amount, String description, GiftCard giftCard) {
        this.amount = amount;
        this.description = description;
        this.giftCard = giftCard;
        this.createdAt = LocalDateTime.now();
    }
}
