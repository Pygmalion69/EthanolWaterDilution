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

 
    DensityByTemperature volumeDensity = DensityByTemperature.getVolumeDensity();
    DensityByTemperature massDensity = DensityByTemperature.getMassDensity();

    private double abv;
    private EthanolWaterMixture mixture;

    @Test
    public void testCalcAbv() {
        abv = Calc.abv(100, 96.3, 300, 22);
        System.out.println("ABV = " + abv);
        assertEquals(24.51, abv, 0.1);
    }

    @Test
    public void testMixture() {
        mixture = new EthanolWaterMixture(volumeDensity, massDensity, 100, 96.3, 22);
        abv = mixture.abv(300);
        System.out.println("ABV = " + abv);
        assertEquals(24.51, abv, 0.1);
    }

    @Test
    public void testSolver() {
        mixture = new EthanolWaterMixture(volumeDensity, massDensity, 100, 96.3, 22);
        FunctionCallable functionCallable = (double x) -> mixture.abv(x);

        Solver solver = new Solver();
        double initialGuess = 100;
        double waterQuantity = 0;

        try {
            waterQuantity = solver.solve(functionCallable, 24.51, initialGuess);
        } catch (SolverException e) {
            e.printStackTrace();
        }

        System.out.println("Water = " + waterQuantity);

        assertEquals(300, waterQuantity, 0.1);
    }

    @Test
    public void testCalcWaterToAdd() {
        double waterQuantity = Calc.waterToAdd(100, 96.3, 22, 24.51);
        System.out.println("Water = " + waterQuantity);
        assertEquals(300, waterQuantity, 0.1);
    }

    @Test
    public void testCalc() {
        double waterQuantity = Calc.waterToAdd(200, 80, 22, 40);
        System.out.println("Water = " + waterQuantity);
        assertEquals(207.4, waterQuantity, 0.1);
    }

    @Test
    public void testMixtureAbv() {
        EthanolWaterMixture newMixture = new EthanolWaterMixture(100, 96.3, 22);
        abv = newMixture.abv(300);
        System.out.println("ABV = " + abv);
        assertEquals(24.51, abv, 0.1);
    }

}
