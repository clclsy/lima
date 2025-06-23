package nutrisci.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Meal implements Serializable {
    private LocalDate date;
    private MealType type;
    private List<MealEntry> entries = new ArrayList<>();

    public Meal(LocalDate date, MealType type) {
        this.date = date;
        this.type = type;
    }

    public void addMealEntry(MealEntry entry) {
        entries.add(entry);
    }

    public List<MealEntry> getEntries() { return entries; }
    public LocalDate getDate() { return date; }
    public MealType getType() { return type; }
}
