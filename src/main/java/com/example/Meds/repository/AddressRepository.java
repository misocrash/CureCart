package com.example.Meds.repository;

import com.example.Meds.entity.Address;
import com.example.Meds.entity.User;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {


    List<Address> findByUserId(int userId);
}
