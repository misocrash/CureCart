import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authToken = localStorage.getItem('authToken'); // Retrieve the token from localStorage
    console.log("auth token in interceptor:", authToken);
    if (authToken) {
      // Clone the request and add the Authorization header
      const clonedRequest = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${authToken}`)
      });
      return next.handle(clonedRequest);
    }

    // If no token, pass the request as is
    return next.handle(req);
  }
}