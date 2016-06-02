package Rollercoaster.Model;

import Rollercoaster.Model.Track.Segment;

public class Cart {

    // private String cartIdentifier; // Implicit
    private Segment location;

    public boolean isOpen;
    public boolean moving;
    public boolean locked; // Lock after closing the braces, until it moves again
    public String id;

    public Cart(String id, Track.Segment location) {
        this.id = id;
        this.location = location;
        this.moving = false;
    }

    public void forward(Track.Segment to) {
        this.location = to;
        if (to == Segment.STATION) this.locked = false;
    }

    public void backward(Track.Segment to) {
        this.location = to;
    }

    public void openBraces() {
        this.isOpen = true;
    }

    public void closeBraces() {
        this.isOpen = false;
        this.locked = true;
    }

    // Getters
    public Track.Segment getLocation() {
        return this.location;
    }

}
