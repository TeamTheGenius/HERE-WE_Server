package com.genius.herewe.business.invitation.facade;

import com.genius.herewe.business.invitation.dto.InvitationRequest;

public interface InvitationFacade {
	void inviteCrew(InvitationRequest invitationRequest);

	void joinCrew(String inviteToken);
}
