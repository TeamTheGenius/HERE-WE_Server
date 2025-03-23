package com.genius.herewe.business.moment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

public interface MomentApi {
	SingleResponse<MomentResponse> createMoment(@HereWeUser User user, @RequestParam(name = "crewId") Long crewId,
		@RequestBody MomentRequest momentRequest);

	SingleResponse<MomentResponse> modifyMoment(@PathVariable Long momentId, @RequestBody MomentRequest momentRequest);
}
