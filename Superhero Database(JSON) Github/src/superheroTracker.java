import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;

/**
 * A class that creates a superhero object and stores it's
 * characteristics (name, superpower, height, civilians saved)
 *
 * @author Mike Kreutz
 */
class Superhero {
    private String name;
    private String superPower;
    private double heightInCm;
    private int civiliansSaved;


    //constructor
    public Superhero(String newName, String newSuperPower, double newHeight, int newCiviliansSaved) {
        name = newName;
        superPower = newSuperPower;
        heightInCm = newHeight;
        civiliansSaved = newCiviliansSaved;
    }

    public String getName() {
        return name;
    }

    public String getSuperPower() {
        return superPower;
    }

    public double getHeightInCm() {
        return heightInCm;
    }

    public int getCiviliansSaved() {
        return civiliansSaved;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setSuperPower(String newPower) {
        superPower = newPower;
    }

    public void setHeightInCm(double newHeight) {
        heightInCm = newHeight;
    }

    public void setCiviliansSaved(int newCiviliansSaved) {
        civiliansSaved = newCiviliansSaved;
    }


    //source: Dr. Brian Fraser's Java Tutorials https://www.youtube.com/watch?v=PIkd-ZTz6bU
    @Override
    public String toString() {
        return "Superhero{" +
                "name='" + name + '\'' +
                ", superPower='" + superPower + '\'' +
                ", heightInCm=" + heightInCm +
                ", civiliansSaved=" + civiliansSaved +
                '}';
    }
}


/**
 * A class that displays a menu (title & options)
 *
 * @author Mike Kreutz
 */
class Menu {
    final static private String title = "SuperHero Tracker";
    public static String[] menuOptions = {"List All Superheros", "Add A New Superhero", "Remove A Superhero",
            "Update Number Of Civilians Saved By A Superhero", "List Top 3 Superheros",
            "Debug Dump (toString)", "Exit"};


    Menu(){}        //default constructor


    private void printTitle() {
        final int ROW = title.length() + 5;
        String headerFooter = "";

        //automatically place rectangle of '*' around title
        for (int i = 0; i < ROW; i++) {
            headerFooter += '*';
        }
        System.out.println();
        System.out.println(headerFooter);
        System.out.println(" " + title);
        System.out.println(headerFooter);
        System.out.println();
    }//printTitle method


    private void printMenuOptions() {
        for (int i = 0; i < menuOptions.length; i++) {
            System.out.print(i + 1);                    //automatically order options starting at '1'
            System.out.println(". " + menuOptions[i]);
        }
    }//printMenuOptions method


    public void displayMenu() {
        printTitle();
        printMenuOptions();
        System.out.println();
    }//displayMenu method
}//menu class

/**
 * A class that uses the Menu & Superhero classes to implement the application.
 * If a Json file exists, reads in the contents and stores in an ArrayList.
 * Reads the users input and call methods dependant on user choice. Saves results
 * stored in the ArrayList to a Json file for easy access in the future.
 *
 * @author Mike Kreutz
 */
class SuperheroTracker {
    //constants
    final private static int DISPLAY_HEROES = 1;
    final private static int ADD_HERO = 2;
    final private static int REMOVE_HER0 = 3;
    final private static int UPDATE_CIVILIANS_SAVED = 4;
    final private static int TOP_HEROES = 5;
    final private static int DEBUG_DUMP = 6;
    final private static int EXIT_PROGRAM = 7;
    final private static int MINIMUM_HEROES_REQUIRED = 3;
    final private static int TOP_HEROES_COUNT = 3;

    private static List<Superhero> superheroes;
    private static Scanner console;

    //default constructor (creates a new ArrayList and a new Scanner)
    SuperheroTracker(){
        //ArrayList: Dr.Brian Fraser's Java tutorial (source: https://www.youtube.com/watch?v=JtUWp9JzxJA)
        superheroes = new ArrayList<>();    //creates new array-list and assigns it to the static variable
        console = new Scanner(System.in);   //creates a new scanner and assigns it to the static variable
    }

    public static void main(String[] args) {
        SuperheroTracker tracker = new SuperheroTracker();
        tracker.readInFile();           //reads in a Json file (if it exists)
        tracker.operation();            //performs program functionality
    }//main method


    private void operation() {
        while(true) {
            Menu newMenu = new Menu();
            newMenu.displayMenu();
            int userChoice = getUserChoice();

            if (userChoice == DISPLAY_HEROES) {
                System.out.println("You have selected \'" + Menu.menuOptions[0] + "\'");
                displayAllHeroes();
            } else if (userChoice == ADD_HERO) {
                System.out.println("You have selected \'" + Menu.menuOptions[1] + "\'");
                addANewHero();
            } else if (userChoice == REMOVE_HER0) {
                System.out.println("You have selected \'" + Menu.menuOptions[2] + "\'");
                removeHero();
            } else if (userChoice == UPDATE_CIVILIANS_SAVED) {
                System.out.println("You have selected \'" + Menu.menuOptions[3] + "\'");
                changeCivilianCount();
            } else if (userChoice == TOP_HEROES) {
                System.out.println("You have selected \'" + Menu.menuOptions[4] + "\'");
                displayTopHeroes();
            } else if (userChoice == DEBUG_DUMP) {
                System.out.println("You have selected \'" + Menu.menuOptions[5] + "\'");
                debugDump();
            } else if (userChoice == EXIT_PROGRAM) {
                System.out.println("You have selected \'" + Menu.menuOptions[6] + "\'");
                writeToFile();
                console.close();
                System.out.println("Thank you for using the system.");
                break;          //breaks out of system while loop
            } else {
                System.out.println("Entry \'" + userChoice + "\' is Invalid, please try again.. ");
            }
        }//while loop
    }//operation() method


    private int getUserChoice() {
        System.out.print("Please make a selection:");
        System.out.println();
        int userChoice = console.nextInt();
        console.nextLine();     //resets the console input
        return userChoice;
    }


    private void convertFromJsonArray(JsonArray arrayToExtract) {
        //Json Objects: Dr.Brian Fraser's online Tutorial (source: https://www.youtube.com/watch?v=HSuVtkdej8Q)
        for (JsonElement superheroElement : arrayToExtract) {
            JsonObject superheroJsonObject = superheroElement.getAsJsonObject();        //Get the JsonObject

            //initialized values of objects to null
            String name = null;
            String superpower = null;
            Double heightInCm = null;
            Integer civiliansSaved = null;

            // Extract data:
            if (superheroJsonObject.has("name")) {
                name = superheroJsonObject.get("name").getAsString();
            }
            if (superheroJsonObject.has("superpower")) {
                superpower = superheroJsonObject.get("superpower").getAsString();
            }
            if (superheroJsonObject.has("heightInCm")) {
                heightInCm = superheroJsonObject.get("heightInCm").getAsDouble();
            }
            if (superheroJsonObject.has("civiliansSaved")) {
                civiliansSaved = superheroJsonObject.get("civiliansSaved").getAsInt();
            }
            Superhero superhero = new Superhero(name, superpower, heightInCm, civiliansSaved);
            superheroes.add(superhero);     //adds to arrayList
        }
    }//convertFromJsonArray() method


    private void convertToJsonArray(JsonArray arrayToWrite) {
        for (Superhero superhero : superheroes) {
            JsonObject currentHero = new JsonObject();

            String name = superhero.getName();
            String superpower = superhero.getSuperPower();
            Double heightInCm = superhero.getHeightInCm();
            Integer civiliansSaved = superhero.getCiviliansSaved();

            currentHero.addProperty("name", name);
            currentHero.addProperty("superpower", superpower);
            currentHero.addProperty("heightInCm", heightInCm);
            currentHero.addProperty("civiliansSaved", civiliansSaved);

            arrayToWrite.add(currentHero);
        }
    }//convertToJsonArray() method


    private void readInFile() {
        //Read in Json: Brian Fraser's online Tutorial (source: https://www.youtube.com/watch?v=HSuVtkdej8Q)
        File input = new File("./superheroes.json");
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));    //creates a Json reader
            JsonArray jsonArrayOfSuperheroes = fileElement.getAsJsonArray();            //creates a JsonArray
            convertFromJsonArray(jsonArrayOfSuperheroes);

        } catch (FileNotFoundException e) {                        //if no current file exists in directory
            System.err.println("No previous JSON file found, A New JSON " +
                    "file will be created upon program exit!");
        } catch (Exception e) {
            System.err.println("Error processing input file!");
            e.printStackTrace();
        }
    }//readInFile() method


    private void writeToFile() {
        //Write to Json: (source: https://howtodoinjava.com/library/json-simple-read-write-json-examples/)
        //Write to Json: (source: https://stackoverflow.com/questions/51801905/how-to-programmatically-write-data-to-a-json-file-and-then-save-it)
        JsonArray jsonSuperHeroList = new JsonArray();
        this.convertToJsonArray(jsonSuperHeroList);

        try (FileWriter file = new FileWriter("superheroes.json")) {
            file.write(jsonSuperHeroList.toString());
        } catch (IOException e) {
            System.err.println("Error unable to write to output file!");
            e.printStackTrace();
        }
    }//writeToFile() method


    private int selectHero(String application) {
        displayAllHeroes();
        System.out.println();
        System.out.println("Enter Hero number to " + application + " or Enter \'0\' to cancel");
        int userChoice = console.nextInt();
        console.nextLine(); //resets the console input
        return userChoice;
    }//selectHero() method


    private void displayAllHeroes() {
        System.out.println();
        if (superheroes.size() == 0) {
            System.out.println("There are currently no heroes..");
        } else {
            for (int i = 0; i < superheroes.size(); i++) {
                System.out.println(
                        (i + 1) + ". " +
                        "Hero name: " + superheroes.get(i).getName() + ", " +
                        "Superpower: " + superheroes.get(i).getSuperPower() + ", " +
                        "Height: " + superheroes.get(i).getHeightInCm() + "cm, " +
                        "saved " + superheroes.get(i).getCiviliansSaved() + " civilians.");
            }//loops through superheroes ArrayList
        }//if-else statement
    }//displayAllHeroes method


    private void addANewHero() {
        System.out.println();
        System.out.println("Enter the Hero's \'Name\': ");
        String newName = console.nextLine();

        System.out.println("Enter the Hero's \'Superpower\': ");
        String newPower = console.nextLine();

        double newHeight;
        while (true) {
            System.out.println("Enter the Hero's \'Height in Cm\': ");
            newHeight = console.nextDouble();
            if (newHeight < 0) {
                System.out.println("A Hero cannot have a negative height, please try again..");
            } else {
                break;
            }
        }//while-loop
        Superhero heroToAdd = new Superhero(newName, newPower, newHeight, 0);
        superheroes.add(heroToAdd);
        System.out.println("\'" + newName + "\' has been successfully added to the list of superheroes..");
    }//addANewHero() method


    private void removeHero() {
        while(true) {
            int userChoice = selectHero("be removed");

            if (userChoice > 0 && userChoice <= superheroes.size()) {
                String name = superheroes.get(userChoice - 1).getName();
                superheroes.remove(userChoice - 1);
                System.out.println("\'" + name + "\' has been removed from the list of superheroes");
                break;
            } else if (userChoice == 0) {
                break;
            } else {
                System.out.println("\'" + userChoice + "\' is Invalid, please try again..");
            }
        }//while-loop
    }//removeHero method


    private void updateCivilianVar(int heroNumber, int newCount) {
        int oldCount = superheroes.get(heroNumber - 1).getCiviliansSaved();
        superheroes.get(heroNumber - 1).setCiviliansSaved(newCount);

        System.out.println("Number of civilians saved by " +
                superheroes.get(heroNumber - 1).getName() +
                " has been updated from " + oldCount + " to " + newCount);
    }//updateCivilianVar method (used inside changeCivilianCount method)


    private void changeCivilianCount() {
        boolean isUpdated = false;
        while (!isUpdated) {
            int userChoice = this.selectHero("update civilian count");
            if (userChoice > 0 && userChoice <= superheroes.size()) {
                while (true) {
                    System.out.println("Enter new civilian save count: ");
                    int newCount = console.nextInt();
                    console.nextLine();         //resets input
                    if (newCount >= 0) {
                        updateCivilianVar(userChoice, newCount);
                        isUpdated = true;  //a value that will prevent next iteration of outer-loop
                        break;      //breaks out of inner loop
                    } else {
                        System.out.println("\'" + newCount + "\' is not valid, please try again..");
                    }
                }//inner-while-loop
            } else if (userChoice == 0) {
                break;
            } else {
                System.out.println("\'" + userChoice + " is not valid, please try again..");
            }
        }//outer-while-loop
    }//changeCivilianCount method


    private boolean enoughHeroes(List<Superhero> sortedList){
        for(int i = 0; i < TOP_HEROES_COUNT; i++){
            if(sortedList.get(i).getCiviliansSaved() == 0){
                return false;
            }
        }
        return true;
    }

    private void displayTopHeroes() {
        List<Superhero> sortedList = makeDeepCopyAndSort();  // sorts in ascending order
        Collections.reverse(sortedList);       // reverses order so highest civilians count at first index
        System.out.println();
        if (sortedList.size() < MINIMUM_HEROES_REQUIRED) {
            System.out.println("There is not enough superheroes in the list.");
        } else {
            boolean enoughHeroes = enoughHeroes(sortedList);
            if(!enoughHeroes) {
                System.out.println("The superheroes have not saved enough civilians");
            } else {
                int count = 1;
                for (Superhero hero : sortedList) {
                    System.out.println(count + ". " +
                            hero.getName() + " has saved " +
                            hero.getCiviliansSaved() + " civilians"
                    );
                    if (count == TOP_HEROES_COUNT) {
                        break;
                    }
                    count++;
                }//for-each loop
            }//nested-else
        }//else
    }//displayTop3Heroes() method


    private void debugDump() {
        System.out.println();
        int count = 1;
        for (Superhero hero : superheroes) {
            System.out.println(count + ". " + hero.toString());
            count++;
        }
    }//debugDump method


    private List<Superhero> makeDeepCopyAndSort() {
        List<Superhero> tempCopy = new ArrayList<>();
        for (Superhero hero : superheroes) {           //makes deepCopy of list parameter
            tempCopy.add(hero);
        }

        Collections.sort(tempCopy, new Comparator<Superhero>() {        //sorts the list copy
            @Override
            public int compare(Superhero o1, Superhero o2) {
                return o1.getCiviliansSaved() - o2.getCiviliansSaved();
            }
        });

        return tempCopy;
    }//makeDeepCopyAndSort() method
}//SuperheroTracker Class
