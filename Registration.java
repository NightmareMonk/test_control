import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Registration implements AutoCloseable {

  private List<Animals> animals = new ArrayList<>();

  private static Counter counter = new Counter();

  public void addNewAnimal(Animals animal) {
    animals.add(animal);
    counter.add();
  }

  public void teachCommand(Animals animal, String command) {
    animal.setCommand(command);

    // Запись данных в базу данных
    try (FileWriter writer = new FileWriter("DataBase.csv", true)) {
      String animalType = getAnimalType(animal);
      String animalName = animal.getName();
      String line = animalType + "," + animalName + "," + command + "\n";
      writer.write(line);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String getAnimalType(Animals animal) {
    if (animal instanceof Dogs) {
      return "Dog";
    } else if (animal instanceof Cats) {
      return "Cat";
    } else if (animal instanceof Hamsters) {
      return "Hamster";
    } else if (animal instanceof Horses) {
      return "Horse";
    } else if (animal instanceof Camels) {
      return "Camel";
    } else if (animal instanceof Donkeys) {
      return "Donkey";
    }
    return "";
  }

  public List<String> getCommands(Animals animal) {
    List<String> commands = new ArrayList<>();
    commands.add(animal.getCommand());
    return commands;
  }

  public void readDatabase() {
    // Создание файла базы данных, если он не существует
    File databaseFile = new File("DataBase.csv");
    if (!databaseFile.exists()) {
      try {
        databaseFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Чтение данных из базы данных
    try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length >= 2) {
          String animalName = data[0];
          String command = data[1];
          Animals animal = animals.stream().filter(a -> a.getName().equals(animalName)).findFirst().orElse(null);
          if (animal != null) {
            animal.setCommand(command);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    try (Registration Registration = new Registration()) {
      Scanner scanner = new Scanner(System.in);
      while (true) {
        System.out.println("1. Add new animal");
        System.out.println("2. Teach command");
        System.out.println("3. Get commands");
        System.out.println("4. Exit");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
          case 1:
            System.out.println("Enter animal type: ");
            String type = scanner.nextLine();
            System.out.println("Enter animal name: ");
            String name = scanner.nextLine();
            Animals animal;
            switch (type) {
              case "Dog":
                animal = new Dogs(name);
                break;
              case "Cat":
                animal = new Cats(name);
                break;
              case "Hamster":
                animal = new Hamsters(name);
                break;
              case "Horse":
                animal = new Horses(name);
                break;
              case "Camel":
                animal = new Camels(name);
                break;
              case "Donkey":
                animal = new Donkeys(name);
                break;
              default:
                throw new IllegalStateException("Unexpected value: " + type);
            }
            Registration.addNewAnimal(animal);
            break;
          case 2:
            System.out.println("Enter animal name: ");
            String animalName = scanner.nextLine();
            Animals foundAnimal = Registration.animals.stream()
                .filter(a -> a.getName().equals(animalName))
                .findFirst()
                .orElse(null);
            if (foundAnimal == null) {
              System.out.println("No such animal");
              break;
            }
            System.out.println("Enter command: ");
            String command = scanner.nextLine();
            Registration.teachCommand(foundAnimal, command);
            break;
          case 3:
            System.out.println("Enter animal name: ");
            String aName = scanner.nextLine();
            Animals fAnimal = Registration.animals.stream()
                .filter(a -> a.getName().equals(aName))
                .findFirst()
                .orElse(null);
            if (fAnimal == null) {
              System.out.println("No such animal");
              break;
            }
            List<String> commands = Registration.getCommands(fAnimal);
            for (String cmd : commands) {
              System.out.println(cmd);
            }
            break;
          case 4:
            return;
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }

  @Override
  public void close() throws Exception {
    if (counter.getCount() == 0) {
      throw new Exception("Counter was not used in try-with-resources block");
    } else {
      counter.resetCount();
    }
  }

}

class Counter {

  private int count;

  public void add() {
    count++;
  }

  public int getCount() {
    return count;
  }

  public void resetCount() {
    count = 0;
  }

}