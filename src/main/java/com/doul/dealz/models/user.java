package com.doul.dealz.models;

import com.doul.dealz.models.enums.TypeCompte;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password;
    private boolean isActive = true;
    private boolean isPremuim = false;
    private TypeCompte typeCompte;
    private String Localisation;

}
