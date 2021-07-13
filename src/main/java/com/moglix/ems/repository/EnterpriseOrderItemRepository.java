package com.moglix.ems.repository;

import com.moglix.ems.entities.EnterpriseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnterpriseOrderItemRepository extends JpaRepository<EnterpriseOrderItem,Integer> {

    Optional<EnterpriseOrderItem> findByItemRef(String itemRef);
}
