import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        //JAVA QUIZ GAME

        String[] questions = {"1. When was I born?",
                              "2. Which part is called the brain of the computer?",
                              "3. Which year is it now?",
                              "4. Who discovered gravity?",
                              "5. Which programming language is this?"};

        String[][] options = {{"1. 2001" , "2. 2003" , "3. 2004" , "4. 2005"},
                              {"1. GPU" , "2. Keyboard" , "3. CPU" , "4. Mouse"},
                              {"1. 2021" , "2. 2022" , "3. 2024" , "4. 2025"},
                              {"1. Einstein" , "2. Isaac Newton" , "3. Charles Babbage" , "4. Rutherford"},
                              {"1. JAVA" , "2. C++" , "3. JavaScript" , "4. HTML"}};

        int[] answers = {4 , 3 , 4 , 2 , 1};

        int score = 0;
        int guess;

        Scanner scanner = new Scanner(System.in);

        System.out.println("******************************");
        System.out.println("WELCOME TO THE JAVA QUIZ GAME!");
        System.out.println("******************************");


        for(int i = 0; i < questions.length; i++){
            System.out.println(questions[i]);

            for(String option : options[i]){
                System.out.println(option);
            }

            System.out.print("Enter your guess: ");
            guess = scanner.nextInt();

            if(guess == answers[i]){

                System.out.println("********");
                System.out.println("CORRECT!");
                System.out.println("********");
                score++;
            }
            else{

                System.out.println("******");
                System.out.println("WRONG!");
                System.out.println("******");
            }
            }

        System.out.println("Your score is: " + score + " out of " + questions.length);
        scanner.close();
        }
    }
