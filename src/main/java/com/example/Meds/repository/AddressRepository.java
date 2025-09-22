package com.example.Meds.repository;

import com.example.Meds.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {


//    Address findAddressById(long addressId);

    List<Address> findByUserId(int userId);
}
