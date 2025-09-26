import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// Define the structure for an item within an order
interface OrderItem {
  medicine: string;
  quantity: number;
  price: number;
}

// Update the main Order interface
type OrderStatus = 'pending' | 'shipped';
interface Order {
  id: string;
  date: string;
  customer: string;
  items: number;
  total: number;
  status: OrderStatus;
  orderItems: OrderItem[]; // Add a list of items
}

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent {
  // Add detailed item data to the mock orders
  orders: Order[] = [
    { 
      id: 'ORD-001', date: 'Jan 15, 2024', customer: 'John Smith', items: 3, total: 48.48, status: 'pending',
      orderItems: [
        { medicine: 'Paracetamol 500mg', quantity: 2, price: 12.99 },
        { medicine: 'Vitamin D3', quantity: 1, price: 22.50 }
      ]
    },
    { 
      id: 'ORD-002', date: 'Jan 14, 2024', customer: 'Maria Garcia', items: 1, total: 135.00, status: 'shipped',
      orderItems: [
        { medicine: 'Insulin Pen', quantity: 1, price: 135.00 }
      ]
    },
  ];

  isModalOpen = false;
  selectedOrder: Order | null = null;

  viewOrder(order: Order) {
    this.selectedOrder = order;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedOrder = null;
  }

  get pendingOrdersCount(): number {
    return this.orders.filter(o => o.status === 'pending').length;
  }

  get shippedOrdersCount(): number {
    return this.orders.filter(o => o.status === 'shipped').length;
  }
}