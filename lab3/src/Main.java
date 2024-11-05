import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private final JPanel canvas;
    private final JTextField x0Field;
    private final JTextField y0Field;
    private final JTextField x1Field;
    private final JTextField y1Field;
    private final JTextField radiusField;
    private final JTextField cellSizeField;
    private final JRadioButton stepAlgoButton;
    private final JRadioButton ddaAlgoButton;
    private final JRadioButton bresenhamAlgoButton;
    private final JRadioButton bresenhamCircleAlgoButton;
    private final JTextArea logArea;
    private int centerX;
    private int centerY;
    private int cellSize = 20;

    public Main() {
        setTitle("Rasterization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
                drawAxis(g);
                rasterizeShape(g);
            }
        };
        canvas.setPreferredSize(new Dimension(400, 400));
        add(canvas, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(12, 2, 5, 5));
        add(controlPanel, BorderLayout.WEST);

        ButtonGroup algoGroup = new ButtonGroup();
        stepAlgoButton = new JRadioButton("step-by-step", true);
        ddaAlgoButton = new JRadioButton("dda");
        bresenhamAlgoButton = new JRadioButton("bresenham");
        bresenhamCircleAlgoButton = new JRadioButton("circle bresenham");
        algoGroup.add(stepAlgoButton);
        algoGroup.add(ddaAlgoButton);
        algoGroup.add(bresenhamAlgoButton);
        algoGroup.add(bresenhamCircleAlgoButton);

        controlPanel.add(new JLabel("Algorithm:"));
        JPanel algoPanel = new JPanel(new GridLayout(4, 1));
        algoPanel.add(stepAlgoButton);
        algoPanel.add(ddaAlgoButton);
        algoPanel.add(bresenhamAlgoButton);
        algoPanel.add(bresenhamCircleAlgoButton);
        controlPanel.add(algoPanel);

        controlPanel.add(new JLabel("x0 (center X):"));
        x0Field = new JTextField("0");
        controlPanel.add(x0Field);

        controlPanel.add(new JLabel("y0 (center Y):"));
        y0Field = new JTextField("0");
        controlPanel.add(y0Field);

        controlPanel.add(new JLabel("x1 (end X):"));
        x1Field = new JTextField("0");
        controlPanel.add(x1Field);

        controlPanel.add(new JLabel("y1 (end Y):"));
        y1Field = new JTextField("0");
        controlPanel.add(y1Field);

        controlPanel.add(new JLabel("radius:"));
        radiusField = new JTextField("0");
        radiusField.setVisible(false);
        controlPanel.add(radiusField);

        controlPanel.add(new JLabel("cell size:"));
        cellSizeField = new JTextField(Integer.toString(cellSize));
        controlPanel.add(cellSizeField);

        JButton drawButton = new JButton("draw");
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cellSize = Integer.parseInt(cellSizeField.getText());
                    canvas.repaint();
                } catch (NumberFormatException ex) {
                    logArea.setText("Invalid cell size!");
                }
            }
        });
        controlPanel.add(new JLabel());
        controlPanel.add(drawButton);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 50));
        add(scrollPane, BorderLayout.SOUTH);

        ActionListener algoListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isCircleMode = bresenhamCircleAlgoButton.isSelected();
                x1Field.setVisible(!isCircleMode);
                y1Field.setVisible(!isCircleMode);
                radiusField.setVisible(isCircleMode);
                controlPanel.revalidate();
                controlPanel.repaint();
            }
        };

        stepAlgoButton.addActionListener(algoListener);
        ddaAlgoButton.addActionListener(algoListener);
        bresenhamAlgoButton.addActionListener(algoListener);
        bresenhamCircleAlgoButton.addActionListener(algoListener);
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < canvas.getWidth(); i += cellSize) {
            g.drawLine(i, 0, i, canvas.getHeight());
        }
        for (int i = 0; i < canvas.getHeight(); i += cellSize) {
            g.drawLine(0, i, canvas.getWidth(), i);
        }
    }

    private void drawAxis(Graphics g) {
        g.setColor(Color.GRAY);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        centerX = (width / 2 / cellSize) * cellSize;
        centerY = (height / 2 / cellSize) * cellSize;

        g.drawLine(centerX, 0, centerX, height);
        g.drawLine(0, centerY, width, centerY);

        g.drawString("x", width - 20, centerY - 10);
        g.drawString("y", centerX + 10, 20);

        for (int i = 0; i <= width / 2 - 2 * cellSize; i += cellSize) {
            g.drawLine(centerX + i, centerY - 5, centerX + i, centerY + 5);
            if (i != 0) {
                g.drawString(Integer.toString(i / cellSize), centerX + i - 5, centerY + 20);
            }

            g.drawLine(centerX - i, centerY - 5, centerX - i, centerY + 5);
            if (i != 0) {
                g.drawString(Integer.toString(-i / cellSize), centerX - i - 10, centerY + 20);
            }
        }

        for (int i = 0; i <= height / 2 - 2 * cellSize; i += cellSize) {
            g.drawLine(centerX - 5, centerY - i, centerX + 5, centerY - i);
            if (i != 0) {
                g.drawString(Integer.toString(i / cellSize), centerX + 10, centerY - i + 5);
            }

            g.drawLine(centerX - 5, centerY + i, centerX + 5, centerY + i);
            if (i != 0) {
                g.drawString(Integer.toString(-i / cellSize), centerX + 10, centerY + i + 5);
            }
        }
    }

    private void rasterizeShape(Graphics g) {
        int x0 = Integer.parseInt(x0Field.getText());
        int y0 = Integer.parseInt(y0Field.getText());

        if (bresenhamCircleAlgoButton.isSelected()) {
            int radius = Integer.parseInt(radiusField.getText());
            circle(g, x0, y0, radius);
        } else {
            int x1 = Integer.parseInt(x1Field.getText());
            int y1 = Integer.parseInt(y1Field.getText());
            if (stepAlgoButton.isSelected()) {
                stepbystep(g, x0, y0, x1, y1);
            } else if (ddaAlgoButton.isSelected()) {
                dda(g, x0, y0, x1, y1);
            } else if (bresenhamAlgoButton.isSelected()) {
                bresenham(g, x0, y0, x1, y1);
            }
        }
    }

    private void stepbystep(Graphics g, int x0, int y0, int x1, int y1) {
        if (x0 > x1) {
            var buf = x0;
            x0 = x1;
            x1 = buf;
        }

        if (y0 > y1) {
            var buf = y0;
            y0 = y1;
            y1 = buf;
        }

        logArea.setText("step-by-step:\n");
        g.setColor(Color.BLACK);

        int dx = x1 - x0;
        int dy = y1 - y0;

        if (dx > dy) {
            double slope = (double) dy / dx;

            for (int x = x0; x <= x1; x++) {
                double y = y0 + slope * (x - x0);
                int yRounded = (int) Math.floor(y);
                logArea.append("point (" + x + ", " + yRounded + ")\n");
                var cellX = x * cellSize + centerX;
                var cellY = -yRounded * cellSize + centerY - cellSize;
                g.fillRect(cellX, cellY, cellSize, cellSize);
            }
        } else {
            double slope = (double) dx / dy;

            for (int y = y0; y <= y1; y++) {
                double x = x0 + slope * (y - y0);
                int xRounded = (int) Math.floor(x);
                logArea.append("point (" + xRounded + ", " + y + ")\n");
                var cellX = xRounded * cellSize + centerX;
                var cellY = -y * cellSize + centerY - cellSize;
                g.fillRect(cellX, cellY, cellSize, cellSize);
            }
        }
    }

    private void dda(Graphics g, int x0, int y0, int x1, int y1) {
        logArea.setText("dda:\n");
        g.setColor(Color.BLACK);
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        double xInc = dx / (double) steps;
        double yInc = dy / (double) steps;
        double x = x0, y = y0;

        for (int i = 0; i <= steps; i++) {
            logArea.append("point (" + (int) Math.round(x) + ", " + (int) Math.round(y) + ")\n");
            var cellX = (int) Math.round(x) * cellSize + centerX;
            var cellY = -(int) Math.round(y) * cellSize + centerY - cellSize;
            g.fillRect(cellX, cellY, cellSize, cellSize);
            x += xInc;
            y += yInc;
        }
    }

    private void bresenham(Graphics g, int x0, int y0, int x1, int y1) {
        logArea.setText("bresenham:\n");
        g.setColor(Color.BLACK);

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            logArea.append("point (" + x0 + ", " + y0 + ")\n");
            var cellX = x0 * cellSize + centerX;
            var cellY = -y0 * cellSize + centerY - cellSize;
            g.fillRect(cellX, cellY, cellSize, cellSize);

            if (x0 == x1 && y0 == y1) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    private void circle(Graphics g, int xc, int yc, int r) {
        logArea.setText("circle bresenham:\n");
        g.setColor(Color.BLACK);

        int x = 0;
        int y = r;
        int d = 3 - 2 * r;

        while (x <= y) {
            drawcircle(g, xc, yc, x, y);
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
        }
    }

    private void drawcircle(Graphics g, int xc, int yc, int x, int y) {
        drawpoint(g, xc + x, yc + y);
        drawpoint(g, xc - x, yc + y);
        drawpoint(g, xc + x, yc - y);
        drawpoint(g, xc - x, yc - y);
        drawpoint(g, xc + y, yc + x);
        drawpoint(g, xc - y, yc + x);
        drawpoint(g, xc + y, yc - x);
        drawpoint(g, xc - y, yc - x);
    }

    private void drawpoint(Graphics g, int x, int y) {
        g.fillRect(x * cellSize + centerX, -y * cellSize + centerY - cellSize, cellSize, cellSize);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}
