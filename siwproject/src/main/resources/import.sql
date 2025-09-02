-- 1. Inserimento categorie
INSERT INTO categoria (id, nome) VALUES (1, 'Arredamento');
INSERT INTO categoria (id, nome) VALUES (2, 'Illuminazione');
INSERT INTO categoria (id, nome) VALUES (3, 'Accessori Outdoor');
INSERT INTO categoria (id, nome) VALUES (4, 'Tecnologia');
INSERT INTO categoria (id, nome) VALUES (5, 'Cucina');
INSERT INTO categoria (id, nome) VALUES (6, 'Sport e Tempo Libero');
INSERT INTO categoria (id, nome) VALUES (7, 'Benessere');

-- 2. Inserimento prodotti
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (1, 'Sedia Ergonomica', 129.99, 'Sedia da ufficio con supporto lombare regolabile');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (2, 'Lampada da Tavolo LED', 39.90, 'Lampada moderna a luce fredda e calda con intensità regolabile');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (3, 'Zaino Tecnico 30L', 79.50, 'Zaino resistente all’acqua, ideale per trekking e viaggi');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (4, 'Auricolari Wireless', 59.00, 'Auricolari Bluetooth con custodia di ricarica');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (5, 'Borraccia Termica 750ml', 24.99, 'Borraccia in acciaio inox, mantiene freddo/caldo fino a 12 ore');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (6, 'Tastiera Meccanica RGB', 89.00, 'Tastiera da gaming con retroilluminazione personalizzabile');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (7, 'Cuscino Memory Foam', 45.90, 'Cuscino ortopedico per un sonno confortevole');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (8, 'Ombrellone da Giardino', 149.00, 'Grande ombrellone rotondo con base in acciaio');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (9, 'Set Pentole Antiaderenti', 199.00, 'Batteria di pentole 8 pezzi rivestite in pietra');
INSERT INTO prodotto (id, nome, prezzo, descrizione) VALUES (10, 'Bici da Città', 349.00, 'Bicicletta leggera con cambio Shimano a 6 velocità');

-- 3. Popolamento tabella di join (prodotto_categorie)
-- Sedia Ergonomica → Arredamento
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (1, 1);

-- Lampada da Tavolo LED → Illuminazione
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (2, 2);

-- Zaino Tecnico 30L → Accessori Outdoor
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (3, 3);

-- Auricolari Wireless → Tecnologia
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (4, 4);

-- Borraccia Termica 750ml → Accessori Outdoor
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (5, 3);

-- Tastiera Meccanica RGB → Tecnologia
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (6, 4);

-- Cuscino Memory Foam → Benessere
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (7, 7);

-- Ombrellone da Giardino → Arredamento + Accessori Outdoor
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (8, 1);
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (8, 3);

-- Set Pentole Antiaderenti → Cucina
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (9, 5);

-- Bici da Città → Sport e Tempo Libero
INSERT INTO prodotto_categorie (prodotti_id, categorie_id) VALUES (10, 6);
