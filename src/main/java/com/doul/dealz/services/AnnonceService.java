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
        annonce.setDescription(dto.description());
        annonce.setPrix(dto.prix());
        annonce.setUrlImage(dto.urlImage());
        annonce.setUrlImages(dto.urlImages() != null ? dto.urlImages() : new ArrayList<>());
        annonce.setEtat(dto.etat());
        annonce.setPointRetrait(dto.pointRetrait());
        annonce.setVendeur(vendeur);
        annonce.setCategorie(categorie);

        return toResponse(annonceRepository.save(annonce));
    }

    public AnnonceResponseDTO publierAnnonce(String id, String vendeurId) {
        Annonce annonce = annonceRepository.findByIdAndVendeurId(id, vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable ou non autorisée."));
        if (annonce.getStatut() == StatutAnnonce.VENDUE) {
            throw new IllegalArgumentException("Une annonce vendue ne peut pas être republiée.");
        }
        annonce.publier();
        return toResponse(annonceRepository.save(annonce));
    }

    @Transactional(readOnly = true)
    public List<AnnonceResponseDTO> getAnnonces(String categorieId, String motCle) {
        List<Annonce> annonces;
        if (motCle != null && !motCle.isBlank()) {
            annonces = annonceRepository.searchByMotCle(StatutAnnonce.PUBLIEE, motCle);
        } else if (categorieId != null && !categorieId.isBlank()) {
            annonces = annonceRepository.findByStatutAndCategorieIdOrderByDatePublicationDesc(
                    StatutAnnonce.PUBLIEE, categorieId);
        } else {
            annonces = annonceRepository.findByStatutOrderByDatePublicationDesc(StatutAnnonce.PUBLIEE);
        }
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
        if (annonce.getStatut() == StatutAnnonce.VENDUE) {
            throw new IllegalArgumentException("Une annonce vendue ne peut pas être modifiée.");
        }
        Categorie categorie = categorieRepository.findById(dto.categorieId())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable."));

        annonce.setTitre(dto.titre());
        annonce.setDescription(dto.description());
        annonce.setPrix(dto.prix());
        annonce.setUrlImage(dto.urlImage());
        if (dto.urlImages() != null) annonce.setUrlImages(dto.urlImages());
        annonce.setEtat(dto.etat());
        annonce.setPointRetrait(dto.pointRetrait());
        annonce.setCategorie(categorie);

        return toResponse(annonceRepository.save(annonce));
    }

    public AnnonceResponseDTO archiverAnnonce(String id, String vendeurId) {
        Annonce annonce = annonceRepository.findByIdAndVendeurId(id, vendeurId)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable ou non autorisée."));
        if (annonce.getStatut() == StatutAnnonce.VENDUE) {
            throw new IllegalArgumentException("Une annonce vendue ne peut pas être archivée.");
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
        if (annonce.getStatut() == StatutAnnonce.VENDUE) {
            throw new IllegalArgumentException("Une annonce vendue ne peut pas être supprimée.");
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
                a.getId(), a.getTitre(), a.getDescription(), a.getPrix(),
                a.getUrlImage(), a.getUrlImages(), a.getEtat(), a.getStatut(),
                a.getDatePublication(), a.isBoosted(), a.getPointRetrait(),
                new UserSummaryDTO(v.getId(), v.getNom(), v.getPrenom(), v.getNoteMoyenne()),
                new CategorieResponseDTO(c.getId(), c.getNom())
        );
    }

    Annonce findById(String id) {
        return annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));
    }
}
