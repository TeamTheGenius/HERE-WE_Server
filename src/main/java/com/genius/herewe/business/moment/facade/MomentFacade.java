package com.genius.herewe.business.moment.facade;

import java.time.LocalDateTime;
import java.util.List;

import com.genius.herewe.business.moment.dto.MomentMemberResponse;
import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.core.user.domain.User;

public interface MomentFacade {
	MomentResponse inquirySingle(User user, Long momentId);

	MomentResponse create(Long userId, Long crewId, MomentRequest momentRequest);

	MomentResponse modify(Long momentId, MomentRequest momentRequest);

	void delete(Long momentId);

	MomentResponse join(Long userId, Long momentId, LocalDateTime now);

	void quit(Long userId, Long momentId, LocalDateTime now);

	List<MomentMemberResponse> inquiryJoinedMembers(Long momentId);
}
