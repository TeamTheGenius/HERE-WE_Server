package com.genius.herewe.business.moment.facade;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.genius.herewe.business.moment.dto.MomentIncomingResponse;
import com.genius.herewe.business.moment.dto.MomentMemberResponse;
import com.genius.herewe.business.moment.dto.MomentPreviewResponse;
import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.core.user.domain.User;

public interface MomentFacade {
	Page<MomentIncomingResponse> inquiryIncomingList(Long userId, LocalDateTime now, Pageable pageable);

	Page<MomentPreviewResponse> inquiryList(Long userId, Long crewId, LocalDateTime now, Pageable pageable);

	MomentResponse inquirySingle(User user, Long momentId, LocalDateTime now);

	MomentResponse create(Long userId, Long crewId, MomentRequest momentRequest);

	MomentResponse modify(Long momentId, MomentRequest momentRequest, LocalDateTime now);

	void delete(Long momentId);

	MomentResponse join(Long userId, Long momentId, LocalDateTime now);

	void quit(Long userId, Long momentId, LocalDateTime now);

	Slice<MomentMemberResponse> inquiryJoinedMembers(Long momentId, Pageable pageable);
}
