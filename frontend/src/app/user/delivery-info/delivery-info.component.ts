import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Observable, of } from 'rxjs';
import { Address, AddressService } from '../../services/address.service';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment.development';

// Matches the backend Address DTO
interface BackendAddress {
  addressId: number;
  line1: string;
  line2: string | null;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  default: boolean;
}

interface CartItem {
  medicineName: string;
  quantity: number;
  subtotal: number;
}
 
@Component({
  selector: 'app-delivery-info',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './delivery-info.component.html',
  styleUrls: ['./delivery-info.component.css']
})
export class DeliveryInfoComponent implements OnInit {
  view: 'select' | 'add' = 'select';
  addresses: Address[] = [];
  selectedAddressId: string | null = null;
 
  cartItems: CartItem[] = [];
  subtotal = 0;
  tax = 0;
  total = 0;
  itemsCount = 0;

  showToast = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';

  // This object now perfectly matches the expected type in the service
  newAddress = {
    country: 'India',
    addressLine1: '',
    addressLine2: '',
    city: '',
    postalCode: '',
    state: ''
  };
 
  constructor(
    private addressService: AddressService,
    private router: Router,
    private http: HttpClient
  ) {}
 
  ngOnInit() {
    this.fetchAddresses();
    // Mock cart data for summary, as cartService is removed
    // In a real app, this would come from a shared state or API
    this.cartItems = history.state.items || [];
    this.calculateSummary();
  }

  fetchAddresses() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<BackendAddress[]>(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/addresses`, { headers: headers as { [header: string]: string | string[] }}).pipe(
      catchError(err => {
        console.error('Error fetching addresses:', err);
        this.showErrorToast('Failed to load your saved addresses.');
        return of([]);
      })
    ).subscribe(backendAddresses => {
      // Map backend response to frontend Address model
      this.addresses = backendAddresses.map(addr => ({
        id: addr.addressId.toString(),
        country: addr.country,
        postalCode: addr.postalCode,
        addressLine1: addr.line1,
        addressLine2: addr.line2 || '',
        state: addr.state,
        isDefault: addr.default
      }));

      // Set the default selected address
      const defaultAddress = this.addresses.find(addr => addr.isDefault);
      if (defaultAddress && !this.selectedAddressId) {
        this.selectedAddressId = defaultAddress.id;
      }
    });
  }
 
  calculateSummary() {
    const state = history.state;
    this.subtotal = state.totalAmount;
    this.tax = this.subtotal * 0.05;
    this.itemsCount = state.itemsCount;
    this.total = this.subtotal + this.tax;
  }
 
  onAddAddress() {
    if (!this.newAddress.addressLine1 || !this.newAddress.postalCode || !this.newAddress.city || !this.newAddress.state) {
      this.showErrorToast('Please fill out all required fields.');
      return;
    }

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const payload = {
      line1: this.newAddress.addressLine1,
      line2: this.newAddress.addressLine2,
      city: this.newAddress.city,
      state: this.newAddress.state,
      postalCode: this.newAddress.postalCode,
      country: this.newAddress.country,
      isDefault: this.addresses.length === 0, // Make default if it's the first address
      userId: localStorage.getItem('userId')
    };

    this.http.post<BackendAddress>(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/addresses`, payload, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error adding address:', err);
        this.showErrorToast('Failed to save your new address. Please try again.');
        return of(null);
      })
    ).subscribe(savedAddress => {
      if (savedAddress) {
        this.showSuccessToast('Address saved successfully!');
        this.fetchAddresses(); // Refresh the list of addresses
        this.view = 'select'; // Switch back to the selection view
        // Automatically select the newly added address
        this.selectedAddressId = savedAddress.addressId.toString();
        // Reset form
        this.newAddress = { country: 'India', addressLine1: '', addressLine2: '', city: '', postalCode: '', state: '' };
      }
    });
  }
 
  placeOrder() {
    if (!this.selectedAddressId) {
      this.showErrorToast('Please select a delivery address.');
      return;
    }
    
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    const payload = {
      userId: +localStorage.getItem('userId' )!,
      addressId: +this.selectedAddressId,
    };

    this.http.post(`${environment.endpoints.userBaseEndpoint}/${localStorage.getItem('userId')}/orders`, payload, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error placing order:', err);
        this.showErrorToast('Failed to place the order');
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log(response)
        this.showSuccessToast('Order placed successfully!');
        setTimeout(() => {
          this.router.navigate(['/orders']);
        }, 1500);
      }});
  }

  private showSuccessToast(message: string) {
    this.toastMessage = message;
    this.toastType = 'success';
    this.showToast = true;
    setTimeout(() => this.showToast = false, 2000); // Hide after 3 seconds
  }

  private showErrorToast(message: string) {
    this.toastMessage = message;
    this.toastType = 'error';
    this.showToast = true;
    setTimeout(() => this.showToast = false, 2000); // Hide after 3 seconds
  }
}