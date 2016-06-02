package Rollercoaster.Model;

public class Track {

    public enum Segment {
        STATION, BRAKING, REPAIR, LIFT, MAIN
    }

    Segment location1;
    Segment location2;

    public Track(Segment location1, Segment location2) {
        this.location1 = location1;
        this.location2 = location2;
    }

    // Guards
    public boolean allowsForward(Segment from, Segment to) {
        // Sanity check
        assert from == location1 || from == location2;

        // Other cart position
        Segment c2;
        if (from == location1) c2 = location2;
        else c2 = location1;

        return (!isBlocked(from, c2) && isReachable(from, to));
    }

    public void move(Segment from, Segment to) {
        if (from == location1) location1 = to;
        else if (from == location2) location2 = to;
    }

    public boolean allowsBackward(Segment from, Segment to) {
        // Sanity check
        assert(from == location1 || from == location2);

        // Other cart position
        Segment c2;
        if (from == location1) c2 = location2;
        else c2 = location1;

        // System.out.printf("Track Checking: %s vs %s: %b, %s->%s: %b\r\n", from, c2, !isBlocked(from, c2), from, to, isReachable(from, to));
        return (!isBlocked(from, c2) && isReverseReachable(from, to));
    }

    public boolean allowsSwitchUp() {
        return (location1 == Segment.BRAKING && (location2 == Segment.REPAIR || location2 == Segment.STATION)) 
            || (location2 == Segment.BRAKING && (location1 == Segment.REPAIR || location1 == Segment.STATION));
    }

    public boolean allowsSwitchDown() {
        return this.allowsSwitchUp(); // Are equivalent
    }

    public boolean allowsOpenBraces(Cart cart) {
        return cart.getLocation() == Segment.STATION;
    }

    public boolean allowsGatesOpen() {
        return location1 == Segment.STATION || location2 == Segment.STATION;
    }

    public boolean allowsGatesClose() {
        return location1 == Segment.STATION || location2 == Segment.STATION;
    }

    // Helper functions
    public static Segment nextSegment(Segment from) {
        if (from == Segment.STATION) return Segment.LIFT;
        if (from == Segment.LIFT) return Segment.MAIN;
        if (from == Segment.MAIN) return Segment.REPAIR;
        if (from == Segment.BRAKING) return Segment.STATION;
        if (from == Segment.REPAIR) return Segment.STATION;
        return null;
    }

    public static Segment previousSegment(Segment from) {
        if (from == Segment.STATION) return Segment.REPAIR;
        return null;
    }

    public boolean isBlocked(Segment s1, Segment s2) {
        if (s1 == Segment.STATION && (s2 != Segment.BRAKING && s2 != Segment.REPAIR)) return true;
        if (s1 == Segment.LIFT && (s2 != Segment.STATION && s2 != Segment.REPAIR)) return true;
        if (s1 == Segment.REPAIR && s2 != Segment.BRAKING) return true;
        if (s1 == Segment.BRAKING && s2 == Segment.STATION) return true;

        return false;
    }

    private boolean isReverseReachable(Segment from, Segment to) {
        if (from == Segment.STATION && to == Segment.REPAIR) return true;
        return false;
    }

    private boolean isReachable(Segment from, Segment to) {
        if (from == Segment.STATION && to == Segment.LIFT) return true;
        if (from == Segment.LIFT && to == Segment.MAIN) return true;
        if (from == Segment.MAIN && to == Segment.BRAKING) return true;
        if (from == Segment.REPAIR && to == Segment.STATION) return true;
        if (from == Segment.BRAKING && to == Segment.STATION) return true;

        return false;
    }

}
