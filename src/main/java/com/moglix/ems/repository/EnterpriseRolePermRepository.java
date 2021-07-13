package com.moglix.ems.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.moglix.ems.entities.EnterpriseRolePerm;

@Repository
public interface EnterpriseRolePermRepository extends CrudRepository<EnterpriseRolePerm, Integer>{

	@Query(value = "select users.email from enterprise_role_perm erp join enterprise_user_role eur on erp.role_id = eur.role_id join users on eur.user_id = users.id where erp.perm_id = 254", nativeQuery = true)
	List<String> getPermittedUsers();
}
