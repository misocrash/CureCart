import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
 
// The corrected Address interface
export interface Address {
  id: string;
  country: string;
  postalCode: string;
  addressLine1: string;
  addressLine2?: string; // Optional field
  state: string;
  isDefault: boolean;
}
 
@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private addresses: Address[] = [
    // Updated mock data to match the new interface
    {
      id: '1',
      country: 'India',
      postalCode: '400001',
      addressLine1: '123 Marine Drive, Apt 4B',
      state: 'Maharashtra',
      isDefault: true
    },
  ];
  private addressesSubject = new BehaviorSubject<Address[]>(this.addresses);
  addresses$ = this.addressesSubject.asObservable();
 
  constructor() { }
 
  // This method now expects the correct address type
  addAddress(address: Omit<Address, 'id' | 'isDefault'>) {
    const newAddress: Address = {
      ...address,
      id: Date.now().toString(),
      isDefault: this.addresses.length === 0
    };
    this.addresses.push(newAddress);
    this.addressesSubject.next(this.addresses);
  }
 
  setDefault(id: string) {
    this.addresses.forEach(addr => addr.isDefault = (addr.id === id));
    this.addressesSubject.next(this.addresses);
  }
 
  deleteAddress(id: string) {
    this.addresses = this.addresses.filter(addr => addr.id !== id);
    this.addressesSubject.next(this.addresses);
  }
}