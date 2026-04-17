import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationGui {
    private JFrame frame;
    private BoardPanel boardPanel;
    private SimulationBoard simBoard;
    private SimulationController controller;
    private Timer timer;

    public SimulationGui(SimulationBoard simBoard, SimulationController controller) {
        this.simBoard = simBoard;
        this.controller = controller;

        setupGUI();
    }

    private void setupGUI() {
        frame = new JFrame("Trafiksimulering");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Timer som driver simuleringen framåt (10 FPS)
        timer = new Timer(10, e -> {
            controller.step();
            boardPanel.repaint();
        });

        frame.setLocationRelativeTo(null); // Centrerar fönstret på skärmen
        frame.setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel control = new JPanel();
        control.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        control.setBackground(new Color(240, 240, 240));

        JComboBox<String> laneSelector = new JComboBox<>();
        List<Lane> lanes = simBoard.getLanes();
        if (lanes != null) {
            for (int i = 0; i < lanes.size(); i++) {
                laneSelector.addItem("Väg " + (i + 1));
            }
        }

        JButton addCarBtn = new JButton("Lägg till bil");
        JButton stepBtn = new JButton("Stega 1x");
        JButton startBtn = new JButton("Starta");
        JButton stopBtn = new JButton("Stoppa");

        addCarBtn.addActionListener(e -> {
            int laneIndex = laneSelector.getSelectedIndex();
            if (laneIndex >= 0 && lanes != null) {
                // Lägg till en bil på vald väg
                controller.addCar(lanes.get(laneIndex), 0, 1);
                boardPanel.repaint();
            }
        });

        stepBtn.addActionListener(e -> {
            controller.step();
            boardPanel.repaint();
        });

        startBtn.addActionListener(e -> {
            timer.start();
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        });

        stopBtn.addActionListener(e -> {
            timer.stop();
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        });
        stopBtn.setEnabled(false); // Avstängd från början

        control.add(new JLabel("Välj väg:"));
        control.add(laneSelector);
        control.add(addCarBtn);
        control.add(new JSeparator(SwingConstants.VERTICAL));
        control.add(stepBtn);
        control.add(startBtn);
        control.add(stopBtn);

        return control;
    }

    private class BoardPanel extends JPanel {
        private static final int PAD = 60; // Mer padding för tydlighet

        public BoardPanel() {
            setBackground(new Color(230, 235, 230)); // Ljusgrön bakgrund
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            // Slå på antialiasing för mjukare grafik
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<Lane> lanes = simBoard.getLanes();
            List<Car> cars = simBoard.getCars();

            if (lanes == null || lanes.isEmpty()) return;

            // 1. Hitta gränserna (bounding box) för alla vägar
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

            for (Lane lane : lanes) {
                if (lane.getStart() == null || lane.getEnd() == null) continue;
                minX = Math.min(minX, Math.min(lane.getStart().getX(), lane.getEnd().getX()));
                minY = Math.min(minY, Math.min(lane.getStart().getY(), lane.getEnd().getY()));
                maxX = Math.max(maxX, Math.max(lane.getStart().getX(), lane.getEnd().getX()));
                maxY = Math.max(maxY, Math.max(lane.getStart().getY(), lane.getEnd().getY()));
            }

            // Inkludera debug-punkter i gränserna
            List<Point> debugPoints = controller.getPointsForIntersection();
            if (debugPoints != null) {
                for (Point p : debugPoints) {
                    minX = Math.min(minX, p.getX());
                    minY = Math.min(minY, p.getY());
                    maxX = Math.max(maxX, p.getX());
                    maxY = Math.max(maxY, p.getY());
                }
            }

            // Undvik division med noll om alla vägar/punkter är på samma plats
            if (maxX == minX) maxX = minX + 1;
            if (maxY == minY) maxY = minY + 1;

            // 2. Räkna ut skalning för att passa in i fönstret
            double availW = getWidth() - 2.0 * PAD;
            double availH = getHeight() - 2.0 * PAD;
            double worldW = maxX - minX;
            double worldH = maxY - minY;

            double scale = Math.min(availW / worldW, availH / worldH);

            // Centrera innehållet i fönstret
            double extraW = availW - (worldW * scale);
            double extraH = availH - (worldH * scale);
            double originX = PAD + (extraW / 2.0);
            double originY = PAD + (extraH / 2.0);

            // 3. Rita vägarna (Lanes) med bredd
            for (Lane lane : lanes) {
                if (lane.getStart() == null || lane.getEnd() == null) continue;
                int sx = (int) (originX + (lane.getStart().getX() - minX) * scale);
                int sy = (int) (originY + (lane.getStart().getY() - minY) * scale);
                int ex = (int) (originX + (lane.getEnd().getX() - minX) * scale);
                int ey = (int) (originY + (lane.getEnd().getY() - minY) * scale);

                // Rita den breda asfalten
                float laneWidth = (float)(lane.getWidth() * scale);
                g2.setStroke(new BasicStroke(laneWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(60, 60, 70)); // Mörkgrå asfalt
                g2.drawLine(sx, sy, ex, ey);

                // Rita en streckad mittlinje
                float dash[] = {10.0f};
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.setColor(new Color(240, 240, 240)); // Ljusgrå linje
                g2.drawLine(sx, sy, ex, ey);
            }

            // 4. Rita bilarna (Cars)
            if (cars != null) {
                for (Car car : cars) {
                    Lane lane = car.getCurrentLane();
                    if (lane == null || lane.getStart() == null || lane.getEnd() == null) continue;

                    // Räkna ut procentuell position på vägen (0.0 till 1.0)
                    double t = car.getPositionOnLane();


                    double sx = originX + (lane.getStart().getX() - minX) * scale;
                    double sy = originY + (lane.getStart().getY() - minY) * scale;
                    double ex = originX + (lane.getEnd().getX() - minX) * scale;
                    double ey = originY + (lane.getEnd().getY() - minY) * scale;

                    // Interpolera bilens position
                    int cx = (int) (sx + t * (ex - sx));
                    int cy = (int) (sy + t * (ey - sy));

                    // Skala bilens storlek baserat på vägens bredd
                    int carSize = (int)Math.max(6, lane.getWidth() * scale * 0.7);

                    // Rita själva bilen
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.setColor(new Color(200, 40, 40)); // Rödare färg
                    g2.fillOval(cx - carSize/2, cy - carSize/2, carSize, carSize);

                    g2.setColor(Color.BLACK);
                    g2.drawOval(cx - carSize/2, cy - carSize/2, carSize, carSize);
                }
            }

            // -- NYTT: RITA UT DEBUG-PUNKTER FÖR VISUALISERING --
            if (debugPoints != null && !debugPoints.isEmpty()) {
                g2.setColor(Color.BLUE); // Blå färg för debug
                g2.setStroke(new BasicStroke(2.0f));
                int crossSize = 6; // Storlek på krysset

                for (Point p : debugPoints) {
                    // Beräkna punktens skärmposition
                    int px = (int) (originX + (p.getX() - minX) * scale);
                    int py = (int) (originY + (p.getY() - minY) * scale);

                    // Rita ett kryss (två linjer)
                    g2.drawLine(px - crossSize, py - crossSize, px + crossSize, py + crossSize);
                    g2.drawLine(px - crossSize, py + crossSize, px + crossSize, py - crossSize);
                }
            }
            // ------------------------------------------------
        }
    }
}