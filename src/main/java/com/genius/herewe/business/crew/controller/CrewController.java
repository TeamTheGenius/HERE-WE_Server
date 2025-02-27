package com.genius.herewe.business.crew.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewCreateResponse;
import com.genius.herewe.business.crew.facade.CrewFacade;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crew")
@RequiredArgsConstructor
public class CrewController {
	private final CrewFacade crewFacade;

	@PostMapping
	public SingleResponse<CrewCreateResponse> createCrew(
		@HereWeUser User user,
		@RequestBody CrewCreateRequest crewCreateRequest) {

		CrewCreateResponse response = crewFacade.createCrew(user.getId(), crewCreateRequest);

		return new SingleResponse<>(HttpStatus.CREATED, response);
	}
}
