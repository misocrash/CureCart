import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

export type UserRole = 'admin' | 'user' | null;

@Injectable({
  providedIn: 'root' 
})
export class AuthService {
  private currentUserRole: UserRole = null;

  constructor(private router: Router) { }

  login(role: 'admin' | 'user'): void {
    this.currentUserRole = role;
    if (role === 'admin') {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  logout(): void {
    this.currentUserRole = null;
    this.router.navigate(['/signin']);
  }

  isAuthenticated(): boolean {
    return !!this.currentUserRole;
  }

  hasRole(role: UserRole): boolean {
    return this.currentUserRole === role;
  }
}