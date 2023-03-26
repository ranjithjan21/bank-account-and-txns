package eu.whitebox.bank.data.repo;

import eu.whitebox.bank.data.entity.Account;
import eu.whitebox.bank.data.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ranjith
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.transactionDate >= :fromDate")
    List<Transaction> findTransactionsByAccountIdAndFromDate(@Param("accountId") String accountId, @Param("fromDate") LocalDateTime fromDate);

    List<Account> findAccountsByBalanceLessThan(BigDecimal balance);
}
