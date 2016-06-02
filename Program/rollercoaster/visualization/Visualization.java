package Rollercoaster.Visualization;

import Rollercoaster.Listener;
import Rollercoaster.Model.Track;
import Rollercoaster.Model.Track.Segment;

public class Visualization extends Listener {

    private Frame frame;
    private Segment cart1pos;
    private Segment cart2pos;
    private boolean cart1open;
    private boolean cart2open;
    private boolean switchup;
    private boolean gatesopen;

    public Visualization(Segment pos1, Segment pos2, Rollercoaster.Controller controller) {
        cart1pos = pos1;
        cart2pos = pos2;

        this.frame = new Frame("Rollercoaster", controller);
        this.frame.setVisible(true);
    }

    public Visualization(Segment pos1, Segment pos2) {
        this(pos1, pos2, null);
    }

    @Override
    public void forward(String cartId, Segment from, Segment to) {
        if (from == cart1pos) cart1pos = to;
        else if (from == cart2pos) cart2pos = to;
        this.draw();
    }

    @Override
    public void backward(String cartId, Segment from, Segment to) {
        if (from == cart1pos) cart1pos = to;
        else if (from == cart2pos) cart2pos = to;
        this.draw();
    }
    
    @Override
    public void openBraces(String cartId) {
        if (cart1pos == Segment.STATION) this.cart1open = true;
        else if (cart2pos == Segment.STATION) this.cart2open = true;
        this.draw();
    }

    @Override
    public void closeBraces(String cartId) {
        this.cart1open = false;
        this.cart2open = false;
        this.draw();
    }

    @Override
    public void switchUp() {
        this.switchup = true;
        this.draw();
    }

    @Override
    public void switchDown() {
        this.switchup = false;
        this.draw();
    }

    @Override
    public void gatesOpen() {
        this.gatesopen = true;
        this.draw();
    }

    @Override
    public void gatesClose() {
        this.gatesopen = false;
        this.draw();
    }

    @Override
    public void cartMoving() {
        this.draw();
    }

    public void draw() {
        this.frame.redraw(cart1pos, cart2pos, cart1open, cart2open, switchup, gatesopen);
    }
}
