package com.doul.dealz.model;

import com.doul.dealz.model.enums.StatutTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction entre un acheteur et un vendeur pour une annonce.
 *
 * Relations :
 *   ManyToOne → User (acheteur)  : N transactions initiées par 1 acheteur
 *   ManyToOne → Annonce          : N transactions → 1 annonce (en général 1 seule confirmée)
 *   OneToOne  → Paiement         : 1 transaction génère 1 paiement
 *   OneToMany → Avis             : 1 transaction peut générer 2 avis (acheteur + vendeur)
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private float montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTransaction statut = StatutTransaction.EN_COURS;

    private LocalDateTime dateTransaction;

    /** Mode de remise : "DOMICILE", "POINT_RELAIS", "MAIN_PROPRE" */
    private String modeRetrait;

    // ── ManyToOne : N Transactions → 1 User (acheteur) ───────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acheteur_id", nullable = false)
    private User acheteur;

    // ── ManyToOne : N Transactions → 1 Annonce ───────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce;

    // ── OneToOne : 1 Transaction → 1 Paiement ────────────────────
    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, optional = false)
    private Paiement paiement;

    // ── OneToMany : 1 Transaction → max 2 Avis ───────────────────
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<Avis> avis = new ArrayList<>();

    // ── Méthodes métier ───────────────────────────────────────────

    /**
     * Confirme la transaction après réception de l'article par l'acheteur.
     * Libère le paiement au vendeur et marque l'annonce comme vendue.
     */
    public void confirmer() {
        this.statut = StatutTransaction.CONFIRMEE;
        this.annonce.marquerVendue();
    }

    /** Annule la transaction et déclenche un remboursement. */
    public void annuler() {
        this.statut = StatutTransaction.ANNULEE;
    }

    /** Place les fonds en séquestre en attente de confirmation. */
    public void mettreEnSequestre() {
        this.statut = StatutTransaction.SEQUESTRE;
    }

}
