package com.genius.herewe.business.crew.facade;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewResponse;

public interface CrewFacade {
	CrewResponse createCrew(Long userId, CrewCreateRequest request);

	CrewResponse modifyCrew(Long userId, Long crewId, CrewModifyRequest request);

	void joinCrew(Long userId, Long crewId);
}
