import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;

// Main GUI
class ColorConverterApp extends JFrame {
    private final ColorConverter colorConverter = new ColorConverter();
    private boolean isUpdating = false;

    private List<Integer> rgbColor = Arrays.asList(0, 0, 0);
    private List<Double> cmykColor = Arrays.asList(0.0, 0.0, 0.0, 1.0);
    private List<Double> hlsColor = Arrays.asList(0.0, 0.0, 0.0);

    private List<JTextField> rgbFields = new ArrayList<>();
    private List<JTextField> cmykFields = new ArrayList<>();
    private List<JTextField> hlsFields = new ArrayList<>();

    private List<JSlider> rgbSliders = new ArrayList<>();
    private List<JSlider> cmykSliders = new ArrayList<>();
    private List<JSlider> hlsSliders = new ArrayList<>();

    private JPanel colorDisplayPanel = new JPanel();

    public ColorConverterApp() {
        setTitle("Color Converter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 3));

        rgbColor.forEach(value -> rgbFields.add(new JTextField(5)));
        cmykColor.forEach(value -> cmykFields.add(new JTextField(5)));
        hlsColor.forEach(value -> hlsFields.add(new JTextField(5)));

        for (int i = 0; i < 3; i++) {
            rgbSliders.add(createSlider(0, 255));
        }
        for (int i = 0; i < 4; i++) {
            cmykSliders.add(createSlider(0, 100));
        }
        hlsSliders.add(createSlider(0, 360));
        hlsSliders.add(createSlider(0, 100));
        hlsSliders.add(createSlider(0, 100));

        colorDisplayPanel.setPreferredSize(new Dimension(100, 100));
        colorDisplayPanel.setBackground(Color.BLACK);

        JPanel rgbPanel = new JPanel();
        rgbPanel.add(new JLabel("RGB:"));
        rgbFields.forEach(rgbPanel::add);
        rgbSliders.forEach(rgbPanel::add);
        add(rgbPanel);

        JPanel cmykPanel = new JPanel();
        cmykPanel.add(new JLabel("CMYK:"));
        cmykFields.forEach(cmykPanel::add);
        cmykSliders.forEach(cmykPanel::add);
        add(cmykPanel);

        JPanel hlsPanel = new JPanel();
        hlsPanel.add(new JLabel("HLS:"));
        hlsFields.forEach(hlsPanel::add);
        hlsSliders.forEach(hlsPanel::add);
        add(hlsPanel);

        add(colorDisplayPanel);

        JButton colorChooserButton = new JButton("Open palette");
        colorChooserButton.addActionListener(e -> chooseColor());
        add(colorChooserButton);

        rgbFields.forEach(field -> addDocumentListener(field, Changed.RGB));
        cmykFields.forEach(field -> addDocumentListener(field, Changed.CMYK));
        hlsFields.forEach(field -> addDocumentListener(field, Changed.HLS));

        for (int i = 0; i < rgbSliders.size(); i++) {
            addSliderListener(rgbSliders.get(i), rgbFields.get(i), Changed.RGB);
        }
        for (int i = 0; i < cmykSliders.size(); i++) {
            addSliderListener(cmykSliders.get(i), cmykFields.get(i), Changed.CMYK);
        }
        for (int i = 0; i < hlsSliders.size(); i++) {
            addSliderListener(hlsSliders.get(i), hlsFields.get(i), Changed.HLS);
        }

        updateFields();
        updateSliders();
        updateColorDisplay();
        pack();
        setVisible(true);
    }

    private void chooseColor() {
        Color color = JColorChooser.showDialog(this, "Choose color", null);
        if (color != null) {
            rgbColor = Arrays.asList(color.getRed(), color.getGreen(), color.getBlue());
            updateFromRgb();
            updateFields();
            updateSliders();
            updateColorDisplay();
        }
    }

    private void addDocumentListener(JTextField field, Changed changed) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateColors(changed);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateColors(changed);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateColors(changed);
            }
        });
    }

    private void addSliderListener(JSlider slider, JTextField field, Changed changed) {
        slider.addChangeListener(e -> {
            if (!isUpdating) {
                field.setText(String.valueOf(slider.getValue()));
            }
        });
    }

    private JSlider createSlider(int min, int max) {
        JSlider slider = new JSlider(min, max);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    private void updateColors(Changed changed) {
        if (isUpdating) return;
        SwingUtilities.invokeLater(() -> {
            isUpdating = true;
            try {
                System.out.println("Changed " + changed.name());
                switch (changed) {
                    case RGB -> {
                        if (rgbFields.stream().anyMatch(field -> field.getText().isEmpty())) {
                            return;
                        }
                        List<Integer> rgbValues = rgbFields.stream().map(field -> Integer.parseInt(field.getText())).toList();
                        rgbColor = rgbValues;
                        updateFromRgb();
                        updateCMYKField();
                        updateHLSField();
                    }
                    case CMYK -> {
                        if (cmykFields.stream().anyMatch(field -> field.getText().isEmpty())) {
                            return;
                        }
                        List<Double> cmykValues = cmykFields.stream().map(field -> Double.parseDouble(field.getText()) / 100).toList();
                        List<Integer> rgbValues = colorConverter.cmykToRgb(cmykValues.get(0), cmykValues.get(1), cmykValues.get(2), cmykValues.get(3));
                        rgbColor = rgbValues;
                        cmykColor = cmykValues;
                        hlsColor = colorConverter.rgbToHls(rgbValues.get(0), rgbValues.get(1), rgbValues.get(2));
                        updateRGBField();
                        updateHLSField();
                    }
                    case HLS -> {
                        if (hlsFields.stream().anyMatch(field -> field.getText().isEmpty())) {
                            return;
                        }
                        List<Double> hlsValues = hlsFields.stream().map(field -> Double.parseDouble(field.getText())).toList();
                        double l = hlsValues.get(1) / 100;
                        double s = hlsValues.get(2) / 100;
                        List<Integer> rgbValues = colorConverter.hlsToRgb(hlsValues.get(0), l, s);
                        rgbColor = rgbValues;
                        cmykColor = colorConverter.rgbToCmyk(rgbValues.get(0), rgbValues.get(1), rgbValues.get(2));
                        hlsColor = hlsValues;
                        updateRGBField();
                        updateCMYKField();
                    }
                }
                updateSliders();
                updateColorDisplay();
            } catch (NumberFormatException e) {
                System.out.println(e.toString());
            } finally {
                isUpdating = false;
            }
        });
    }

    private void updateRGBField() {
        updateFieldInteger(rgbFields, rgbColor);
    }

    private void updateCMYKField() {
        updateFieldDouble(cmykFields,
                cmykColor.stream().map(value -> value * 100).toList());
    }

    private void updateHLSField() {
        List<Double> hlsValues = hlsColor;
        updateFieldDouble(hlsFields, Arrays.asList(hlsValues.get(0), hlsValues.get(1) * 100, hlsValues.get(2) * 100));
    }

    private void updateFields() {
        updateRGBField();
        updateCMYKField();
        updateHLSField();
    }

    private void updateFieldDouble(List<JTextField> fields, List<Double> values) {
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).setText(String.format("%.2f", values.get(i)));
        }
        System.out.println("updated for " + values);
    }
    private void updateFieldInteger(List<JTextField> fields, List<Integer> values) {
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).setText(String.format("%d", values.get(i)));
        }
        System.out.println("updated for " + values);
    }

    private void updateColorDisplay() {
        int r = rgbColor.get(0);
        int g = rgbColor.get(1);
        int b = rgbColor.get(2);
        colorDisplayPanel.setBackground(new Color(r, g, b));
        colorDisplayPanel.repaint();
    }

    private void updateSliders() {
        List<Double> hlsValues = hlsColor;
        Map<List<JSlider>, List<Double>> sliderValuesMap = new HashMap<>();
        sliderValuesMap.put(rgbSliders, rgbColor.stream().map(Double::valueOf).toList());
        sliderValuesMap.put(cmykSliders, cmykColor.stream().map(value -> value * 100).toList());
        sliderValuesMap.put(hlsSliders, Arrays.asList(hlsValues.get(0), hlsValues.get(1) * 100, hlsValues.get(2) * 100));

        sliderValuesMap.forEach((sliders, values) -> {
            for (int i = 0; i < sliders.size(); i++) {
                sliders.get(i).setValue(values.get(i).intValue());
            }
        });
    }

    private void updateFromRgb() {
        int r = rgbColor.get(0);
        int g = rgbColor.get(1);
        int b = rgbColor.get(2);
        cmykColor = colorConverter.rgbToCmyk(r, g, b);
        hlsColor = colorConverter.rgbToHls(r, g, b);
    }
}
