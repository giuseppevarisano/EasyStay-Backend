-- 1. POPOLAMENTO UTENTI
-- Usiamo IGNORE: se l'email (che è UNIQUE) esiste già, non fa nulla.
INSERT IGNORE INTO utenti (id, email, nome, password, ruolo) VALUES
(1, 'admin@easystay.it', 'Admin', '$2a$10$t.upuAP7Rh.EGKeD3vLA6.9GycgrldjuxRpIQxBm.94K3iwa0ODjO', 'ADMIN'),
(2, 'ospite@test.it', 'Giuseppe', '$2a$10$t.upuAP7Rh.EGKeD3vLA6.9GycgrldjuxRpIQxBm.94K3iwa0ODjO', 'USER');

-- 2. POPOLAMENTO CASEVACANZA
-- Qui usiamo l'ID come chiave di controllo
INSERT IGNORE INTO casevacanza (id, nome, indirizzo, citta, prezzo_notte, version) VALUES
(1, 'Villa Paradiso', 'Via Mare 1', 'Olbia', 200.0, 1),
(2, 'Baita Relax', 'Via Monti 10', 'Aosta', 120.0, 1),
(3, 'Loft Urbano', 'Via Torino 5', 'Milano', 95.0, 1);

-- 3. POPOLAMENTO PRENOTAZIONI
-- Colleghiamo gli ID che abbiamo forzato sopra
INSERT IGNORE INTO prenotazioni (id, data_inizio, data_fine, casa_id, utente_id) VALUES
(1, '2026-06-01', '2026-06-08', 1, 2),
(2, '2026-07-15', '2026-07-22', 2, 2);