package com.genius.herewe.business.location.domain;

import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.moment.domain.Moment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "location",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_location_index_moment",
			columnNames = {"location_index", "moment_id"}
		)
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "location_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moment_id")
	private Moment moment;

	@Column(nullable = false, name = "location_index")
	@Max(value = 100, message = "장소는 최대 100개까지 등록할 수 있습니다.")
	private int locationIndex;

	@Column(nullable = false)
	private Long placeId;

	@Column(nullable = false)
	private String name;

	private String address;

	private String roadAddress;

	private String url;

	@Column(nullable = false)
	private Double x;

	@Column(nullable = false)
	private Double y;

	private String phone;

	@Builder
	public Location(int locationIndex, Long placeId, String name, String address, String roadAddress, String url,
		Double x,
		Double y,
		String phone) {
		this.locationIndex = locationIndex;
		this.placeId = placeId;
		this.name = name;
		this.address = address;
		this.roadAddress = roadAddress;
		this.url = url;
		this.x = x;
		this.y = y;
		this.phone = phone;
	}

	public static Location createFromPlace(Place place, int index) {
		return Location.builder()
			.locationIndex(index)
			.placeId(place.id())
			.name(place.name())
			.address(place.address())
			.roadAddress(place.roadAddress())
			.url(place.url())
			.x(place.x())
			.y(place.y())
			.phone(place.phone())
			.build();
	}

	//== 연관관계 편의 메서드 ==//
	public void addMoment(Moment moment) {
		this.moment = moment;
		if (!moment.getLocations().contains(this)) {
			moment.getLocations().add(this);
		}
	}

	//== 비지니스 로직 ==//
	public void update(Place place) {
		this.name = place.name();
		this.address = place.address();
		this.roadAddress = place.roadAddress();
		this.url = place.url();
		this.x = place.x();
		this.y = place.y();
		this.phone = place.phone();
	}
}
