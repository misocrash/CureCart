import { Component } from '@angular/core';
import { CommonModule, TitleCasePipe } from '@angular/common';

type OrderStatus = 'completed' | 'pending' | 'cancelled' | 'shipped';
interface Order {
  id: string; date: string; totalAmount: number; status: OrderStatus; customerName: string;
}

@Component({
  selector: 'app-admin-order-history',
  standalone: true,
  imports: [CommonModule, TitleCasePipe],
  templateUrl: './admin-order-history.component.html',
  styleUrls: ['../../order-history/order-history.component.css'] // Reuse user styles
})
export class AdminOrderHistoryComponent {
  orders: Order[] = [
    { id: 'ORD-001', date: '2025-01-15', totalAmount: 34.48, status: 'shipped', customerName: 'John Smith' },
    { id: 'ORD-003', date: '2025-01-12', totalAmount: 78.90, status: 'cancelled', customerName: 'Peter Jones' },
  ];
}