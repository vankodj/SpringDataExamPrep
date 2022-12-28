package com.example.gamestore.domain.dtos;

import com.example.gamestore.constants.Validations;
import com.example.gamestore.domain.entities.Game;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GameDTO {

    private String title;

    private String trailerId;

    private String imageUrl;

    private float size;

    private BigDecimal price;

    private String description;

    private LocalDate releaseDate;

    public GameDTO() {
    }

    public GameDTO(String title, String trailerId, String imageUrl, float size
            , BigDecimal price, String description, LocalDate releaseDate) {
        setTitle(title);
        setTrailerId(trailerId);
        setImageUrl(imageUrl);
        setSize(size);
        setPrice(price);
        setDescription(description);
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!(Character.isUpperCase(title.charAt(0)))
                || title.length() <3 || title.length() >100) {
            throw new IllegalArgumentException(Validations.NO_VALID_GAME_TITLE);
        }

        this.title = title;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        if(trailerId.length() != 11){
            throw new IllegalArgumentException(Validations.NO_VALID_TRAILER_ID);
        }

        this.trailerId = trailerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        if (!(imageUrl.startsWith("https://")) && !(imageUrl.startsWith("http://"))){
            throw new IllegalArgumentException(Validations.NO_VALID_THUMBNAIL_URL);
        }

        this.imageUrl = imageUrl;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        if (size <0){
            throw new IllegalArgumentException(Validations.NO_VALID_SIZE);
        }

        this.size = size;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price.longValue() < 0 ){
            throw new IllegalArgumentException(Validations.NO_VALID_PRICE);
        }

        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description.length() < 20){
            throw new IllegalArgumentException(Validations.NO_VALID_DESCRIPTION_SIZE);
        }

        this.description = description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Game toGame(){
       return new Game(title, trailerId, imageUrl, size, price, description, releaseDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Title %s",getTitle()))
                .append(System.lineSeparator())
                .append(String.format("Price %.2f",getPrice()))
                .append(System.lineSeparator())
                .append(String.format("Description %s",getDescription()))
                .append(System.lineSeparator()).
                append(String.format("Release date %s",getReleaseDate()));
        return sb.toString();
    }
}
