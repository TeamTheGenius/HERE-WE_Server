package com.genius.herewe.core.security.facade;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthFacade implements AuthFacade {
}
