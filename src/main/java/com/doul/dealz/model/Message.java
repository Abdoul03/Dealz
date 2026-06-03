package com.doul.dealz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Message échangé entre deux utilisateurs dans la messagerie intégrée.
 * Les numéros de téléphone ne sont jamais exposés dans les messages.
 *
 * Relations :
 *   ManyToOne → User (expediteur)   : N messages envoyés par 1 utilisateur
 *   ManyToOne → User (destinataire) : N messages reçus par 1 utilisateur
 *   ManyToOne → Annonce             : messages liés à une annonce précise
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi;

    private boolean isRead = false;

    // ── ManyToOne : N Messages → 1 User (expéditeur) ──────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private User expediteur;

    // ── ManyToOne : N Messages → 1 User (destinataire) ────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private User destinataire;

    // ── ManyToOne : N Messages → 1 Annonce (contexte) ─────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annonce_id")
    private Annonce annonce;

    // ── Méthodes métier ────────────────────────────────────────────
    public void marquerLu() {
        this.isRead = true;
    }
}
