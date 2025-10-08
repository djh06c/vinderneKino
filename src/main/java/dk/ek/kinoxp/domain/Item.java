package dk.ek.kinoxp.domain;

import dk.ek.kinoxp.domain.enums.ItemCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "items")
public class Item {
    @Id @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Positive
    private int price;

    private boolean active = true;

    // getters/setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public ItemCategory getCategory() { return category; }
    public int getPrice() { return price; }
    public boolean isActive() { return active; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(ItemCategory category) { this.category = category; }
    public void setPrice(int price) { this.price = price; }
    public void setActive(boolean active) { this.active = active; }
}
