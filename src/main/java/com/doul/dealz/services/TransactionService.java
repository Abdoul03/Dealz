package com.doul.dealz.services;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Transaction;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.TransactionRequestDTO;
import com.doul.dealz.model.dto.response.TransactionResponseDTO;
import com.doul.dealz.model.dto.response.UserSummaryDTO;
import com.doul.dealz.model.enums.StatutAnnonce;
import com.doul.dealz.model.enums.StatutTransaction;
import com.doul.dealz.repository.AnnonceRepository;
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
    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;

    public TransactionResponseDTO initierTransaction(TransactionRequestDTO dto, String acheteurId) {
        Annonce annonce = annonceRepository.findByIdForUpdate(dto.annonceId())
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));

        if (annonce.getStatut() != StatutAnnonce.PUBLIEE) {
            throw new IllegalArgumentException("Cette annonce n'est plus disponible.");
        }
        if (annonce.getVendeur().getId().equals(acheteurId)) {
            throw new IllegalArgumentException("Vous ne pouvez pas acheter votre propre annonce.");
        }
        if (transactionRepository.existsByAnnonceIdAndStatutIn(dto.annonceId(),
                List.of(StatutTransaction.EN_COURS, StatutTransaction.CONFIRMATION_PARTIELLE,
                        StatutTransaction.SEQUESTRE))) {
            throw new IllegalArgumentException("Une transaction est déjà en cours pour cette annonce.");
        }

        User acheteur = userRepository.findById(acheteurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        Transaction transaction = new Transaction();
        transaction.setMontant(annonce.getPrix());
        transaction.setAcheteur(acheteur);
        transaction.setAnnonce(annonce);
        transaction.setModeRetrait("MAIN_PROPRE");
        transaction.setDateTransaction(LocalDateTime.now());
        annonce.marquerReservee();
        annonceRepository.save(annonce);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return toResponse(savedTransaction);
    }

    public TransactionResponseDTO confirmerTransaction(String id, String acheteurId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction introuvable."));

        transaction.confirmerRemise(acheteurId);
        transactionRepository.save(transaction);
        return toResponse(transaction);
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
        if (transaction.getAnnonce().getStatut() == StatutAnnonce.RESERVEE) {
            transaction.getAnnonce().publier();
        }
        transactionRepository.save(transaction);

        return toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getMesAchats(String acheteurId) {
        return transactionRepository.findByAcheteurIdOrderByDateTransactionDesc(acheteurId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getMesVentes(String vendeurId) {
        return transactionRepository.findByVendeurId(vendeurId)
                .stream().map(this::toResponse).toList();
    }

    private TransactionResponseDTO toResponse(Transaction t) {
        Annonce annonce = t.getAnnonce();
        User vendeur = annonce.getVendeur();
        User acheteur = t.getAcheteur();
        return new TransactionResponseDTO(
                t.getId(), t.getMontant(), t.getStatut(), t.getDateTransaction(),
                t.getModeRetrait(), annonce.getId(), annonce.getTitre(),
                new UserSummaryDTO(acheteur.getId(), acheteur.getNom(), acheteur.getPrenom(), acheteur.getNoteMoyenne()),
                new UserSummaryDTO(vendeur.getId(), vendeur.getNom(), vendeur.getPrenom(), vendeur.getNoteMoyenne()),
                t.isAcheteurConfirmeRemise(),
                t.isVendeurConfirmeRemise()
        );
    }
}
