/*
 * Copyright (C) 2012 SeqWare
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
package com.github.seqware.queryengine.tutorial;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.workers.ImportConstants;
import com.github.seqware.queryengine.util.SGID;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a quick and sample application built on top of our API, created during the August 9th Hackathon. 
 * It demonstrates query restrictions and exporting.
 * Based on VCFDumper. This will dump VCF files given a FeatureSet that was originally imported from
 * a VCF file.
 *
 * @author dyuen
 */
public class BrianTest {

    private String[] args;

    public static void main(String[] args) {
        BrianTest dumper = new BrianTest(args);
        dumper.export();
    }

    public void export() {

        if (args.length < 1 || args.length > 2) {
            System.err.println(args.length + " arguments found");
            System.out.println("BrianTest <featureSetID> [outputFile]");
            System.exit(-1);
        }

        // parse a SGID from a String representation, we need a more elegant solution here
        SGID sgid = Utility.parseSGID(args[0]);
        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(sgid, FeatureSet.class);

        // if this featureSet does not exist
        if (fSet == null) {
            System.out.println("referenceID not found");
            System.exit(-2);
        }

      // get a FeatureSet from the back-end
        String structure = "chr16";
        int start = 0;
        int stop = Integer.MAX_VALUE;
        QueryFuture<FeatureSet> future = SWQEFactory.getQueryInterface().getFeaturesByRange(0, fSet, QueryInterface.Location.INCLUDES, structure, start, stop);
	FeatureSet resultSet = future.get();
        QueryFuture<FeatureSet> featuresByTag = SWQEFactory.getQueryInterface().getFeaturesByTag(0, resultSet, "nonsynonymous", null, null);



        BufferedWriter outputStream = null;
        try {

            if (args.length == 2) {
                outputStream = new BufferedWriter(new FileWriter(args[1]));
            } else {
                outputStream = new BufferedWriter(new OutputStreamWriter(System.out));
            }
            outputStream.append("#CHROM	POS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n");
            //SeqWareIterable<FeatureSet> featureSets = SWQEFactory.getQueryInterface().getFeatureSets();
            boolean caughtNonVCF = false;
            for (Feature feature : featuresByTag.get()) {
                outputStream.append(feature.getSeqid() + "\t" + (feature.getStart() + 1) + "\t");
                if (feature.getTagByKey(ImportConstants.VCF_SECOND_ID) == null) {
                    outputStream.append(".\t");
                } else {
                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_SECOND_ID).getValue().toString() + "\t");
                }
                try {
                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_REFERENCE_BASE).getValue().toString() + "\t");
                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_CALLED_BASE).getValue().toString() + "\t");
                    outputStream.append(feature.getScore() + "\t");
                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_FILTER).getValue().toString() + "\t");
                    outputStream.append(feature.getTagByKey(ImportConstants.VCF_INFO).getValue().toString());
                } catch (NullPointerException npe) {
                    if (!caughtNonVCF) {
                        Logger.getLogger(BrianTest.class.getName()).log(Level.INFO, "VCF exporting non-VCF feature");

                    }
                    // this may occur when exporting Features that were not originally VCF files
                    caughtNonVCF = true;
                }
                outputStream.newLine();
            }
        } // TODO: clearly this should be expanded to include closing database etc 
        catch (Exception e) {
            Logger.getLogger(BrianTest.class.getName()).log(Level.SEVERE, "Exception thrown exporting to file:", e);
            System.exit(-1);
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
                SWQEFactory.getStorage().closeStorage();
            } catch (IOException ex) {
                Logger.getLogger(BrianTest.class.getName()).log(Level.SEVERE, "Exception thrown flushing to file:", ex);
            }
        }
    }

    public BrianTest(String[] args) {
        this.args = args;
    }
}
