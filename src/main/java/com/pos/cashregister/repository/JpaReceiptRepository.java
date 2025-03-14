package com.pos.cashregister.repository;

import com.pos.cashregister.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReceiptRepository extends JpaRepository<Receipt, Long> {

}
