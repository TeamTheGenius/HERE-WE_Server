package com.genius.herewe.infra.mail.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.genius.herewe.infra.mail.dto.MailRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailManager {
	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;

	public void send(MailRequest mailRequest) {
		Context context = new Context();

		context.setVariable("nickname", mailRequest.nickname());
		context.setVariable("crewName", mailRequest.crewName());
		context.setVariable("crewIntroduce", mailRequest.introduce());
		context.setVariable("memberCount", mailRequest.memberCount());
		context.setVariable("inviteUrl", mailRequest.inviteUrl());

		MimeMessagePreparator preparatory = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

			String content = templateEngine.process("crew-invitation", context);

			helper.setTo(mailRequest.receiver());
			helper.setFrom("genius.herewe@gmail.com", "HERE:WE");
			helper.setSubject("[HERE:WE] 크루 초대가 도착했습니다 :)");

			helper.setText(content, true);
		};

		mailSender.send(preparatory);
	}
}
