import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  stats = [
    {
      title: 'Total Products',
      value: '1,247',
      change: '+12%',
      changeType: 'positive' as const,
      icon: '📦'
    },
    {
      title: 'Orders Today',
      value: '89',
      change: '+8%',
      changeType: 'positive' as const,
      icon: '🛒'
    },
    {
      title: 'Low Stock Items',
      value: '23',
      change: '-5%',
      changeType: 'negative' as const,
      icon: '📉'
    },
    {
      title: 'Active Users',
      value: '156',
      change: '+3%',
      changeType: 'positive' as const,
      icon: '👥'
    }
  ];
}