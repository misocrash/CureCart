import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  menuItems = [
    { label: 'Dashboard', icon: '🏠', link: '/dashboard' },
    { label: 'Products', icon: '📦', link: '/products' },
    { label: 'My Orders', icon: '🛒', link: '/orders' },
    { label: 'Settings', icon: '⚙️', link: '/settings' },
  ];
  constructor(private authService: AuthService) {}
  logout(): void {
    this.authService.logout();
  }
}