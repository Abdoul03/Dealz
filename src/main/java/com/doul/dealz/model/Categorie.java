package com.doul.dealz.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Catégorie d'articles sur Dealz.
 * Exemples : Vêtements Homme, Électronique, Maison, Chaussures…
 */
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String nom;

    // ── OneToMany : 1 Categorie → N Annonces ──────────────────────
    @OneToMany(mappedBy = "categorie")
    private List<Annonce> annonces = new ArrayList<>();
}
