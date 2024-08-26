import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {
  signupForm: FormGroup;
  signUpSuccess: boolean = false;
  userAlreadyExists: boolean = false;

  constructor(private formBuilder: FormBuilder, private http: HttpClient) {
    this.signupForm = this.formBuilder.group({
      username: '',
      password: '',
      email: '',
      gender: '',
      weight: '', 
      height: '',
      targetWeight:''
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    this.http.post('http://localhost:9090/signup', this.signupForm.value, {
      headers: {
        'Content-Type': 'application/json'
      }
    }).pipe(
      catchError(error => {
        if (error.status === 409) {
          this.userAlreadyExists = true; 
        } else {
          console.error('Sign up failed:', error.message);
        }
        return throwError(error);
      })
    ).subscribe(responseData => {
      console.log('Sign up successful:', responseData);
      this.signUpSuccess = true;
    });
  }
}
