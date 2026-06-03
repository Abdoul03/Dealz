package com.doul.dealz.model;

import com.doul.dealz.model.enums.TypeCompte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente tout utilisateur de la plateforme Dealz.
 * Un utilisateur devient VENDEUR en publiant une annonce
 * et ACHETEUR en initiant une transaction. Ces rôles sont dynamiques.
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCompte typeCompte = TypeCompte.NORMAL;

    private boolean isPremium = false;

    private boolean isActive = true;

    private float noteMoyenne = 0.0f;

    private String localisation;

    // ── Rôle VENDEUR : un User publie plusieurs annonces ──────────
    // OneToMany : 1 User → N Annonces
    @OneToMany(mappedBy = "vendeur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Annonce> annoncesPubliees = new ArrayList<>();

    // ── Rôle ACHETEUR : un User initie plusieurs transactions ─────
    // OneToMany : 1 User (acheteur) → N Transactions
    @OneToMany(mappedBy = "acheteur", cascade = CascadeType.ALL)
    private List<Transaction> transactionsEffectuees = new ArrayList<>();

    // ── Messages envoyés ──────────────────────────────────────────
    // OneToMany : 1 User → N Messages envoyés
    @OneToMany(mappedBy = "expediteur", cascade = CascadeType.ALL)
    private List<Message> messagesEnvoyes = new ArrayList<>();

    // ── Messages reçus ────────────────────────────────────────────
    @OneToMany(mappedBy = "destinataire")
    private List<Message> messagesRecus = new ArrayList<>();

    // ── Avis reçus (en tant que vendeur ou acheteur évalué) ───────
    // OneToMany : 1 User → N Avis reçus
    @OneToMany(mappedBy = "cible")
    private List<Avis> avisRecus = new ArrayList<>();

    // ── Avis rédigés ──────────────────────────────────────────────
    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL)
    private List<Avis> avisRediges = new ArrayList<>();

    // ── Signalements émis ─────────────────────────────────────────
    @OneToMany(mappedBy = "signaleur", cascade = CascadeType.ALL)
    private List<Signalement> signalementsEmis = new ArrayList<>();

    // ── Méthodes métier ───────────────────────────────────────────

    /** Recalcule la note moyenne après chaque nouvel avis reçu. */
    public void recalculerNoteMoyenne() {
        if (avisRecus.isEmpty()) {
            this.noteMoyenne = 0.0f;
            return;
        }
        this.noteMoyenne = (float) avisRecus.stream()
                .mapToInt(Avis::getNote)
                .average()
                .orElse(0.0);
    }
}
