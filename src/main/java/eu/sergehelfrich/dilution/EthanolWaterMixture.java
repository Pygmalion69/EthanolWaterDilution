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

/**
 *
 * @author helfrich
 */
public class EthanolWaterMixture {

    private DensityByTemperature volumeDensity;
    private DensityByTemperature massDensity;
    private double initialQuantity;
    private double abv1;
    private double t;

    /**
     *
     * @param volumeDensity
     * @param massDensity
     * @param initialQuantity
     * @param abv1 initial ABV
     * @param t
     */
    public EthanolWaterMixture(DensityByTemperature volumeDensity, DensityByTemperature massDensity, double initialQuantity, double abv1, double t) {
        this.volumeDensity = volumeDensity;
        this.massDensity = massDensity;
        this.initialQuantity = initialQuantity;
        this.abv1 = abv1;
        this.t = t;
    }
    
    /**
     * 
     * @param initialQuantity
     * @param abv1
     * @param t 
     */
    public EthanolWaterMixture(double initialQuantity, double abv1, double t) {
        this.volumeDensity = DensityByTemperature.getVolumeDensity();
        this.massDensity = DensityByTemperature.getMassDensity();
        this.initialQuantity = initialQuantity;
        this.abv1 = abv1;
        this.t = t;
    }

    /**
     * ABV
     *
     * @param q2 initial water quantity
     * @return
     */
    double abv(double q2) {

        double q1 = initialQuantity;

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
        double abv = ro / 0.78927 * p;

        return abv;
    }

}
