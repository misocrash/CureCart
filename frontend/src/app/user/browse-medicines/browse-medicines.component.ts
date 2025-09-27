import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService, CartItem } from '../../services/cart.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../../environments/environment.development';

interface Medicine {
  id: string; name: string; category: string; stock: number; price: number; description: string; quantityInCart: number; cartItemId: number | null;
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

  constructor(private http: HttpClient, private cartService: CartService) { }

  ngOnInit() {
    this.fetchMedicines();
    this.syncWithCart();
  }

  fetchMedicines() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<any[]>(`${environment.endpoints.medicineBaseEndpoint}`, { headers: headers as { [header: string]: string | string[] } }).subscribe({
      next: (response) => {
        this.allMedicines = response.map(med => ({
          id: med.id.toString(),
          name: med.name,
          category: med.compositionText || 'Uncategorized',
          stock: med.stock,
          price: med.price,
          description: med.description || `${med.pack_size} by ${med.manufacture_name}`,
          quantityInCart: 0,
          cartItemId: null
        }));
        this.filteredMedicines = this.allMedicines;
        this.categories = ['All', ...new Set(this.allMedicines.map(m => m.category))];
        this.syncWithCart();
      },
      error: (error) => {
        console.error('Failed to fetch medicines:', error);
        alert('Failed to load medicines. Please try again later.');
      }
    });
  }

  syncWithCart() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<any>(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/cart`, { headers: headers as { [header: string]: string | string[] } }).subscribe(cart => {
      if (cart && cart.items) {
        this.allMedicines.forEach(med => {
          const itemInCart = cart.items.find((item: any) => item.medicineId === +med.id);
          if (itemInCart) {
            med.quantityInCart = itemInCart.quantity;
            med.cartItemId = itemInCart.cartItemId;
          } else {
            med.quantityInCart = 0;
            med.cartItemId = null;
          }
        });
        this.filterMedicines();
      }
    });
  }

  filterMedicines() {
    let tempMeds = this.allMedicines;

    if (this.selectedCategory !== 'All') {
      tempMeds = tempMeds.filter(med => med.category === this.selectedCategory);
    }

    if (this.searchTerm) {
      tempMeds = tempMeds.filter(med =>
        med.name.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }

    this.filteredMedicines = tempMeds;
  }

  increaseQuantity(medicine: Medicine) {
    const userId = localStorage.getItem('userId');
    const authToken = localStorage.getItem('authToken');
    if (!userId) {
      alert('You must be logged in to add items to the cart.');
      return;
    }

    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    const payload = {
      medicineId: +medicine.id,
      quantity: 1 // one at a time
    };

    const request = this.http.post<any>(`${environment.endpoints.userBaseEndpoint}/${userId}/cart/items`, payload, { headers: headers as { [header: string]: string | string[] } });

    request.pipe(
      catchError(err => {
        console.error('Error increasing quantity:', err);
        alert(`Failed to update cart for ${medicine.name}.`);
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        medicine.quantityInCart++;
        if (response.cartItemId) { 
          medicine.cartItemId = response.cartItemId;
        }
        console.log('Quantity increased:', response);
      }
    });
  }

  decreaseQuantity(medicine: Medicine) {
    if (medicine.quantityInCart <= 0) return;

    const userId = localStorage.getItem('userId');
    const authToken = localStorage.getItem('authToken');
    if (!userId || !medicine.cartItemId) {
      alert('An error occurred. Please refresh and try again.');
      return;
    }

    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const params = new HttpParams().set('quantity', medicine.quantityInCart - 1);

    const request = this.http.put<any>(`${environment.endpoints.userBaseEndpoint}/${userId}/cart/items/${medicine.cartItemId}`, {}, { headers: headers as { [header: string]: string | string[] }, params });
    request.pipe(
      catchError(err => {
        console.error('Error decreasing quantity:', err);
        alert(`Failed to update cart for ${medicine.name}.`);
        return of(null);
      })
    ).subscribe(response => {
      medicine.quantityInCart--;
      if (medicine.quantityInCart === 0) {
        medicine.cartItemId = null;
      }
      console.log('Quantity decreased');
    });
  }
}