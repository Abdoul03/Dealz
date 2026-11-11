package com.doul.dealz.services;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Categorie;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.AnnonceRequestDTO;
import com.doul.dealz.model.dto.response.AnnonceResponseDTO;
import com.doul.dealz.model.dto.response.CategorieResponseDTO;
import com.doul.dealz.model.dto.response.UserSummaryDTO;
import com.doul.dealz.model.enums.StatutAnnonce;
import com.doul.dealz.repository.AnnonceRepository;
import com.doul.dealz.repository.CategorieRepository;
import com.doul.dealz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;

    public AnnonceResponseDTO createAnnonce(AnnonceRequestDTO dto, String vendeurId) {
        User vendeur = userRepository.findById(vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));
        Categorie categorie = categorieRepository.findById(dto.categorieId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable."));

        Annonce annonce = new Annonce();
        annonce.setTitre(dto.titre());
        annonce.setMarque(dto.marque());
        annonce.setTaille(dto.taille());
        annonce.setDescription(dto.description());
        annonce.setPrix(dto.prix());
        annonce.setUrlImage(dto.urlImage());
        annonce.setUrlImages(dto.urlImages() != null ? dto.urlImages() : new ArrayList<>());
        annonce.setEtat(dto.etat());
        annonce.setQuartier(dto.quartier());
        annonce.setVendeur(vendeur);
        annonce.setCategorie(categorie);
        annonce.publier(); // publication immédiate à la création

        return toResponse(annonceRepository.save(annonce));
    }

    public AnnonceResponseDTO publierAnnonce(String id, String vendeurId) {
        Annonce annonce = annonceRepository.findByIdAndVendeurId(id, vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable ou non autorisée."));
        if (annonce.getStatut() == StatutAnnonce.VENDUE) {
            throw new IllegalArgumentException("Une annonce vendue ne peut pas être republiée.");
        }
        if (annonce.getStatut() == StatutAnnonce.RESERVEE) {
            throw new IllegalArgumentException("Cette annonce est réservée.");
        }
        annonce.publier();
        return toResponse(annonceRepository.save(annonce));
    }

    @Transactional(readOnly = true)
    public List<AnnonceResponseDTO> getAnnonces(
            String categorieId,
            String motCle,
            String taille,
            Float prixMin,
            Float prixMax,
            int page,
            int size) {
        if (prixMin != null && prixMax != null && prixMin > prixMax) {
            throw new IllegalArgumentException("Le prix minimum dépasse le prix maximum.");
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 40));
        String categorie = categorieId == null || categorieId.isBlank() ? null : categorieId;
        String recherche = motCle == null || motCle.isBlank() ? null : motCle.trim();
        String tailleFiltre = taille == null || taille.isBlank() ? null : taille.trim();
        List<Annonce> annonces = annonceRepository.findAvailableWithFilters(
                StatutAnnonce.PUBLIEE,
                categorie,
                recherche,
                tailleFiltre,
                prixMin,
                prixMax,
                pageable).getContent();
        return annonces.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnnonceResponseDTO getAnnonceById(String id) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));
        return toResponse(annonce);
    }

    @Transactional(readOnly = true)
    public List<AnnonceResponseDTO> getMesAnnonces(String vendeurId) {
        return annonceRepository.findByVendeurIdOrderByDatePublicationDesc(vendeurId)
                .stream().map(this::toResponse).toList();
    }

    public AnnonceResponseDTO updateAnnonce(String id, AnnonceRequestDTO dto, String vendeurId) {
        Annonce annonce = annonceRepository.findByIdAndVendeurId(id, vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable ou non autorisée."));
        if (annonce.getStatut() == StatutAnnonce.VENDUE || annonce.getStatut() == StatutAnnonce.RESERVEE) {
            throw new IllegalArgumentException("Une annonce vendue ou réservée ne peut pas être modifiée.");
        }
        Categorie categorie = categorieRepository.findById(dto.categorieId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable."));

        annonce.setTitre(dto.titre());
        annonce.setMarque(dto.marque());
        annonce.setTaille(dto.taille());
        annonce.setDescription(dto.description());
        annonce.setPrix(dto.prix());
        annonce.setUrlImage(dto.urlImage());
        if (dto.urlImages() != null) annonce.setUrlImages(dto.urlImages());
        annonce.setEtat(dto.etat());
        annonce.setQuartier(dto.quartier());
        annonce.setCategorie(categorie);

        return toResponse(annonceRepository.save(annonce));
    }

    public AnnonceResponseDTO archiverAnnonce(String id, String vendeurId) {
        Annonce annonce = annonceRepository.findByIdAndVendeurId(id, vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable ou non autorisée."));
        if (annonce.getStatut() == StatutAnnonce.VENDUE || annonce.getStatut() == StatutAnnonce.RESERVEE) {
            throw new IllegalArgumentException("Une annonce vendue ou réservée ne peut pas être archivée.");
        }
        annonce.archiver();
        return toResponse(annonceRepository.save(annonce));
    }

    public void deleteAnnonce(String id, String vendeurId) {
        Annonce annonce = annonceRepository.findByIdAndVendeurId(id, vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable ou non autorisée."));
        if (annonce.getStatut() == StatutAnnonce.PUBLIEE) {
            throw new IllegalArgumentException("Archivez l'annonce avant de la supprimer.");
        }
        if (annonce.getStatut() == StatutAnnonce.VENDUE || annonce.getStatut() == StatutAnnonce.RESERVEE) {
            throw new IllegalArgumentException("Une annonce vendue ou réservée ne peut pas être supprimée.");
        }
        annonceRepository.delete(annonce);
    }

    @Transactional(readOnly = true)
    public List<AnnonceResponseDTO> getAnnoncesByVendeurPublic(String vendeurId) {
        return annonceRepository
                .findByStatutAndVendeurIdOrderByDatePublicationDesc(StatutAnnonce.PUBLIEE, vendeurId)
                .stream().map(this::toResponse).toList();
    }

    AnnonceResponseDTO toResponse(Annonce a) {
        User v = a.getVendeur();
        Categorie c = a.getCategorie();
        return new AnnonceResponseDTO(
                a.getId(), a.getTitre(), a.getMarque(), a.getTaille(), a.getDescription(), a.getPrix(),
                a.getUrlImage(), a.getUrlImages(), a.getEtat(), a.getStatut(),
                a.getDatePublication(), a.isBoosted(), a.getQuartier(),
                new UserSummaryDTO(v.getId(), v.getNom(), v.getPrenom(), v.getNoteMoyenne()),
                new CategorieResponseDTO(c.getId(), c.getNom())
        );
    }

    Annonce findById(String id) {
        return annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));
    }
}
