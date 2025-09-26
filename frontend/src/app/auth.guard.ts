import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const expectedRole = route.data['expectedRole'] as 'admin' | 'user';

  if (authService.isAuthenticated() && authService.hasRole(expectedRole)) {
    return true; // Access granted!
  }

  // If not logged in or role doesn't match, send back to signin
  authService.logout();
  return false;
};