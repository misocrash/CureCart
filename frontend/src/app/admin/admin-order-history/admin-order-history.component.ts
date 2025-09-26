import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// --- Interfaces for our data structure ---
interface OrderItem {
  medicine: string;
  quantity: number;
  price: number;
}

type OrderStatus = 'pending' | 'shipped' | 'delivered' | 'cancelled';

interface Order {
  id: string;
  date: string;
  customer: string;
  items: number;
  total: number;
  status: OrderStatus;
  orderItems: OrderItem[];
}

@Component({
  selector: 'app-admin-order-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-order-history.component.html',
  styleUrls: ['./admin-order-history.component.css']
})
export class AdminOrderHistoryComponent implements OnInit {
  // Correctly named properties to match the previous response
  allOrders: Order[] = [];
  filteredOrders: Order[] = [];
  
  isModalOpen = false;
  selectedOrder: Order | null = null;
  searchTerm: string = '';

  ngOnInit() {
    // --- Mock Data ---
    this.allOrders = [
      { 
        id: 'ORD-001', date: 'Jan 15, 2024', customer: 'John Smith', items: 2, total: 48.48, status: 'pending',
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
       { 
        id: 'ORD-003', date: 'Jan 13, 2024', customer: 'David Wilson', items: 3, total: 75.20, status: 'delivered',
        orderItems: [
          { medicine: 'Ibuprofen 400mg', quantity: 1, price: 18.75 },
          { medicine: 'Cough Syrup', quantity: 2, price: 15.25 },
           { medicine: 'Band-Aids', quantity: 1, price: 25.95 }
        ]
      },
    ];
    // Initialize filteredOrders with all orders
    this.filteredOrders = this.allOrders;
  }

  // --- UI Logic ---
  get totalRevenue(): number {
    return this.allOrders.reduce((sum, order) => sum + order.total, 0);
  }

  filterOrders() {
    const term = this.searchTerm.toLowerCase();
    this.filteredOrders = this.allOrders.filter(order => 
      order.id.toLowerCase().includes(term) ||
      order.customer.toLowerCase().includes(term) ||
      order.status.toLowerCase().includes(term)
    );
  }

  // --- Modal Logic ---
  viewOrder(order: Order) {
    this.selectedOrder = order;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedOrder = null;
  }
}