package com.example.Meds.controller;


import com.example.Meds.dto.MedicineAddRequestDTO;
import com.example.Meds.entity.Medicine;
import com.example.Meds.service.MedicineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {
    //POST   /medicine/add          → Add new medicine//done
    //GET    /medicine/             → Get all medicines//DONE
    //GET    /medicine/{id}         → Get medicine by ID//DONE
    //PUT    /medicine/update/{id}  → Update medicine//NAHI CHAHIYE
    //DELETE /medicine/delete/{id}  → Delete medicine

    public final MedicineService medicineService;

    public MedicineController(MedicineService medicineService){
        this.medicineService=medicineService;
    }

    @DeleteMapping("/delete/{medicineId}")
    public ResponseEntity<?> deleteById(@PathVariable int medicineId){
        medicineService.deleteById(medicineId);
        return ResponseEntity.ok("Medicine deleted");
    }


    @PostMapping("/addMedicine")
    public ResponseEntity<?> addMedicine(@RequestBody MedicineAddRequestDTO addedMedicine){
        medicineService.addMedicine(addedMedicine);
        return new ResponseEntity<>("Medicine Added!!", HttpStatus.CREATED);
    }


    @GetMapping("/")
    public ResponseEntity<List<Medicine>> findAllMedicines(){
        List<Medicine> medicineList =medicineService.findAllMedicines();
        return ResponseEntity.ok(medicineList);
    }


    @GetMapping("/{medicineId}")
    public ResponseEntity<?> findMedicineById(@PathVariable int medicineId){
        Medicine med= medicineService.findMedicineById(medicineId);
        return ResponseEntity.ok(med);
    }

}
