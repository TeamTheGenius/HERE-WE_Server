package com.genius.herewe.business.crew.facade;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.dto.CrewResponse;

public interface CrewFacade {
	CrewResponse inquiryCrew(Long userId, Long crewId);

	// Page<CrewMemberResponse> inquiryMembers(Long crewId);

	CrewPreviewResponse createCrew(Long userId, CrewCreateRequest request);

	CrewPreviewResponse modifyCrew(Long userId, Long crewId, CrewModifyRequest request);

	void joinCrew(Long userId, Long crewId);
}
