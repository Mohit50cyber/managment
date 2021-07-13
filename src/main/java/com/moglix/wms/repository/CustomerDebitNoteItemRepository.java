package com.moglix.wms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.CustomerDebitNoteItem;

@Repository
public interface CustomerDebitNoteItemRepository extends CrudRepository<CustomerDebitNoteItem, Integer> {

}
