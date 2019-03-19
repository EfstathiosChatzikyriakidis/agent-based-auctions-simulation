package auctions.Helpers;

import auctions.Agent.Exceptions.PlatformException;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashSet;
import jade.util.Logger;
import javafx.util.Pair;
import java.util.List;
import java.util.Set;
import java.io.File;

public class Catalogue {
    private final Logger logger = Logger.getMyLogger(getClass().getName());

    private final List<Pair<String, Integer>> items = new ArrayList<Pair<String, Integer>>();

    public Pair<String, Integer> getFirstItem() {
        return items.stream().findFirst().get();
    }

    public int getItemPriceByName(String itemName) {
        return items.stream().filter(o -> o.getKey().equals(itemName)).findFirst().get().getValue();
    }

    public boolean hasItems() {
        return items.size() > 0;
    }

    public boolean removeItem (Pair<String, Integer> item) {
        return items.remove(item);
    }
    
    public void placeItemAsLast (Pair<String, Integer> item) {
        items.remove(item);

        items.add(item);
    }

    public void initializeFromFile (String filePath) {
        items.clear();

        Set<String> uniqueItems = new HashSet<String> ();

        try {
            try (Scanner scanner = new Scanner(new File (filePath))) {
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");

                    if (parts.length != 2) {
                        logger.log(Level.SEVERE, "The file ''{0}'' contains invalid items.", filePath);

                        throw new PlatformException("The file '" + filePath + "' contains invalid items.");
                    }

                    String itemName = parts[0];
                    
                    int itemPrice = Integer.parseInt(parts[1]);

                    items.add(new Pair<String, Integer> (itemName, itemPrice));
                    
                    uniqueItems.add(itemName);
                }
            }
        }
        catch (FileNotFoundException exception) {
            logger.log(Level.SEVERE, "The file ''{0}'' does not exist.", filePath);

            throw new PlatformException("The file '" + filePath + "' does not exist.", exception);
        }
        catch (NumberFormatException exception) {
            logger.log(Level.SEVERE, "The file ''{0}'' contains invalid items.", filePath);

            throw new PlatformException("The file '" + filePath + "' contains invalid items.", exception);
        }

        if (items.isEmpty()) {
            logger.log(Level.SEVERE, "The file ''{0}'' has no items.", filePath);

            throw new PlatformException("The file '" + filePath + "' has no items.");
        }

        if (items.size() != uniqueItems.size()) {
            logger.log(Level.SEVERE, "The file ''{0}'' contains duplicate items.", filePath);

            throw new PlatformException("The file '" + filePath + "' contains duplicate items.");
        }
    }
}