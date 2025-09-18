package com.example.Meds.controller;


import com.example.Meds.dto.AddressAddRequestDTO;
import com.example.Meds.dto.AddressGetAllRequestDTO;
import com.example.Meds.dto.AddressResponseDTO;
import com.example.Meds.dto.AddressUpdateRequestDTO;
import com.example.Meds.entity.Address;
import com.example.Meds.service.AddressService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;
    public AddressController(AddressService addressService){
        this.addressService=addressService;
    }

//    //WAOW YE HO GAYA// POST   /address/add           → Add a new address
//    GET    /address/              → Get all addresses// nahi chahiye
//    GET    /address/{id}          → Get address by user ID//miso
//    PUT    /address/update/{id}   → Update address by address ID//arnav
//    DELETE /address/delete/{id}   → Delete address by address ID

    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addAddress(@PathVariable int userId, @RequestBody AddressAddRequestDTO addedAddress){
        addressService.addAddress(userId, addedAddress);
        return new ResponseEntity<>("Address Added!!", HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable int id, @RequestBody AddressUpdateRequestDTO updatedAddressDTO) {
        AddressResponseDTO responseDTO = addressService.updateAddress(id, updatedAddressDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AddressGetAllRequestDTO>> getAddressById(@PathVariable int userId){
        List<AddressGetAllRequestDTO> addressList= addressService.getAddressById(userId);
        return ResponseEntity.ok(addressList);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAddressById(@PathVariable long id) {
        addressService.deleteAddressById(id);
        return ResponseEntity.ok("Address deleted!");
    }


}
