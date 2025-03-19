package com.genius.herewe.business.location.search.client;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.genius.herewe.business.location.search.dto.Meta;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.location.search.dto.SearchResponse;
import com.genius.herewe.core.global.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoSearchClient {
	private final RestClient restClient;

	/**
	 * Kakao Map API를 통해 키워드를 이용해 검색 결과 반환
	 * @param keyword 검색 키워드
	 * @param size 키워드에 대한 검색 결과를 받을 페이지의 사이즈 (기본 값 10)
	 * @param page 키워드 검색 결과의 페이지 번호 - Kakao API에서는 1부터 시작, Spring에서는 0부터 시작임을 유의
	 */
	public Slice<Place> searchByKeyword(String keyword, int size, int page) {
		validatePageParam(keyword, size, page);

		SearchResponse searchResponse = restClient.get()
			.uri(uriBuilder ->
				uriBuilder.path("/search/keyword.json")
					.queryParam("query", keyword)
					.queryParam("size", size)
					.queryParam("page", page + 1)
					.build())
			.retrieve()
			.body(SearchResponse.class);

		List<Place> places = searchResponse.places();
		Meta meta = searchResponse.meta();

		return new SliceImpl<>(places, PageRequest.of(page, size), !meta.isEnd());
	}

	private void validatePageParam(String keyword, int size, int page) {
		if (keyword == null || keyword.isBlank()) {
			throw new BusinessException(INVALID_KEYWORD);
		}
		if (size <= 0 || size > 15 || page < 0) {
			throw new BusinessException(INVALID_PAGINATION_PARAM);
		}
	}
}
