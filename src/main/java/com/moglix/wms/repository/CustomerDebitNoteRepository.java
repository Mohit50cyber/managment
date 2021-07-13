package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.constants.CustomerDebitNoteStatus;
import com.moglix.wms.entities.CustomerDebitNote;

@Repository
public interface CustomerDebitNoteRepository extends CrudRepository<CustomerDebitNote, Integer> {
	
	CustomerDebitNote findByDebitNoteNumber(String debitNoteNumber);

	CustomerDebitNote findByDebitNoteNumberAndStatus(String customerDebitNoteNumber, CustomerDebitNoteStatus created);

}
