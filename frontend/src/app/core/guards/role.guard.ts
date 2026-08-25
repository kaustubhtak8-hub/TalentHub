import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const currentUser = this.authService.currentUserValue;
    const expectedRoles = route.data['roles'] as Array<string>;

    if (currentUser && expectedRoles && expectedRoles.includes(currentUser.role)) {
      return true;
    }

    // Role unauthorized, redirect to home page or standard landing
    this.router.navigate(['/']);
    return false;
  }
}
