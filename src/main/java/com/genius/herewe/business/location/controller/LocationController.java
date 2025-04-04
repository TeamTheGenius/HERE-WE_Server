package com.genius.herewe.business.location.controller;

import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.dto.PlaceResponse;
import com.genius.herewe.business.location.facade.LocationFacade;
import com.genius.herewe.business.location.search.client.KakaoSearchClient;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.global.response.SlicingResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LocationController implements LocationApi {
	private final KakaoSearchClient kakaoSearchClient;
	private final LocationFacade locationFacade;

	@GetMapping("/search/location")
	public SlicingResponse<Place> search(@RequestParam("keyword") String keyword,
										 @RequestParam(defaultValue = "0") int page,
										 @RequestParam(defaultValue = "15") int size) {

		Slice<Place> places = kakaoSearchClient.searchByKeyword(keyword, size, page);

		return new SlicingResponse<>(HttpStatus.OK, places);
	}

	@GetMapping("/location/{momentId}")
	public SingleResponse<PlaceResponse> inquiryPlaces(@PathVariable Long momentId) {
		PlaceResponse placeResponse = locationFacade.inquiryAll(momentId);
		return new SingleResponse<>(HttpStatus.OK, placeResponse);
	}

	@PostMapping("/location/{momentId}")
	public CommonResponse addPlace(@HereWeUser User user,
								   @PathVariable Long momentId,
								   @Valid @RequestBody LocationRequest locationRequest) {

		locationFacade.addPlace(user.getId(), momentId, locationRequest);
		return CommonResponse.created();
	}

	@DeleteMapping("/location/{momentId}")
	public CommonResponse deletePlace(@HereWeUser User user,
									  @PathVariable Long momentId,
									  @RequestParam("index") int locationIndex) {

		locationFacade.deletePlace(user.getId(), momentId, locationIndex);
		return CommonResponse.ok();
	}
}
