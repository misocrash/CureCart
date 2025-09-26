import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css']
})
export class MyProfileComponent {
  profile = {
    name: 'Sarah Johnson',
    email: 'sarah.johnson@example.com',
    role: 'User',
  };

  onProfileSubmit() {
    console.log('Profile saved:', this.profile);
    alert('Profile updated!');
  }
}