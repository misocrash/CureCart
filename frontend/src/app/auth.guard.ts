import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const expectedRole = route.data['expectedRole'] as 'admin' | 'user';

  if (authService.isAuthenticated() && authService.hasRole(expectedRole)) {
    return true;
  }

  // Redirect to signin page if not authenticated or role mismatch
  authService.logout();
  return false;
};