package com.challenge.tenpo.sumador.repository;

import com.challenge.tenpo.sumador.entities.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Optional<History> findTopByOrderByIdDesc();
}

