package com.genius.herewe.business.moment.facade;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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

	Slice<MomentMemberResponse> inquiryJoinedMembers(Long momentId, Pageable pageable);
}
