import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-admin-sidebar',
  standalone: true, // <-- FIX: Added standalone: true
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-sidebar.component.html',
  styleUrls: ['./admin-sidebar.component.css']
})
export class AdminSidebarComponent {
  menuItems = [
    { label: 'Medicines List', icon: '📋', link: '/admin/medicines' },
    { label: 'Manage Medicines', icon: '⚙️', link: '/admin/manage-medicines' },
    { label: 'Orders', icon: '🛒', link: '/admin/orders' },
    { label: 'Order History', icon: '📜', link: '/admin/order-history' },
  ];
  constructor(private authService: AuthService) {}
  logout() {
    this.authService.logout();
  }
}