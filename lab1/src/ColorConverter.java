import java.util.Arrays;
import java.util.List;

class ColorConverter {
    public List<Double> rgbToCmyk(int r, int g, int b) {
        if (Arrays.asList(r, g, b).stream().allMatch(value -> value == 0)) {
            return Arrays.asList(0.0, 0.0, 0.0, 1.0);
        }
        double rNorm = r / 255.0;
        double gNorm = g / 255.0;
        double bNorm = b / 255.0;

        double k = 1 - Math.max(rNorm, Math.max(gNorm, bNorm));
        double c = (1 - rNorm - k) / (1 - k);
        double m = (1 - gNorm - k) / (1 - k);
        double y = (1 - bNorm - k) / (1 - k);

        return Arrays.asList(c, m, y, k);
    }

    public List<Integer> cmykToRgb(double c, double m, double y, double k) {
        int r = (int) Math.round(255 * (1 - c) * (1 - k));
        int g = (int) Math.round(255 * (1 - m) * (1 - k));
        int b = (int) Math.round(255 * (1 - y) * (1 - k));

        return Arrays.asList(r, g, b);
    }

    public List<Double> rgbToHls(int r, int g, int b) {
        double rNorm = r / 255.0;
        double gNorm = g / 255.0;
        double bNorm = b / 255.0;

        double max = Math.max(rNorm, Math.max(gNorm, bNorm));
        double min = Math.min(rNorm, Math.min(gNorm, bNorm));
        if (max == min) {
            return Arrays.asList(0.0, 0.0, max);
        }
        double delta = max - min;

        double l = (max + min) / 2;
        double s = delta / (1 - Math.abs(2 * l - 1));
        double h;
        if (max == rNorm) {
            h = (gNorm - bNorm) / delta;
        } else if (max == gNorm) {
            h = ((bNorm - rNorm) / delta) + 2;
        } else {
            h = ((rNorm - gNorm) / delta) + 4;
        }
        h = 60 * ((h + 6) % 6);

        return Arrays.asList(h, l, s);
    }

    public List<Integer> hlsToRgb(double h, double l, double s) {
        double c = (1 - Math.abs(2 * l - 1)) * s;
        double x = c * (1 - Math.abs((h / 60) % 2 - 1));
        double m = l - c / 2;

        double r1, g1, b1;
        if (h < 60) {
            r1 = c;
            g1 = x;
            b1 = 0.0;
        } else if (h < 120) {
            r1 = x;
            g1 = c;
            b1 = 0.0;
        } else if (h < 180) {
            r1 = 0.0;
            g1 = c;
            b1 = x;
        } else if (h < 240) {
            r1 = 0.0;
            g1 = x;
            b1 = c;
        } else if (h < 300) {
            r1 = x;
            g1 = 0.0;
            b1 = c;
        } else {
            r1 = c;
            g1 = 0.0;
            b1 = x;
        }

        int r = (int) Math.round((r1 + m) * 255);
        int g = (int) Math.round((g1 + m) * 255);
        int b = (int) Math.round((b1 + m) * 255);

        return Arrays.asList(r, g, b);
    }
}
