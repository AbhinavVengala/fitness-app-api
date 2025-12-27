package com.abhi.FitnessTracker.Config;

import com.abhi.FitnessTracker.Model.Exercise;
import com.abhi.FitnessTracker.Model.Food;
import com.abhi.FitnessTracker.Repository.ExerciseRepository;
import com.abhi.FitnessTracker.Repository.FoodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

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
        List<Food> foods = List.of(
            // ===== INDIAN FOODS =====
            // North Indian & Breads
            new Food(null, "Roti / Chapati (1 medium)", 85, 3, 15, 1.5, "indian"),
            new Food(null, "Butter Roti (1 medium)", 115, 3, 15, 4.5, "indian"),
            new Food(null, "Paratha (1 plain)", 180, 4, 25, 7, "indian"),
            new Food(null, "Aloo Paratha (1 medium)", 210, 5, 32, 8, "indian"),
            new Food(null, "Paneer Paratha (1 medium)", 240, 9, 28, 10, "indian"),
            new Food(null, "Gobi Paratha (1 medium)", 190, 5, 29, 7, "indian"),
            new Food(null, "Naan (1 piece)", 260, 8, 45, 5, "indian"),
            new Food(null, "Butter Naan (1 piece)", 320, 8, 45, 12, "indian"),
            new Food(null, "Garlic Naan (1 piece)", 280, 8, 46, 7, "indian"),
            new Food(null, "Bhatura (1 large)", 280, 7, 40, 11, "indian"),
            new Food(null, "Kulcha (1 piece)", 220, 6, 38, 4, "indian"),
            new Food(null, "Thepla (1 medium)", 120, 4, 18, 5, "indian"),
            new Food(null, "Missi Roti (1 medium)", 130, 6, 20, 4, "indian"),

            // Curries & Dals
            new Food(null, "Dal Fry (1 cup)", 150, 9, 20, 4, "indian"),
            new Food(null, "Dal Makhani (1 cup)", 300, 10, 25, 18, "indian"),
            new Food(null, "Dal Tadka (1 cup)", 170, 9, 21, 6, "indian"),
            new Food(null, "Moong Dal (1 cup cooked)", 140, 12, 18, 3, "indian"),
            new Food(null, "Chana Masala (1 cup)", 200, 11, 30, 6, "indian"),
            new Food(null, "Rajma (1 cup)", 210, 12, 32, 6, "indian"),
            new Food(null, "Paneer Butter Masala (1 cup)", 380, 14, 15, 30, "indian"),
            new Food(null, "Palak Paneer (1 cup)", 260, 16, 12, 18, "indian"),
            new Food(null, "Matar Paneer (1 cup)", 280, 14, 20, 16, "indian"),
            new Food(null, "Kadai Paneer (1 cup)", 300, 15, 18, 20, "indian"),
            new Food(null, "Aloo Gobi (1 cup)", 160, 4, 22, 6, "indian"),
            new Food(null, "Bhindi Masala (1 cup)", 120, 3, 15, 6, "indian"),
            new Food(null, "Baingan Bharta (1 cup)", 110, 3, 14, 5, "indian"),
            new Food(null, "Mix Veg Curry (1 cup)", 190, 6, 22, 9, "indian"),
            new Food(null, "Chicken Curry (1 cup)", 280, 28, 8, 15, "indian"),
            new Food(null, "Butter Chicken (1 cup)", 450, 25, 12, 35, "indian"),
            new Food(null, "Fish Curry (1 cup)", 220, 22, 6, 12, "indian"),
            new Food(null, "Mutton Rogan Josh (1 cup)", 400, 28, 10, 28, "indian"),
            new Food(null, "Egg Curry (2 eggs + gravy)", 240, 16, 8, 16, "indian"),
             
            // Rice Dishes
            new Food(null, "Plain White Rice (1 cup cooked)", 205, 4, 45, 0.5, "indian"),
            new Food(null, "Jeera Rice (1 cup)", 230, 4, 45, 4, "indian"),
            new Food(null, "Veg Pulao (1 cup)", 250, 6, 42, 7, "indian"),
            new Food(null, "Peas Pulao (1 cup)", 240, 6, 42, 6, "indian"),
            new Food(null, "Veg Biryani (1 plate)", 350, 9, 55, 12, "indian"),
            new Food(null, "Chicken Biryani (1 plate)", 550, 35, 60, 18, "indian"),
            new Food(null, "Mutton Biryani (1 plate)", 650, 35, 60, 28, "indian"),
            new Food(null, "Egg Biryani (1 plate)", 450, 18, 55, 16, "indian"),
            new Food(null, "Khichdi (1 cup)", 180, 8, 28, 4, "indian"),
            new Food(null, "Curd Rice (1 cup)", 220, 8, 35, 6, "indian"),
            new Food(null, "Lemon Rice (1 cup)", 250, 5, 40, 9, "indian"),
            new Food(null, "Tamarind Rice (1 cup)", 280, 5, 48, 10, "indian"),
            new Food(null, "Tomato Rice (1 cup)", 240, 4, 42, 7, "indian"),
            
            // South Indian Breakfast
            new Food(null, "Plain Dosa (1 medium)", 130, 3, 24, 3, "indian"),
            new Food(null, "Masala Dosa (1 medium)", 350, 8, 45, 16, "indian"),
            new Food(null, "Rava Dosa (1 medium)", 160, 4, 28, 4, "indian"),
            new Food(null, "Mysore Masala Dosa", 380, 8, 48, 18, "indian"),
            new Food(null, "Set Dosa (1 piece)", 100, 2, 18, 2, "indian"),
            new Food(null, "Idli (1 piece)", 50, 2, 10, 0, "indian"),
            new Food(null, "Rava Idli (1 piece)", 70, 2, 12, 1, "indian"),
            new Food(null, "Medu Vada (1 piece)", 140, 4, 15, 8, "indian"),
            new Food(null, "Dal Vada (1 piece)", 120, 5, 12, 6, "indian"),
            new Food(null, "Uttapam (1 medium)", 220, 6, 35, 6, "indian"),
            new Food(null, "Onion Uttapam", 240, 6, 38, 7, "indian"),
            new Food(null, "Appam (1 piece)", 80, 1, 15, 2, "indian"),
            new Food(null, "Upma (1 cup)", 220, 6, 35, 7, "indian"),
            new Food(null, "Vermicelli Upma (1 cup)", 200, 5, 38, 5, "indian"),
            new Food(null, "Ven Pongal (1 cup)", 280, 8, 40, 10, "indian"),
            new Food(null, "Sweet Pongal (1 cup)", 350, 4, 65, 10, "indian"),
            new Food(null, "Puttu (1 piece)", 160, 4, 32, 2, "indian"),
            new Food(null, "Idiyappam (2 pieces)", 120, 2, 26, 1, "indian"),
            new Food(null, "Pesarattu (1 medium)", 200, 10, 30, 5, "indian"),
            new Food(null, "Adai (1 medium)", 220, 10, 32, 6, "indian"),
            
            // Accompaniments (South)
            new Food(null, "Sambhar (1 cup)", 120, 6, 18, 3, "indian"),
            new Food(null, "Coconut Chutney (1 tbsp)", 45, 1, 2, 4, "indian"),
            new Food(null, "Tomato Chutney (1 tbsp)", 25, 0, 3, 1, "indian"),
            new Food(null, "Rasam (1 cup)", 60, 2, 10, 1, "indian"),
            new Food(null, "Avial (1 cup)", 180, 5, 15, 10, "indian"),
            new Food(null, "Poriyal (1 cup)", 100, 3, 12, 5, "indian"),

            // Street Food & Snacks (Chaat)
            new Food(null, "Samosa (1 piece)", 250, 4, 25, 16, "indian"),
            new Food(null, "Samosa Chaat (1 plate)", 350, 8, 45, 18, "indian"),
            new Food(null, "Kachori (1 piece)", 280, 5, 28, 18, "indian"),
            new Food(null, "Pav Bhaji (1 plate)", 450, 12, 60, 18, "indian"),
            new Food(null, "Vada Pav (1 piece)", 300, 8, 40, 14, "indian"),
            new Food(null, "Pani Puri (6 pieces)", 180, 4, 32, 6, "indian"),
            new Food(null, "Bhel Puri (1 plate)", 220, 6, 38, 5, "indian"),
            new Food(null, "Sev Puri (1 plate)", 280, 6, 35, 14, "indian"),
            new Food(null, "Dahi Puri (1 plate)", 320, 8, 38, 16, "indian"),
            new Food(null, "Papdi Chaat (1 plate)", 300, 7, 35, 15, "indian"),
            new Food(null, "Aloo Tikki (1 piece)", 180, 3, 22, 10, "indian"),
            new Food(null, "Dhokla (1 piece)", 60, 2, 8, 2, "indian"),
            new Food(null, "Khandvi (1 roll)", 45, 2, 4, 2, "indian"),
            new Food(null, "Pakora - Onion/Veg (1 piece)", 60, 1, 5, 4, "indian"),
            new Food(null, "Pakora - Paneer (1 piece)", 90, 3, 4, 7, "indian"),
            new Food(null, "Momos - Veg steamed (1 piece)", 40, 1, 8, 0.5, "indian"),
            new Food(null, "Momos - Chicken steamed (1 piece)", 50, 3, 6, 1, "indian"),

            // Sweets & Desserts
            new Food(null, "Gulab Jamun (1 piece)", 150, 2, 22, 6, "indian"),
            new Food(null, "Rasgulla (1 piece)", 120, 2, 26, 1, "indian"),
            new Food(null, "Jalebi (1 large piece)", 180, 1, 30, 8, "indian"),
            new Food(null, "Kheer / Rice Pudding (1 cup)", 280, 7, 40, 11, "indian"),
            new Food(null, "Gajar Ka Halwa (1 cup)", 350, 6, 45, 16, "indian"),
            new Food(null, "Sooji Halwa (1 cup)", 320, 4, 42, 15, "indian"),
            new Food(null, "Mysore Pak (1 piece)", 200, 1, 18, 14, "indian"),
            new Food(null, "Barfi/Katli (1 piece)", 110, 2, 14, 6, "indian"),
            new Food(null, "Ladoo - Besan/Motichoor (1 piece)", 180, 3, 22, 10, "indian"),
            new Food(null, "Payasam / Kheer (1 cup)", 250, 5, 35, 10, "indian"),
            new Food(null, "Rasmalai (1 piece)", 160, 4, 18, 8, "indian"),

            // Beverages
            new Food(null, "Masala Chai (1 cup with milk)", 120, 4, 15, 5, "beverages"),
            new Food(null, "Filter Coffee (1 cup with milk)", 100, 3, 12, 4, "beverages"),
            new Food(null, "Lassi - Sweet (1 glass)", 220, 6, 30, 10, "beverages"),
            new Food(null, "Lassi - Salted/Chaas (1 glass)", 60, 3, 5, 3, "beverages"),
            new Food(null, "Thandai (1 glass)", 280, 6, 35, 12, "beverages"),
            new Food(null, "Badam Milk (1 glass)", 250, 8, 25, 14, "beverages"),
            new Food(null, "Sugarcane Juice (1 glass)", 180, 0, 45, 0, "beverages"),
            new Food(null, "Coconut Water (1 glass)", 40, 1, 9, 0, "beverages"),
            new Food(null, "Nimbu Pani / Lemonade (1 glass)", 50, 0, 13, 0, "beverages"),

             // ===== INTERNATIONAL FOODS (Common in India) =====
            // Proteins
            new Food(null, "Chicken Breast (100g grilled)", 165, 31, 0, 3.6, "protein"),
            new Food(null, "Chicken Thigh (100g)", 209, 26, 0, 11, "protein"),
            new Food(null, "Salmon (100g)", 208, 20, 0, 13, "protein"),
            new Food(null, "Tuna (100g canned)", 130, 28, 0, 1, "protein"),
            new Food(null, "Eggs (1 whole large)", 72, 6, 0.4, 5, "protein"),
            new Food(null, "Egg Whites (1 large)", 17, 3.6, 0.2, 0, "protein"),
            new Food(null, "Paneer (100g)", 265, 18, 1.2, 20.8, "protein"),
            new Food(null, "Tofu (100g)", 76, 8, 2, 4.8, "protein"),
            new Food(null, "Soya Chunks (50g raw)", 172, 26, 16, 0.5, "protein"),
            new Food(null, "Greek Yogurt (1 cup)", 130, 22, 9, 0, "protein"),
            new Food(null, "Whey Protein Scoop (30g)", 120, 24, 3, 1.5, "protein"),
            
            // Grains & Carbs
            new Food(null, "Brown Rice (1 cup cooked)", 216, 5, 45, 1.8, "grains"),
            new Food(null, "Oats (50g raw)", 190, 6.5, 34, 3, "grains"),
            new Food(null, "Quinoa (1 cup cooked)", 222, 8, 39, 3.5, "grains"),
            new Food(null, "Whole Wheat Bread (2 slices)", 180, 8, 32, 2, "grains"),
            new Food(null, "Multigrain Bread (2 slices)", 200, 9, 36, 3, "grains"),
            new Food(null, "Pasta - White (1 cup cooked)", 220, 8, 43, 1.3, "grains"),
            new Food(null, "Pasta - Whole Wheat (1 cup cooked)", 180, 8, 37, 1.5, "grains"),
            new Food(null, "Sweet Potato (150g baked)", 135, 3, 31, 0.2, "grains"),
            new Food(null, "Potato (150g boiled)", 130, 3, 30, 0.2, "grains"),
            new Food(null, "Corn / Bhutta (1 cob)", 90, 3, 19, 1, "grains"),
            
            // Fruits
            new Food(null, "Apple (1 medium)", 95, 0.5, 25, 0.3, "fruits"),
            new Food(null, "Banana (1 medium)", 105, 1.3, 27, 0.4, "fruits"),
            new Food(null, "Orange (1 medium)", 62, 1.2, 15, 0.2, "fruits"),
            new Food(null, "Mango (1 medium)", 200, 2.8, 50, 1, "fruits"),
            new Food(null, "Papaya (1 cup)", 60, 0.7, 15, 0.4, "fruits"),
            new Food(null, "Watermelon (1 cup)", 46, 0.9, 11, 0.2, "fruits"),
            new Food(null, "Grapes (1 cup)", 104, 1.1, 27, 0.2, "fruits"),
            new Food(null, "Pomegranate (1/2 cup)", 72, 1.5, 16, 1, "fruits"),
            new Food(null, "Guava (1 medium)", 38, 1.5, 8, 0.5, "fruits"),
            new Food(null, "Pineapple (1 cup)", 82, 0.9, 21, 0.2, "fruits"),
            
            // Vegetables
            new Food(null, "Broccoli (1 cup)", 55, 3.7, 11, 0.6, "vegetables"),
            new Food(null, "Spinach / Palak (1 cup cooked)", 41, 5, 7, 0.5, "vegetables"),
            new Food(null, "Carrot (1 medium)", 25, 0.6, 6, 0.1, "vegetables"),
            new Food(null, "Cucumber (1 medium)", 30, 2, 6, 0.2, "vegetables"),
            new Food(null, "Tomato (1 medium)", 22, 1, 5, 0.2, "vegetables"),
            new Food(null, "Cauliflower (1 cup)", 27, 2, 5, 0.3, "vegetables"),
            new Food(null, "Cabbage (1 cup)", 22, 1, 5, 0.1, "vegetables"),
            new Food(null, "Green Beans (1 cup)", 31, 2, 7, 0.1, "vegetables"),
            new Food(null, "Bottle Gourd / Lauki (1 cup)", 20, 1, 4, 0.1, "vegetables"),
            
            // Nuts & Seeds
            new Food(null, "Almonds (10 nuts)", 70, 2.5, 2.5, 6, "nuts"),
            new Food(null, "Walnuts (5 halves)", 65, 1.5, 1.4, 6.5, "nuts"),
            new Food(null, "Cashews (10 nuts)", 110, 3.5, 6, 9, "nuts"),
            new Food(null, "Peanuts (1 handful / 30g)", 170, 7, 5, 14, "nuts"),
            new Food(null, "Chia Seeds (1 tbsp)", 70, 2.5, 6, 4.5, "nuts"),
            new Food(null, "Flax Seeds (1 tbsp)", 55, 1.9, 3, 4.3, "nuts"),
            new Food(null, "Peanut Butter (1 tbsp)", 95, 3.5, 3.5, 8, "nuts"),
            
            // Dairy & Others
            new Food(null, "Milk - Toned/Cow (1 glass / 240ml)", 120, 8, 11, 4, "dairy"),
            new Food(null, "Milk - Full Cream/Buffalo (1 glass)", 180, 9, 12, 11, "dairy"),
            new Food(null, "Curd / Dahi (1 cup)", 150, 8, 10, 8, "dairy"),
            new Food(null, "Cheese Slice (1)", 70, 4, 1, 6, "dairy"),
            new Food(null, "Butter (1 tsp)", 34, 0, 0, 4, "dairy"),
            new Food(null, "Ghee (1 tsp)", 45, 0, 0, 5, "dairy"),
            
            // Snacks
            new Food(null, "Popcorn (2 cups air-popped)", 62, 2, 12, 0.7, "snacks"),
            new Food(null, "Dark Chocolate (2 squares)", 60, 1, 6, 4, "snacks"),
            new Food(null, "Makhana / Fox Nuts (1 cup)", 100, 3, 18, 1, "snacks"),
            new Food(null, "Roasted Chana (1 handful)", 100, 6, 15, 1.5, "snacks"),
            new Food(null, "Protein Bar (1 bar)", 220, 20, 24, 8, "snacks"),
            new Food(null, "Marie Biscuit (1 piece)", 25, 0.5, 4, 0.5, "snacks"),
            new Food(null, "Rusk (1 piece)", 45, 1, 8, 1, "snacks"),
            new Food(null, "Maggi Noodles (1 pack)", 310, 5, 45, 12, "snacks")
        );
        
        int addedCount = 0;
        int updatedCount = 0;
        for (Food food : foods) {
            Food existing = foodRepository.findByName(food.getName());
            
            // Assign image
            String imageUrl = getFoodImage(food.getName(), food.getCategory());
            
            if (existing != null) {
                // Update existing
                existing.setImageUrl(imageUrl);
                foodRepository.save(existing);
                updatedCount++;
            } else {
                // New food
                food.setImageUrl(imageUrl);
                foodRepository.save(food);
                addedCount++;
            }
        }
        
        System.out.println("Initialized/Updated foods. Added " + addedCount + ", Updated " + updatedCount + " items.");

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
        
        for (Exercise exercise : exercises) {
            Exercise existing = exerciseRepository.findByName(exercise.getName()).orElse(null);
            
            // Assign image
            String imageUrl = getExerciseImage(exercise.getName(), exercise.getCategory());
            
            if (existing != null) {
                // Update existing
                existing.setImageUrl(imageUrl);
                exerciseRepository.save(existing);
            } else {
                // New exercise
                exercise.setImageUrl(imageUrl);
                exerciseRepository.save(exercise);
            }
        }
        System.out.println("Initialized/Updated exercises with images.");
    }
    
    // --- Image Helper Methods ---
    
    private String getFoodImage(String name, String category) {
        String n = name.toLowerCase();
        String c = category.toLowerCase();
        
        // Base URL
        String baseUrl = "https://images.unsplash.com/photo-";
        String params = "?auto=format&fit=crop&w=500&q=80";

        // --- SPECIFIC INDIAN DISHES ---
        if (n.contains("butter chicken") || n.contains("makhani")) return baseUrl + "1603894584373-b42c7e093cfd" + params; // Butter Chicken
        if (n.contains("palak paneer") || n.contains("saag")) return baseUrl + "1606497525501-696001d4f9a0" + params; // Palak Paneer / Thali
        if (n.contains("paneer")) return baseUrl + "1631452180519-c0250721dc3e" + params; // Generic Paneer
        if (n.contains("chole") || n.contains("chana")) return baseUrl + "1589302596001-4b3c7990a69d" + params; // Chole
        if (n.contains("rajma")) return baseUrl + "1546833999-b9f58160293f" + params; // Rajma (Bean Curry)
        if (n.contains("dal")) return baseUrl + "1589302596001-4b3c7990a69d" + params; // Dal
        if (n.contains("sambar")) return baseUrl + "1601050690597-df0568f70950" + params; // Sambar/Bowl

        if (n.contains("dosa")) return baseUrl + "1668236543090-d2f4927d9877" + params; // Dosa
        if (n.contains("idli")) return baseUrl + "1589301760574-d81d63940642" + params; // Idli
        if (n.contains("vada")) return baseUrl + "1601050690597-df0568f70950" + params; // Vada
        if (n.contains("upma")) return baseUrl + "1515516941852-98876d831de6" + params; // Upma (Semolina texture)
        if (n.contains("poha")) return baseUrl + "1515516941852-98876d831de6" + params; // Poha (Flattened rice)

        if (n.contains("biryani")) return baseUrl + "1633945274405-b6c8069047b0" + params; // Biryani
        if (n.contains("khichdi") || n.contains("pongal")) return baseUrl + "1589302596001-4b3c7990a69d" + params; // Soft rice/lentil
        
        // --- BREADS ---
        if (n.contains("naan")) return baseUrl + "1626082927389-e175950d8880" + params; // Naan
        if (n.contains("roti") || n.contains("chapati") || n.contains("phulka")) return baseUrl + "1601050690597-df0568f70950" + params; // Roti
        if (n.contains("paratha") || n.contains("alu paratha")) return baseUrl + "1626082927389-e175950d8880" + params; // Paratha stuffed
        if (n.contains("bhature") || n.contains("puri")) return baseUrl + "1626082927389-e175950d8880" + params; // Fried Bread

        // --- COMMON FOODS ---
        if (n.contains("rice")) return baseUrl + "1516685018646-612d0c6e57b3" + params; // White Rice
        if (n.contains("jeera rice")) return baseUrl + "1516685018646-612d0c6e57b3" + params;
        
        if (n.contains("egg") || n.contains("omelet") || n.contains("bhurji")) return baseUrl + "1506976785307-8732e854ad03" + params; // Eggs
        if (n.contains("chicken curry")) return baseUrl + "1603894584373-b42c7e093cfd" + params; // Chicken Curry
        if (n.contains("chicken")) return baseUrl + "1604908176997-125f25cc6f3d" + params; // General Chicken
        if (n.contains("fish") || n.contains("prawn")) return baseUrl + "1467003909585-63c6385cdb8d" + params; // Fish
        
        // --- SNACKS / DRINKS ---
        if (n.contains("chai") || n.contains("tea")) return baseUrl + "1541167760496-1628856ab772" + params; // Chai
        if (n.contains("coffee")) return baseUrl + "1497935586351-b67a49e012bf" + params; // Coffee
        if (n.contains("lassi") || n.contains("buttermilk")) return baseUrl + "1563636307-f3ec68297076" + params; // Lassi
        if (n.contains("samosa") || n.contains("pakora")) return baseUrl + "1601050690597-df0568f70950" + params; // Fried Snack
        
        // --- CATEGORIES ---
        if (c.contains("fruit") || n.contains("apple") || n.contains("banana") || n.contains("papaya")) return baseUrl + "1610832958506-aa56368176cf" + params;
        if (c.contains("vegetable") || n.contains("salad") || n.contains("cucumber")) return baseUrl + "1512621776951-a57141f2eefd" + params;
        
        // --- FALLBACK DETERMINISTIC VARIETY ---
        // Use hash of name to pick 1 of 3 generic images so they don't all look identical
        int hash = Math.abs(name.hashCode() % 3);
        if (c.contains("indian")) {
             String[] indianGenerics = {
                 baseUrl + "1589302596001-4b3c7990a69d" + params, // Curry
                 baseUrl + "1606497525501-696001d4f9a0" + params, // Thali
                 baseUrl + "1626082927389-e175950d8880" + params  // Feast
             };
             return indianGenerics[hash];
        }

        // Generic Default
        return baseUrl + "1504674900247-0877df9cc836" + params;
    }
    
    private String getExerciseImage(String name, String category) {
        String n = name.toLowerCase();
        
        String baseUrl = "https://images.unsplash.com/photo-";
        String params = "?auto=format&fit=crop&w=500&q=80";

        // ===== BODYWEIGHT / HOME =====
        if (n.equals("push-ups") || n.equals("pushups")) return baseUrl + "1598971639067-5a4244fa1c49" + params; // Man doing pushups
        if (n.equals("wide push-ups")) return baseUrl + "1598971639067-5a4244fa1c49" + params; 
        if (n.equals("diamond push-ups")) return baseUrl + "1571019614242-c5c5dee9f50b" + params; // Close up gym/floor
        if (n.equals("squats") || n.equals("squat")) return baseUrl + "1574680096141-1dbd6f21d973" + params; // Squat rack/legs
        if (n.equals("jump squats")) return baseUrl + "1595163158971-36ba9519c2c2" + params; // Jumping
        if (n.equals("lunges") || n.equals("lunge")) return baseUrl + "1434608519389-88fa3d92f3e0" + params; // Runner lunging
        if (n.equals("plank")) return baseUrl + "1571019614242-c5c5dee9f50b" + params; // Plank/Floor
        if (n.equals("jumping jacks")) return baseUrl + "1595163158971-36ba9519c2c2" + params; // Jumping
        if (n.equals("burpees")) return baseUrl + "1599058945522-28d584b6f0ff" + params; // Intense training
        if (n.equals("mountain climbers")) return baseUrl + "1434596922112-19c563067271" + params; // Running motion
        
        // ===== GYM / WEIGHTS =====
        if (n.contains("bench press")) return baseUrl + "1517836357463-c25dfe94c0de" + params; // Bench press setup
        if (n.contains("deadlift")) return baseUrl + "1517963843464-6b715bebe059" + params; // Weights on floor
        if (n.contains("pull-ups") || n.contains("pullups")) return baseUrl + "1598971639067-5a4244fa1c49" + params; // Pull up bar context
        if (n.contains("lat pulldown")) return baseUrl + "1541534741688-6078c6bfb5c5" + params; // Gym machines
        if (n.contains("overhead press")) return baseUrl + "1532029837877-3377d6ad0e3a" + params; // Dumbbells overhead
        if (n.contains("bicep curls")) return baseUrl + "1583454110551-21f2fa2afe61" + params; // Dumbbell curl
        if (n.contains("tricep dips")) return baseUrl + "1571019614242-c5c5dee9f50b" + params; // Dips
        if (n.contains("leg press")) return baseUrl + "1574680096141-1dbd6f21d973" + params; // Gym legs
        
        // ===== YOGA =====
        if (n.contains("sun salutation") || n.contains("surya")) return baseUrl + "1593164842264-854604eb9233" + params; // Yoga silhouette
        if (n.contains("tree pose")) return baseUrl + "1566501202865-c740e36852ba" + params; // Tree pose
        if (n.contains("warrior")) return baseUrl + "1506126613408-eca07ce68773" + params; // Yoga beach
        if (n.contains("downward dog")) return baseUrl + "1544367563-121cf94e191e" + params; // Yoga stretch
        if (n.contains("cobra")) return baseUrl + "1575052814088-dcbb52f20556" + params; // Stretching on mat
        if (n.contains("child's pose")) return baseUrl + "1544367563-121cf94e191e" + params; // Resting
        if (n.contains("stretch")) return baseUrl + "1518611012118-696072aa579a" + params; // General stretching
        
        // ===== HIIT / CARDIO =====
        if (n.contains("run") || n.contains("jog") || n.contains("treadmill")) return baseUrl + "1452626038306-3a7a48dfacf0" + params; // Running shoes/track
        if (n.contains("cycle") || n.contains("bike")) return baseUrl + "1541625602330-2277a4c46182" + params; // Cyclist
        if (n.contains("swim")) return baseUrl + "1600965962361-9035dbfd1c50" + params; // Swimmer
        if (n.contains("jump rope")) return baseUrl + "1595163158971-36ba9519c2c2" + params; // Skipping
        if (n.contains("box jump")) return baseUrl + "1599058945522-28d584b6f0ff" + params; // Plyo box
        if (n.contains("kettlebell")) return baseUrl + "1599058945522-28d584b6f0ff" + params; // Kettlebell
        if (n.contains("rowing")) return baseUrl + "1540497077202-7c8a3999166f" + params; // Rower
        if (n.contains("tennis")) return baseUrl + "1622602703816-e575742c0698" + params; // Tennis court
        if (n.contains("badminton")) return baseUrl + "1626248559288-12c828cb2c67" + params; // Badminton
        if (n.contains("football") || n.contains("soccer")) return baseUrl + "1579952363873-27f3bade9f55" + params; // Football
        if (n.contains("cricket")) return baseUrl + "1531415074984-95691ec13189" + params; // Cricket
        if (n.contains("zumba") || n.contains("dance")) return baseUrl + "1524594152303-9fd13543fe6e" + params; // Dancing
        if (n.contains("hike") || n.contains("hiking")) return baseUrl + "1551632811-561732d1e306" + params; // Hiking boots
        
        // Default Fallback
        return baseUrl + "1517836357463-c25dfe94c0de" + params; // General Gym
    }
}

