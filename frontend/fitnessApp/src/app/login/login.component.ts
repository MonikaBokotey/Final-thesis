import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  hide = true;
  loginFailed = false;

  login: FormGroup = new FormGroup({
    password: new FormControl('', [Validators.required, Validators.minLength(3)]),
    username: new FormControl('', [Validators.required, Validators.minLength(3)])
  });

  constructor(private http: HttpClient, private router: Router) {}

  get passwordInput() { return this.login.get('password'); }

  onSubmit() {
    if (this.login.valid) {
      const loginData = this.login.value;
  
      this.http.post('http://localhost:9090/login', loginData)
        .pipe(
          catchError(error => {
            console.error('Login failed:', error.message);
            this.loginFailed = true;
            return throwError(error);
          })
        )
        .subscribe((data: any) => {
          console.log('Login successful');
          console.log('Authenticated user:', data);
          // Store the authentication status and user data
          localStorage.setItem('isAuthenticated', 'true');
          localStorage.setItem('user', JSON.stringify(data));
        
          this.router.navigate(['/home']);
        });
    }
  }
}
