package com.ehr.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {
	private final Logger LOG = Logger.getLogger(TransactionAdvice.class);
	private PlatformTransactionManager transactionManager;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// 트랜잭션 시작
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {

			Object obj = invocation.proceed();
			// 정상이면 Commit
			transactionManager.commit(status);
			return obj;
		} catch (RuntimeException e) {
			LOG.debug("=========================");
			LOG.debug("=Exception=" + e.toString());
			LOG.debug("=rollback =" + transactionManager.toString());
			LOG.debug("=status =" + status.toString());
			LOG.debug("=========================");
			// 실패면 rollback
			transactionManager.rollback(status);
			throw e;
		}
	}

}
