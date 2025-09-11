package com.example.Meds.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;

@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name="manufacture_name")
    private String manufacture_name;

    @Column(name="pack_size")
    private String pack_size;

    @Column(name = "composition_text", columnDefinition = "TEXT")
    private String compositionText;

    // Getters & Setters

    public Medicine(){

    }

    public Medicine(String name, BigDecimal price , String manufacture_name, String pack_size, String compositionText){
        this.name = name;
        this.price = price;
        this.manufacture_name = manufacture_name;
        this.pack_size = pack_size;
        this.compositionText = compositionText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getManufacture_name() {
        return manufacture_name;
    }

    public void setManufacture_name(String manufacture_name) {
        this.manufacture_name = manufacture_name;
    }

    public String getPack_size() {
        return pack_size;
    }

    public void setPack_size(String pack_size) {
        this.pack_size = pack_size;
    }

    public String getCompositionText() {
        return compositionText;
    }

    public void setCompositionText(String compositionText) {
        this.compositionText = compositionText;
    }

    //toString

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", manufacture_name='" + manufacture_name + '\'' +
                ", pack_size='" + pack_size + '\'' +
                ", compositionText='" + compositionText + '\'' +
                '}';
    }
}
