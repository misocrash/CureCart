import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';

// Auth Components - Corrected the casing for SignInComponent
import { SignInComponent } from './auth/signin/signin.component';
import { SignupComponent } from './auth/signup/signup.component';

// User Components
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProductManagementComponent } from './product-management/product-management.component';
import { OrderHistoryComponent } from './order-history/order-history.component';
import { SettingsComponent } from './settings/settings.component';

// Admin Components
import { AdminLayoutComponent } from './admin/admin-layout/admin-layout.component';
import { MedicinesListComponent } from './admin/medicines-list/medicines-list.component';
import { ManageMedicinesComponent } from './admin/manage-medicines/manage-medicines.component';
import { AdminOrdersComponent } from './admin/admin-orders/admin-orders.component';
import { AdminOrderHistoryComponent } from './admin/admin-order-history/admin-order-history.component';

export const routes: Routes = [
  { path: 'signin', component: SignInComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: '', component: LayoutComponent, canActivate: [authGuard], data: { expectedRole: 'user' },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'products', component: ProductManagementComponent },
      { path: 'orders', component: OrderHistoryComponent },
      { path: 'settings', component: SettingsComponent },
    ]
  },
  {
    path: 'admin', component: AdminLayoutComponent, canActivate: [authGuard], data: { expectedRole: 'admin' },
    children: [
      { path: '', redirectTo: 'medicines', pathMatch: 'full' },
      { path: 'medicines', component: MedicinesListComponent },
      { path: 'manage-medicines', component: ManageMedicinesComponent },
      { path: 'orders', component: AdminOrdersComponent },
      { path: 'order-history', component: AdminOrderHistoryComponent },
    ]
  },
  { path: '**', redirectTo: 'signin' }
];