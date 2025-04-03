package com.genius.herewe.business.location.handler;

@FunctionalInterface
public interface RetryableOperation<T> {
	T execute() throws Exception;
}
