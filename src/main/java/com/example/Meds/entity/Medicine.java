package com.example.Meds.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;


import java.math.BigDecimal;

@Entity
//this name doesnt have to match the table name in mysql workbench
@Table(name = "medicines")
@Data
@RequiredArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    //this name has to match sql column
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name="manufacture_name")
    private String manufacture_name;

    @Column(name="pack_size")
    private String pack_size;

    @Column(name = "composition_text", columnDefinition = "TEXT")
    private String compositionText;

    public Medicine(String name, BigDecimal price, String manufacture_name, String pack_size, String compositionText) {
        this.name = name;
        this.price = price;
        this.manufacture_name = manufacture_name;
        this.pack_size = pack_size;
        this.compositionText = compositionText;
    }
}
