package com.genius.herewe.business.location.handler;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import org.hibernate.StaleStateException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.genius.herewe.core.global.exception.BusinessException;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RetryHandler {
	private static final int MAX_RETRIES = 3;

	public <T> T executeWithRetry(RetryableOperation<T> operation) {
		int retryCount = 0;
		while (retryCount < MAX_RETRIES) {
			try {
				return operation.execute();
			} catch (Exception e) {
				retryCount++;
				if (shouldRetry(e) && retryCount < MAX_RETRIES) {
					logRetryAttempt(retryCount, e);
					applyBackoff(retryCount);
				} else {
					throw translateException(e);
				}
			}
		}
		throw new BusinessException(UNEXPECTED_ERROR);
	}

	private boolean shouldRetry(Exception e) {
		boolean isDbException = e instanceof OptimisticLockException
			|| e instanceof DataAccessException
			|| e instanceof StaleStateException;

		boolean isRetryException = (e instanceof BusinessException) &&
			(((BusinessException)e).getErrorCode() == CONCURRENT_MODIFICATION_EXCEPTION);

		boolean hasRetryCause = e.getCause() != null && (
			e.getCause() instanceof DataAccessException ||
				(e.getCause().getMessage() != null &&
					e.getCause().getMessage().contains("Deadlock"))
		);

		return isDbException || isRetryException || hasRetryCause;
	}

	private void applyBackoff(int retryCount) {
		try {
			long delay = (long)(100 * Math.pow(2, retryCount - 1));
			Thread.sleep(delay);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	private void logRetryAttempt(int retryCount, Exception e) {
		log.warn("Retry attempt {}/{} : error={}", retryCount, MAX_RETRIES, e.getMessage());
	}

	private RuntimeException translateException(Exception e) {
		if (e instanceof BusinessException) {
			return (BusinessException)e;
		} else if (e instanceof OptimisticLockException || e instanceof DataAccessException) {
			return new BusinessException(CONCURRENT_MODIFICATION_EXCEPTION, e);
		} else {
			return new BusinessException(UNEXPECTED_ERROR, e);
		}
	}
}
