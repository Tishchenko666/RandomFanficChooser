public class Fanfic {

    private String name;
    private String genre;
    private String originalHistory;

    public Fanfic() {
    }

    public Fanfic(String name, String genre, String originalHistory) {
        this.name = name;
        this.genre = genre;
        this.originalHistory = originalHistory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getOriginalHistory() {
        return originalHistory;
    }

    public void setOriginalHistory(String originalHistory) {
        this.originalHistory = originalHistory;
    }
}
