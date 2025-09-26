import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

export type UserRole = 'admin' | 'user' | null;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserRole: UserRole = null;
  private apiUrl = 'http://localhost:8099/api/auth/login';
  private registerUrl = 'http://localhost:8099/api/users/register';


  constructor(private http: HttpClient, private router: Router) { 

    const storedRole = localStorage.getItem('userRole');
    if (storedRole) {
      this.currentUserRole = storedRole as UserRole;
    }
  }

login(email: string, password: string, role: 'admin' | 'user'): void {
  const loginData = { email, password }; // Prepare the request payload
  console.log(loginData);

  this.http.post<{ token: string, user: { role: UserRole | null } }>(`${this.apiUrl}`, loginData).subscribe({
    next: (response) => {
      console.log('Login successful', response);

      // Store the token in localStorage for subsequent API calls
      localStorage.setItem('authToken', response.token);

      // Check if the role exists and normalize it to lowercase
      if (response.user && response.user.role) {
        this.currentUserRole = response.user.role.toLowerCase() as UserRole;
        localStorage.setItem('userRole', this.currentUserRole as string);
        // Navigate based on the user's role
        if (this.currentUserRole === 'admin') {
          this.router.navigate(['/admin']);
        } else if (this.currentUserRole === 'user') {
          this.router.navigate(['/browse-medicines']);
        } else {
          alert('Invalid role. Please contact support.');
        }
      } else {
        alert('Role is missing in the response. Please contact support.');
      }
    },
    error: (error) => {
      console.error('Login failed', error);
      alert('Login failed. Please check your credentials and try again.');
    }
  });
}


  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('userRole');
    this.currentUserRole = null;
    this.router.navigate(['/signin']);
  }

  signup(name: string, email: string, password: string) {
    const signupData = { name, email, password };
    this.http.post(`${this.registerUrl}`, signupData).subscribe({
      next: (response) => {
        console.log('Signup successful', response);
        alert('Signup successful! Please sign in.');
        this.router.navigate(['/signin']);
      },
      error: (error) => {
        console.error('Signup failed', error);
        alert('Signup failed. Please try again.');
      }
    });
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken');
  }

  hasRole(role: UserRole): boolean {
    return this.currentUserRole === role;
  }
}