package com.compomics.peptizer.util.datatools;

import com.compomics.peptizer.util.enumerator.SearchEngineEnum;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: Marc
 * Date: Apr 9, 2010
 * Time: 1:39:23 PM
 * This class will list the search engines supporting a peptide hit.
 */
public class Advocate implements Serializable {
	// Class specific log4j logger for Advocate instances.
	 private static Logger logger = Logger.getLogger(Advocate.class);

    private HashMap<SearchEngineEnum, Integer> advocates = new HashMap();

    public Advocate(HashMap<SearchEngineEnum, Integer> advocates) {
        this.advocates = advocates;
    }

    public Advocate(SearchEngineEnum advocate, int rank) {
        advocates.put(advocate, rank);
    }

    public void addAdvocate(SearchEngineEnum advocate, int rank) {
        advocates.put(advocate, rank);
    }

    public void addAdvocate(Advocate advocate) {
        this.advocates.putAll(advocate.getAdvocates());
    }

    public ArrayList<SearchEngineEnum> getAdvocatesList() {
        return new ArrayList(advocates.keySet());
    }

    private HashMap<SearchEngineEnum, Integer> getAdvocates() {
        return advocates;
    }

    public String getName() {
        Iterator<SearchEngineEnum> searchEnginesIterator = advocates.keySet().iterator();
        String result = "";
        if (advocates.size() > 1) {
            result += "[";
        }
        SearchEngineEnum currentSearchEngine = searchEnginesIterator.next();
        result += currentSearchEngine.getInitial() + "" + advocates.get(currentSearchEngine);
        while (searchEnginesIterator.hasNext()) {
            currentSearchEngine = searchEnginesIterator.next();
            result += " - " + currentSearchEngine.getInitial() + advocates.get(currentSearchEngine);
        }
        if (advocates.size() > 1) {
            result += "]";
        }
        return result;
    }

    public boolean sameCategoryAs(Advocate anotherAdvocate) {
        if (advocates.size() != anotherAdvocate.getAdvocates().size()) {
            return false;
        }
        for (int i = 0; i < advocates.size(); i++) {
            if (!anotherAdvocate.getAdvocatesList().contains(getAdvocatesList().get(i))) {
                return false;
            }
        }
        return true;
    }

    public int getRank(SearchEngineEnum searchEngine) {
        return advocates.get(searchEngine);
    }
}
