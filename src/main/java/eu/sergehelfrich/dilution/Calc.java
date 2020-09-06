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

import eu.sergehelfrich.dilution.solver.FunctionCallable;
import eu.sergehelfrich.dilution.solver.Solver;
import eu.sergehelfrich.dilution.solver.SolverException;

/**
 * Alcohol amount in the aqueous alcohol solution, by Anton
 * https://planetcalc.com/1481/
 */
public class Calc {

    private static final Solver solver = new Solver();
    private static final double initialGuess = 100;

    private static final DensityByTemperature volumeDensity = DensityByTemperature.getVolumeDensity();
    private static final DensityByTemperature massDensity = DensityByTemperature.getMassDensity();

    /**
     * Alcohol by volume
     *
     * @param q1 initial quantity
     * @param abv1 ABV (to dilute)
     * @param q2 initial water quantity
     * @param t temperature (deg C)
     * @return ABV (fraction 0..1) at 20 deg C
     */
    public static double abv(double q1, double abv1, double q2, double t) {

        DensityByTemperature volumeDensity = DensityByTemperature.getVolumeDensity();
        DensityByTemperature massDensity = DensityByTemperature.getMassDensity();

        double ro1 = volumeDensity.approx(t, abv1);
        double ro2 = volumeDensity.approx(t, 0);
        q1 *= ro1;
        q2 *= ro2;

        double ro1_20 = volumeDensity.approx(20, abv1);
        double p1 = 0.78927 / ro1_20 * abv1;
        double p2 = 0;
        double p = (p1 * q1 + p2 * q2) / (q1 + q2);
        double ro = massDensity.approx(20, p);
        double roT = massDensity.approx(t, p);
        double abv = ro / 0.78927 * p / 100;

        return abv;
    }

    /**
     * Water to add
     *
     * @param q1 initial quantity
     * @param abv1 ABV (to dilute)
     * @param t temperature (deg C)
     * @param abv2 target ABV at 20 deg C
     * @return qty of water to add
     */
    public static double waterToAdd(double q1, double abv1, double t, double abv2) {
        EthanolWaterMixture mixture = new EthanolWaterMixture(volumeDensity, massDensity, q1, abv1, t);
        FunctionCallable functionCallable = (double x) -> mixture.abv(x);
        double waterQuantity = 0;
        try {
            waterQuantity = solver.solve(functionCallable, abv2, initialGuess);
        } catch (SolverException e) {
            e.printStackTrace();
        }
        return waterQuantity;
    }
}
