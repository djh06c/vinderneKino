package dk.ek.kinoxp.domain;

import dk.ek.kinoxp.domain.enums.Genre;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "movies")
public class Movie {
    @Id @GeneratedValue
    private Long id;

    @NotBlank
    private String movieTitle;

    @NotBlank
    private String auditorium;

    @NotNull
    private LocalDate premiereDate;

    @NotNull
    private LocalDate endDate;

    @Positive
    private int price;

    @Enumerated(EnumType.STRING)
    private Genre genre;


    //GETTERS

    public Long getId() {return id;}
    public String getMovieTitle() {return movieTitle;}
    public String getAuditorium() {return auditorium;}
    public LocalDate getPremiereDate() {return premiereDate;}
    public LocalDate getEndDate() {return endDate;}
    public int getPrice() {return price;}
    public Genre getGenre() {return genre;}

    //SETTERS

    public void setId(Long id) {this.id = id;}
    public void setMovieTitle(String movieTitle) {this.movieTitle = movieTitle;}
    public void setAuditorium(String auditorium) {this.auditorium = auditorium;}
    public void setPremiereDate(LocalDate releaseDate) {this.premiereDate = releaseDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}
    public void setPrice(int price) {this.price = price;}
    public void setGenre(Genre genre) {this.genre = genre;}

}
