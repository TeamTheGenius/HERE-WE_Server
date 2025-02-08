package com.genius.herewe.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.genius.herewe.file.domain.Files;

public interface FilesRepository extends JpaRepository<Files, Long> {
}
