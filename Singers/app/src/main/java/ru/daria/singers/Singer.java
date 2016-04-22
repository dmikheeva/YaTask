package ru.daria.singers;

import java.io.Serializable;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

/**
 * Исполнитель
 */
public class Singer implements Serializable {
    private Long id;
    private String name;
    private List<String> genres;
    private int tracks = 0;
    private int albums = 0;
    private String link;
    private String description;
    private Map<coverTypes, String> covers;

    public enum coverTypes {small, big}

    public Singer(Long id, String name, List<String> genres, int tracks, int albums, String link, String description, Map<coverTypes, String> covers) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.covers = covers;
    }

    public Singer(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCovers(Map<coverTypes, String> covers) {
        this.covers = covers;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Map<coverTypes, String> getCovers() {
        return covers;
    }

    /**
     * Функция возвращает сконкатенированный через запятую список жанров
     * @return String
     */
    public String genresToString() {
        if (genres == null || genres.size() == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(genres.get(0));
        for (int i = 1; i < genres.size(); i++) {
            sb.append(", ").append(genres.get((i)));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return name.toString();
    }

    /**
     * Функция возвращает окончание для множественного числа слова на основании числа и массива окончаний
     *
     * @param number      int Число на основе которого нужно сформировать окончание
     * @param endingArray Array Массив слов или окончаний для чисел (1, 4, 5),
     *                    например array('песня', 'песни', 'песен')
     * @return String
     */
    public String getEnding(int number, String[] endingArray) {
        if (endingArray.length != 3)
            throw new ArrayIndexOutOfBoundsException("Out of bounds exception. Array must contains 3 elements!");
        number = number % 100;
        String ending = "";
        if (number >= 11 && number <= 19) {
            ending = endingArray[2];
        } else {
            int i = number % 10;
            switch (i) {
                case (1):
                    ending = endingArray[0];
                    break;
                case (2):
                case (3):
                case (4):
                    ending = endingArray[1];
                    break;
                default:
                    ending = endingArray[2];
            }
        }
        return ending;
    }

}
