package com.doul.dealz.models;

import com.doul.dealz.models.enums.EtatArticle;
import com.doul.dealz.models.enums.StatuAnnoce;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String titre;
    private String description;
    private float prix;
    private String UrlImage;
    private EtatArticle etat;
    private StatuAnnoce statut;
    private Date datePublication;
    private boolean isBoosted = false;
    private String pointRetrait;
}
