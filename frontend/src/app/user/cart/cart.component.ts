import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

// Interface matching the backend response for a single cart item
export interface CartItem {
  cartItemId: number;
  medicineId: number;
  medicineName: string;
  price: number;
  quantity: number;
  subtotal: number;
}

// Interface for the entire cart response
export interface CartResponse {
  cartId: number;
  userId: number;
  items: CartItem[];
  totalAmount: number;
}
@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cartItems: CartItem[] = [];
  totalAmount = 0;
  isLoading = true;
  error: string | null = null;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit() {
    this.fetchCart();
  }

  fetchCart(): void {
    this.isLoading = true;
    this.error = null;
    const userId = localStorage.getItem('userId');
    const authToken = localStorage.getItem('authToken');

    if (!userId) {
      this.error = "User not found. Please log in again.";
      this.isLoading = false;
      return;
    }

    const apiUrl = `http://localhost:8099/api/users/${userId}/cart`;
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<CartResponse>(apiUrl, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error fetching cart:', err);
        this.error = 'Failed to load your cart. Please try again later.';
        this.isLoading = false;
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        this.cartItems = response.items;
        this.totalAmount = response.totalAmount;
      }
      this.isLoading = false;
    });
  }

  proceedToCheckout() {
    this.router.navigate(['/checkout'], { state: { totalAmount: this.totalAmount, items: this.cartItems } });
  }
}