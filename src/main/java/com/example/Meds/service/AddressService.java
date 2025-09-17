package com.example.Meds.service;

import com.example.Meds.dto.AddressAddRequestDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.entity.User;
import com.example.Meds.repository.AddressRepository;
import com.example.Meds.repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    public AddressService(AddressRepository addressRepository, UsersRepository usersRepository) {
        this.addressRepository = addressRepository;
        this.usersRepository=usersRepository;
    }

    public void addAddress(int userId, AddressAddRequestDTO addedAddress) {
        User user= usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address= new Address(
                user,
                addedAddress.getLine1(),
                addedAddress.getLine2(),
                addedAddress.getCity(),
                addedAddress.getState(),
                addedAddress.getPostalCode(),
                addedAddress.getCountry(),
                addedAddress.isDefault()
        );

        addressRepository.save(address);
    }
}
