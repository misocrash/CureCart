import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface CartItem {
  id: string; name: string; quantity: number; price: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSource = new BehaviorSubject<CartItem[]>([]);
  currentCart = this.cartItemsSource.asObservable();

  addToCart(item: CartItem) {
    const currentItems = this.cartItemsSource.getValue();
    const existingItem = currentItems.find(cartItem => cartItem.id === item.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      currentItems.push({ ...item, quantity: 1 });
    }
    this.cartItemsSource.next(currentItems);
  }

  getCartItems() {
    return this.cartItemsSource.getValue();
  }

  clearCart() {
    this.cartItemsSource.next([]);
  }
}