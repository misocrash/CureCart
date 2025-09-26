import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService, CartItem } from '../../services/cart.service';

interface Medicine {
  id: string; name: string; category: string; stock: number; price: number; description: string;
}

@Component({
  selector: 'app-browse-medicines',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './browse-medicines.component.html',
  styleUrls: ['./browse-medicines.component.css']
})
export class BrowseMedicinesComponent implements OnInit {
  allMedicines: Medicine[] = [];
  filteredMedicines: Medicine[] = [];
  categories: string[] = [];
  
  searchTerm = '';
  selectedCategory = 'All';

  constructor(private cartService: CartService) {}

  ngOnInit() {
    // In a real app, this data would come from an API
    this.allMedicines = [
      { id: '1', name: 'Paracetamol 500mg', category: 'Pain Relief', stock: 450, price: 12.99, description: 'Effective pain relief and fever reducer.' },
      { id: '2', name: 'Amoxicillin 250mg', category: 'Antibiotics', stock: 89, price: 24.50, description: 'Treats a wide variety of bacterial infections.' },
      { id: '3', name: 'Ibuprofen 400mg', category: 'Pain Relief', stock: 234, price: 18.75, description: 'Reduces inflammation and treats pain.' },
      { id: '4', name: 'Loratadine 10mg', category: 'Allergy Relief', stock: 150, price: 21.00, description: 'Non-drowsy antihistamine for allergies.' },
    ];
    this.filteredMedicines = this.allMedicines;
    this.categories = ['All', ...new Set(this.allMedicines.map(m => m.category))];
  }

  filterMedicines() {
    let tempMeds = this.allMedicines;

    // Filter by category
    if (this.selectedCategory !== 'All') {
      tempMeds = tempMeds.filter(med => med.category === this.selectedCategory);
    }

    // Filter by search term
    if (this.searchTerm) {
      tempMeds = tempMeds.filter(med => 
        med.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }

    this.filteredMedicines = tempMeds;
  }

  addToCart(medicine: Medicine) {
    this.cartService.addToCart({
      id: medicine.id, name: medicine.name, price: medicine.price, quantity: 1
    });
    alert(`${medicine.name} added to cart!`);
  }
}