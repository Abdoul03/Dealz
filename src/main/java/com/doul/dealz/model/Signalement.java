package com.doul.dealz.model;

import com.doul.dealz.model.enums.TypeSignalement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Signalement émis par un utilisateur contre une annonce ou un autre utilisateur.
 * Traité par l'équipe de modération (Administrateur).
 *
 * Relations :
 *   ManyToOne → User (signaleur)         : N signalements émis par 1 utilisateur
 *   ManyToOne → Annonce (annonceCiblee)  : N signalements contre 1 annonce (nullable)
 *   ManyToOne → User (userCible)         : N signalements contre 1 utilisateur (nullable)
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "signalements")
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSignalement motif;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Statut du traitement : "EN_ATTENTE", "EN_COURS", "RESOLU", "REJETE" */
    @Column(nullable = false)
    private String statut = "EN_ATTENTE";

    private LocalDateTime dateSignalement;

    // ── ManyToOne : N Signalements → 1 User (qui signale) ─────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signaleur_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"annoncesPubliees","transactionsEffectuees","messagesEnvoyes","messagesRecus","avisRecus","avisRediges","signalementsEmis","password"})
    private User signaleur;

    // ── ManyToOne : N Signalements → 1 Annonce (optionnel) ────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annonce_ciblee_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"vendeur","transactions","signalements","urlImages"})
    private Annonce annonceCiblee;

    // ── ManyToOne : N Signalements → 1 User ciblé (optionnel) ─────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_cible_id")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"annoncesPubliees","transactionsEffectuees","messagesEnvoyes","messagesRecus","avisRecus","avisRediges","signalementsEmis","password"})
    private User userCible;

    /** Signalement contre un utilisateur. */
    public Signalement(TypeSignalement motif, String description,
                       User signaleur, User userCible) {
        this.motif = motif;
        this.description = description;
        this.signaleur = signaleur;
        this.userCible = userCible;
        this.dateSignalement = LocalDateTime.now();
    }

    // ── Méthodes métier ────────────────────────────────────────────
    public void traiter(String decision) {
        this.statut = decision;
    }
}
