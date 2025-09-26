import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Medicine {
  manufacturer: string;
  category: string;
  stock: number;
  price: number;
}

@Component({
  selector: 'app-medicines-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './medicines-list.component.html',
  styleUrls: ['./medicines-list.component.css']
})
export class MedicinesListComponent {
  medicines: Medicine[] = [
    { manufacturer: 'Paracetamol 500mg', category: 'Pain Relief', stock: 450, price: 12.99 },
    { manufacturer: 'Amoxicillin 250mg', category: 'Antibiotics', stock: 89, price: 24.50 },
    { manufacturer: 'Ibuprofen 400mg', category: 'Pain Relief', stock: 234, price: 18.75 },
    { manufacturer: 'Insulin Pen', category: 'Diabetes', stock: 45, price: 125.00 },
    { manufacturer: 'Blood Pressure Monitor', category: 'Equipment', stock: 12, price: 85.99 },
    { manufacturer: 'Cough Syrup', category: 'Respiratory', stock: 156, price: 15.25 },
  ];

  isLowStock(stock: number): boolean {
    return stock < 50; // Example threshold for low stock
  }
}