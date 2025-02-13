package com.genius.herewe.business.location.domain;

import com.genius.herewe.business.moment.domain.Moment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "location_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "moment_id")
	private Moment moment;

	@Column(nullable = false)
	private int locationIndex;

	@Column(nullable = false)
	private String name;

	private String address;

	private String roadAddress;

	@Column(nullable = false)
	private Double x;

	@Column(nullable = false)
	private Double y;

	private String phone;

	@Builder

	public Location(int locationIndex, String name, String address, String roadAddress, Double x, Double y,
		String phone) {
		this.locationIndex = locationIndex;
		this.name = name;
		this.address = address;
		this.roadAddress = roadAddress;
		this.x = x;
		this.y = y;
		this.phone = phone;
	}
}
