/*
 * Copyright (C) 2020 helfrich
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.sergehelfrich.dilution;

import java.util.ArrayList;

/**
 *
 * @author helfrich
 */
public class DensityByTemperature {

    int total = 1000000;
    int minDiff = -6;
    ArrayList<ArrayList<Double>> density = new ArrayList<>();

    String[] data;

    private static final DensityByTemperature volumeDensity = new DensityByTemperature(Data.volumePercent);
    private static final DensityByTemperature massDensity = new DensityByTemperature(Data.massPercent);

    private DensityByTemperature(String[] data) {
        this.data = data;
        unpack();
    }

    public static DensityByTemperature getVolumeDensity() {
        return volumeDensity;
    }

    public static DensityByTemperature getMassDensity() {
        return massDensity;
    }

    private void unpack() {
        for (int i = 0; i < data.length; ++i) {
            String line = data[i];
            String s = line.substring(0, 3);
            int base = (100000 - Integer.parseInt(s, 36));
            int prev = base;
            if (density.isEmpty()) {
                density.add(0, new ArrayList<>());
            }
            ArrayList<Double> a = density.get(0);
            a.add(base / 100000d);
            for (int j = 5; j <= line.length(); j += 2) {
                //System.out.println("j = " + j);
                //System.out.println("line = " + line);
                int diff = Integer.parseInt(line.substring(j - 2, j), 36) + minDiff;
                int t = (j - 3) / 2;
                if (density.size() <= t) {
                    density.add(t, new ArrayList<Double>());;
                }
                a = density.get(t);
                prev += diff;
                a.add(prev / 100000d);
            }
        }
    }

    public ArrayList<ArrayList<Double>> getDensity() {
        return density;
    }

    public double getByTP(int t, int p) {
        int ti = 40 - t;
        if (ti < 0 || ti >= density.size()) {
            return 0;
        }
        ArrayList<Double> pp = density.get(ti);
        int pmin = 101 - pp.size();
        int pi = p - pmin;
        if (pi < 0 || pi >= pp.size()) {
            return 0;
        }
        return pp.get(pi);
    }

    double approx(double t, double d) {
        int t1 = (int) Math.floor(t);
        int t2 = t == t1 ? t1 : t1 + 1;
        int d1 = (int) Math.floor(d);
        int d2 = d == d1 ? d1 : d1 + 1;
        if (t1 == t2 && d1 == d2) {
            return getByTP(t1, d1);
        }

        double p_t1d1 = this.getByTP(t1, d1);
        double p_t2d1 = this.getByTP(t2, d1);
        double p_t1d2 = this.getByTP(t1, d2);
        double p_t2d2 = this.getByTP(t2, d2);
        if (t1 == t2) {
            return approx_(d1, d, d2, p_t1d1, p_t1d2);
        }
        if (d1 == d2) {
            return approx_(t1, t, t2, p_t1d1, p_t2d1);
        }

        double td = (t2 - t) / (t2 - t1);
        double A = td * (p_t1d2 - p_t2d2) + p_t2d2;
        double B = td * (p_t1d1 - p_t2d1) + p_t2d1;
        return (d2 - d) / (d2 - d1) * (B - A) + A;
    }

    private double approx_(double left, double m, double right, double leftV, double rightV) {
        double k = (m - left) / (right - left);
        return leftV + (rightV - leftV) * k;
    }

}
