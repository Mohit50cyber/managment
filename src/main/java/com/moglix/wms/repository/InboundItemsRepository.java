package com.moglix.wms.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.wms.entities.InboundItem;

@Repository
public interface InboundItemsRepository extends CrudRepository<InboundItem, Integer>{
	Optional<InboundItem> findByBarcode(String barcode);
}
