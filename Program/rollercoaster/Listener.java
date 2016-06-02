package Rollercoaster;

import Rollercoaster.Model.Track.Segment;

public abstract class Listener {

    public void forward(String cartId, Segment from, Segment to) {}
    public void backward(String cartId, Segment from, Segment to) {}
    public void openBraces(String cartId) {}
    public void closeBraces(String cartId) {}
    public void switchUp() {}
    public void switchDown() {}
    public void gatesOpen() {}
    public void gatesClose() {}
    public void cartMoving() {}

}
