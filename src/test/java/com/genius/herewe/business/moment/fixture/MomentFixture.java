package com.genius.herewe.business.moment.fixture;

import java.time.LocalDateTime;

import com.genius.herewe.business.moment.domain.Moment;

public class MomentFixture {
	public static Moment createDefault() {
		return builder().build();
	}

	public static MomentBuilder builder() {
		return new MomentBuilder();
	}

	public static class MomentBuilder {
		private LocalDateTime now = LocalDateTime.now();

		private String name = "모먼트 이름";
		private int participantCount = 1;
		private int capacity = 20;
		private LocalDateTime meetAt = now.plusDays(10);
		private LocalDateTime closedAt = now.plusDays(5);

		public MomentBuilder name(String name) {
			this.name = name;
			return this;
		}

		public MomentBuilder participantCount(int participantCount) {
			this.participantCount = participantCount;
			return this;
		}

		public MomentBuilder capacity(int capacity) {
			this.capacity = capacity;
			return this;
		}

		public MomentBuilder meetAt(LocalDateTime meetAt) {
			this.meetAt = meetAt;
			return this;
		}

		public MomentBuilder closedAt(LocalDateTime closedAt) {
			this.closedAt = closedAt;
			return this;
		}

		public Moment build() {
			return Moment.builder()
				.name(name)
				.participantCount(participantCount)
				.capacity(capacity)
				.meetAt(meetAt)
				.closedAt(closedAt)
				.build();
		}
	}
}
