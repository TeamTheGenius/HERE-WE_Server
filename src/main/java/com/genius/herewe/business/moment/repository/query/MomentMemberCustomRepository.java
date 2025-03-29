package com.genius.herewe.business.moment.repository.query;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.genius.herewe.core.user.domain.User;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MomentMemberCustomRepository {
	private final EntityManager entityManager;

	public List<User> findAllJoinedUsers(Long momentId) {
		return entityManager.createQuery("""
				SELECT u
				FROM MomentMember mm
				JOIN mm.user u
				WHERE mm.moment.id = :momentId
				ORDER BY mm.joinedAt ASC, u.nickname ASC
				""", User.class)
			.setParameter("momentId", momentId)
			.setMaxResults(100)
			.getResultList();
	}
}
