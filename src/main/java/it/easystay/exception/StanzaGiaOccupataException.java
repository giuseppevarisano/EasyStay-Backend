package it.easystay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Forza il ritorno del codice 409
public class StanzaGiaOccupataException extends RuntimeException {
    public StanzaGiaOccupataException(Long stanzaId) {
        super("La stanza con ID " + stanzaId + " è già occupata per le date selezionate.");
    }
}