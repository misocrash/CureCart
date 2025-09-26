import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

// Matches OrderItemResponseDTO
interface OrderItem {
  orderItemId: number;
  medicineName: string;
  quantity: number;
  priceAtPurchase: number | null;
}

// Matches AddressResponseDTO
interface Address {
  line1: string;
  line2: string | null;
  city: string;
  state: string;
  postalCode: string;
}

// Matches OrderResponseDTO
type OrderStatus = 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
interface Order {
  orderId: number;
  userId: number;
  address: Address;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  items: OrderItem[];
}

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.css']
})
export class AdminOrdersComponent implements OnInit {
  allOrders: Order[] = [];
  pendingActionOrders: Order[] = [];
  processedOrders: Order[] = [];

  isModalOpen = false;
  selectedOrder: Order | null = null;


  private ordersApiUrl = 'http://localhost:8099/api/users/2/orders/admin';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders() {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.get<Order[]>(this.ordersApiUrl, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error('Error fetching orders:', err);
        return of([]);
      })
    ).subscribe(orders => {
      console.log(orders);
      this.allOrders = orders.map(o => ({ ...o, customerName: `User #${o.userId}` })); // Add customer name placeholder
      this.filterOrders();
    });
  }

  filterOrders() {
    this.pendingActionOrders = this.allOrders.filter(o => o.status === 'PENDING');
    this.processedOrders = this.allOrders.filter(o => o.status !== 'PENDING');
  }

  // --- Order Actions ---
  shipOrder(orderToShip: Order) {
    this.updateOrderStatus(orderToShip.orderId, 'SHIPPED');
  }

  cancelOrder(orderToCancel: Order) {
    this.updateOrderStatus(orderToCancel.orderId, 'CANCELLED');
  }

  updateOrderStatus(orderId: number, newStatus: OrderStatus) {
    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};
    const updateUrl = `${this.ordersApiUrl}/${orderId}/status`;

    // The body should match what the backend expects, e.g., { "status": "SHIPPED" }
    const payload = { status: newStatus };

    this.http.put(updateUrl, payload, { headers: headers as { [header: string]: string | string[] } }).pipe(
      catchError(err => {
        console.error(`Failed to update order ${orderId} to ${newStatus}`, err);
        alert(`Error updating order status. Please try again.`);
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log(`Order ${orderId} successfully updated to ${newStatus}`);
        // Find the order in the local array and update its status
        const order = this.allOrders.find(o => o.orderId === orderId);
        if (order) {
          order.status = newStatus;
          this.filterOrders(); // Re-filter the lists to move the order
        }
      }
    });
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

  // --- Stat Getters ---
  get pendingOrdersCount(): number {
    return this.allOrders.filter(o => o.status === 'PENDING').length;
  }

  get shippedOrdersCount(): number {
    return this.allOrders.filter(o => o.status === 'SHIPPED' || o.status === 'DELIVERED').length;
  }

  get totalRevenue(): number {
    return this.allOrders.reduce((sum, order) => sum + order.totalAmount, 0);
  }
}