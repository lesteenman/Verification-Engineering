package Rollercoaster;

import Rollercoaster.Visualization.Visualization;
import Rollercoaster.Model.Track.Segment;
import java.util.ArrayList;
import java.io.*;

public class ConsoleProgram extends Listener {

    private static Segment cart1Start = Segment.BRAKING;
    private static Segment cart2Start = Segment.REPAIR;

    private ArrayList<String> outputMessages;
    private Controller controller;
    private Visualization visualization;
    
    public static void main(String[] args) {
        new ConsoleProgram();
    }

    public ConsoleProgram() {
        this.controller = new Controller(cart1Start, cart2Start, 7500);

        this.outputMessages = new ArrayList<String>();
        controller.addListener(this);

        InputStreamReader inputStream = new InputStreamReader(System.in);
        BufferedReader inputReader = new BufferedReader(inputStream);

        this.visualization = new Visualization(cart1Start, cart2Start);
        controller.addListener(this.visualization);

        boolean running = true;
        try {
            while (running) {
                if (inputReader.ready()) {
                    String input = inputReader.readLine();
                    this.handleInput(input);
                }

                while (this.outputMessages.size() > 0) {
                    String message = this.outputMessages.remove(0);
                    System.out.println(message);
                }
            }

            inputReader.close();
        } catch(IOException e) {
            System.out.println("Error while reading from console: " + e.getMessage());
        }
    }

    // Input Methods (console -> controller)
    private void handleInput(String input) {
        String[] split = input.split(" ");

        String method = split[0];

        // String[] args = new String[split.length - 1];
        // System.arraycopy(split, 1, args, 0, args.length);
        switch (method) {
            case "push_button":
                controller.pushButton();
                break;

            case "station_switch_up":
                controller.switchUp();
                break;
            case "station_switch_down":
                controller.switchDown();
                break;

            case "station_open_gates":
                controller.gatesOpen();
                break;
            case "station_close_gates":
                controller.gatesClose();
                break;

            default:
                System.out.println("Unimplemented method: " + method);
                break;
        }
    }

    // Output Methods (controller -> console)
    @Override
    public void forward(String cartId, Segment from, Segment to) {
        System.out.printf("forward!%s!%s!%s\r\n", cartId, from.toString().toLowerCase(), to.toString().toLowerCase());
    }

    @Override
    public void backward(String cartId, Segment from, Segment to) {
        System.out.printf("backward!%s!%s!%s\r\n", cartId, from.toString().toLowerCase(), to.toString().toLowerCase());
    }

    @Override
    public void openBraces(String cartId) {
        System.out.printf("open_braces!%s\r\n", cartId);
    }

    @Override
    public void closeBraces(String cartId) {
        System.out.printf("set_braces_closed!%s\r\n", cartId);
    }
}
