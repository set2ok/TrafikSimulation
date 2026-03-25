import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SimulationGui {
    private JFrame frame;
    private BoardPanel boardPanel;
    private SimulationBoard simBoard;
    private SimulationController controller;
    private Timer timer;

    public SimulationGui() {
        // sample lanes and cars
        ArrayList<Lane> lanes = new ArrayList<>();
        lanes.add(new Lane(new Point(20, 50), new Point(380, 50)));
        lanes.add(new Lane(new Point(20, 150), new Point(380, 250)));
        ArrayList<Car> cars = new ArrayList<>();

        simBoard = new SimulationBoard(lanes, cars);
        controller = new SimulationController(simBoard);

        frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel control = new JPanel();
        JButton addCarBtn = new JButton("Add Car");
        JButton stepBtn = new JButton("Step");
        JButton startBtn = new JButton("Start");
        JButton stopBtn = new JButton("Stop");

        JComboBox<String> laneSelector = new JComboBox<>();
        for (int i = 0; i < simBoard.getLanes().size(); i++) {
            laneSelector.addItem("Lane " + i);
        }

        addCarBtn.addActionListener(e -> {
            int laneIndex = laneSelector.getSelectedIndex();
            if (laneIndex >= 0) {
                Car car = new Car(simBoard.getLanes().get(laneIndex), 10);
                simBoard.addCar(car);
                boardPanel.repaint();
            }
        });

        stepBtn.addActionListener(e -> {
            controller.step();
            boardPanel.repaint();
        });

        // Timer drives the simulation and repaints
        timer = new Timer(100, e -> {
            controller.step();
            boardPanel.repaint();
        });

        startBtn.addActionListener(e -> timer.start());
        stopBtn.addActionListener(e -> timer.stop());

        control.add(new JLabel("Lane:"));
        control.add(laneSelector);
        control.add(addCarBtn);
        control.add(stepBtn);
        control.add(startBtn);
        control.add(stopBtn);

        frame.add(control, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private class BoardPanel extends JPanel {
        private static final int PAD = 20;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // white background
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            Graphics2D g2 = (Graphics2D) g.create();

            // compute bounds of all lane endpoints
            double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
            for (Lane lane : simBoard.getLanes()) {
                Point s = lane.getStart();
                Point e = lane.getEnd();
                if (s != null) {
                    minX = Math.min(minX, s.getX());
                    minY = Math.min(minY, s.getY());
                    maxX = Math.max(maxX, s.getX());
                    maxY = Math.max(maxY, s.getY());
                }
                if (e != null) {
                    minX = Math.min(minX, e.getX());
                    minY = Math.min(minY, e.getY());
                    maxX = Math.max(maxX, e.getX());
                    maxY = Math.max(maxY, e.getY());
                }
            }

            // fallback if no lanes
            if (!Double.isFinite(minX) || !Double.isFinite(minY) || !Double.isFinite(maxX) || !Double.isFinite(maxY)) {
                minX = 0;
                minY = 0;
                maxX = getWidth();
                maxY = getHeight();
            }

            // expand tiny bounds to avoid division by zero
            if (Math.abs(maxX - minX) < 1e-6) {
                maxX = minX + 1;
            }
            if (Math.abs(maxY - minY) < 1e-6) {
                maxY = minY + 1;
            }

            double availW = Math.max(10, getWidth() - 2.0 * PAD);
            double availH = Math.max(10, getHeight() - 2.0 * PAD);
            double worldW = maxX - minX;
            double worldH = maxY - minY;

            double scale = Math.min(availW / worldW, availH / worldH);

            // center the content in the panel (optional)
            double extraW = availW - worldW * scale;
            double extraH = availH - worldH * scale;
            double originX = PAD + (extraW / 2.0);
            double originY = PAD + (extraH / 2.0);

            // scale stroke and car radius
            float laneStroke = Math.max(1.0f, (float) (3.0 * scale));
            g2.setStroke(new BasicStroke(laneStroke));

            // draw lanes
            for (Lane lane : simBoard.getLanes()) {
                Point s = lane.getStart();
                Point e = lane.getEnd();
                if (s == null || e == null) continue;
                int sx = (int) Math.round(originX + (s.getX() - minX) * scale);
                int sy = (int) Math.round(originY + (s.getY() - minY) * scale);
                int ex = (int) Math.round(originX + (e.getX() - minX) * scale);
                int ey = (int) Math.round(originY + (e.getY() - minY) * scale);
                g2.setColor(Color.DARK_GRAY);
                g2.drawLine(sx, sy, ex, ey);
            }

            // draw cars (use normalized position t to interpolate between start/end)
            for (Car car : simBoard.getCars()) {
                Lane lane = car.getCurrentLane();
                if (lane == null) continue;
                Point s = lane.getStart();
                Point e = lane.getEnd();
                if (s == null || e == null) continue;

                double t = 0.0;
                if (lane.getLength() > 0) {
                    t = car.getPositionOnLane() / (double) lane.getLength();
                    t = Math.max(0.0, Math.min(1.0, t));
                }

                double sx = originX + (s.getX() - minX) * scale;
                double sy = originY + (s.getY() - minY) * scale;
                double ex = originX + (e.getX() - minX) * scale;
                double ey = originY + (e.getY() - minY) * scale;

                int cx = (int) Math.round(sx + t * (ex - sx));
                int cy = (int) Math.round(sy + t * (ey - sy));
                int r = Math.max(4, (int) Math.round(8 * scale)); // radius scales visually

                g2.setColor(Color.RED);
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(Color.BLACK);
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        // Ensure UI runs on EDT
        SwingUtilities.invokeLater(SimulationGui::new);
    }
}