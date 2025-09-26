import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Order {
  id: string;
  date: string;
  totalAmount: number;
  status: 'completed' | 'pending' | 'cancelled';
  customerName: string;
}

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent {
  orders: Order[] = [
    { id: 'ORD-001', date: '2024-01-15', totalAmount: 34.48, status: 'completed', customerName: 'John Smith' },
    { id: 'ORD-002', date: '2024-01-14', totalAmount: 152.00, status: 'pending', customerName: 'Jane Doe' },
    { id: 'ORD-003', date: '2024-01-12', totalAmount: 78.90, status: 'cancelled', customerName: 'Peter Jones' },
  ];
}