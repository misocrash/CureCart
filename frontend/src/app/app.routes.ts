import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';
// Auth
import { SignInComponent } from './auth/signin/signin.component';
import { SignupComponent } from './auth/signup/signup.component';
// User
import { LayoutComponent } from './layout/layout.component';
import { BrowseMedicinesComponent } from './user/browse-medicines/browse-medicines.component';
import { CartComponent } from './user/cart/cart.component';
import { CheckoutComponent } from './user/checkout/checkout.component';
import { OrderHistoryComponent } from './order-history/order-history.component';
import { MyProfileComponent } from './user/my-profile/my-profile.component';
import { FeedbackComponent } from './user/feedback/feedback.component';
import { AboutUsComponent } from './user/about-us/about-us.component';
import { ContactUsComponent } from './user/contact-us/contact-us.component';
// Admin
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
      { path: '', redirectTo: 'browse-medicines', pathMatch: 'full' },
      { path: 'browse-medicines', component: BrowseMedicinesComponent },
      { path: 'cart', component: CartComponent },
      { path: 'checkout', component: CheckoutComponent },
      { path: 'orders', component: OrderHistoryComponent },
      { path: 'my-profile', component: MyProfileComponent },
      { path: 'feedback', component: FeedbackComponent },
      { path: 'about-us', component: AboutUsComponent },
      { path: 'contact-us', component: ContactUsComponent },
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