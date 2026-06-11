package com.doul.dealz.services;

import com.doul.dealz.model.Annonce;
import com.doul.dealz.model.Message;
import com.doul.dealz.model.User;
import com.doul.dealz.model.dto.request.MessageRequestDTO;
import com.doul.dealz.model.dto.response.ConversationResumeDTO;
import com.doul.dealz.model.dto.response.MessageResponseDTO;
import com.doul.dealz.model.dto.response.UserSummaryDTO;
import com.doul.dealz.repository.AnnonceRepository;
import com.doul.dealz.repository.MessageRepository;
import com.doul.dealz.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AnnonceRepository annonceRepository;

    public MessageResponseDTO sendMessage(MessageRequestDTO dto, String expediteurId) {
        if (expediteurId.equals(dto.destinataireId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer un message à vous-même.");
        }
        User expediteur = userRepository.findById(expediteurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));
        User destinataire = userRepository.findById(dto.destinataireId())
                .orElseThrow(() -> new EntityNotFoundException("Destinataire introuvable."));
        Annonce annonce = annonceRepository.findById(dto.annonceId())
                .orElseThrow(() -> new EntityNotFoundException("Annonce introuvable."));

        Message message = new Message();
        message.setContenu(dto.contenu());
        message.setDateEnvoi(LocalDateTime.now());
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setAnnonce(annonce);

        return toResponse(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getConversation(String autreUserId, String annonceId, String currentUserId) {
        return messageRepository.findConversation(currentUserId, autreUserId, annonceId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationResumeDTO> getConversationsResume(String userId) {
        List<Message> tous = messageRepository.findAllByUserId(userId);

        // Grouper par (interlocuteur, annonce) — la liste est déjà triée DESC donc le premier = le plus récent
        Map<String, Message> derniersMessages = new LinkedHashMap<>();
        for (Message m : tous) {
            String autreId = m.getExpediteur().getId().equals(userId)
                    ? m.getDestinataire().getId() : m.getExpediteur().getId();
            String key = autreId + "_" + m.getAnnonce().getId();
            derniersMessages.putIfAbsent(key, m);
        }

        return derniersMessages.values().stream().map(m -> {
            User autre = m.getExpediteur().getId().equals(userId)
                    ? m.getDestinataire() : m.getExpediteur();
            boolean hasUnread = !m.getExpediteur().getId().equals(userId) && !m.isRead();
            return new ConversationResumeDTO(
                    new UserSummaryDTO(autre.getId(), autre.getNom(), autre.getPrenom(), autre.getNoteMoyenne()),
                    m.getAnnonce().getId(),
                    m.getAnnonce().getTitre(),
                    m.getContenu(),
                    m.getDateEnvoi(),
                    hasUnread
            );
        }).toList();
    }

    public void marquerConversationLue(String autreUserId, String annonceId, String destinataireId) {
        List<Message> nonLus = messageRepository.findUnreadMessages(destinataireId, autreUserId, annonceId);
        nonLus.forEach(Message::marquerLu);
        messageRepository.saveAll(nonLus);
    }

    private MessageResponseDTO toResponse(Message m) {
        User exp = m.getExpediteur();
        User dest = m.getDestinataire();
        return new MessageResponseDTO(
                m.getId(), m.getContenu(), m.getDateEnvoi(), m.isRead(),
                new UserSummaryDTO(exp.getId(), exp.getNom(), exp.getPrenom(), exp.getNoteMoyenne()),
                new UserSummaryDTO(dest.getId(), dest.getNom(), dest.getPrenom(), dest.getNoteMoyenne()),
                m.getAnnonce().getId()
        );
    }
}
