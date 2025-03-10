package com.hcl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.entities.QueryResponse;
@Repository
public interface QueryResponseRepository extends JpaRepository<QueryResponse, Integer>{

}
