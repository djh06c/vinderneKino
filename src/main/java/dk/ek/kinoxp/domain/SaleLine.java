package dk.ek.kinoxp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name="sale_lines")
public class SaleLine {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(optional=false)
    private Sale sale;

    @ManyToOne(optional=false)
    private Item item;

    @Positive
    private int quantity;

    private int lineTotal;


    public Long getId() {return id;}
    public Sale getSale() {return sale;}
    public Item getItem() {return item;}
    public int getQuantity() {return quantity;}
    public int getLineTotal() {return lineTotal;}

    public void setSale(Sale sale) {this.sale = sale;}
    public void setItem(Item item) {this.item = item;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
    public void setLineTotal(int lineTotal) {this.lineTotal = lineTotal;}
}
