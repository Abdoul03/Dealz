package com.doul.dealz.model;

import com.doul.dealz.model.enums.StatutTransaction;
import com.doul.dealz.model.enums.TypeAvis;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Avis laissé par un utilisateur sur un autre après une transaction confirmée.
 * Règle métier : un avis ne peut être créé que si la transaction est CONFIRMEE.
 * Maximum 2 avis par transaction (1 de l'acheteur, 1 du vendeur).
 *
 * Relations :
 *   ManyToOne → User (auteur)      : N avis rédigés par 1 utilisateur
 *   ManyToOne → User (cible)       : N avis reçus par 1 utilisateur
 *   ManyToOne → Transaction        : N avis liés à 1 transaction (max 2)
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "avis",
    // Contrainte : un utilisateur ne peut laisser qu'un seul avis par transaction
    uniqueConstraints = @UniqueConstraint(columnNames = {"auteur_id", "transaction_id"})
)
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Note de 1 à 5 étoiles. */
    @Column(nullable = false)
    private int note;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    private LocalDateTime dateAvis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAvis typeAvis;

    // ── ManyToOne : N Avis → 1 User (auteur) ──────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private User auteur;

    // ── ManyToOne : N Avis → 1 User (cible évaluée) ───────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cible_id", nullable = false)
    private User cible;

    // ── ManyToOne : N Avis → 1 Transaction ────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    // ── Méthodes métier ────────────────────────────────────────────

    /**
     * Valide la cohérence de l'avis avant persistance.
     * Vérifie que la transaction est bien CONFIRMEE.
     */
    @PrePersist
    public void valider() {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5.");
        }
        if (transaction == null || transaction.getStatut() != StatutTransaction.CONFIRMEE) {
            throw new IllegalStateException("Un avis ne peut être laissé que sur une transaction confirmée.");
        }
        this.dateAvis = LocalDateTime.now();
        // Déclenche le recalcul de la note moyenne de la cible
        cible.recalculerNoteMoyenne();
    }

}
