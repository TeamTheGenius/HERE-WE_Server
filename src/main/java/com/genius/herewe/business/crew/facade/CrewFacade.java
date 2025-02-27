package com.genius.herewe.business.crew.facade;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewCreateResponse;

public interface CrewFacade {
	CrewCreateResponse createCrew(Long userId, CrewCreateRequest request);
}
