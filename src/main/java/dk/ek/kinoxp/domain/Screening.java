package dk.ek.kinoxp.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name= "screenings")
public class Screening {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(optional = false)
    private Movie movie;

    @Column(nullable = false)
    private Integer auditorium;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private int availableSeats;

    //Getters
    public long getId() {return id;}
    public Movie getMovie() {return movie;}
    public Integer getAuditorium() {return auditorium;}
    public LocalDate getDate() {return date;}
    public LocalTime getTime() {return time;}
    public int getAvailableSeats() {return availableSeats;}

    //Setters
    public void setId(long id) {this.id = id;}
    public void setMovie(Movie movie) {this.movie = movie;}
    public void setAuditorium(Integer auditorium) {this.auditorium = auditorium;}
    public void setDate(LocalDate date) {this.date = date;}
    public void setTime(LocalTime time) {this.time = time;}
    public void setAvailableSeats(int availableSeats) {this.availableSeats = availableSeats;}

    @jakarta.persistence.Transient
    public Integer getCapacity() {
        var spec = Theater.of(auditorium);
        return (spec == null) ? null : spec.getCapacity();
    }

}
