import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormControl } from '@angular/forms';
import { CanvasJSAngularChartsModule } from '@canvasjs/angular-charts';
import * as forge from 'node-forge';

interface Recommendation {
  Name: string;
  Description: string;
  Calories: number;
  Images: string;
}
export interface Meal {
  mealName: string;
  calories: number;
  carbohydrates: number;
  fats: number;
  protein: number;
  mealTime: string;
}


interface User {
  height: number; // in cm
  weight: number; // in kg
  gender: 'male' | 'female';
}

interface Workout {
  name: string;
  intensity: 'Low' | 'Medium' | 'High';
  reps: number;
  duration:number;

}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})

export class HomeComponent{
  recommendations: any;
  //meals: any; 
  workouts:any;
  preferences:any;
  progressBarValue = 0; 
  cappedProgressBarValue=0;
  workoutStarted = false;
  workoutSaved = false;
  totalCalories: number;
  totalDailyCalories: number;
  meals: Meal[] = [];

  mealName: string = '';
  calories: number = 0;
  mealTime: string = '';
  userId: string = '';

  carbohydrates=0;
  fats=0;
  protein=0;
  targetCarbs: number = 0;
  targetProtein: number = 0;
  targetFats: number = 0;
  targetMessageContent: string = '';



  chartOptions = {
    animationEnabled: true,
    data: [{
      type: "pie",
      startAngle: -90,
      indexLabel: "{name}: {y}",
      yValueFormatString: "#,###.##'%'",
      dataPoints: [
        { y: this.progressBarValue, name: "Consumed" ,color: '#00ff00'},
        { y: 100 - this.progressBarValue, name: "Remaining", color: "#808080"  }
      ]
    }]

  }

  workoutNames = ['Push-ups', 'Sit-ups', 'Squats', 'Lunges', 'Jumping Jacks', 'Burpees', 'Mountain Climbers', 'Planks', 'Deadlifts', 'Bench Press', 'Leg Press', 'Overhead Press', 'Bicep Curls', 'Tricep Dips', 'Pull-ups', 'Chin-ups', 'Treadmill Running', 'Stationary Bike', 'Elliptical Training', 'Swimming', 'Rowing', 'Yoga', 'Pilates', 'Boxing', 'Kickboxing', 'Zumba'];
  intensities = ['Low', 'Medium', 'High'];

  nameControl = new FormControl();
  intensityControl = new FormControl();
  repsControl = new FormControl();
  durationControl = new FormControl();

  constructor(private http: HttpClient,private router: Router) {this.totalCalories = 0; this.totalDailyCalories=0;}
  ngOnInit(): void {
    this.showWorkouts();
    this.targetWeight();

    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim();

          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                console.log('Decrypted User ID:', decryptedUserId);

                this.http.get(`http://localhost:9090/getUserPreferences/${decryptedUserId}`)
                  .subscribe(
                    data => {
                      this.preferences = data;
                      const weightElement = document.getElementById('target-weight');
                      if (weightElement) {
                        weightElement.textContent = `Target weight: ${this.preferences[0].target_weight} kg`;
                      }
                      this.calculateTargetCalories();
                      this.calculateTargetNutrients();
                    },
                    error => console.error('Error fetching user preferences:', error)
                  );

                this.http.get<Meal[]>(`http://localhost:9090/users/${decryptedUserId}/meals`)
                  .subscribe(
                    (meals: Meal[]) => {
                      this.meals = meals;
                      this.displayTotalCalories();
                      this.displayTotalNutrients();
                      this.updateChart();
                    },
                    error => console.error('Error fetching user meals:', error)
                  );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }


  

  calculateTargetNutrients() {
    const weight = this.preferences[0].user.weight;

    // Define the target ratios based on the user's weight
  
    const caloriesPerGramCarbs = 4;
    const caloriesPerGramProtein = 4;
    const caloriesPerGramFats = 9;

    // Define the percentage of daily calories from each nutrient
    const percentageCarbs = 0.45;
    const percentageProtein = 0.20;
    const percentageFats = 0.15;

    // Calculate BMR
    let bmr;
    const height = this.preferences[0].user.height;
    const age = 25;
    const sex = 'male';
    if (sex === 'male') {
      bmr = 88.362 + (13.397 * weight) + (4.799 * height * 100) - (5.677 * age);
    } else {
      bmr = 447.593 + (9.247 * weight) + (3.098 * height * 100) - (4.330 * age);
    }

    const targetCalories = bmr * 1.2;

    // Calculate the target grams for each nutrient
    this.targetCarbs = (targetCalories * percentageCarbs) / caloriesPerGramCarbs;
    this.targetProtein = (targetCalories * percentageProtein) / caloriesPerGramProtein;
    this.targetFats = (targetCalories * percentageFats) / caloriesPerGramFats;

    console.log(`Carbs: ${this.targetCarbs}, Protein: ${this.targetProtein}, Fats: ${this.targetFats}`);

    // Update target values in the HTML
    this.updateNutrientElements();
  }


  updateNutrientElements() {
    const targetCarbsElement = document.getElementById('target-carbs');
    if (targetCarbsElement) {
      targetCarbsElement.textContent = `${this.targetCarbs.toFixed(2)}g`;
    }

    const targetProteinElement = document.getElementById('target-protein');
    if (targetProteinElement) {
      targetProteinElement.textContent = `${this.targetProtein.toFixed(2)}g`;
    }

    const targetFatsElement = document.getElementById('target-fats');
    if (targetFatsElement) {
      targetFatsElement.textContent = `${this.targetFats.toFixed(2)}g`;
    }
  }
  displayTotalNutrients() {
    let totalCarbs = 0;
    let totalProtein = 0;
    let totalFats = 0;

    this.meals.forEach((meal: Meal) => {
      totalCarbs += meal.carbohydrates;
      totalProtein += meal.protein;
      totalFats += meal.fats;
    });

    const carbsElement = document.getElementById('total-carbs');
    if (carbsElement) {
      carbsElement.textContent = ` ${totalCarbs}g`;
    }

    const proteinElement = document.getElementById('total-protein');
    if (proteinElement) {
      proteinElement.textContent = `${totalProtein}g`;
    }

    const fatsElement = document.getElementById('total-fats');
    if (fatsElement) {
      fatsElement.textContent = `${totalFats}g`;
    }

    // Update target values
    this.calculateTargetNutrients();
  }


  showRecommendations() {
    this.http.get('http://localhost:5000/getRecommendations')
    .subscribe(
      (data: any) => {
        console.log(data);  // Log the data to check its structure
        if (data.recommendations) {
          this.recommendations = data.recommendations.map((recommendation: Recommendation) => {
            console.log(recommendation);  // Log each recommendation to check its fields
            // Check if the 'Images' field is not empty and not equal to 'character(0)'
            if (recommendation.Images && recommendation.Images !== 'character(0)') {
             
              let images = recommendation.Images.match(/https?:\/\/[^"]+/g);
              
              recommendation.Images = images ? images[0] : '';
            } else {
             
              recommendation.Images = '';
            }
            return recommendation;
          });
        } else {
          console.error('Error: Data does not contain recommendations');
        }
      },
      error => console.error('Error:', error)
    );
  }
  
  decryptAndFetchUserData(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); 

          
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                console.log('Decrypted User ID:', decryptedUserId);

               
                this.fetchUserPreferences(decryptedUserId);

              
                this.fetchUserMeals(decryptedUserId);
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }

  fetchUserPreferences(decryptedUserId: string): void {
    this.http.get(`http://localhost:9090/getUserPreferences/${decryptedUserId}`)
      .subscribe(
        data => {
          this.preferences = data;
          const weightElement = document.getElementById('target-weight');
          if (weightElement) {
            weightElement.textContent = `Target weight: ${this.preferences[0].target_weight} kg`;
          }
          this.calculateTargetCalories();
        },
        error => console.error('Error fetching user preferences:', error)
      );
  }

  fetchUserMeals(decryptedUserId: string): void {
    this.http.get<any[]>(`http://localhost:9090/users/${decryptedUserId}/meals`)
      .subscribe(
        (meals: any[]) => {
          this.meals = meals.map(meal => ({
            mealName: meal.mealName,
            calories: meal.calories,
            carbohydrates: meal.carbohydrates,
            fats: meal.fats,
            protein: meal.protein, 
            mealTime: meal.mealTime 
          }));
          this.displayTotalCalories();
        },
        error => console.error('Error fetching user meals:', error)
      );
  }

  
  
  showMeals(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); // Extract the encrypted ID from the response

          // Decrypt the user ID by sending it to the backend
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                console.log('Decrypted User ID:', decryptedUserId);

                // Fetch user meals using the decrypted user ID
                this.fetchUserMeals(decryptedUserId);
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }
  
  calculateTotalCalories() {
    let totalCalories = 0;
    for (let meal of this.meals) {
      totalCalories += meal.calories;
    }
    return totalCalories;
  }
  
  displayTotalCalories() {
    const totalCalories = this.calculateTotalCalories();
    const caloriesElement = document.getElementById('total-calories');
    if (caloriesElement) {
      caloriesElement.textContent = ` ${totalCalories}`;
    }
  }

  
  //------------------------------------------------------
  targetWeight(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); 

         
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                console.log('Decrypted User ID:', decryptedUserId);

                
                this.fetchUserPreferences(decryptedUserId);
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }
  
  calculateTargetCalories() {
    const weight = this.preferences[0].user.weight;
    const height = this.preferences[0].user.height;
    const targetWeight = this.preferences[0].target_weight;
    console.log("target weight=" ,targetWeight);
    

    const age = 25; 
    const sex = 'male'; 
  
    let bmr;
    if (sex === 'male') {
      bmr = 88.362 + (13.397 * targetWeight) + (4.799 * height * 100) - (5.677 * age);
    } else {
      bmr = 447.593 + (9.247 * targetWeight) + (3.098 * height * 100) - (4.330 * age);
    }


    const targetCalories = bmr * 1.2;
  
    const targetCaloriesElement = document.getElementById('target-calories');
    if (targetCaloriesElement) {
      targetCaloriesElement.textContent = ` ${Math.round(targetCalories)}`;
    }

const totalCaloriesConsumed = this.calculateTotalCalories();
const percentageConsumed = (totalCaloriesConsumed / targetCalories) * 100;

if (percentageConsumed > 100) {
  
  let excessPercentage = percentageConsumed - 100;

  
  let messageElement = document.getElementById('excess-message');


  if (messageElement) {
    messageElement.textContent = `Calories exceeded by ${excessPercentage.toFixed(2)}%`;
  }


  const bmiTarget = targetWeight / (height * height);

  console.log("bmi=",bmiTarget);
  const targetMessageElement = document.getElementById('target-message');
  if (targetMessageElement) {
    if (bmiTarget < 18.5) {
      targetMessageElement.textContent = 'Target BMI is too low, consider changing weight goal';
    } else if (bmiTarget > 24.9) {
      targetMessageElement.textContent = 'Target BMI is too high, consider changing weight goal';
    } else {
      targetMessageElement.textContent = '';
    }
  } else {
    console.error("Element with ID 'target-message' not found.");
  }

 
  if (targetMessageElement)
  console.log("targetMessageElement=",targetMessageElement.textContent);
  this.updateChart();
}




this.progressBarValue = percentageConsumed;

this.cappedProgressBarValue= Math.min(percentageConsumed, 100);

console.log("progressbar=",this.progressBarValue);

this.chartOptions = {
  animationEnabled: true,
  data: [{
    type: "pie",
    startAngle: -90,
    indexLabel: "{name}: {y}",
    yValueFormatString: "#,###.##'%'",
    dataPoints: [
      { y: this.cappedProgressBarValue, name: "Consumed", color: '#673ab7' },
      { y: 100 - this.cappedProgressBarValue, name: "Remaining", color: "#808080" }
    ]
  }]

};

    
  }


  
  
  //--------------------------------------------------------------------
  startWorkout() {
    this.http.post('http://localhost:5001/start-workout', {}, { responseType: 'text' })
      .subscribe(
        response => {
          console.log(response); // Logs "Workout started"
          this.workoutStarted = true;
        },
        error => console.error('Error:', error)
      );
  }

  // POST MEAL
  addMeal(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); 

        
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.userId = decryptedUserId; 

                
                let today = new Date();

               
                let [hours, minutes] = this.mealTime.split(':');
                today.setHours(Number(hours), Number(minutes));

                const meal: Meal = {
                  mealName: this.mealName,
                  calories: this.calories,
                  carbohydrates: this.carbohydrates,
                  fats: this.fats,
                  protein: this.protein,
                  mealTime: today.toISOString() 
                };

                const url = `http://localhost:9090/users/${this.userId}/getMeals`; 
                this.http.post(url, meal).subscribe(
                  response => {
                    console.log('Meal added successfully', response);
                    window.location.reload(); 
                  },
                  error => console.error('Error:', error)
                );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }

  showWorkouts(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); 

          
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                console.log('Decrypted User ID:', decryptedUserId);

                this.http.get<any[]>(`http://localhost:9090/users/${decryptedUserId}/workouts`)
                  .subscribe(
                    (data: any[]) => {
                      this.workouts = data.map(workout => ({ 
                        workoutName: workout.name, 
                        reps: workout.reps, 
                        duration: workout.duration 
                      }));
                    },
                    error => console.error('Error fetching user workouts:', error)
                  );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }
  openPreferences() {
    
    this.router.navigate(['/preferences']);
  }
  saveWorkout(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); 

         
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.http.post(`http://localhost:9090/users/${decryptedUserId}/addWorkoutFromCsv`, {}, { responseType: 'text' })
                  .subscribe(
                    () => {
                      this.workoutSaved = true;
                     
                    },
                    error => console.error('Error saving workout:', error)
                  );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }

  addWorkout(workout: { name: string; reps: number; duration: number; intensity: string; }): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim();  // Extract the encrypted ID from the response

          // Decrypt the user ID by sending it to the backend
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.http.post(`http://localhost:9090/users/${decryptedUserId}/addWorkout`, workout)
                  .subscribe(
                    () => {
                      console.log('Workout added successfully!');
                      // Refresh the page
                      location.reload();
                    },
                    error => console.error('Error adding workout:', error)
                  );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }


  estimateCaloriesBurned(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim();  // Extract the encrypted ID from the response

          // Decrypt the user ID by sending it to the backend
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.http.get<any[]>(`http://localhost:9090/users/${decryptedUserId}/workouts`)
                  .subscribe(
                    (workouts: any[]) => {
                      this.totalCalories = 0;
                      workouts.forEach(workout => {
                        const duration = workout.duration; // Assuming duration is in minutes
                        const caloriesBurned = 5 * duration; // 5 calories burned per minute
                        this.totalCalories += caloriesBurned;
                      });
                      console.log(`Total calories burned: ${this.totalCalories}`);
                    },
                    error => console.error('Error fetching user workouts:', error)
                  );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }


  addMealFromRecommendation(recommendation: any): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); // Extract the encrypted userId

          // Decrypt the user ID by sending it to the backend
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.userId = decryptedUserId;

                const meal: Meal = {
                  mealName: recommendation.Name,
                  calories: recommendation.Calories,
                  carbohydrates: recommendation.Carbohydrates,
                  fats: recommendation.Fats,
                  protein: recommendation.Protein,
                  mealTime: this.mealTime // You might want to adjust this
                };

                const url = `http://localhost:9090/users/${this.userId}/getMeals`; // Use the userId here
                this.http.post(url, meal).subscribe(
                  response => console.log('Meal added successfully', response),
                  error => console.error('Error:', error)
                );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }


  calculateNetCalories() {
   
    const totalConsumedCalories = this.calculateTotalCalories();
  

    const totalBurnedCalories = this.totalCalories;
  
    const netCalories = totalConsumedCalories - totalBurnedCalories;
  
    return netCalories;
  }

  displayNetCalories() {
    const netCalories = this.calculateNetCalories();
    const caloriesElement = document.getElementById('net-calories');
    if (caloriesElement) {
      caloriesElement.textContent = `Net calories for the day: ${netCalories}`;
    }

  }

  updateChart() {
    this.chartOptions = {
      animationEnabled: true,
      data: [{
        type: "pie",
        startAngle: -90,
        indexLabel: "{name}: {y}",
        yValueFormatString: "#,###.##'%'",
        dataPoints: [
          { y: this.progressBarValue, name: "Consumed", color: '#673ab7' },
          { y: 100 - this.progressBarValue, name: "Remaining", color: "#808080" }
        ]
      }]
    };
  }


  openUserProfile() {
    this.router.navigate(['/user-profile']);
  }
  
}



