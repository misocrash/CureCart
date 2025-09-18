package com.example.Meds.service;

import com.example.Meds.dto.AddressAddRequestDTO;
import com.example.Meds.dto.AddressResponseDTO;
import com.example.Meds.dto.AddressUpdateRequestDTO;
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

    public AddressResponseDTO updateAddress(long id, AddressUpdateRequestDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No address found"));

        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setDefault(dto.isDefault());

        Address savedAddress = addressRepository.save(address);
        return convertToResponseDTO(savedAddress);
    }

    public AddressResponseDTO convertToResponseDTO(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setAddressId(address.getAddressId());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setDefault(address.isDefault());
        return dto;
    }

}
