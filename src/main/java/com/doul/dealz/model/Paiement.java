package com.doul.dealz.model;

import com.doul.dealz.model.enums.TypePaiement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Paiement lié à une Transaction.
 *
 * Relations :
 *   OneToOne → Transaction : 1 paiement est lié à exactement 1 transaction
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "paiements")
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePaiement type;

    @Column(nullable = false)
    private float montant;

    private LocalDateTime datePaiement;

    /**
     * Indique si les fonds sont en séquestre (retenus par la plateforme)
     * ou libérés au vendeur.
     */
    private boolean estLibere = false;

    // ── OneToOne : 1 Paiement → 1 Transaction ─────────────────────
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    // ── Méthodes métier ────────────────────────────────────────────

    /** Effectue le paiement et le met en séquestre. */
    public void effectuer() {
        this.datePaiement = LocalDateTime.now();
        this.estLibere = false;
    }

    /** Libère les fonds au vendeur après confirmation de réception. */
    public void libererFonds() {
        this.estLibere = true;
    }
}
