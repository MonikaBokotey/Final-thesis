import numpy as np
import re
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import MinMaxScaler
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import FunctionTransformer
import pandas as pd
from pydantic import BaseModel,conlist
from typing import List,Optional
import pandas as pd
import requests
import json
from scipy.sparse import hstack
from sklearn.metrics.pairwise import cosine_similarity
from sklearn.impute import SimpleImputer
from flask import Flask, jsonify
from flask_cors import CORS
from flask import request

dataset=pd.read_csv('C:/Users/Monika/Desktop/dataset2/recipes2.csv')

#print(dataset['AggregatedRating'])

app = Flask(__name__)
CORS(app)


def process_user_preferences(api_url,user_id):

    
    url = f"{api_url}/{user_id}"
    #token = "1"
   
    
    # print("token is",token)
    # headers = {
    # "Authorization": f"{token}"
    # }
    
    response =requests.get(url)
    if response.status_code == 200:
        user_preferences = response.json()
    print( user_preferences)

    preferred_foods_list = []
    target_weight_list = []
    height=[]
    weight=[]
    gender=[]

   
    # Example: Print the user preferences
    for user_preference in user_preferences:
        preferred_foods = user_preference.get('foodName')
        target_weight = user_preference.get('userPreferences', {}).get('target_weight')

        user_data = user_preference.get('userPreferences', {}).get('user', {})
        height = user_data.get('height')
        weight = user_data.get('weight')
        gender = user_data.get('gender')

        preferred_foods_list.append(preferred_foods)


        print("json stuff=", preferred_foods_list, target_weight,height, weight,gender)
        

        # Return or use the variables as needed
    return preferred_foods_list, target_weight,height,weight,gender

def calculate_nutrition(target_weight,height,weight,gender):

    height_cm=height*100.0
    print(target_weight,height_cm,weight,gender)
    if gender=='male':
        BMR=88.362 + (13.397 * weight) + (4.799 *height_cm) - (5.677 * 25)
    else:
        BMR = 447.593 + (9.247 * weight) + (3.098 * height_cm) - (4.330 * 25)

    #suppose sedentary for now 
    TDEE=BMR*1.2
    if target_weight>weight:
        calories=TDEE+500
    else:
        calories=TDEE-500

    
    return calories

@app.route('/getRecommendations', methods=['GET'])
def get_recommendations():

    try:
        #this works
        api_url = 'http://localhost:9090/preferredFoods'
    
        df = pd.read_csv('C:/Users/Monika/Desktop/java_licenta/healthapp/user_data.csv')

        # Extract the 'ID' column as a Series
        user_ids = df['ID']
        last_user_id = user_ids.iloc[-1]

        # Convert the user IDs to strings
        # Convert the user ID to string
        encrypted_user_id = str(last_user_id)
        print("Encrypted ID:", encrypted_user_id)

        # Decrypt the user ID by sending it to the backend
        decrypt_url = 'http://localhost:9090/decryptUserId'
        response = requests.post(decrypt_url, data=encrypted_user_id)
        
        if response.status_code != 200:
            raise Exception("Error decrypting user ID")

        user_id = response.text
        print("Decrypted ID:", user_id)


        preferred_foods_list, target_weight,height,weight,gender= process_user_preferences(api_url,user_id)
        

        scaler = MinMaxScaler()
        dataset['NormalizedRating'] = scaler.fit_transform(dataset[['AggregatedRating']])
   
        rating_weight = 0.8
        
        # Apply the weight to the normalized rating
        dataset['WeightedRating'] = dataset['NormalizedRating'] * rating_weight
        
        # Display the dataset with normalized and weighted ratings
        #print(dataset[['AggregatedRating', 'NormalizedRating', 'WeightedRating']])

        dataset['NormalizedCalories'] = scaler.fit_transform(dataset[['Calories']])

        
        
        ingredient_data = dataset['RecipeIngredientParts']
        
        # TF-IDF vectorization
        vectorizer = TfidfVectorizer()
        ingredients_tfidf = vectorizer.fit_transform(ingredient_data)

        combined_features = hstack([ingredients_tfidf, dataset[['WeightedRating', 'NormalizedCalories']].values])
        imputer = SimpleImputer(strategy='mean')
        combined_features_imputed = imputer.fit_transform(combined_features)


        user_preferences = preferred_foods_list

        user_preferences_tfidf = vectorizer.transform([" ".join(user_preferences)])

        calories_needed = calculate_nutrition(target_weight,height,weight,gender)/3
        normalized_calories_needed = scaler.transform([[calories_needed]])


        user_features = hstack([user_preferences_tfidf, np.array([[rating_weight, normalized_calories_needed[0][0]]])])


        user_features_imputed = imputer.transform(user_features)

        user_recipe_similarity = cosine_similarity(user_features_imputed, combined_features_imputed)

        recommended_recipe_indices = np.argsort(user_recipe_similarity[0])[::-1]



        top_n_recommendations = recommended_recipe_indices[:20]
        print("Top Recommendations:", dataset.iloc[top_n_recommendations]['Name'])

        recipe_calories = dataset.iloc[top_n_recommendations]['Calories']  
        calories_difference = abs(recipe_calories - calories_needed)

        # Sort the indices of the recommended recipes based on the calorie difference
        sorted_indices = np.argsort(calories_difference)

        # Select the top 5 recommendations with the smallest calorie difference
        top_5_calorie_friendly_recommendations = top_n_recommendations[sorted_indices[:5]]


        recommended_recipes = dataset.iloc[top_5_calorie_friendly_recommendations]
        top_recommendations = recommended_recipes[['Name', 'Images', 'Description', 'Calories','ProteinContent','CarbohydrateContent','FatContent']].to_dict('records')



        return jsonify({'recommendations': top_recommendations})

    except Exception as e:
        return jsonify(error=str(e)), 500


if __name__ == '__main__':
    app.run(port=5000)

### http://localhost:5000/getRecommendations