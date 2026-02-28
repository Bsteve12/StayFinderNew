package com.stayFinder.proyectoFinal.repository;

import com.stayFinder.proyectoFinal.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
