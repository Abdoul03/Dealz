package com.doul.dealz.model.enums;

public enum StatutTransaction {
    EN_COURS,
    CONFIRMATION_PARTIELLE,
    /** Valeur conservée pour lire les anciennes transactions avant migration des données. */
    SEQUESTRE,
    CONFIRMEE,
    ANNULEE
}
