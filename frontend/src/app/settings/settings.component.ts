import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent {
  profile = {
    name: 'Sarah Johnson',
    email: 'sarah.johnson@example.com',
  };

  notifications = {
    lowStock: true,
    newOrders: false,
    systemUpdates: true,
  };

  onProfileSubmit() {
    console.log('Profile saved:', this.profile);
    // In a real app, you would send this to a server
  }
}