package com.doul.dealz.services;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Signalement;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.SignalementRequestDTO;
import com.doul.dealz.repository.AnnonceRepository;
import com.doul.dealz.repository.SignalementRepository;
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
public class SignalementService {

    private final SignalementRepository signalementRepository;
    private final UserRepository userRepository;
    private final AnnonceRepository annonceRepository;

    public void createSignalement(SignalementRequestDTO dto, String signaleurId) {
        if (dto.annonceId() == null && dto.userCibleId() == null) {
            throw new IllegalArgumentException("Un signalement doit cibler une annonce ou un utilisateur.");
        }

        User signaleur = userRepository.findById(signaleurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        Signalement signalement = new Signalement();
        signalement.setMotif(dto.motif());
        signalement.setDescription(dto.description());
        signalement.setSignaleur(signaleur);
        signalement.setDateSignalement(LocalDateTime.now());

        if (dto.annonceId() != null) {
            Annonce annonce = annonceRepository.findById(dto.annonceId())
                    .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));
            signalement.setAnnonceCiblee(annonce);
        }
        if (dto.userCibleId() != null) {
            User userCible = userRepository.findById(dto.userCibleId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur cible introuvable."));
            signalement.setUserCible(userCible);
        }

        signalementRepository.save(signalement);
    }

    @Transactional(readOnly = true)
    public List<Signalement> getAllSignalements() {
        return signalementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Signalement> getMesSignalements(String signaleurId) {
        return signalementRepository.findBySignaleurId(signaleurId);
    }
}
