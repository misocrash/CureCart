import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

type OrderStatus = 'pending' | 'shipped' | 'cancelled';
interface Order {
  id: string; date: string; totalAmount: number; status: OrderStatus; customerName: string;
}

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent {
  orders: Order[] = [
    { id: 'ORD-001', date: '2025-09-23', totalAmount: 34.48, status: 'pending', customerName: 'John Smith' },
    { id: 'ORD-002', date: '2025-09-22', totalAmount: 152.00, status: 'pending', customerName: 'Jane Doe' },
  ];

  updateStatus(orderId: string, newStatus: OrderStatus) {
    const order = this.orders.find(o => o.id === orderId);
    if (order) {
      order.status = newStatus;
    }
  }
}