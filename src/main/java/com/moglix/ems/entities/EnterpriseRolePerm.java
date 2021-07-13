package com.moglix.ems.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "enterprise_role_perm")
public class EnterpriseRolePerm {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_perm_id")
	private Integer rolePermId;
	
	@Column(name = "role_id")
	private Integer roleId;
	
	@Column(name = "perm_id")
	private Integer permId;
}
