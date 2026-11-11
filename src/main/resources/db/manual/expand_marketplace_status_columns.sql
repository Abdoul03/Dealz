-- Existing databases may still store statut as an ENUM with the old values.
-- VARCHAR allows the new reservation and handover confirmation states.
ALTER TABLE annonces
    MODIFY COLUMN statut VARCHAR(30) NOT NULL;

ALTER TABLE transactions
    MODIFY COLUMN statut VARCHAR(30) NOT NULL;
