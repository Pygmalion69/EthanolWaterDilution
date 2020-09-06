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
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author helfrich
 */
public class DilutionTest {

    @Test
    public void testDilution() {

        ArrayList<ArrayList<Double>> density = DensityByTemperature.getVolumeDensity().getDensity();
        //System.out.println(density.toString());

        // Test init
        DensityByTemperature volumeDensity = DensityByTemperature.getVolumeDensity();
        DensityByTemperature massDensity = DensityByTemperature.getMassDensity();

        double firstQ = 100;
        double firstP = 96.3;
        double secondQ = 300;
        double secondP = 0;
        double t = 22;

        double q1 = firstQ;
        double q2 = secondQ;

        double ro1 = volumeDensity.approx(t, firstP);
        double ro2 = volumeDensity.approx(t, secondP);
        q1 *= ro1;
        q2 *= ro2;

        double ro1_20 = volumeDensity.approx(20, firstP);
        double ro2_20 = volumeDensity.approx(20, secondP);
        double p1 = 0.78927 / ro1_20 * firstP;
        double p2 = 0.78927 / ro2_20 * secondP;
        double p = (p1 * q1 + p2 * q2) / (q1 + q2);
        double ro = massDensity.approx(20, p);
        double roT = massDensity.approx(t, p);
        double volume = (q1 + q2) / roT;
        double volume20 = (q1 + q2) / ro;
        double percentM = p / 100;
        double percent = ro / 0.78927 * p / 100;
        double mass = q1 + q2;

        // Test
        double abv = Calc.abv(100, 96.3, 300, 22);
        System.out.println("ABV = " + abv);

        assertEquals(0.2451, abv, 0.001);

        // Test
        EthanolWaterMixture mixture = new EthanolWaterMixture(volumeDensity, massDensity, 100, 96.3, 22);
        abv = mixture.abv(300);
        System.out.println("ABV = " + abv);

        assertEquals(0.2451, abv, 0.001);

        FunctionCallable functionCallable = (double x) -> mixture.abv(x);

        Solver solver = new Solver();
        double initialGuess = 100;
        double waterQuantity = 0;

        try {
            waterQuantity = solver.solve(functionCallable, .24508744179965763, initialGuess);
        } catch (SolverException e) {
            e.printStackTrace();
        }

        System.out.println("Water = " + waterQuantity);

        assertEquals(300, waterQuantity, 0.001);

        // Test Calc method
        waterQuantity = Calc.waterToAdd(100, 96.3, 22, .24508744179965763);

        System.out.println("Water = " + waterQuantity);

        assertEquals(300, waterQuantity, 0.001);

    }
}
