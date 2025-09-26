import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent {
  address = {
    fullName: '', address: '', city: '', state: '', zipCode: '', phone: ''
  };

  constructor(private cartService: CartService, private router: Router) {}

  placeOrder() {
    console.log('Order placed for address:', this.address);
    this.cartService.clearCart();
    alert('Order placed successfully!');
    this.router.navigate(['/dashboard']);
  }
}