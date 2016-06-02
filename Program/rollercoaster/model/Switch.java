package Rollercoaster.Model;

public class Switch {

    public boolean isUp;

    public Switch() {
        this.isUp = false;
    }

    // Actuators
    public void up() {
        this.isUp = true;
    }

    public void down() {
        this.isUp = false;
    }

    // Guards
    public boolean allowsForward(Track.Segment from, Track.Segment to) {
        if (this.isUp) {
            return from == Track.Segment.REPAIR && to == Track.Segment.STATION;
        } else {
            return (from == Track.Segment.BRAKING && to == Track.Segment.STATION) || 
                (from != Track.Segment.BRAKING && from != Track.Segment.REPAIR);
        }
    }

    public boolean allowsBackward(Track.Segment from, Track.Segment to) {
        if (this.isUp) {
            return from == Track.Segment.STATION && to == Track.Segment.REPAIR;
        } else {
            return from != Track.Segment.STATION;
        }
    }

    public boolean allowsGatesOpen() {
        return !this.isUp;
    }
}
