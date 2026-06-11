package com.doul.dealz.repository;

import com.doul.dealz.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, String> {

    @Query("SELECT m FROM Message m WHERE " +
           "((m.expediteur.id = :userId1 AND m.destinataire.id = :userId2) OR " +
           "(m.expediteur.id = :userId2 AND m.destinataire.id = :userId1)) " +
           "AND m.annonce.id = :annonceId ORDER BY m.dateEnvoi ASC")
    List<Message> findConversation(@Param("userId1") String userId1,
                                   @Param("userId2") String userId2,
                                   @Param("annonceId") String annonceId);

    @Query("SELECT m FROM Message m WHERE m.expediteur.id = :userId OR m.destinataire.id = :userId ORDER BY m.dateEnvoi DESC")
    List<Message> findAllByUserId(@Param("userId") String userId);

    @Query("SELECT m FROM Message m WHERE m.destinataire.id = :destinataireId AND m.expediteur.id = :expediteurId AND m.annonce.id = :annonceId AND m.isRead = false")
    List<Message> findUnreadMessages(@Param("destinataireId") String destinataireId,
                                     @Param("expediteurId") String expediteurId,
                                     @Param("annonceId") String annonceId);
}
