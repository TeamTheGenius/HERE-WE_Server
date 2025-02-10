package com.genius.herewe.user.domain;

import java.util.ArrayList;
import java.util.List;

import com.genius.herewe.crew.domain.CrewMember;
import com.genius.herewe.file.domain.FileHolder;
import com.genius.herewe.file.domain.Files;
import com.genius.herewe.moment.domain.MomentMember;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User implements FileHolder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "users_id")
	private Long id;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CrewMember> crewMembers = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MomentMember> momentMembers = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "files_id")
	private Files files;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Role role;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private ProviderInfo providerInfo;

	private String email;

	@Column(unique = true, length = 20)
	private String nickname;

	@Builder
	public User(ProviderInfo providerInfo, Role role, String email, String nickname) {
		this.providerInfo = providerInfo;
		this.role = role;
		this.email = email;
		this.nickname = nickname;
	}

	@Override
	public void setFiles(Files files) {
		this.files = files;
	}
}
