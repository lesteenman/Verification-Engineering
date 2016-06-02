package Rollercoaster.Visualization;

import Rollercoaster.Controller;
import Rollercoaster.Model.Track.Segment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.imageio.*;
import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ClassLoader;

class Panel extends JPanel {

    public Segment cart1pos;
    public Segment cart2pos;
    public boolean cart1open;
    public boolean cart2open;
    public boolean switchup;
    public boolean gatesopen;

    private BufferedImage imgBraking;
    private BufferedImage imgRepair;
    private BufferedImage imgCorner;
    private BufferedImage imgLift;
    private BufferedImage imgStation;
    private BufferedImage imgSwitchUp;
    private BufferedImage imgSwitchDown;
    private BufferedImage imgGatesOpen;
    private BufferedImage imgGatesClosed;
    private BufferedImage imgCart1Open;
    private BufferedImage imgCart1Closed;
    private BufferedImage imgCart2Open;
    private BufferedImage imgCart2Closed;

    public Panel() {
        // Load all images up-front
        try {
            imgBraking = ImageIO.read(new File(getClass().getResource("/res/braking.png").getPath()));
            imgRepair = ImageIO.read(new File(getClass().getResource("/res/repair.png").getPath()));
            imgCorner = ImageIO.read(new File(getClass().getResource("/res/corner.png").getPath()));
            imgLift = ImageIO.read(new File(getClass().getResource("/res/lift.png").getPath()));
            imgStation = ImageIO.read(new File(getClass().getResource("/res/station.png").getPath()));
            imgSwitchUp = ImageIO.read(new File(getClass().getResource("/res/switch_up.png").getPath()));
            imgSwitchDown = ImageIO.read(new File(getClass().getResource("/res/switch_down.png").getPath()));
            imgGatesOpen = ImageIO.read(new File(getClass().getResource("/res/gates_open.png").getPath()));
            imgGatesClosed = ImageIO.read(new File(getClass().getResource("/res/gates_closed.png").getPath()));
            imgCart1Open = ImageIO.read(new File(getClass().getResource("/res/cart1_open.png").getPath()));
            imgCart1Closed = ImageIO.read(new File(getClass().getResource("/res/cart1_closed.png").getPath()));
            imgCart2Open = ImageIO.read(new File(getClass().getResource("/res/cart2_open.png").getPath()));
            imgCart2Closed = ImageIO.read(new File(getClass().getResource("/res/cart2_closed.png").getPath()));
        } catch (Exception e) {
            System.err.println("Error while loading images! " + e.getMessage());
        }

        this.setBackground(Color.WHITE);
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawString("Cart 1: ", 5, 5);
        g2d.drawString("Position: " + this.cart1pos, 15, 25);
        g2d.drawString("Open: " + this.cart1open, 15, 45);
        g2d.drawString("Cart 2: ", 5, 65);
        g2d.drawString("Position: " + this.cart2pos, 15, 85);
        g2d.drawString("Open: " + this.cart2open, 15, 105);
        g2d.drawString("Switch: " + this.switchup, 5, 125);
        g2d.drawString("Gates: " + this.gatesopen, 5, 145);
        g2d.setPaint(Color.black);
    }

    private void drawStaticTrack(Graphics g) {
        g.drawImage(imgBraking, 0, 295, this);
        g.drawImage(imgStation, 206, 295, this);
        g.drawImage(imgRepair, 24, 228, this);
        g.drawImage(imgCorner, 455, 46, this);
        g.drawImage(imgLift, 0, 46, this);
    }

    private void drawActiveTrack(Graphics g) {
        BufferedImage switchImage = null;
        if (this.switchup) {
            switchImage = imgSwitchUp;
        } else {
            switchImage = imgSwitchDown;
        }
        g.drawImage(switchImage, 163, 228, this);

        BufferedImage gatesImage = null;
        if (this.gatesopen) {
            gatesImage = imgGatesOpen;
        } else {
            gatesImage = imgGatesClosed;
        }
        g.drawImage(gatesImage, 293, 345, this);
    }

    private void drawCart1(Graphics g) {
        BufferedImage cartImage = null;
        if (this.cart1open) {
            cartImage = imgCart1Open;
        } else {
            cartImage = imgCart1Closed;
        }
        int x = 0, y = 0, rotation = 0;
        if (this.cart1pos == Segment.BRAKING) {
            x = 69;
            y = 295;
        } else if (this.cart1pos == Segment.REPAIR) {
            x = 66;
            y = 228;
        } else if (this.cart1pos == Segment.STATION) {
            x = 371;
            y = 295;
        } else if (this.cart1pos == Segment.LIFT) {
            x = 275;
            y = 45;
            rotation = 180;
        } else if (this.cart1pos == Segment.MAIN) {
            x = 0;
            y = 150;
            rotation = 90;
        }

        double rotationRequired = Math.toRadians(rotation);
        double locationX = cartImage.getWidth() / 2;
        double locationY = cartImage.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        g.drawImage(op.filter(cartImage, null), x, y, this);
    }

    private void drawCart2(Graphics g) {
        BufferedImage cartImage = null;
        if (this.cart2open) {
            cartImage = imgCart2Open;
        } else {
            cartImage = imgCart2Closed;
        }
        int x = 0, y = 0, rotation = 0;
        if (this.cart2pos == Segment.BRAKING) {
            x = 69;
            y = 295;
        } else if (this.cart2pos == Segment.REPAIR) {
            x = 66;
            y = 228;
        } else if (this.cart2pos == Segment.STATION) {
            x = 371;
            y = 295;
        } else if (this.cart2pos == Segment.LIFT) {
            x = 275;
            y = 45;
            rotation = 180;
        } else if (this.cart2pos == Segment.MAIN) {
            x = 0;
            y = 150;
            rotation = 90;
        }

        double rotationRequired = Math.toRadians(rotation);
        double locationX = cartImage.getWidth() / 2;
        double locationY = cartImage.getHeight() / 2;
        AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        g.drawImage(op.filter(cartImage, null), x, y, this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Graphical Version
        drawStaticTrack(g);
        drawActiveTrack(g);
        drawCart1(g);
        drawCart2(g);

        // Text Version
        // doDrawing(g);
    }
}

class UI extends JPanel implements ActionListener {

    Controller controller;
    JButton braceButton;
    JButton openGatesButton;
    JButton closeGatesButton;
    JButton switchUpButton;
    JButton switchDownButton;

    public UI(Controller controller) {
        this.controller = controller;
        this.setBackground(Color.DARK_GRAY);
        this.setLayout(null);

        this.braceButton = new JButton("Braces Closed");
        this.braceButton.setSize(140, 25);
        this.braceButton.setActionCommand("push_button");
        this.braceButton.setLocation(15, 15);
        this.braceButton.addActionListener(this);
        this.add(braceButton);

        this.openGatesButton = new JButton("Open Gates");
        this.openGatesButton.setSize(140, 25);
        this.openGatesButton.setLocation(170, 15);
        this.openGatesButton.setActionCommand("station_open_gates");
        this.openGatesButton.addActionListener(this);
        this.add(openGatesButton);

        this.closeGatesButton = new JButton("Close Gates");
        this.closeGatesButton.setSize(140, 25);
        this.closeGatesButton.setLocation(325, 15);
        this.closeGatesButton.setActionCommand("station_close_gates");
        this.closeGatesButton.addActionListener(this);
        this.add(closeGatesButton);

        this.switchUpButton = new JButton("Switch Up");
        this.switchUpButton.setSize(140, 25);
        this.switchUpButton.setLocation(170, 55);
        this.switchUpButton.setActionCommand("station_switch_up");
        this.switchUpButton.addActionListener(this);
        this.add(switchUpButton);

        this.switchDownButton = new JButton("Switch Down");
        this.switchDownButton.setSize(140, 25);
        this.switchDownButton.setLocation(325, 55);
        this.switchDownButton.setActionCommand("station_switch_down");
        this.switchDownButton.addActionListener(this);
        this.add(switchDownButton);
    }

    public void setButtons(boolean braces, boolean opengates, boolean closegates, boolean switchup, boolean switchdown) {
        this.braceButton.setEnabled(braces);
        this.openGatesButton.setEnabled(opengates);
        this.closeGatesButton.setEnabled(closegates);
        this.switchUpButton.setEnabled(switchup);
        this.switchDownButton.setEnabled(switchdown);
    }

    public void actionPerformed(ActionEvent e) {
        String method = e.getActionCommand();

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

}

public class Frame extends JFrame {
    private Panel panel;
    private UI ui;
    private Controller controller;

    public Frame(String title, Controller controller) {
        this.setLayout(null);

        this.panel = new Panel();
        this.panel.setSize(640, 450);
        this.panel.setLocation(0, 0);
        add(this.panel);

        if (controller != null) {
            this.controller = controller;
            this.ui = new UI(controller);
            this.ui.setSize(640, 100);
            this.ui.setLocation(0, 450);
            add(this.ui);
            setSize(640, 590);
        } else {
            setSize(640, 450);
        }

        setTitle(title);
    }

    public Frame(String title) {
        this(title, null);
    }

    public void redraw(Segment cart1pos, Segment cart2pos, boolean cart1open, boolean cart2open, boolean switchup, boolean gatesopen) {
        this.panel.cart1pos = cart1pos;
        this.panel.cart1open = cart1open;
        this.panel.cart2pos = cart2pos;
        this.panel.cart2open = cart2open;
        this.panel.switchup = switchup;
        this.panel.gatesopen = gatesopen;
        this.panel.repaint();

        if (this.ui != null && this.controller != null) {
            this.ui.setButtons(
                this.controller.allowsCloseBraces(),
                this.controller.allowsOpenGates(),
                this.controller.allowsCloseGates(),
                this.controller.allowsSwitchUp(),
                this.controller.allowsSwitchDown()
            );
        }
    }
}
