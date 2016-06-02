package Rollercoaster.Model;

public class Button {

    // Whether or not the braces are reported as open
    public boolean isOpen;

    public Button() {
        this.isOpen = false;
    }

    // Actuators
    public void detectOpenBraces() {
        assert(!this.isOpen);
        this.isOpen = true;
    }

    public void push() {
        this.isOpen = false;
    }

    // Guards

    public boolean confirmBracesClosed() {
        return !this.isOpen;
    }

    public boolean allowsForward(Track.Segment from) {
        return !this.isOpen || from != Track.Segment.STATION;
    }

    public boolean allowsGatesOpen() {
        return this.isOpen;
    }

}
