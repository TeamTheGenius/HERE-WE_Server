package com.genius.herewe.infra.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.infra.file.domain.Files;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
