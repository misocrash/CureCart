import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService, CartItem } from '../../services/cart.service';
import { HttpClient } from '@angular/common/http';

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

  private medicineUrl = 'http://localhost:8099/api/medicines'; // Replace with your API endpoint

  constructor(private http: HttpClient, private cartService: CartService) {}

  ngOnInit() {
    this.fetchMedicines();
  }

  fetchMedicines() {

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    
    this.http.get<any[]>(this.medicineUrl, { headers: headers as { [header: string]: string | string[] } }).subscribe({
      next: (response) => {
      // Transform the response to match the Medicine interface
      this.allMedicines = response.map(med => ({
        id: med.id.toString(),
        name: med.name,
        category: med.compositionText || 'Uncategorized', // Use compositionText as category
        stock: med.stock,
        price: med.price,
        description: med.description || `${med.pack_size} by ${med.manufacture_name}`
      }));

      // Initialize filteredMedicines and categories
      this.filteredMedicines = this.allMedicines;
      this.categories = ['All', ...new Set(this.allMedicines.map(m => m.category))];
      },
      error: (error) => {
      console.error('Failed to fetch medicines:', error);
      alert('Failed to load medicines. Please try again later.');
      }
    });
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