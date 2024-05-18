package com.hcl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hcl.entities.QueryRequest;
@Repository
public interface QueryRequestRepository extends JpaRepository<QueryRequest,Integer>{
	
	@Query(value="select * from Query_request where status=?1 and user_id=?2",nativeQuery=true)
	public QueryRequest findQueries(String status,int user_id); 

}
