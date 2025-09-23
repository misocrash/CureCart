package com.example.Meds.service;

import com.example.Meds.entity.Medicine;

import java.util.List;

public interface MedicineService {

    Medicine addMedicine(Medicine medicine);

    Medicine getMedicineById(Long id);

    List<Medicine> getAllMedicines();

    Medicine updateMedicine(Long id, Medicine updatedMedicine);

    void deleteMedicine(Long id);
}