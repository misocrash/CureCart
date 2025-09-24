import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Product {
  id: string;
  name: string;
  category: string;
  stock: number;
  price: number;
  minStock: number;
}

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-management.component.html',
  styleUrls: ['./product-management.component.css']
})
export class ProductManagementComponent {
  products: Product[] = [
    { id: '1', name: 'Paracetamol 500mg', category: 'Pain Relief', stock: 450, price: 12.99, minStock: 100 },
    { id: '2', name: 'Amoxicillin 250mg', category: 'Antibiotics', stock: 89, price: 24.50, minStock: 50 },
    { id: '3', name: 'Ibuprofen 400mg', category: 'Pain Relief', stock: 234, price: 18.75, minStock: 100 },
  ];
}