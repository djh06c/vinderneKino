package dk.ek.kinoxp.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="sales")
public class Sale {
    @Id @GeneratedValue
    private Long id;
    private LocalDateTime soldAt = LocalDateTime.now();
    private int total;

    @OneToMany(mappedBy="sale", cascade=CascadeType.ALL)
    private List<SaleLine> lines = new ArrayList<>();


    public Long getId() {return id;}
    public LocalDateTime getSoldAt() {return soldAt;}
    public int getTotal() {return total;}
    public List<SaleLine> getLines() {return lines;}
    public void setTotal(int total) {this.total = total;}
}
