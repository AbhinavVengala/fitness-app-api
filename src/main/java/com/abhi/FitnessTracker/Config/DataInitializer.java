package com.abhi.FitnessTracker.Config;

import com.abhi.FitnessTracker.Model.Exercise;
import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Repository.ExerciseRepository;
import com.abhi.FitnessTracker.Repository.FoodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes the database with default food and exercise data.
 * Runs on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final FoodRepository foodRepository;
    private final ExerciseRepository exerciseRepository;
    
    public DataInitializer(FoodRepository foodRepository, ExerciseRepository exerciseRepository) {
        this.foodRepository = foodRepository;
        this.exerciseRepository = exerciseRepository;
    }
    
    @Override
    public void run(String... args) {
        initializeFoods();
        initializeExercises();
    }
    
    private void initializeFoods() {
        // Only initialize if empty
        if (foodRepository.count() > 0) {
            return;
        }
        
        List<Food> foods = List.of(
            // ===== INDIAN FOODS =====
            // North Indian
            new Food(null, "Roti / Chapati (1 medium)", 85, 3, 15, 1.5, "indian"),
            new Food(null, "Paratha (1 plain)", 180, 4, 25, 7, "indian"),
            new Food(null, "Naan (1 piece)", 260, 8, 45, 5, "indian"),
            new Food(null, "Dal Fry (1 cup)", 150, 9, 20, 4, "indian"),
            new Food(null, "Dal Makhani (1 cup)", 250, 10, 25, 12, "indian"),
            new Food(null, "Paneer Butter Masala (1 cup)", 350, 15, 12, 28, "indian"),
            new Food(null, "Palak Paneer (1 cup)", 280, 14, 10, 20, "indian"),
            new Food(null, "Chole / Chana Masala (1 cup)", 200, 10, 30, 6, "indian"),
            new Food(null, "Rajma (1 cup)", 180, 10, 28, 3, "indian"),
            new Food(null, "Aloo Gobi (1 cup)", 150, 4, 22, 5, "indian"),
            new Food(null, "Biryani (1 plate)", 400, 12, 50, 15, "indian"),
            new Food(null, "Pulao (1 cup)", 220, 5, 40, 5, "indian"),
            new Food(null, "Samosa (1 piece)", 150, 3, 18, 8, "indian"),
            new Food(null, "Chole Bhature (1 serving)", 450, 12, 55, 20, "indian"),
            new Food(null, "Poori (2 pieces)", 200, 4, 28, 8, "indian"),
            new Food(null, "Kheer (1 cup)", 250, 6, 35, 10, "indian"),
            new Food(null, "Gulab Jamun (2 pieces)", 300, 4, 45, 12, "indian"),
            
            // South Indian
            new Food(null, "Plain Dosa (1 medium)", 120, 3, 25, 2, "indian"),
            new Food(null, "Masala Dosa", 250, 6, 40, 8, "indian"),
            new Food(null, "Idli (2 pieces)", 80, 2, 16, 0.4, "indian"),
            new Food(null, "Vada (1 piece)", 130, 4, 15, 6, "indian"),
            new Food(null, "Upma (1 cup)", 200, 5, 35, 5, "indian"),
            new Food(null, "Pongal (1 cup)", 230, 6, 38, 6, "indian"),
            new Food(null, "Sambhar (1 cup)", 100, 5, 15, 2, "indian"),
            new Food(null, "Rasam (1 cup)", 50, 2, 8, 1, "indian"),
            new Food(null, "Uttapam (1 medium)", 180, 5, 30, 4, "indian"),
            new Food(null, "Appam (2 pieces)", 160, 3, 28, 3, "indian"),
            
            // Street Food
            new Food(null, "Pav Bhaji (1 serving)", 400, 10, 55, 15, "indian"),
            new Food(null, "Bhel Puri (1 plate)", 200, 4, 35, 5, "indian"),
            new Food(null, "Pani Puri (6 pieces)", 180, 3, 30, 5, "indian"),
            new Food(null, "Vada Pav (1 piece)", 290, 6, 40, 12, "indian"),
            new Food(null, "Dabeli (1 piece)", 250, 5, 38, 8, "indian"),
            new Food(null, "Kachori (1 piece)", 180, 4, 22, 9, "indian"),
            
            // ===== INTERNATIONAL FOODS =====
            // Proteins
            new Food(null, "Chicken Breast (100g grilled)", 165, 31, 0, 3.6, "protein"),
            new Food(null, "Chicken Thigh (100g)", 209, 26, 0, 11, "protein"),
            new Food(null, "Salmon (100g)", 208, 20, 0, 13, "protein"),
            new Food(null, "Tuna (100g canned)", 130, 28, 0, 1, "protein"),
            new Food(null, "Eggs (2 whole)", 140, 12, 1, 10, "protein"),
            new Food(null, "Egg Whites (4)", 68, 14, 0, 0, "protein"),
            new Food(null, "Paneer (100g)", 265, 18, 4, 20, "protein"),
            new Food(null, "Tofu (100g)", 76, 8, 2, 4, "protein"),
            new Food(null, "Cottage Cheese (1 cup)", 220, 28, 6, 10, "protein"),
            new Food(null, "Greek Yogurt (1 cup)", 150, 15, 10, 5, "protein"),
            new Food(null, "Whey Protein Scoop (30g)", 120, 24, 3, 1.5, "protein"),
            
            // Grains & Carbs
            new Food(null, "White Rice (1 cup cooked)", 205, 4, 45, 0.4, "grains"),
            new Food(null, "Brown Rice (1 cup cooked)", 215, 5, 45, 1.8, "grains"),
            new Food(null, "Oats (1/2 cup dry)", 150, 5, 27, 2.5, "grains"),
            new Food(null, "Quinoa (1 cup cooked)", 220, 8, 39, 3.5, "grains"),
            new Food(null, "Whole Wheat Bread (2 slices)", 160, 7, 28, 2, "grains"),
            new Food(null, "Pasta (1 cup cooked)", 220, 8, 43, 1.3, "grains"),
            new Food(null, "Sweet Potato (1 medium)", 103, 2, 24, 0.1, "grains"),
            new Food(null, "Potato (1 medium boiled)", 130, 3, 30, 0.1, "grains"),
            
            // Fruits
            new Food(null, "Apple (1 medium)", 95, 0.5, 25, 0.3, "fruits"),
            new Food(null, "Banana (1 medium)", 105, 1.3, 27, 0.4, "fruits"),
            new Food(null, "Orange (1 medium)", 62, 1, 15, 0.2, "fruits"),
            new Food(null, "Mango (1 cup sliced)", 100, 1, 25, 0.6, "fruits"),
            new Food(null, "Papaya (1 cup cubed)", 55, 0.8, 14, 0.2, "fruits"),
            new Food(null, "Watermelon (1 cup)", 46, 0.9, 11, 0.2, "fruits"),
            new Food(null, "Grapes (1 cup)", 104, 1, 27, 0.2, "fruits"),
            new Food(null, "Pomegranate (1 cup)", 145, 3, 33, 2, "fruits"),
            
            // Vegetables
            new Food(null, "Broccoli (1 cup)", 55, 4, 11, 0.5, "vegetables"),
            new Food(null, "Spinach (1 cup cooked)", 41, 5, 7, 0.5, "vegetables"),
            new Food(null, "Carrot (1 medium)", 25, 0.6, 6, 0.1, "vegetables"),
            new Food(null, "Cucumber (1 cup sliced)", 16, 0.8, 4, 0.1, "vegetables"),
            new Food(null, "Tomato (1 medium)", 22, 1, 5, 0.2, "vegetables"),
            new Food(null, "Bell Pepper (1 medium)", 30, 1, 7, 0.3, "vegetables"),
            new Food(null, "Onion (1 medium)", 44, 1, 10, 0.1, "vegetables"),
            
            // Nuts & Seeds
            new Food(null, "Almonds (1 oz)", 164, 6, 6, 14, "nuts"),
            new Food(null, "Walnuts (1 oz)", 185, 4, 4, 18, "nuts"),
            new Food(null, "Cashews (1 oz)", 157, 5, 9, 12, "nuts"),
            new Food(null, "Peanuts (1 oz)", 161, 7, 5, 14, "nuts"),
            new Food(null, "Chia Seeds (2 tbsp)", 138, 5, 12, 9, "nuts"),
            new Food(null, "Flax Seeds (2 tbsp)", 110, 4, 6, 8, "nuts"),
            new Food(null, "Peanut Butter (2 tbsp)", 190, 7, 7, 16, "nuts"),
            
            // Dairy
            new Food(null, "Milk - Full Fat (1 glass)", 150, 8, 12, 8, "dairy"),
            new Food(null, "Milk - Skimmed (1 glass)", 90, 8, 12, 0.5, "dairy"),
            new Food(null, "Curd / Yogurt (1 cup)", 100, 4, 8, 5, "dairy"),
            new Food(null, "Buttermilk (1 glass)", 40, 2, 5, 1, "dairy"),
            new Food(null, "Cheese Slice (1)", 70, 4, 0.5, 5.5, "dairy"),
            
            // Beverages
            new Food(null, "Green Tea (1 cup)", 2, 0, 0, 0, "beverages"),
            new Food(null, "Black Coffee (1 cup)", 5, 0.3, 0, 0, "beverages"),
            new Food(null, "Chai / Tea with Milk (1 cup)", 60, 2, 8, 2, "beverages"),
            new Food(null, "Coconut Water (1 glass)", 45, 2, 9, 0.5, "beverages"),
            new Food(null, "Fresh Lime Soda", 50, 0, 12, 0, "beverages"),
            new Food(null, "Lassi - Sweet (1 glass)", 180, 5, 25, 6, "beverages"),
            new Food(null, "Protein Shake (with milk)", 280, 30, 20, 8, "beverages"),
            
            // Snacks
            new Food(null, "Popcorn (2 cups air-popped)", 62, 2, 12, 0.7, "snacks"),
            new Food(null, "Dark Chocolate (1 oz)", 170, 2, 13, 12, "snacks"),
            new Food(null, "Makhana / Fox Nuts (1 cup)", 100, 3, 18, 1, "snacks"),
            new Food(null, "Roasted Chana (1/4 cup)", 120, 7, 18, 2, "snacks"),
            new Food(null, "Protein Bar (1 bar)", 200, 20, 22, 7, "snacks")
        );
        
        foodRepository.saveAll(foods);
        System.out.println("Initialized " + foods.size() + " foods in database");
    }
    
    private void initializeExercises() {
        // Only initialize if empty
        if (exerciseRepository.count() > 0) {
            return;
        }
        
        List<Exercise> exercises = List.of(
            // ===== HOME EXERCISES =====
            new Exercise(null, "Jumping Jacks", "duration", null, 8.0, "home"),
            new Exercise(null, "Push-ups", "reps", 0.8, null, "home"),
            new Exercise(null, "Wide Push-ups", "reps", 0.7, null, "home"),
            new Exercise(null, "Diamond Push-ups", "reps", 0.9, null, "home"),
            new Exercise(null, "Squats", "reps", 0.6, null, "home"),
            new Exercise(null, "Jump Squats", "reps", 1.0, null, "home"),
            new Exercise(null, "Lunges", "reps", 0.5, null, "home"),
            new Exercise(null, "Walking Lunges", "reps", 0.6, null, "home"),
            new Exercise(null, "Plank", "duration", null, 3.0, "home"),
            new Exercise(null, "Side Plank", "duration", null, 3.5, "home"),
            new Exercise(null, "Burpees", "reps", 1.5, null, "home"),
            new Exercise(null, "Mountain Climbers", "duration", null, 8.0, "home"),
            new Exercise(null, "High Knees", "duration", null, 7.0, "home"),
            new Exercise(null, "Butt Kicks", "duration", null, 6.5, "home"),
            new Exercise(null, "Crunches", "reps", 0.3, null, "home"),
            new Exercise(null, "Bicycle Crunches", "reps", 0.4, null, "home"),
            new Exercise(null, "Leg Raises", "reps", 0.4, null, "home"),
            new Exercise(null, "Flutter Kicks", "duration", null, 5.0, "home"),
            new Exercise(null, "Superman Hold", "duration", null, 3.0, "home"),
            new Exercise(null, "Glute Bridges", "reps", 0.4, null, "home"),
            new Exercise(null, "Wall Sit", "duration", null, 4.0, "home"),
            new Exercise(null, "Inchworm", "reps", 0.8, null, "home"),
            
            // ===== GYM EXERCISES =====
            // Chest
            new Exercise(null, "Bench Press", "reps", 1.2, null, "gym"),
            new Exercise(null, "Incline Bench Press", "reps", 1.1, null, "gym"),
            new Exercise(null, "Dumbbell Flyes", "reps", 0.7, null, "gym"),
            new Exercise(null, "Cable Crossover", "reps", 0.6, null, "gym"),
            // Back
            new Exercise(null, "Deadlift", "reps", 1.8, null, "gym"),
            new Exercise(null, "Lat Pulldown", "reps", 0.8, null, "gym"),
            new Exercise(null, "Bent Over Row", "reps", 1.0, null, "gym"),
            new Exercise(null, "Seated Cable Row", "reps", 0.8, null, "gym"),
            new Exercise(null, "Pull-ups", "reps", 1.0, null, "gym"),
            // Shoulders
            new Exercise(null, "Shoulder Press", "reps", 0.9, null, "gym"),
            new Exercise(null, "Lateral Raises", "reps", 0.5, null, "gym"),
            new Exercise(null, "Front Raises", "reps", 0.5, null, "gym"),
            new Exercise(null, "Face Pulls", "reps", 0.4, null, "gym"),
            // Arms
            new Exercise(null, "Bicep Curls", "reps", 0.5, null, "gym"),
            new Exercise(null, "Hammer Curls", "reps", 0.5, null, "gym"),
            new Exercise(null, "Tricep Dips", "reps", 0.6, null, "gym"),
            new Exercise(null, "Tricep Pushdown", "reps", 0.5, null, "gym"),
            new Exercise(null, "Skull Crushers", "reps", 0.6, null, "gym"),
            // Legs
            new Exercise(null, "Barbell Squat", "reps", 1.5, null, "gym"),
            new Exercise(null, "Leg Press", "reps", 1.0, null, "gym"),
            new Exercise(null, "Leg Extension", "reps", 0.6, null, "gym"),
            new Exercise(null, "Leg Curl", "reps", 0.5, null, "gym"),
            new Exercise(null, "Calf Raises", "reps", 0.3, null, "gym"),
            new Exercise(null, "Romanian Deadlift", "reps", 1.2, null, "gym"),
            
            // ===== YOGA =====
            new Exercise(null, "Sun Salutation (Surya Namaskar)", "reps", 3.5, null, "yoga"),
            new Exercise(null, "Tree Pose (Vrikshasana)", "duration", null, 2.0, "yoga"),
            new Exercise(null, "Warrior Pose (Virabhadrasana)", "duration", null, 2.5, "yoga"),
            new Exercise(null, "Downward Dog", "duration", null, 2.0, "yoga"),
            new Exercise(null, "Cobra Pose (Bhujangasana)", "duration", null, 1.5, "yoga"),
            new Exercise(null, "Child's Pose", "duration", null, 1.0, "yoga"),
            new Exercise(null, "Boat Pose (Navasana)", "duration", null, 3.0, "yoga"),
            new Exercise(null, "Bridge Pose", "duration", null, 2.0, "yoga"),
            new Exercise(null, "Cat-Cow Stretch", "duration", null, 1.5, "yoga"),
            new Exercise(null, "Pigeon Pose", "duration", null, 1.5, "yoga"),
            
            // ===== HIIT =====
            new Exercise(null, "Box Jumps", "reps", 1.2, null, "hiit"),
            new Exercise(null, "Kettlebell Swings", "reps", 1.0, null, "hiit"),
            new Exercise(null, "Battle Ropes", "duration", null, 10.0, "hiit"),
            new Exercise(null, "Sled Push", "duration", null, 12.0, "hiit"),
            new Exercise(null, "Rowing Machine", "duration", null, 8.0, "hiit"),
            new Exercise(null, "Assault Bike", "duration", null, 10.0, "hiit"),
            new Exercise(null, "Treadmill Sprint", "duration", null, 12.0, "hiit"),
            
            // ===== SPORTS & CARDIO =====
            new Exercise(null, "Running", "duration", null, 9.8, "sports"),
            new Exercise(null, "Jogging", "duration", null, 7.0, "sports"),
            new Exercise(null, "Walking", "duration", null, 3.5, "sports"),
            new Exercise(null, "Brisk Walking", "duration", null, 5.0, "sports"),
            new Exercise(null, "Cycling", "duration", null, 7.5, "sports"),
            new Exercise(null, "Cycling (High Intensity)", "duration", null, 10.0, "sports"),
            new Exercise(null, "Swimming", "duration", null, 8.0, "sports"),
            new Exercise(null, "Jump Rope", "duration", null, 12.0, "sports"),
            new Exercise(null, "Tennis", "duration", null, 7.0, "sports"),
            new Exercise(null, "Badminton", "duration", null, 5.5, "sports"),
            new Exercise(null, "Basketball", "duration", null, 8.0, "sports"),
            new Exercise(null, "Football/Soccer", "duration", null, 9.0, "sports"),
            new Exercise(null, "Cricket", "duration", null, 5.0, "sports"),
            new Exercise(null, "Dancing", "duration", null, 6.0, "sports"),
            new Exercise(null, "Zumba", "duration", null, 7.5, "sports"),
            new Exercise(null, "Hiking", "duration", null, 6.0, "sports"),
            new Exercise(null, "Stair Climbing", "duration", null, 8.5, "sports")
        );
        
        exerciseRepository.saveAll(exercises);
        System.out.println("Initialized " + exercises.size() + " exercises in database");
    }
}

