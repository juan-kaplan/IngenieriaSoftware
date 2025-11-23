package org.udesa.giftcards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@Entity
public class    GiftCard extends ModelEntity {
    public static final String CargoImposible = "CargoImposible";
    public static final String InvalidCard = "InvalidCard";

    @Column(unique = true)
    private String cardId;

    @Column(nullable = false)
    private int balance;

    @Column
    private String owner;

    @OneToMany(mappedBy = "giftCard", cascade = ALL, orphanRemoval = true)
    private List<Charge> charges = new ArrayList<>();

    public GiftCard() {
    }

    public GiftCard(String cardId, int initialBalance ) {
        this.cardId = cardId;
        this.balance = initialBalance;
    }

    public GiftCard charge( int anAmount, String description ) {
        if ( !owned() || ( balance - anAmount < 0 ) ) throw new RuntimeException( CargoImposible );

        balance -= anAmount;
        charges.add(new Charge(anAmount, description, this));
        return this;
    }

    public GiftCard redeem( String newOwner ) {
        if ( owned() ) throw new RuntimeException( InvalidCard );

        owner = newOwner;
        return this;
    }

    // proyectors
    public boolean owned() {                            return owner != null;                   }
    public boolean isOwnedBy( String aPossibleOwner ) { return owner.equals( aPossibleOwner );  }

    // accessors
//    public String id() {            return id;      }
//    public int balance() {          return balance; }
    public List<String> charges() { return charges.stream()
            .map(Charge::getDescription)
            .collect(Collectors.toList()); }

}
