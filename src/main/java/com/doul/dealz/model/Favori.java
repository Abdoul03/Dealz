package com.doul.dealz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "favoris", uniqueConstraints = @UniqueConstraint(
        name = "uk_favori_user_annonce",
        columnNames = {"user_id", "annonce_id"}
))
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User utilisateur;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce;

    @Column(nullable = false)
    private LocalDateTime dateAjout = LocalDateTime.now();
}
