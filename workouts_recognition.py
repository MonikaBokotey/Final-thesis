from flask import Flask, request
import threading
from flask_cors import CORS
import csv



app = Flask(__name__)
CORS(app)

  
class ExerciseStateMachine:
    def __init__(self):
        self.current_exercise = None
        self.previous_exercise = None
        self.exercise_counts = {'Curls': 0, 'Jumping Jacks': 0, 'Squats': 0}

    def update(self, detected_exercise):
        if detected_exercise is not None:
            self.previous_exercise = self.current_exercise
            self.current_exercise = detected_exercise

            # Only increment the count if the exercise has changed or it's the first exercise
            if self.previous_exercise is None or self.current_exercise != self.previous_exercise:
                self.exercise_counts[self.current_exercise] += 1
        else:
            self.current_exercise = None

        print(f"Detected exercise: {detected_exercise}")
        print(f"Current exercise: {self.current_exercise}, Count: {self.exercise_counts.get(self.current_exercise, 0)}")
        return self.current_exercise, self.exercise_counts.get(self.current_exercise, 0)


with open('exercise_data.csv', 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(["Exercise", "Repetition Count"])


def run_pose_detection():
    import cv2
    import mediapipe as mp
    import numpy as np
    from keras.models import load_model

    model1 = load_model('C:/Users/Monika/Desktop/pythonn/curls2.h5')
    model2 = load_model('C:/Users/Monika/Desktop/pythonn/jumping_jacks.h5')
    model3 = load_model('C:/Users/Monika/Desktop/pythonn/squats3.h5')
    #model4 = load_model('C:/Users/Monika/Desktop/pythonn/rest.h5')


    # Initialize MediaPipe Pose
    mp_drawing = mp.solutions.drawing_utils
    mp_pose = mp.solutions.pose
    pose = mp_pose.Pose()

    def calculate_angle(a,b,c):
        a = np.array(a) # First
        b = np.array(b) # Mid
        c = np.array(c) # End

        radians = np.arctan2(c[1]-b[1], c[0]-b[0]) - np.arctan2(a[1]-b[1], a[0]-b[0])
        angle = np.abs(radians*180.0/np.pi)

        if angle >180.0:
            angle = 360-angle

        return angle 

    # Start capturing video from the webcam
    cap = cv2.VideoCapture(0)
    state_machine = ExerciseStateMachine()

    last_written_exercise = None
    last_written_rep_count = 0

    while cap.isOpened():
        ret, frame = cap.read()

        if not ret:
            break

        # Convert the BGR image to RGB
        image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        annotated_image = image.copy() 

        # Process the image and get the pose landmarks
        result = pose.process(image)

        if result.pose_landmarks is not None:
            annotated_image = image.copy()
            # Draw the pose landmarks on the image
            annotated_image = image.copy()
            mp_drawing.draw_landmarks(annotated_image, result.pose_landmarks, mp_pose.POSE_CONNECTIONS)

            # Get the coordinates of the landmarks
            landmarks = result.pose_landmarks.landmark

            # Calculate the first angle (wrist, elbow, shoulder)
            shoulder = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x, landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
            elbow = [landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].x, landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].y]
            wrist = [landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].y]
            angle1 = calculate_angle(shoulder, elbow, wrist)

            left_wrist = [landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].y]
            right_wrist = [landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_WRIST.value].y]
            left_ankle = [landmarks[mp_pose.PoseLandmark.LEFT_ANKLE.value].x, landmarks[mp_pose.PoseLandmark.LEFT_ANKLE.value].y]
            right_ankle = [landmarks[mp_pose.PoseLandmark.RIGHT_ANKLE.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_ANKLE.value].y]
                
            landmarks_flat = left_wrist + right_wrist + left_ankle + right_ankle
            right_knee = [landmarks[mp_pose.PoseLandmark.RIGHT_KNEE.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_KNEE.value].y]
            angle3 = calculate_angle(right_ankle, right_knee, left_ankle)

            # Calculate the fourth angle (right ankle, right knee, right hip)
            right_hip = [landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].x, landmarks[mp_pose.PoseLandmark.RIGHT_HIP.value].y]
            angle4 = calculate_angle(right_ankle, right_knee, right_hip)

               
            # Calculate the second angle (hip, shoulder, elbow)
            hip = [landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].x, landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].y]
            angle2 = calculate_angle(hip, shoulder, elbow)
            distance_left = ((hip[0] - left_ankle[0]) ** 2 + (hip[1] - left_ankle[1]) ** 2) ** 0.5

            sequence1 = np.array([[angle1, angle2, angle3, angle4]])  
            sequence1 = sequence1.reshape(1, 1, 4)


            sequence2 = np.array([[angle1, angle2, angle3, angle4,distance_left]])  
            sequence2 = sequence2.reshape(1, 1, 5)

            # Make a prediction for each model
            prediction1 = model1.predict(sequence2)[0][0]
            prediction2 = model2.predict(sequence1)[0][0]
            prediction3 = model3.predict(sequence1)[0][0]
            #prediction4 = model4.predict(sequence2)[0][0]

           
            max_prob = max(prediction1, prediction2, prediction3)
            label_dict = {prediction1: "Curls", prediction2: "Jumping Jacks", prediction3: "Squats"}
            exercise = label_dict[max_prob]

            if max_prob > 0.5:
                exercise = label_dict[max_prob]
            else:
                exercise = None
                
            exercise, rep_count = state_machine.update(exercise)

            if rep_count > 0 and (exercise != last_written_exercise or rep_count != last_written_rep_count):
                with open('exercise_data.csv', 'a', newline='') as file:
                    writer = csv.writer(file)
                    writer.writerow([exercise, rep_count])
            last_written_exercise = exercise
            last_written_rep_count = rep_count

            # Display the exercise and repetition count on the video frame
            if exercise is not None:

                cv2.putText(annotated_image, f"{exercise}: {rep_count} reps", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 0, 0), 2, cv2.LINE_AA)


        # Convert the image back to BGR for displaying
        image = cv2.cvtColor(annotated_image, cv2.COLOR_RGB2BGR)

        # Display the image
        cv2.imshow('MediaPipe Pose', image)

        # Exit if 'q' is pressed
        if cv2.waitKey(5) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

@app.route('/start-workout', methods=['POST'])
def start_workout():
    # Start the pose detection in a new thread
    threading.Thread(target=run_pose_detection).start()
    return 'Workout started'

if __name__ == '__main__':
    app.run(port=5001)
