package com.compomics.peptizer.util.enumerator;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: 22.04.2009
 * Time: 10:24:38
 * To change this template use File | Settings | File Templates.
 */
public enum IonTypeEnum {
    // Nitrium losses on a, c and z ions are not implemented as not likely to be found outside the noise.
    aH2O, aNH3, a, b, bH2O, bNH3, c, x, yH2O, yNH3, y, z, MH, MHH2O, MHNH3, immonium,
    immoniumA, immoniumC, immoniumD, immoniumE, immoniumF, immoniumG, immoniumH, immoniumI, immoniumK, immoniumL, immoniumM, immoniumN, immoniumP, immoniumQ, immoniumR, immoniumS, immoniumT, immoniumV, immoniumW, immoniumY,
    other;

    public String getName() {
        switch (this) {
            case a:
                return "a";
            case aH2O:
                return "a-H2O";
            case aNH3:
                return "a-NH3";
            case b:
                return "b";
            case bH2O:
                return "b-H2O";
            case bNH3:
                return "b-NH3";
            case c:
                return "c";
            case x:
                return "x";
            case y:
                return "y";
            case yH2O:
                return "y-H2O";
            case yNH3:
                return "y-NH3";
            case z:
                return "z";
            case immonium:
                return "Immonium";
            case immoniumA:
                return "immA";
            case immoniumC:
                return "immC";
            case immoniumD:
                return "immD";
            case immoniumE:
                return "immE";
            case immoniumF:
                return "immF";
            case immoniumG:
                return "immG";
            case immoniumH:
                return "immH";
            case immoniumI:
                return "immI";
            case immoniumK:
                return "immK";
            case immoniumL:
                return "immL";
            case immoniumM:
                return "immM";
            case immoniumN:
                return "immN";
            case immoniumP:
                return "immP";
            case immoniumQ:
                return "immQ";
            case immoniumR:
                return "immR";
            case immoniumS:
                return "immS";
            case immoniumT:
                return "immT";
            case immoniumV:
                return "immV";
            case immoniumW:
                return "immW";
            case immoniumY:
                return "immY";
            case MH:
                return "MH";
            case MHH2O:
                return "MH-H2O";
            case MHNH3:
                return "MH-NH3";
            default:
                return "unknown";
        }
    }
}
