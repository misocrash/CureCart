package com.example.Meds.service;


import com.example.Meds.entity.Address;

import java.util.List;

public interface AddressService {
    Address addAddress(Address address);
    List<Address> getAddressesByUserId(Integer userId);
    Address setDefaultAddress(Long addressId, Integer userId);
    void deleteAddress(Long addressId);
}
