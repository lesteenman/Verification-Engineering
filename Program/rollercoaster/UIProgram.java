package Rollercoaster;

import Rollercoaster.Visualization.Visualization;
import Rollercoaster.Model.Track.Segment;
import java.util.ArrayList;
import java.io.*;

public class UIProgram extends Listener {

    private static Segment cart1Start = Segment.BRAKING;
    private static Segment cart2Start = Segment.REPAIR;

    private Controller controller;
    private Visualization visualization;

    public static void main(String[] args) {
        new UIProgram();
    }

    public UIProgram() {
        this.controller = new Controller(cart1Start, cart2Start, 7500);

        controller.addListener(this);

        this.visualization = new Visualization(cart1Start, cart2Start, this.controller);
        controller.addListener(this.visualization);
    }
}
