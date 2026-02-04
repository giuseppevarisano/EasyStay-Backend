-- data.sql (VERSIONE MIGLIORATA)

-- 1. ADMIN di default (sempre presente)
INSERT INTO utenti (email, nome, password, ruolo)
SELECT 'admin@easystay.it', 'Admin', '$2a$10$t.upuAP7Rh.EGKeD3vLA6.9GycgrldjuxRpIQxBm.94K3iwa0ODjO', 'ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM utenti WHERE email = 'admin@easystay.it');

-- 2. Utente di test (solo se non esiste)
INSERT INTO utenti (email, nome, password, ruolo)
SELECT 'ospite@test.it', 'Giuseppe', '$2a$10$t.upuAP7Rh.EGKeD3vLA6.9GycgrldjuxRpIQxBm.94K3iwa0ODjO', 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM utenti WHERE email = 'ospite@test.it');

-- 3. Case vacanza (solo se tabella vuota)
INSERT INTO casevacanza (nome, indirizzo, citta, prezzo_notte, version)
SELECT 'Villa Paradiso', 'Via Mare 1', 'Olbia', 200.0, 1
    WHERE NOT EXISTS (SELECT 1 FROM casevacanza WHERE nome = 'Villa Paradiso');

INSERT INTO casevacanza (nome, indirizzo, citta, prezzo_notte, version)
SELECT 'Baita Relax', 'Via Monti 10', 'Aosta', 120.0, 1
    WHERE NOT EXISTS (SELECT 1 FROM casevacanza WHERE nome = 'Baita Relax');

INSERT INTO casevacanza (nome, indirizzo, citta, prezzo_notte, version)
SELECT 'Loft Urbano', 'Via Torino 5', 'Milano', 95.0, 1
    WHERE NOT EXISTS (SELECT 1 FROM casevacanza WHERE nome = 'Loft Urbano');