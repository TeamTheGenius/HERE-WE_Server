package com.genius.herewe.business.crew.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewExpelRequest;
import com.genius.herewe.business.crew.dto.CrewMemberResponse;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.dto.CrewResponse;

public interface CrewFacade {
	Page<CrewPreviewResponse> inquiryMyCrews(Long userId, Pageable pageable);

	CrewResponse inquiryCrew(Long userId, Long crewId);

	Page<CrewMemberResponse> inquiryMembers(Long crewId, Pageable pageable);

	CrewPreviewResponse createCrew(Long userId, CrewCreateRequest request);

	CrewPreviewResponse modifyCrew(Long userId, Long crewId, CrewModifyRequest request);

	void expelCrew(Long userId, CrewExpelRequest expelRequest);
}
