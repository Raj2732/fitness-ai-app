# 🏋️‍♂️ FitAI: Intelligent Health & Wellness Orchestrator

FitAI is a full-stack AI-driven fitness application designed to provide hyper-personalized health strategies. By leveraging machine learning and data analysis, the app automates the creation of nutrition, training, and recovery protocols tailored to individual biometrics.

---

## 🚀 Core Features

### 🥗 Automated Diet Planning
The AI calculates daily caloric requirements and macronutrient splits based on the user's Total Daily Energy Expenditure (TDEE).
- **Precision Macros**: Calculated using the Mifflin-St Jeor Equation.
- **Dynamic Adjustments**: Plans evolve based on weekly weight fluctuations.

### ⚡ Smart Workout Generation
Automated training splits (Push/Pull/Legs, Upper/Lower, or Full Body) generated based on:
- **Volume Tracking**: Optimization of sets and reps for hypertrophy or strength.
- **Recovery AI**: Adjusts intensity based on reported soreness and sleep data.

### 🤖 AMI (Artificial Muscle Intelligence)
A custom AI assistant that acts as a 24/7 fitness coach. 
- **Natural Language Processing**: Ask AMI for meal replacements or form cues.
- **Data Insights**: Visualizes progress trends using Matplotlib/Seaborn.

---

## 🛠️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Python / Java / java script/ css|
| **Data Engine** | Pandas, NumPy |
| **AI/ML** | Scikit-learn (for predictive modeling) |
| **Database** | SQLite3 / PostgreSQL (via DB-API) |
| **Visualization** | Seaborn, Matplotlib |

---

## 🧬 The Mathematics of Fitness (AI Logic)

The app uses the following formulas to generate your automated plans:

### 1. Basal Metabolic Rate (BMR)
For men:
$$BMR = 10w + 6.25h - 5a + 5$$
For women:
$$BMR = 10w + 6.25h - 5a - 161$$
*(Where $w$ = weight in kg, $h$ = height in cm, $a$ = age in years)*

### 2. Training Volume Load
The AI tracks progress using the Volume Load formula to ensure Progressive Overload:
$$\text{Volume} = \text{Sets} \times \text{Reps} \times \text{Weight}$$

---

## 📂 Project Structure
