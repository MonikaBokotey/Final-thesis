import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.component.html',
  styleUrls: ['./preferences.component.css']
})
export class PreferencesComponent {
  myControl = new FormControl();
  options: string[] = ['Salt', 'Sugar', 'Butter', 'Olive Oil', 'Garlic', 'Onions', 'Carrots', 'Tomatoes', 'Pepper', 'Chicken', 'Beef', 'Pork', 'Rice', 'Wheat', 'Corn', 'Potatoes', 'Beans', 'Eggs', 'Milk', 'Cheese', 'Yogurt', 'Apples', 'Bananas', 'Oranges', 'Strawberries', 'Broccoli', 'Spinach', 'Cucumbers', 'Lettuce', 'Mushrooms'];
  filteredOptions!: Observable<string[]>;
  selectedFoods: string[] = [];
  hasPreferences: boolean = false;
  selectedChips: string[] = [];
  preferredFoods: string[] = [];
  userId?: string;

  constructor(private http: HttpClient) { } 

  ngOnInit(): void {
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filter(value))
    );

    this.decryptAndFetchUserData();
  }

  decryptAndFetchUserData(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); // Extract the encrypted ID from the response

          // Decrypt the user ID by sending it to the backend
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.userId = decryptedUserId;
                console.log('Decrypted User ID:', decryptedUserId);

                // Fetch the user's preferred foods
                this.http.get<any[]>(`http://localhost:9090/preferredFoods/${decryptedUserId}`)
                  .subscribe(
                    (response: any[]) => {
                      this.preferredFoods = response.map(item => item.foodName);
                    },
                    error => console.error('Error fetching preferred foods:', error)
                  );

                // Check if the user has any preferences saved
                this.http.get(`http://localhost:9090/hasPreferences/${decryptedUserId}`, { responseType: 'text' })
                  .subscribe(
                    (preferences: string) => {
                      this.hasPreferences = preferences.includes("preferences saved");
                    },
                    error => console.error('Error checking user preferences:', error)
                  );
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }


  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    if (this.options.includes(value)) {
      this.selectedFoods.push(value);
    }

    return this.options.filter(option => option.toLowerCase().includes(filterValue));
  }

  addPreferredFood(): void {
    this.http.get('http://localhost:9090/csv', { responseType: 'text' })
      .subscribe(
        (response: string) => {
          const encryptedUserId = response.split('\n')[1].trim(); // Extract the encrypted userId

          // Decrypt the user ID by sending it to the backend
          this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
            .subscribe(
              (decryptedUserId: string) => {
                this.userId = decryptedUserId;

                this.selectedFoods.forEach(food => {
                  const url = `http://localhost:9090/addPreferredFood/${this.userId}`;
                  this.http.post(url, food, { responseType: 'text' }).subscribe(
                    res => console.log('Food added successfully'),
                    err => console.error('Error adding food', err)
                  );
                });
              },
              error => console.error('Error decrypting user ID:', error)
            );
        },
        error => console.error('Error fetching CSV:', error)
      );
  }

  toggleSelectedChip(food: string) {
    const index = this.selectedChips.indexOf(food);
    if (index >= 0) {
        this.selectedChips.splice(index, 1);
    } else {
        this.selectedChips.push(food);
    }
}
deletePreferredFood(food: string): void {
  this.http.get('http://localhost:9090/csv', { responseType: 'text' })
    .subscribe(
      (response: string) => {
        const encryptedUserId = response.split('\n')[1].trim(); // Extract the encrypted ID from the response

        // Decrypt the user ID by sending it to the backend
        this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
          .subscribe(
            (decryptedUserId: string) => {
              this.userId = decryptedUserId;

              const url = `http://localhost:9090/deletePreferredFood/${this.userId}`;
              this.http.delete(url, { responseType: 'text' }).subscribe(
                res => {
                  console.log('Food deleted successfully');
                  // Remove the food from the preferredFoods and selectedChips arrays
                  this.preferredFoods = this.preferredFoods.filter(item => item !== food);
                  this.selectedChips = this.selectedChips.filter(item => item !== food);
                },
                err => console.error('Error deleting food', err)
              );
            },
            error => console.error('Error decrypting user ID:', error)
          );
      },
      error => console.error('Error fetching CSV:', error)
    );
}
remove(food: string): void {
  const index = this.preferredFoods.indexOf(food);

  if (index >= 0) {
    this.preferredFoods.splice(index, 1);
    this.selectedChips.splice(index, 1);
  }

  this.http.get('http://localhost:9090/csv', { responseType: 'text' })
    .subscribe(
      (response: string) => {
        const encryptedUserId = response.split('\n')[1].trim(); // Extract the encrypted ID from the response

        // Decrypt the user ID by sending it to the backend
        this.http.post('http://localhost:9090/decryptUserId', encryptedUserId, { responseType: 'text' })
          .subscribe(
            (decryptedUserId: string) => {
              this.userId = decryptedUserId;

              const url = `http://localhost:9090/deletePreferredFood/${this.userId}`;
              const body = { foodName: food };
              this.http.request('delete', url, { body }).subscribe(
                res => console.log('Food deleted successfully'),
                err => console.error('Error deleting food', err)
              );
            },
            error => console.error('Error decrypting user ID:', error)
          );
      },
      error => console.error('Error fetching CSV:', error)
    );
}

}
