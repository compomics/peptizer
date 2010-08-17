package com.compomics.peptizer.util.datatools.implementations.xtandem;

import com.compomics.peptizer.util.datatools.interfaces.PeptizerProteinHit;
import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import de.proteinms.xtandemparser.xtandem.Peptide;
import de.proteinms.xtandemparser.xtandem.Protein;

/**
 * Created by IntelliJ IDEA.
 * User: Thilo Muth
 * Date: 21.09.2009
 * Time: 16:32:31
 * To change this template use File | Settings | File Templates.
 */
public class XTandemProteinHit implements PeptizerProteinHit {
    private final SearchEngineEnum iSearchEngineEnum = SearchEngineEnum.XTandem;
    private Protein iProtein;
    private Peptide iPeptide;

    public XTandemProteinHit() {

    }

    public XTandemProteinHit(Protein aProtein, Peptide aPeptide) {
        iProtein = aProtein;
        iPeptide = aPeptide;
    }

    public String getAccession() {
        return iProtein.getLabel();
    }

    public SearchEngineEnum getSearchEngineEnum() {
        return iSearchEngineEnum;
    }

    public Integer getStart() {
        return iPeptide.getDomainStart();
    }

    public Integer getEnd() {
        return iPeptide.getDomainEnd();
    }
}
