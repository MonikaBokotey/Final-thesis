import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate{

  constructor(private router: Router) { }

// AuthGuardService
canActivate(): boolean {
  let isAuthenticated = false;

  if (typeof localStorage !== 'undefined') {
    isAuthenticated = localStorage.getItem('isAuthenticated') === 'true';
  }

  if (!isAuthenticated) {
    this.router.navigate(['/login']);
    return false;
  }
  return true;
}

}
