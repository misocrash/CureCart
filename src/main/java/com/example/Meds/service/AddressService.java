package com.example.Meds.service;

import com.example.Meds.dto.AddressAddRequestDTO;
import com.example.Meds.dto.AddressGetAllRequestDTO;
import com.example.Meds.dto.AddressResponseDTO;
import com.example.Meds.dto.AddressUpdateRequestDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.entity.User;
import com.example.Meds.repository.AddressRepository;
import com.example.Meds.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<AddressGetAllRequestDTO> getAddressById(int userId) {
        User user= usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Address> addresses= addressRepository.findByUserId(userId);
//        return addresses.stream()
//                .map(address -> new AddressGetAllRequestDTO(
//                        address.getAddressId(),
//                        address.getUser().getId(),
//                        address.getLine1(),
//                        address.getLine2(),
//                        address.getCity(),
//                        address.getState(),
//                        address.getPostalCode(),
//                        address.getCountry(),
//                        address.isDefault()
//
//                )).collect(Collectors.toList());

        List<AddressGetAllRequestDTO> response = new ArrayList<>();
        for(Address address: addresses){
            AddressGetAllRequestDTO dto = new AddressGetAllRequestDTO(
                    address.getAddressId(),
                    address.getUser().getId(),
                    address.getLine1(),
                    address.getLine2(),
                    address.getCity(),
                    address.getState(),
                    address.getPostalCode(),
                    address.getCountry(),
                    address.isDefault()
            );
            response.add(dto);
        }

        return response;

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

