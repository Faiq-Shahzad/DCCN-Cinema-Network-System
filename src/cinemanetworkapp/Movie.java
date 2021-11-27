/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cinemanetworkapp;

import java.io.Serializable;

/**
 *
 * @author Ahmed
 */
public class Movie implements Serializable {
    int id;
    String name;
    int rating;
    int year;

    public Movie(int id, String name, int rating, int year) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.year = year;
    }

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", name=" + name + ", rating=" + rating + ", year=" + year + '}';
    }





}

