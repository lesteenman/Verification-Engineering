package Rollercoaster.Model;

public class Gate {

    public boolean isOpen;

    public Gate() {
        this.isOpen = false;
    }

    //Actuators
    public void open() {
        this.isOpen = true;
    }

    public void close() {
        this.isOpen = false;
    }

    // Guards
    public boolean allowsForward(Track.Segment from) {
        return !isOpen || from != Track.Segment.STATION;
    }

    public boolean allowsBackward(Track.Segment from) {
        return !isOpen || from != Track.Segment.STATION;
    }

}
