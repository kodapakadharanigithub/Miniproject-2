package com.hcl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hcl.entities.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users,Integer>{

}
