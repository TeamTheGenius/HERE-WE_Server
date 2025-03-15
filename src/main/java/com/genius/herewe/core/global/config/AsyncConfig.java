package com.genius.herewe.core.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.genius.herewe.core.global.exception.AsyncExceptionHandler;

@Configuration
public class AsyncConfig implements AsyncConfigurer {
	private int CORE_POOL_SIZE = 10; // 스레드 풀의 코어 스레드 수
	private int MAX_POOL_SIZE = 30; // 스레드 풀의 최대 스레드 수
	private int QUEUE_CAPACITY = 10000; // 작업 큐의 용량

	@Bean(name = "threadExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

		taskExecutor.setCorePoolSize(CORE_POOL_SIZE);
		taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
		taskExecutor.setQueueCapacity(QUEUE_CAPACITY);
		taskExecutor.setThreadNamePrefix("Executor-");
		taskExecutor.initialize();

		// NOTE: SecurityContext, 사용자 컨텍스트 전파 필요 시 Decorator 도입하기

		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncExceptionHandler();
	}
}
