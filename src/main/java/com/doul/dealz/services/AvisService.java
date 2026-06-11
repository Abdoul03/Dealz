package com.doul.dealz.services;

import com.doul.dealz.model.Avis;
import com.doul.dealz.model.Transaction;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.AvisRequestDTO;
import com.doul.dealz.model.dto.response.AvisResponseDTO;
import com.doul.dealz.model.dto.response.UserSummaryDTO;
import com.doul.dealz.model.enums.StatutTransaction;
import com.doul.dealz.repository.AvisRepository;
import com.doul.dealz.repository.TransactionRepository;
import com.doul.dealz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AvisService {

    private final AvisRepository avisRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AvisResponseDTO createAvis(AvisRequestDTO dto, String auteurId) {
        Transaction transaction = transactionRepository.findById(dto.transactionId())
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable."));

        if (transaction.getStatut() != StatutTransaction.CONFIRMEE) {
            throw new IllegalArgumentException("Un avis ne peut être laissé que sur une transaction confirmée.");
        }

        String acheteurId = transaction.getAcheteur().getId();
        String vendeurId = transaction.getAnnonce().getVendeur().getId();

        if (!auteurId.equals(acheteurId) && !auteurId.equals(vendeurId)) {
            throw new SecurityException("Vous n'êtes pas partie à cette transaction.");
        }
        if (avisRepository.existsByAuteurIdAndTransactionId(auteurId, dto.transactionId())) {
            throw new IllegalArgumentException("Vous avez déjà laissé un avis pour cette transaction.");
        }

        User auteur = userRepository.findById(auteurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));
        String cibleId = auteurId.equals(acheteurId) ? vendeurId : acheteurId;
        User cible = userRepository.findById(cibleId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur cible introuvable."));

        Avis avis = new Avis();
        avis.setNote(dto.note());
        avis.setCommentaire(dto.commentaire());
        avis.setTypeAvis(dto.typeAvis());
        avis.setAuteur(auteur);
        avis.setCible(cible);
        avis.setTransaction(transaction);

        Avis saved = avisRepository.save(avis);

        // Recalcul correct après persistance (le @PrePersist calcule n-1)
        double moyenne = avisRepository.findByCibleId(cibleId)
                .stream().mapToInt(Avis::getNote).average().orElse(0.0);
        cible.setNoteMoyenne((float) moyenne);
        userRepository.save(cible);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AvisResponseDTO> getAvisRecus(String userId) {
        return avisRepository.findByCibleId(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AvisResponseDTO> getAvisByTransaction(String transactionId) {
        return avisRepository.findByTransactionId(transactionId)
                .stream().map(this::toResponse).toList();
    }

    private AvisResponseDTO toResponse(Avis a) {
        User auteur = a.getAuteur();
        User cible = a.getCible();
        return new AvisResponseDTO(
                a.getId(), a.getNote(), a.getCommentaire(), a.getDateAvis(), a.getTypeAvis(),
                new UserSummaryDTO(auteur.getId(), auteur.getNom(), auteur.getPrenom(), auteur.getNoteMoyenne()),
                new UserSummaryDTO(cible.getId(), cible.getNom(), cible.getPrenom(), cible.getNoteMoyenne())
        );
    }
}
