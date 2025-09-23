package com.example.Meds.service.impl;

import com.example.Meds.entity.Address;
import com.example.Meds.repository.AddressRepository;
import com.example.Meds.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address addAddress(Address address) {
        if (address.isDefault()) {
            // Unset other default addresses for the user
            List<Address> existing = addressRepository.findByUserId(address.getUser().getId());
            for (Address addr : existing) {
                if (addr.isDefault()) {
                    addr.setDefault(false);
                    addressRepository.save(addr);
                }
            }
        }
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddressesByUserId(Integer userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address setDefaultAddress(Long addressId, Integer userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        Address target = addressRepository.findById(addressId).orElse(null);

        if (target == null || !target.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Address not found or does not belong to user");
        }

        for (Address addr : addresses) {
            addr.setDefault(addr.getAddressId().equals(addressId));
            addressRepository.save(addr);
        }

        return target;
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
