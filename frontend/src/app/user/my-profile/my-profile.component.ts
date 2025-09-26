import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

// Matches the backend UserDTO
interface UserUpdatePayload {
  name: string | null;
  email: string | null;
  password?: string; // Optional
}

// Matches the backend UserResponseDTO
interface UserResponse {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'USER';
}

@Component({
  selector: 'app-my-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
})
export class MyProfileComponent implements OnInit {
  profile = {
    name: '',
    email: '',
    password: '', // For the new password input
    role: '',
  };
  isSaving = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.profile.name = localStorage.getItem('userName') || '';
    this.profile.email = localStorage.getItem('userEmail') || '';
    this.profile.role = localStorage.getItem('userRole')?.toUpperCase() || 'USER';
  }

  onProfileSubmit() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
      alert('Error: User not found. Please log in again.');
      return;
    }

    this.isSaving = true;
    const apiUrl = `http://localhost:8099/api/users/${userId}`;

    const payload: UserUpdatePayload = {
      name: this.profile.name,
      email: this.profile.email
    };

    // Only include the password if the user has entered one
    if (this.profile.password) {
      payload.password = this.profile.password;
    }

    const authToken = localStorage.getItem('authToken');
    const headers = authToken ? { 'Authorization': `Bearer ${authToken}` } : {};

    this.http.put<UserResponse>(apiUrl, payload, { headers: headers as { [header: string]: string | string[] }}).pipe(
      catchError(err => {
        console.error('Profile update failed', err);
        alert('An error occurred while updating your profile. Please try again.');
        this.isSaving = false;
        return of(null);
      })
    ).subscribe(response => {
      if (response) {
        console.log('Profile updated successfully', response);

        // Update localStorage with the new data from the response
        localStorage.setItem('userName', response.name);
        localStorage.setItem('userEmail', response.email);

        // Update the component's profile data to reflect the change
        this.profile.name = response.name;
        this.profile.email = response.email;
        this.profile.password = ''; // Clear the password field

        alert('Profile updated successfully!');
        localStorage.removeItem('authToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userRole');
        this.router.navigate(['/signin']);
      }
      this.isSaving = false;
    });
  }
}