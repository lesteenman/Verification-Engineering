package Rollercoaster;

import Rollercoaster.Model.*;
import Rollercoaster.Model.Track.Segment;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    private ArrayList<Listener> listeners;
    private ArrayList<RollercoasterAction> scheduledActions;
    private Timer ticker;

    private static int cartMoveDelay = 1000;
    private static int tickRate = 250;

    private Cart cart1, cart2;
    private Track track;
    private Gate gates;
    private Switch switches; // switch is a keyword..
    private Button button;

    public Controller(Segment cart1Start, Segment cart2Start, int brakingTimeout) {
        this.listeners = new ArrayList<Listener>();

        this.track = new Track(cart1Start, cart2Start);
        this.cart1 = new Cart("cart1_id", cart1Start);
        this.cart2 = new Cart("cart2_id", cart2Start);
        this.gates = new Gate();
        this.switches = new Switch();
        this.button = new Button();

        this.brakingTimeout = brakingTimeout;
        this.brakingWaitingTime = brakingTimeout;

        this.start();
    }

    /**
     * Subscribe a listener for external events
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void start() {
        this.scheduledActions = new ArrayList<RollercoasterAction>();
        this.ticker = new Timer();

        this.ticker.schedule(new TickerTask(this), 0, tickRate); // 4 times a second
    }

    class TickerTask extends TimerTask {
        public Controller target;

        public TickerTask(Controller target) {
            this.target = target;
        }

        public void run() {
            this.target.tick();
        }
    }

    public void tick() {
        // First attempt automatic movements. These are station -> lift -> main -> braking.

        ArrayList<Cart> carts = new ArrayList<Cart>();
        carts.add(this.cart1);
        carts.add(this.cart2);

        for (Cart cart:carts) {
            this.tryOpenBraces(cart);
            this.tryForward(cart);
            // this.tryBackward(cart);
        }

        // Next, move on all scheduled actions
        for (int i = 0; i < this.scheduledActions.size(); i++) {
            RollercoasterAction action = this.scheduledActions.get(i);
            action.time -= this.tickRate;
            if (action.time <= 0) {
                action.execute();
                this.scheduledActions.remove(action);
                i--;
            }
        }
    }

    private void tryOpenBraces(Cart cart) {
        // track_allow_open_braces | cart_open_braces | button_detect_open_braces -> open_braces,
        if (!cart.isOpen && !cart.locked && this.track.allowsOpenBraces(cart)) {
            // No need to schedule, opening can happen immediately.
            this.openBraces(cart);
        }
    }

    private int brakingTimeout = 7500;
    private int brakingWaitingTime = 7500;
    private void tryForward(Cart cart) {
        Segment from = cart.getLocation();

        if (cart.moving) return;

        // Don't automatically move from the repair area
        if (from == Segment.REPAIR) return;

        // Pause /brakingTimeout/ milliseconds in braking to allow switching tracks.
        if (from == Segment.BRAKING) {
            brakingWaitingTime += tickRate;
            if (brakingWaitingTime < brakingTimeout) return;
        }

        for (Segment to:Segment.values()) {
            if (this.allowsForward(from, to)) {
                if (from == Segment.STATION && !cart.locked) return;
                cart.moving = true;
                for (int i = 0; i < this.listeners.size(); i++) {
                    Listener listener = this.listeners.get(i);
                    listener.cartMoving();
                }
                // this.cartForward(cart, to);
                this.schedule(this, "cartForward", cartMoveDelay, cart, to);
            }
        }
    }

    private void tryBackward(Cart cart) {
        
    }

    public boolean allowsForward(Segment from, Segment to) {
        // System.out.printf("Allow %s -> %s? %b, %b, %b, %b\r\n", from, to, this.track.allowsForward(from, to),
        //     this.switches.allowsForward(from, to),
        //     this.gates.allowsForward(from),
        //     this.button.allowsForward(from));
        return this.track.allowsForward(from, to) &&
            this.switches.allowsForward(from, to) &&
            this.gates.allowsForward(from) &&
            this.button.allowsForward(from);
    }

    private void schedule(Object target, String action, int time, Object... arguments) {
        if (arguments.length > 0) {
            this.scheduledActions.add(new RollercoasterAction(target, action, time, arguments));
        } else {
            this.scheduledActions.add(new RollercoasterAction(target, action, time));
        }
    }

    /**
     * Output functions of the system. Double as actually performing the action on the models.
     */
    public void cartForward(Cart cart, Segment to) {
        Segment from = cart.getLocation();
        if (!this.allowsForward(from, to)) return;

        if (to == Segment.BRAKING) brakingWaitingTime = 0;
        track.move(from, to);
        cart.forward(to);
        cart.moving = false;

        if (from == Segment.REPAIR && to == Segment.STATION) {
            this.schedule(this, "cartBackward", 8000, cart, Segment.REPAIR);
        }

        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.forward(cart.id, from, to);
        }
    }

    public void cartBackward(Cart cart, Segment to) {
        Segment from = cart.getLocation();
        if (
                !this.track.allowsBackward(from, to) ||
                !this.switches.allowsBackward(from, to) ||
                this.gates.isOpen ||
                !cart.locked
                ) {
            // System.out.printf("Not actually going %s->%s, since: %s, %s\r\n", from, to, this.track.allowsBackward(from, to), this.switches.allowsBackward(from, to));
            return;
        }

        cart.backward(to);
        track.move(from, to);
        cart.moving = false;
        this.schedule(this, "cartForward", 8000, cart, Segment.STATION);

        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.backward(cart.id, from, to);
        }
    }

    public void openBraces(Cart cart) {
        cart.openBraces();
        this.button.detectOpenBraces();
        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.openBraces(cart.id);
        }
    }

    public void closeBraces(Cart cart) {
        cart.closeBraces();
        // Always try to move backwards after pushing the button.
        this.schedule(this, "cartBackward", cartMoveDelay, cart, Segment.REPAIR);
        for (Listener listener: this.listeners) listener.closeBraces(cart.id);
    }

    public void gatesOpen() {
        assert this.switches.allowsGatesOpen();
        assert this.track.allowsGatesOpen();

        this.gates.open();
        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.gatesOpen();
        }
    }

    public void gatesClose() {
        assert this.track.allowsGatesClose();

        this.gates.close();
        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.gatesClose();
        }
    }

    /**
     * Input functions to the system
     */
    public void pushButton() {
        this.button.push();
        // this.schedule(this.button, "push", 0);

        Cart cart = null;
        if (this.cart1.getLocation() == Segment.STATION) {
            cart = cart1;
        } else {
            cart = cart2;
        }
        this.closeBraces(cart);
    }

    // Set the switch to /up/, and move the cart between station and repair.
    public void switchUp() {
        assert !this.switches.isUp;
        assert this.cart1.getLocation() == Segment.BRAKING || this.cart2.getLocation() == Segment.BRAKING;

        this.switches.up();
        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.switchUp();
        }

        // System.out.printf("Switch up, locations: %s, %s\r\n", this.cart1.getLocation(), this.cart2.getLocation());
        if (this.cart1.getLocation() == Segment.BRAKING) {
            // Move cart2
            this.cart2.moving = true;
            if (this.cart2.getLocation() == Segment.STATION) {
                // System.out.println("Going backward");
                this.schedule(this, "cartBackward", cartMoveDelay, this.cart2, Segment.REPAIR);
            } else {
                // System.out.println("Going forward");
                this.schedule(this, "cartForward", cartMoveDelay, this.cart2, Segment.STATION);
            }
            for (int i = 0; i < this.listeners.size(); i++) {
                Listener listener = this.listeners.get(i);
                listener.cartMoving();
            }
        } else if (this.cart2.getLocation() == Segment.BRAKING) {
            // Move cart1
            this.cart1.moving = true;
            if (this.cart1.getLocation() == Segment.STATION) {
                this.schedule(this, "cartBackward", cartMoveDelay, this.cart1, Segment.REPAIR);
            } else {
                this.schedule(this, "cartForward", cartMoveDelay, this.cart1, Segment.STATION);
            }
            for (int i = 0; i < this.listeners.size(); i++) {
                Listener listener = this.listeners.get(i);
                listener.cartMoving();
            }
        }
    }

    public void switchDown() {
        assert this.switches.isUp;

        this.switches.down();
        for (int i = 0; i < this.listeners.size(); i++) {
            Listener listener = this.listeners.get(i);
            listener.switchDown();
        }
    }

    // Collections of 'allows' functions.
    public boolean allowsCloseBraces() {
        return this.button.isOpen;
    }

    public boolean allowsOpenGates() {
        // System.out.printf("Allows open gates: switches %b, track %b, gates closed %b, button %b, cart1 still %b, cart2 still %b\r\n",
        //         this.switches.allowsGatesOpen(),
        //         this.track.allowsGatesOpen(),
        //         !this.gates.isOpen,
        //         this.button.allowsGatesOpen(),
        //         !this.cart1.moving,
        //         !this.cart2.moving
        //         );
        return this.switches.allowsGatesOpen() && this.track.allowsGatesOpen() && !this.gates.isOpen && this.button.allowsGatesOpen() && !this.cart1.moving && !this.cart2.moving;
    }

    public boolean allowsCloseGates() {
        return this.track.allowsGatesClose() && this.gates.isOpen;
    }

    public boolean allowsSwitchUp() {
        return !this.switches.isUp && this.track.allowsSwitchUp();
    }

    public boolean allowsSwitchDown() {
        return this.switches.isUp && this.track.allowsSwitchDown();
    }

    // Rollercoaster Actions: Schedulable actions.
    private class RollercoasterAction {
        public Object target;
        public String action;
        public int time;
        public Object[] arguments;

        public RollercoasterAction(Object target, String action, int time, Object[] arguments) {
            this.target = target;
            this.action = action;
            this.time = time;
            this.arguments = arguments;
        }

        public RollercoasterAction(Object target, String action, int time) {
            this.target = target;
            this.action = action;
            this.time = time;
            this.arguments = null;
        }

        public void execute() {
            java.lang.reflect.Method method;
            try {
                if (this.arguments != null) {
                    Class[] parameterTypes = new Class[this.arguments.length];
                    for (int i = 0; i < this.arguments.length; i++) {
                        parameterTypes[i] = this.arguments[i].getClass();
                    }
                    method = this.target.getClass().getMethod(this.action, parameterTypes);
                } else {
                    method = this.target.getClass().getMethod(this.action);
                }
            } catch (SecurityException e) {
                System.err.println("Error calling function " + this.action + ": " + e.getMessage());
                return;
            } catch (NoSuchMethodException e) {
                System.err.println("Unknown function: " + this.action + " in target class " + this.target.getClass() + ", " + e.getMessage());
                return;
            }

            try {
                if (this.arguments != null) {
                    method.invoke(this.target, (Object[]) this.arguments);
                } else {
                    method.invoke(this.target);
                }
            } catch (Exception e) {
                System.err.println("Error while invoking " + this.action + ": " + e.getCause());

                System.err.println("Stack Trace:");
                e.printStackTrace();

                System.err.println("Stack Trace of cause:");
                e.getCause().printStackTrace();
            }
        }
    }

}
