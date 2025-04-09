package com.genius.herewe.business.invitation.facade;

import com.genius.herewe.business.crew.dto.CrewIdResponse;
import com.genius.herewe.business.invitation.dto.InvitationRequest;

public interface InvitationFacade {
	void inviteCrew(InvitationRequest invitationRequest);

	CrewIdResponse joinCrew(String inviteToken);
}
