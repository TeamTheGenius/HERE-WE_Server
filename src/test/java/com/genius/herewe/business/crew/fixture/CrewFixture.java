package com.genius.herewe.business.crew.fixture;

import com.genius.herewe.business.crew.domain.Crew;

public class CrewFixture {
	public static Crew createDefault() {
		return builder()
			.build();
	}

	public static Crew createByName(String name) {
		return builder()
			.name(name)
			.build();
	}

	public static CrewFixture.CrewBuilder builder() {
		return new CrewFixture.CrewBuilder();
	}

	public static class CrewBuilder {
		private Long id = 1L;
		private String leaderName = "";
		private String name = "crew name";
		private String introduce = "crew introduce";
		private int participantCount = 1;

		public CrewBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public CrewBuilder leaderName(String leaderName) {
			this.leaderName = leaderName;
			return this;
		}

		public CrewBuilder name(String name) {
			this.name = name;
			return this;
		}

		public CrewBuilder introduce(String introduce) {
			this.introduce = introduce;
			return this;
		}

		public CrewBuilder participantCount(int participantCount) {
			this.participantCount = participantCount;
			return this;
		}

		public Crew build() {
			return Crew.builder()
				.id(id)
				.leaderName(leaderName)
				.name(name)
				.introduce(introduce)
				.participantCount(participantCount)
				.build();
		}
	}
}
