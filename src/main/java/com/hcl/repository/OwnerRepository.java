package com.hcl.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.entities.Owner;
@Repository
public interface OwnerRepository extends JpaRepository<Owner,Integer>{
	

}
