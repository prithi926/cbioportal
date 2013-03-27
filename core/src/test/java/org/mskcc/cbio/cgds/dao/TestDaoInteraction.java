/** Copyright (c) 2012 Memorial Sloan-Kettering Cancer Center.
**
** This library is free software; you can redistribute it and/or modify it
** under the terms of the GNU Lesser General Public License as published
** by the Free Software Foundation; either version 2.1 of the License, or
** any later version.
**
** This library is distributed in the hope that it will be useful, but
** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
** documentation provided hereunder is on an "as is" basis, and
** Memorial Sloan-Kettering Cancer Center 
** has no obligations to provide maintenance, support,
** updates, enhancements or modifications.  In no event shall
** Memorial Sloan-Kettering Cancer Center
** be liable to any party for direct, indirect, special,
** incidental or consequential damages, including lost profits, arising
** out of the use of this software and its documentation, even if
** Memorial Sloan-Kettering Cancer Center 
** has been advised of the possibility of such damage.  See
** the GNU Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public License
** along with this library; if not, write to the Free Software Foundation,
** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
**/

package org.mskcc.cbio.cgds.dao;

import junit.framework.TestCase;
import org.mskcc.cbio.cgds.dao.DaoException;
import org.mskcc.cbio.cgds.dao.DaoInteraction;
import org.mskcc.cbio.cgds.dao.MySQLbulkLoader;
import org.mskcc.cbio.cgds.scripts.ResetDatabase;
import org.mskcc.cbio.cgds.model.CanonicalGene;
import org.mskcc.cbio.cgds.model.Interaction;

import java.util.ArrayList;

/**
 * JUnit Tests for DaoInteraction.
 */
public class TestDaoInteraction extends TestCase {

    /**
     * Test the DaoInteraction Class.
     *
     * @throws DaoException Database Error
     */
    public void testDaoInteraction() throws DaoException {

        // test with both values of MySQLbulkLoader.isBulkLoad()
        MySQLbulkLoader.bulkLoadOff();
        runTheTest();
        MySQLbulkLoader.bulkLoadOn();
        runTheTest();
    }

    private void runTheTest() throws DaoException{
        ResetDatabase.resetDatabase();
        DaoInteraction daoInteraction = DaoInteraction.getInstance();

        CanonicalGene geneA = new CanonicalGene (672, "BRCA1");
        CanonicalGene geneB = new CanonicalGene (675, "BRCA2");

        int recordsAdded = daoInteraction.addInteraction(geneA, geneB, "pp", "HPRD",
                "Y2H", "12344");
        assertEquals (1, recordsAdded);

        recordsAdded = daoInteraction.addInteraction(geneA, geneB, "state_change", "REACTOME",
                "in-vivo", "12355");
        assertEquals (1, recordsAdded);

        // if bulkLoading, execute LOAD FILE
        if( MySQLbulkLoader.isBulkLoad()){
            daoInteraction.flushToDatabase();
        }

        //  Get the interactions back
        ArrayList<Interaction> interactionList = daoInteraction.getAllInteractions();
        assertEquals (2, interactionList.size());

        Interaction interaction1 = interactionList.get(0);
        assertEquals (672, interaction1.getGeneA());
        assertEquals (675, interaction1.getGeneB());
        assertEquals ("pp", interaction1.getInteractionType());
        assertEquals ("HPRD", interaction1.getSource());
        assertEquals ("Y2H", interaction1.getExperimentTypes());
        assertEquals ("12344", interaction1.getPmids());

        Interaction interaction2 = interactionList.get(1);
        assertEquals (672, interaction2.getGeneA());
        assertEquals (675, interaction2.getGeneB());
        assertEquals ("state_change", interaction2.getInteractionType());
        assertEquals ("REACTOME", interaction2.getSource());
        assertEquals ("in-vivo", interaction2.getExperimentTypes());
        assertEquals ("12355", interaction2.getPmids());

        //  Get the Interactions back by a direct query
        interactionList = daoInteraction.getInteractions(geneA);
        assertEquals (2, interactionList.size());
    }

}