package com.genius.herewe.business.moment.facade;

import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.core.user.domain.User;

public interface MomentFacade {
	MomentResponse inquiryMoment(User user, Long momentId);

	MomentResponse createMoment(Long userId, Long crewId, MomentRequest momentRequest);

	MomentResponse modifyMoment(Long momentId, MomentRequest momentRequest);

	void deleteMoment(Long momentId);
}
