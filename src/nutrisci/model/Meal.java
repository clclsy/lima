package nutrisci.model;

import java.time.LocalDate;
import java.util.List;

public class Meal {
    private int userId;
    private LocalDate date;
    private MealType type;
    private List<MealItem> items;

    public Meal(int userId, LocalDate date, MealType type, List<MealItem> items) {
        this.userId = userId;
        this.date = date;
        this.type = type;
        this.items = items;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public MealType getType() {
        return type;
    }

    public List<MealItem> getItems() {
        return items;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public void setItems(List<MealItem> items) {
        this.items = items;
    }
}
