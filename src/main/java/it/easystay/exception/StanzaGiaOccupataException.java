package it.easystay.exception;

// Estendiamo RuntimeException così non siamo obbligati a mettere try-catch ovunque
public class StanzaGiaOccupataException extends RuntimeException {

    public StanzaGiaOccupataException(Long stanzaId) {
        super("La stanza con ID " + stanzaId + " è già occupata per le date selezionate.");
    }
}