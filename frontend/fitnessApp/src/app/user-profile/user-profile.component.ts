import { Component,OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { MatCalendarCellClassFunction } from '@angular/material/datepicker';
//import { map } from 'rxjs/operators';
import { map, catchError, switchMap } from 'rxjs/operators';
import { throwError } from 'rxjs';

interface WeightResponse {
  weight: number;

}
interface UserPreferences {
  weight: number;
 
}

interface User {
  user_id: number;
  username: string;
  password: string;
  email: string;
  age: number;
  gender: string;
  height: number;
  weight: number;
  created_at: string | null;
  updated_at: string | null;
}

interface DailyLog {
  daily_log_id: number;
  user: User;
  logDateTime: string;
}

interface Meal {
  meal_id: number;
  dailyLog: DailyLog;
  mealName: string;
  calories: number;
  carbohydrates: number;
  fats: number;
  protein: number;
  mealTime: string;
}

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})



export class UserProfileComponent implements OnInit{
  userId: string = '';
  weight: number = 0;
  hasPreferences: boolean = false;
  preferencesForm: FormGroup;
  dataLoaded: boolean = false;
  newWeightValue: number | null = null;
  preferences:any;
  targetCalories: number = 0;
  daysMetTarget: number = 0;
  daysMetTargetDates: string[] = [];
  caloriesLog: { date: string, targetCalories: number, totalCalories: number }[] = [];
  mealsByDay: { [date: string]: Meal[] } | undefined;
  mealDays: string[] | undefined;
  newTargetWeight: number = 0;
  displayedColumns: string[] = ['mealName', 'calories', 'carbohydrates', 'fats', 'protein'];


  
  constructor(private formBuilder: FormBuilder, private http: HttpClient, private changeDetector: ChangeDetectorRef)  {
    this.preferencesForm = this.formBuilder.group({
      height: '',
      weight: '',
      age: '',
      dietary_restrictions: '',
      preferred_intensity_level: '',
      preferred_workout_duration: '',
      target_weight: '',
     
    });
  }

  ngOnInit(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .pipe(
        switchMap(response => {
          const encryptedUserId = response.split('\n')[1].trim();
          return this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' });
        }),
        switchMap((decryptedUserId: string) => {
          this.userId = decryptedUserId;
          return this.http.get<User>(`http://localhost:9090/${this.userId}`);
        }),
        switchMap(user => {
          this.weight = user.weight;
          return this.http.get<Meal[]>(`http://localhost:9090/users/${this.userId}/allMeals`);
        }),
        map(meals => {
          return meals.reduce((acc, meal) => {
            const date = new Date(meal.dailyLog.logDateTime).toLocaleDateString();
            if (!acc[date]) {
              acc[date] = [];
            }
            acc[date].push(meal);
            return acc;
          }, {} as { [date: string]: Meal[] });
        }),
        catchError(error => {
          console.error('Error occurred:', error);
          return throwError(error);
        })
      )
      .subscribe(
        data => {
          this.mealsByDay = data;
          this.mealDays = Object.keys(data);
          this.calculateDaysMetTarget();
        },
        error => {
          console.error('Failed to fetch meals:', error);
        }
      );
  }

  modifyTargetWeight(): void {
    this.http.put(`http://localhost:9090/updateTargetWeight/${this.userId}`, null, {
      params: { targetWeight: this.newTargetWeight.toString() }
    })
    .subscribe(
      () => {
        console.log('Target weight updated successfully');
       
        this.calculateDaysMetTarget();
      },
      error => {
        console.error('Error updating target weight:', error);
      }
    );
  }


  onSubmit(): void {
    const preferences = this.preferencesForm.value;

    ['height', 'weight', 'age'].forEach(field => {
      this.http.put(`http://localhost:9090/update${field.charAt(0).toUpperCase() + field.slice(1)}/${this.userId}`, { [field]: preferences[field] })
        .subscribe(
          response => console.log(`Updated ${field}:`, response),
          error => console.error(`Failed to update ${field}:`, error)
        );
    });

 
    this.http.post(`http://localhost:9090/addUserPreferences/${this.userId}`, {
      preferred_intensity_level: preferences.preferred_intensity_level,
      preferred_workout_duration: preferences.preferred_workout_duration,
      target_weight: preferences.target_weight,
      dietary_restrictions: preferences.dietary_restrictions
    }).subscribe(
      response => console.log('Added preferences:', response),
      error => console.error('Failed to add preferences:', error)
    );
  }

  modifyWeight(): void {
    if (this.newWeightValue !== null) {
      this.http.put(`http://localhost:9090/updateWeight/${this.userId}`, { weight: this.newWeightValue })
        .subscribe(
          response => {
            console.log('Updated weight:', response);
            if (this.newWeightValue !== null) {
              this.weight = this.newWeightValue; // Update the displayed weight
            }
            
          },
          error => console.error('Failed to update weight:', error)
        );
    }
  }
  
  calculateTargetCalories(): Promise<number> {
    return new Promise((resolve, reject) => {
      this.http.get(`http://localhost:9090/getUserPreferences/${this.userId}`)
        .subscribe(
          data => {
            this.preferences = data;
            console.log(`Data: ${JSON.stringify(data, null, 2)}`);

            const weight = this.preferences[0].user.weight;
            const height = this.preferences[0].user.height;
            const targetWeight = this.preferences[0].target_weight;

            console.log(`Weight: ${weight}`);
            console.log(`Height: ${height}`);
            console.log(`Target Weight: ${targetWeight}`);

            const age = 25;
            const sex = 'male';

            let bmr;
            if (sex === 'male') {
              bmr = 88.362 + (13.397 * weight) + (4.799 * height * 100) - (5.677 * age);
            } else {
              bmr = 447.593 + (9.247 * weight) + (3.098 * height * 100) - (4.330 * age);
            }

            const targetCalories = bmr * 1.2;
            resolve(targetCalories);
          },
          error => {
            console.error('Failed to fetch user preferences:', error);
            reject(error);
          }
        );
    });
  }

  calculateDaysMetTarget(): void {
    this.calculateTargetCalories().then(targetCalories => {
      this.targetCalories = targetCalories;

      this.http.get<Meal[]>(`http://localhost:9090/users/${this.userId}/allMeals`).subscribe(meals => {
        let daysMetTarget = 0;
        let currentDate: string | null = null;
        let totalCalories = 0;

        meals.sort((a, b) => new Date(a.mealTime).getTime() - new Date(b.mealTime).getTime());

        meals.forEach(meal => {
          const mealDate = new Date(meal.mealTime).toDateString();

          if (mealDate !== currentDate) {
            if (currentDate !== null) {
              this.caloriesLog.push({ date: currentDate, targetCalories: this.targetCalories, totalCalories });
              if (totalCalories >= this.targetCalories) {
                daysMetTarget++;
                this.daysMetTargetDates.push(currentDate);
              }
            }
            totalCalories = 0;
            currentDate = mealDate;
          }

          totalCalories += meal.calories;
        });

        if (currentDate !== null) {
          this.caloriesLog.push({ date: currentDate, targetCalories: this.targetCalories, totalCalories });
          if (totalCalories >= this.targetCalories) {
            daysMetTarget++;
            this.daysMetTargetDates.push(currentDate);
          }
        }

        console.log(`The target calories have been met on ${daysMetTarget} days:`, this.daysMetTargetDates);
        console.log('Calories log:', this.caloriesLog);
        this.daysMetTarget = daysMetTarget;
      });
    }).catch(error => {
      console.error('Error calculating target calories:', error);
    });
  }
}


