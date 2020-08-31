package com.kakaopay.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kakaopay.model.CompletedMoney;
import com.kakaopay.model.Money;

public interface MoneyJpaRepo extends JpaRepository<Money, String> {
	@Query(value = "SELECT * FROM TMONEY WHERE TOKEN=?1 AND RCV_ID IS NULL LIMIT 1;", nativeQuery = true)
	Money findOneByToken(String tokenStr);
	
	@Query(value = "SELECT COUNT(*) FROM TMONEY WHERE TOKEN = ?1 AND RCV_ID = ?2", nativeQuery = true)
	int countByTokenAndUserId(String tokenStr, String userId);
	
	@Query(value = "SELECT * FROM TMONEY WHERE TOKEN = ?1 AND RCV_ID IS NOT NULL", nativeQuery = true)
	List<Money> findRecvCompletedAllByToken(String tokenStr);
	
	@Query(value = "SELECT new com.kakaopay.model.CompletedMoney(t.partAmt, t.rcvId) FROM Money as t WHERE t.token = ?1 AND t.rcvId IS NOT NULL")
	List<CompletedMoney> findRecvCompletedPartInfoAllByToken(String tokenStr);
	
}
