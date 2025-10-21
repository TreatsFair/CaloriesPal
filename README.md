# CaloriesPal 🥗 

CaloriesPal is a **fitness and nutrition tracking desktop application** built using **JavaFX and ScalaFX**, designed to help users monitor their daily calorie intake, manage meals, and stay on track with their health goals.


---

## Features
- **User Profiles** — Register and log in with personalized calorie and fitness goals.  
- **Meal Logging** — Add, view, and manage meals for **breakfast, lunch, dinner, and snacks**.  
- **Food Search** — Search foods from a preloaded **CSV nutrition dataset**.  
- **Data Persistence** — All records are stored using a **Derby Database**.  
- **Progress Tracking** — View daily summaries and historical calorie progress.  
- **Workout Logging** — Record workouts, durations, and calories burned.  
- **Goal Management** — Update weight, calorie goals, and fitness preferences in your profile.  

---

## Tech Stack
| Category | Tools |
|-----------|-------|
| **Language** | Scala 3|
| **UI Framework** | JavaFX, ScalaFX, SceneBuilder |
| **Database** | Apache Derby (embedded) |
| **Data Handling** | Tototoshi CSV Parser |
| **Build Tool** | SBT |
| **IDE** | IntelliJ IDEA |

---
## How to Run
### Prerequisites
Ensure the following are installed:
- Java Development Kit (JDK 21 or higher)  
- Scala 3+  
- SBT (Scala Build Tool)

Check if SBT is installed:
```
sbt --version
```
Clone the repository:
```
git clone https://github.com/TreatsFair/CaloriesPal.git
cd CaloriesPal
```
Build and Run the app:
```
sbt run
```
---

## How to Use
1. **Log in or Register** to create your personal account.  
2. **Navigate** through the sidebar menu to access your dashboard, food logs, exercise logs, and profile.  
3. **Set up your Profile and Goals** with current weight and calorie target for accurate tracking.  
4. **Add, Update, or Delete** meals and workout entries to keep data up-to-date.  
5. **Monitor Progress** daily or historically and adjust your goals as needed.  

---

## Credits
Developer: Chin Chun Hung

Institution: Sunway University, Faculty of Engineering and Technology, BSc (Hons) in Computer Science
Course: PRG2104 - Object Oriented Programming
Academic Session: April 2025

---

This project was developed as part of the Object Oriented Programming course, showcasing the practical application of OOP principles in game development.
