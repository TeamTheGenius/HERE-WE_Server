package com.genius.herewe.business.moment.facade;

import com.genius.herewe.business.moment.dto.MomentCreateRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;

public interface MomentFacade {

	MomentResponse createMoment(Long userId, MomentCreateRequest momentCreateRequest);

}
