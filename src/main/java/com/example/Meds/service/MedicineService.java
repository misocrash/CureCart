package com.example.Meds.service;

import com.example.Meds.dto.MedicineAddRequestDTO;
import com.example.Meds.entity.Medicine;
import com.example.Meds.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {
    public final MedicineRepository medicineRepository;

    public MedicineService (MedicineRepository medicineRepository){
        this.medicineRepository=medicineRepository;

    }
    public List<Medicine> findAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine findMedicineById(int medicineId) {
        return medicineRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
    }

    public void addMedicine(MedicineAddRequestDTO addedMedicine) {
        Medicine med= new Medicine(
                addedMedicine.getName(),
                addedMedicine.getPrice(),
                addedMedicine.getManufacture_name(),
                addedMedicine.getPack_size(),
                addedMedicine.getCompositionTeXT()

        );
        medicineRepository.save(med);

    }

    public void deleteById(int medicineID) {
        medicineRepository.deleteById(medicineID);
    }
}
