package com.compomics.peptizer.util.datatools;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: Apr 9, 2010
 * Time: 1:39:23 PM
 * This class will list the search engines supporting a peptide hit.
 */
public class Advocate implements Serializable {

    private ArrayList<SearchEngineEnum> advocates = new ArrayList();

    public Advocate(ArrayList<SearchEngineEnum> advocates) {
        this.advocates = advocates;
    }

    public Advocate(SearchEngineEnum advocate) {
        advocates.add(advocate);
    }

    public void addAdvocate(SearchEngineEnum advocate) {
        advocates.add(advocate);
    }

    public void addAdvocate(Advocate advocate) {
        this.advocates.addAll(advocate.getAdvocates());
    }

    public ArrayList<SearchEngineEnum> getAdvocates() {
        return advocates;
    }

    public String getName() {
        String result = "" + advocates.get(0).getName().charAt(0);
        for (int i = 1; i < advocates.size(); i++) {
            result += " - " + advocates.get(i).getName().charAt(0);
        }
        return result;
    }

    public boolean isSameAs(Advocate anotherAdvocate) {
        if (advocates.size() != anotherAdvocate.getAdvocates().size()) {
            return false;
        }
        for (int i = 0; i < advocates.size(); i++) {
            if (!anotherAdvocate.getAdvocates().contains(advocates.get(i))) {
                return false;
            }
        }
        return true;
    }
}
