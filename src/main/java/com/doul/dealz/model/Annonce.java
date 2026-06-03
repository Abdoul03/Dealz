package com.doul.dealz.model;

import com.doul.dealz.model.enums.EtatArticle;
import com.doul.dealz.model.enums.StatutAnnonce;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Annonce publiée par un utilisateur (rôle vendeur).
 *
 * Relations :
 *   ManyToOne → User (vendeur)         : N annonces appartiennent à 1 vendeur
 *   ManyToOne → Categorie              : N annonces appartiennent à 1 categorie
 *   OneToMany → Transaction            : 1 annonce peut générer 1 transaction
 *   OneToMany → Signalement            : 1 annonce peut être signalée plusieurs fois
 *   ManyToMany → (pas de M2M ici)
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "annonces")
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private float prix;

    /** URL principale de l'image (image de couverture). */
    private String urlImage;

    /** Liste de toutes les photos de l'article (stockées en JSON ou table liée). */
    @ElementCollection
    @CollectionTable(name = "annonce_images", joinColumns = @JoinColumn(name = "annonce_id"))
    @Column(name = "url")
    private List<String> urlImages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatArticle etat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAnnonce statut = StatutAnnonce.EN_ATTENTE;

    private LocalDateTime datePublication;

    private boolean isBoosted = false;

    /** Point de retrait proposé par le vendeur (ex: "Marché de Médine, Bamako"). */
    private String pointRetrait;

    // ── ManyToOne : N Annonces → 1 User (vendeur) ─────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendeur_id", nullable = false)
    private User vendeur;

    // ── ManyToOne : N Annonces → 1 Categorie ──────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    // ── OneToMany : 1 Annonce → 1 Transaction (max) ───────────────
    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    // ── OneToMany : 1 Annonce → N Signalements ────────────────────
    @OneToMany(mappedBy = "annonceCiblee", cascade = CascadeType.ALL)
    private List<Signalement> signalements = new ArrayList<>();

    // ── Méthodes métier ───────────────────────────────────────────

    /** Publie l'annonce : passe le statut à PUBLIEE et horodate. */
    public void publier() {
        this.statut = StatutAnnonce.PUBLIEE;
        this.datePublication = LocalDateTime.now();
    }

    /** Archive l'annonce (plus visible mais conservée). */
    public void archiver() {
        this.statut = StatutAnnonce.ARCHIVEE;
    }

    /** Marque l'annonce comme vendue après confirmation de transaction. */
    public void marquerVendue() {
        this.statut = StatutAnnonce.VENDUE;
    }
}
