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
    name: 'Dr. Sarah Johnson',
    email: 'sarah.johnson@meditrack.com',
  };

  notifications = {
    lowStock: true,
    newOrders: false,
  };

  onProfileSubmit() {
    // Handle profile update logic
    console.log('Profile saved:', this.profile);
  }
}