import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CartService, CartItem } from '../../services/cart.service';
import { catchError } from 'rxjs/operators';
import { of, forkJoin } from 'rxjs';

interface CartItemPayload {
  medicineId: number;
  quantity: number;
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {
  address = {
    fullName: '', address: '', city: '', state: '', zipCode: '', phone: ''
  };

  cartItems: CartItem[] = [];
  totalAmount = 0;
  isPlacingOrder = false;
  cartId: number | null = null;

  constructor(private http: HttpClient, private cartService: CartService, private router: Router) {}

  ngOnInit(): void {
    const state = history.state;
    if (state && state.items && state.totalAmount !== undefined) {
      this.cartItems = state.items;
      this.totalAmount = state.totalAmount;
    } else {
      this.router.navigate(['/cart']);
    }
  }

  placeOrder() {
    console.log('Order placed for address:', this.address);
    const authToken = localStorage.getItem('authToken');
    const userId = localStorage.getItem('userId');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    if (!userId) {
      console.error('User ID not found in localStorage');
      alert('Unable to fetch cart. Please log in again.');
      return;
    }

    this.isPlacingOrder = true;

    // Step 1: Fetch cartId from backend
    this.http.get<any>(`http://localhost:8099/api/users/${userId}/cart`, {
      headers: headers as { [header: string]: string | string[] }
    }).subscribe({
      next: (response) => {
        this.cartId = response.cartId;
        console.log('Fetched cartId:', this.cartId);

        const orderApiUrl = `http://localhost:8099/api/users/${userId}/cart/items`;

        // Step 2: Send individual POST requests for each item
        const requests = this.cartItems.map(item => {
          const payload = {
            cartId: this.cartId,
            medicineId: +item.id,
            quantity: item.quantity
          };

          return this.http.post(orderApiUrl, payload, {
            headers: headers as { [header: string]: string | string[] }
          }).pipe(
            catchError(err => {
              console.error(`Failed to place order for medicineId ${item.id}`, err);
              return of(null);
            })
          );
        });

        forkJoin(requests).subscribe(responses => {
          const successful = responses.filter(r => r !== null);
          if (successful.length === this.cartItems.length) {
            console.log('All items placed successfully');
            this.cartService.clearCart();
            alert('Order placed successfully!');
            this.router.navigate(['/orders']);
          } else {
            alert('Some items failed to place. Please check your cart.');
          }
          this.isPlacingOrder = false;
        });
      },
      error: (error) => {
        console.error('Failed to fetch cart:', error);
        alert('Failed to load cart. Please try again later.');
        this.isPlacingOrder = false;
      }
    });
  }
}
