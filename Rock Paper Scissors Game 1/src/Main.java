import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //ROCK PAPER SCISSORS GAME

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        String[] choices = {"rock" , "paper" , "scissors"};
        String playerChoice;
        String computerChoice;
        String playAgain = "yes";
        int score = 0;
        int numberOfTurns = 0;

        System.out.println("***********************************");
        System.out.println("WELCOME TO ROCK PAPER SCISSORS GAME");
        System.out.println("***********************************");

        do{
            System.out.print("Enter your move: ");
            playerChoice = scanner.nextLine().toLowerCase();

            if(!playerChoice.equals("rock") &&
                    !playerChoice.equals("paper") &&
                    !playerChoice.equals("scissors")){

                System.out.println("Invalid Input!");
                continue;
            }

            computerChoice = choices[random.nextInt(3)];
            System.out.println("Computer choice: " + computerChoice);



            if(playerChoice.equals("rock") && computerChoice.equals("scissors") ||
                    playerChoice.equals("paper") && computerChoice.equals("rock") ||
                    playerChoice.equals("scissors") && computerChoice.equals("paper")){
                System.out.println("YOU WIN!");
                score++;
            }
            else if(playerChoice.equals(computerChoice)) {
                System.out.println("IT'S A TIE!");
            }
            else{
                System.out.println("YOU LOSE!");
            }

            numberOfTurns++;

            System.out.print("Do you wish to play again (yes/no): ");
            playAgain = scanner.nextLine().toLowerCase();
        }while(playAgain.equals("yes"));

        System.out.println("Thanks for playing!");
        System.out.println("You won " + score + " out of " + numberOfTurns + " games");



    }
}