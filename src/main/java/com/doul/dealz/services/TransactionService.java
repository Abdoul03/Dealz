package com.doul.dealz.services;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Paiement;
import com.doul.dealz.model.Transaction;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.TransactionRequestDTO;
import com.doul.dealz.model.dto.response.TransactionResponseDTO;
import com.doul.dealz.model.dto.response.UserSummaryDTO;
import com.doul.dealz.model.enums.StatutAnnonce;
import com.doul.dealz.model.enums.StatutTransaction;
import com.doul.dealz.repository.AnnonceRepository;
import com.doul.dealz.repository.PaiementRepository;
import com.doul.dealz.repository.TransactionRepository;
import com.doul.dealz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaiementRepository paiementRepository;
    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;

    public TransactionResponseDTO initierTransaction(TransactionRequestDTO dto, String acheteurId) {
        Annonce annonce = annonceRepository.findById(dto.annonceId())
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));

        if (annonce.getStatut() != StatutAnnonce.PUBLIEE) {
            throw new IllegalArgumentException("Cette annonce n'est plus disponible.");
        }
        if (annonce.getVendeur().getId().equals(acheteurId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas acheter votre propre annonce.");
        }
        if (transactionRepository.existsByAnnonceIdAndStatutIn(dto.annonceId(),
                List.of(StatutTransaction.EN_COURS, StatutTransaction.SEQUESTRE))) {
            throw new IllegalArgumentException("Une transaction est déjà en cours pour cette annonce.");
        }

        User acheteur = userRepository.findById(acheteurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        Transaction transaction = new Transaction();
        transaction.setMontant(annonce.getPrix());
        transaction.setAcheteur(acheteur);
        transaction.setAnnonce(annonce);
        transaction.setModeRetrait(dto.modeRetrait());
        transaction.setDateTransaction(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);

        Paiement paiement = new Paiement();
        paiement.setType(dto.typePaiement());
        paiement.setMontant(annonce.getPrix());
        paiement.setTransaction(savedTransaction);
        paiement.effectuer();
        paiementRepository.save(paiement);

        savedTransaction.mettreEnSequestre();
        savedTransaction = transactionRepository.save(savedTransaction);

        return toResponse(savedTransaction, paiement);
    }

    public TransactionResponseDTO confirmerTransaction(String id, String acheteurId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable."));

        if (!transaction.getAcheteur().getId().equals(acheteurId)) {
            throw new SecurityException("Seul l'acheteur peut confirmer la réception.");
        }
        if (transaction.getStatut() == StatutTransaction.CONFIRMEE) {
            throw new IllegalArgumentException("Cette transaction est déjà confirmée.");
        }
        if (transaction.getStatut() == StatutTransaction.ANNULEE) {
            throw new IllegalArgumentException("Cette transaction a été annulée.");
        }

        transaction.confirmer();
        if (transaction.getPaiement() != null) {
            transaction.getPaiement().libererFonds();
        }
        transactionRepository.save(transaction);

        return toResponse(transaction, transaction.getPaiement());
    }

    public TransactionResponseDTO annulerTransaction(String id, String userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable."));

        String acheteurId = transaction.getAcheteur().getId();
        String vendeurId = transaction.getAnnonce().getVendeur().getId();

        if (!acheteurId.equals(userId) && !vendeurId.equals(userId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à annuler cette transaction.");
        }
        if (transaction.getStatut() == StatutTransaction.CONFIRMEE) {
            throw new IllegalArgumentException("Une transaction confirmée ne peut pas être annulée.");
        }

        transaction.annuler();
        transactionRepository.save(transaction);

        return toResponse(transaction, transaction.getPaiement());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getMesAchats(String acheteurId) {
        return transactionRepository.findByAcheteurIdOrderByDateTransactionDesc(acheteurId)
                .stream().map(t -> toResponse(t, t.getPaiement())).toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getMesVentes(String vendeurId) {
        return transactionRepository.findByVendeurId(vendeurId)
                .stream().map(t -> toResponse(t, t.getPaiement())).toList();
    }

    private TransactionResponseDTO toResponse(Transaction t, Paiement p) {
        Annonce annonce = t.getAnnonce();
        User vendeur = annonce.getVendeur();
        User acheteur = t.getAcheteur();
        return new TransactionResponseDTO(
                t.getId(), t.getMontant(), t.getStatut(), t.getDateTransaction(),
                t.getModeRetrait(), annonce.getId(), annonce.getTitre(),
                new UserSummaryDTO(acheteur.getId(), acheteur.getNom(), acheteur.getPrenom(), acheteur.getNoteMoyenne()),
                new UserSummaryDTO(vendeur.getId(), vendeur.getNom(), vendeur.getPrenom(), vendeur.getNoteMoyenne()),
                p != null ? p.getType() : null,
                p != null && p.isEstLibere()
        );
    }
}
