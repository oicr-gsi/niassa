package net.sourceforge.seqware.pipeline.plugins.checkdb;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import io.seqware.pipeline.SqwKeys;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;

import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A database validation tool for your SeqWare metadb
 * 
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class CheckDB extends Plugin {
    private final Logger logger = LoggerFactory.getLogger(CheckDB.class);

    public static final int NUMBER_TO_OUTPUT = 100;

    /**
     * <p>
     * Constructor for HelloWorld.
     * </p>
     */
    public CheckDB() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public final ReturnValue init() {
        try {
            HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
            if (!ConfigTools.isValidDBConnectionParam(settings)) {
                ReturnValue ret = new ReturnValue();
                System.out.println(MetadataFactory.NO_DATABASE_CONFIG);
                ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
                return ret;
            }
        } catch (Exception e) {
            ReturnValue ret = new ReturnValue();
            ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
            return ret;
        }
        return new ReturnValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return new ReturnValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public final ReturnValue do_run() {
        ReturnValue ret = new ReturnValue();
        Collection<CheckDBPluginInterface> plugins = (Collection<CheckDBPluginInterface>) Lookup.getDefault().lookupAll(
                CheckDBPluginInterface.class);
        Map<CheckDBPluginInterface, SortedMap<CheckDBPluginInterface.Level, Set<String>>> resultMap = new HashMap<>();
        for (CheckDBPluginInterface plugin : plugins) {

            SortedMap<Level, Set<String>> result = new TreeMap<>();
            for (Level l : CheckDBPluginInterface.Level.values()) {
                result.put(l, new HashSet<String>());
            }
            logger.info("Running " + plugin.getClass().getSimpleName());
            try {
                plugin.check(new SelectQueryRunner(DBAccess.get()), result);
                resultMap.put(plugin, result);
            } catch (Exception e) {
                logger.error("Plugin " + plugin.getClass().getSimpleName() + " died", e);
                if (!result.containsKey(Level.SEVERE)) {
                    // defensive check in case plugin author decided to corrupt the map
                    result.put(Level.SEVERE, new HashSet<String>());
                }
                resultMap.get(plugin).get(Level.SEVERE).add("Plugin threw an exception and died");
                resultMap.put(plugin, result);
            }
        }

        try {
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer;
            File reportFile;
            FileOutputStream fileOutputStream;

            reportFile = File.createTempFile("report", ".html");
            fileOutputStream = new FileOutputStream(reportFile);
            writer = xmlOutputFactory.createXMLStreamWriter(fileOutputStream);
            writer.writeStartDocument("utf-8", "1.0");
            writer.writeStartElement("html");
            writer.writeStartElement("head");
            writer.writeEndElement();
            writer.writeStartElement("body");
            writer.writeStartElement("h1");
            writer.writeCharacters(this.getClass().getSimpleName() + " Report");
            writer.writeEndElement();
            for (Entry<CheckDBPluginInterface, SortedMap<CheckDBPluginInterface.Level, Set<String>>> pluginEntry : resultMap.entrySet()) {
                writer.writeStartElement("h2");
                writer.writeCharacters(pluginEntry.getKey().getClass().getSimpleName());
                writer.writeEndElement();
                for (Entry<CheckDBPluginInterface.Level, Set<String>> warning : pluginEntry.getValue().entrySet()) {
                    writer.writeStartElement("h3");
                    writer.writeCharacters(warning.getKey().name());
                    writer.writeEndElement();
                    writer.writeStartElement("ol");

                    //warning.getValue() is html that shouldn't be escaped, need to write directly to outputstream as XMLStreamWriter escapes
                    writer.writeCharacters(""); //needed to stop text from being written into above element
                    writer.flush();
                    for (String entry : warning.getValue()) {
                        fileOutputStream.write("<li>".getBytes(StandardCharsets.UTF_8));
                        fileOutputStream.write(entry.getBytes(StandardCharsets.UTF_8));
                        fileOutputStream.write("</li>".getBytes(StandardCharsets.UTF_8));
                    }
                    fileOutputStream.flush();

                    writer.writeEndElement(); //ol
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();

            System.out.println("Printed report to " + reportFile.getAbsolutePath());
        } catch (XMLStreamException | IOException ex) {
            throw new RuntimeException(ex);
        }
        return ret;
    }

    /**
     * <p>
     * get_description.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    @Override
    public final String get_description() {
        return ("A database validation tool for your SeqWare metadb.");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        CheckDB mp = new CheckDB();
        mp.init();
        List<String> arr = new ArrayList<>();
        mp.setParams(arr);
        mp.parse_parameters();
        ReturnValue doRun = mp.do_run();
        Desktop.getDesktop().browse(new URI(doRun.getUrl()));
    }

    @Override
    public final ReturnValue clean_up() {
        return new ReturnValue();
    }

    /**
     * Convenience method for processing output and appending it to the warning map
     * 
     * @param result
     *            a sorted map where results can be appended
     * @param level
     *            Level of message to create
     * @param description
     *            A description of the sw_accessions to be reported from list
     * @param list
     *            a list of sw_accessions to report
     */
    public static void processOutput(SortedMap<Level, Set<String>> result, Level level, String description, List<Integer> list) {
        if (list.size() > 0) {
            String swRestUrl = ConfigTools.getSettings().get(SqwKeys.SW_REST_URL.getSettingKey());
            Metadata md = MetadataFactory.get(ConfigTools.getSettings());
            Collections.sort(list);
            // shorten list if required
            List<Integer> outputList = new ArrayList<>(list);
            if (list.size() > NUMBER_TO_OUTPUT) {
                outputList = outputList.subList(0, NUMBER_TO_OUTPUT);
            }
            StringBuilder warnings = new StringBuilder();
            warnings.append(description);
            int[] parentAccessions = ArrayUtils.toPrimitive(outputList.toArray(new Integer[outputList.size()]));
            List<Object> parentModels = md.getViaAccessions(parentAccessions);
            // let's try constructing hyperlinks here
            for (int i = 0; i < parentModels.size(); i++) {
                String url = null;
                if (parentModels.get(i) instanceof Experiment) {
                    url = swRestUrl + "/experiments/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof net.sourceforge.seqware.common.model.File) {
                    url = swRestUrl + "/files/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof IUS) {
                    url = swRestUrl + "/ius/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof Lane) {
                    url = swRestUrl + "/lanes/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof Processing) {
                    url = swRestUrl + "/processes/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof Sample) {
                    url = swRestUrl + "/samples/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof SequencerRun) {
                    url = swRestUrl + "/sequencerruns/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof Study) {
                    url = swRestUrl + "/studies/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof WorkflowRun) {
                    url = swRestUrl + "/workflowruns/" + parentAccessions[i];
                } else if (parentModels.get(i) instanceof Workflow) {
                    url = swRestUrl + "/workflows/" + parentAccessions[i];
                }
                if (url != null) {
                    warnings.append(" <a href=\"").append(url).append("\">").append(parentAccessions[i]).append("</a> ");
                } else {
                    warnings.append(parentAccessions[i]);
                }
                if (i != parentModels.size() - 1) {
                    warnings.append(',');
                }
            }
            if (list.size() != outputList.size()) {
                warnings.append(" (Truncated, ").append(list.size()).append(" in total)");
            }
            result.get(level).add(warnings.toString());
        }
    }
}
