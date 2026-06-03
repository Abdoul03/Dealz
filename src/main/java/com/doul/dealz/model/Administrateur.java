package com.doul.dealz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Administrateur de la plateforme Dealz.
 * Hérite de User (@Inheritance) et dispose de droits étendus
 * de gestion des utilisateurs, annonces et signalements.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "administrateurs")
@PrimaryKeyJoinColumn(name = "user_id")
public class Administrateur extends User {

    private String niveau;

    private LocalDate dateCreation;


    // ── Méthodes métier ───────────────────────────────────────────

    /** Suspend un utilisateur en désactivant son compte. */
    public void suspendreUser(User user) {
        user.setActive(false);
    }

    /** Réactive un compte suspendu. */
    public void reactiverUser(User user) {
        user.setActive(true);
    }

    /** Traite un signalement en changeant son statut. */
    public void validerSignalement(Signalement signalement, String decision) {
        signalement.setStatut(decision);
    }
}
