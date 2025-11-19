package org.udesa.giftcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.udesa.giftcards.model.GifCardFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class GiftCardFacadeController {

    GifCardFacade systemFacade;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String user, @RequestParam String pass) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", systemFacade.login(user, pass).toString());
        return ResponseEntity.ok( response );
    }

    @PostMapping("/{cardId}/redeem")
    public ResponseEntity<String> redeemCard(@RequestHeader("Authorization") String header, @PathVariable String cardId ) {
        systemFacade.redeem(UUID.fromString(header), cardId);
        return ResponseEntity.ok( "" );
    }

    @GetMapping("/{cardId}/balance")
    public ResponseEntity<Map<String, Object>> balance( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {
        Map<String, Object> response = new HashMap<>();
        response.put("balance", systemFacade.balance(UUID.fromString(header), cardId) );
        return ResponseEntity.ok( response );
    }

    @GetMapping("/{cardId}/details")
    public ResponseEntity<Map<String, Object>> details( @RequestHeader("Authorization") String tokenHeader, @PathVariable String cardId ) {
        Map<String, Object> response = new HashMap<>();
        response.put("details", systemFacade.details(UUID.fromString(tokenHeader), cardId));
        return ResponseEntity.ok( response );
    }

    @PostMapping("/{cardId}/charge") public ResponseEntity<String> charge( @RequestParam String merchant, @RequestParam int amount, @RequestParam String description, @PathVariable String cardId ) {
        systemFacade.charge(merchant, cardId, amount, description);
        return ResponseEntity.ok( "" );
    }
}
