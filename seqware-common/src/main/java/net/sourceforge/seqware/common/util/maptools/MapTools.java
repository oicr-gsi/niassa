package net.sourceforge.seqware.common.util.maptools;

import static net.sourceforge.seqware.common.util.Rethrow.rethrow;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

import net.sourceforge.seqware.common.model.WorkflowRunParam;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>
 * MapTools class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class MapTools {

	/**
	 * <p>
	 * ini2Map.
	 * </p>
	 * 
	 * @param iniFile
	 *            a {@link java.lang.String} object.
	 * @param hm
	 *            a {@link java.util.Map} object.
	 */
	public static void ini2Map(String iniFile, Map<String, String> hm) {
		ini2Map(iniFile, hm, false);
	}

	/**
	 * <p>
	 * ini2Map.
	 * </p>
	 * 
	 * @param iniFile
	 *            a {@link java.lang.String} object.
	 * @param hm
	 *            a {@link java.util.Map} object.
	 * @param keyToUpper
	 *            a boolean.
	 */
	public static void ini2Map(String iniFile, Map<String, String> hm, boolean keyToUpper) {
		// Load config ini from disk
		FileInputStream iStream = null;
		try {
			iStream = new FileInputStream(iniFile);
			ini2Map(iStream, hm, keyToUpper);
		} catch (Exception e) {
			throw rethrow(e);
		} finally {
			IOUtils.closeQuietly(iStream);
		}
	}

	/**
	 * <p>
	 * ini2Map.
	 * </p>
	 * 
	 * @param iniFile
	 *            a {@link java.io.InputStream} object.
	 * @param hm
	 *            a {@link java.util.Map} object.
	 * @param keyToUpper
	 *            a boolean.
	 * @throws java.io.IOException
	 *             if any.
	 */
	public static void ini2Map(InputStream iniFile, Map<String, String> hm, boolean keyToUpper) throws IOException {
		// Load config ini from stream
		Properties config = new Properties();
		config.load(iniFile);

		// Convert to hashmap
		Enumeration<?> en = config.propertyNames();

		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			if (keyToUpper) {
				hm.put(key.toUpperCase(), config.getProperty(key));
			} else {
				hm.put(key, config.getProperty(key));
			}
		}
	}

	/**
	 * Method to getValues all "--key=value" or "--key value" parameters and add
	 * them to hashmap
	 * 
	 * @param args
	 *            an array of {@link java.lang.String} objects.
	 * @param hm
	 *            a {@link java.util.Map} object.
	 */
	public static void cli2Map(String[] args, Map<String, String> hm) {
		if (args == null || hm == null) {
			Log.info("cliMap input NULL");
			return;
		}
		// Parse command line arguments for --key=value
		String currKey = "";
		for (String arg : args) {
			Log.info("CURR KEY: " + arg);
			// If it starts with --, try to split on =
			if (arg.startsWith("--")) {
				String[] split = arg.split("\\=");
				// If had =, turn key into args
				if (split.length == 2) {
					// Strip starting --
					hm.put(split[0].substring(2), split[1]);
					currKey = "";
				} else {
					Log.info("FOUND KEY " + currKey);
					currKey = arg.substring(2);
				}
			} else {
				if (!"".equals(currKey)) {
					Log.info("PUTTING KEY VALUE " + currKey + " " + arg);
					hm.put(currKey, arg);
				}
			}
		}
	}

	/*
	 * Iterate through a Map and change all Strings to ints where possible
	 */
	/**
	 * <p>
	 * mapString2Int.
	 * </p>
	 * 
	 * @param map
	 *            a {@link java.util.Map} object.
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, Object> mapString2Int(Map<?, ?> map) {
		Map<String, Object> result = new HashMap<>();
		Iterator<?> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();

			// Try to convert string to number
			try {
				Long number = Long.parseLong((String) map.get(key));
				result.put(key, number);
			} catch (NumberFormatException e) {
				// Ignore exception, caust just means it is a string
				result.put(key, map.get(key));
			}
		}
		return (result);
	}

	public static final String VAR_RANDOM = "sqw.random";
	public static final String VAR_DATE = "sqw.date";
	public static final String VAR_DATETIME = "sqw.datetime";
	public static final String VAR_TIMESTAMP = "sqw.timestamp";
	public static final String VAR_UUID = "sqw.uuid";
	public static final String VAR_BUNDLE_DIR = "sqw.bundle-dir";
	public static final String LEGACY_VAR_RANDOM = "random";
	public static final String LEGACY_VAR_DATE = "date";
	public static final String LEGACY_VAR_BUNDLE_DIR = "workflow_bundle_dir";
	public static final String VAR_WORKFLOW_VERSION = "sqw.bundle-seqware-version";

	public static void provideBundleVersion(Map<String, String> m, String bundleVersion) {
		m.put(VAR_WORKFLOW_VERSION, bundleVersion);
	}

	public static void provideBundleDir(Map<String, String> m, String bundleDir) {
		m.put(VAR_BUNDLE_DIR, bundleDir);
		m.put(LEGACY_VAR_BUNDLE_DIR, bundleDir);
	}

	public static Map<String, String> providedMap(String bundleDir, String bundleSeqwareVersion) {
		Map<String, String> m = new HashMap<>();
		provideBundleDir(m, bundleDir);
		provideBundleVersion(m, bundleSeqwareVersion);
		return m;
	}

	public static String generatedValue(String key) {
		if (key.equals(VAR_RANDOM))
			return String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
		if (key.equals(VAR_DATE))
			return DatatypeConverter.printDate(Calendar.getInstance());
		if (key.equals(VAR_DATETIME))
			return DatatypeConverter.printDateTime(Calendar.getInstance());
		if (key.equals(VAR_TIMESTAMP))
			return String.valueOf(System.currentTimeMillis());
		if (key.equals(VAR_UUID))
			return UUID.randomUUID().toString();

		if (key.equals(LEGACY_VAR_RANDOM)) {
			Log.warn(String.format("Variable '%s' is deprecated. Please use '%s' instead.", LEGACY_VAR_RANDOM,
					VAR_RANDOM));
			return String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
		}
		if (key.equals(LEGACY_VAR_DATE)) {
			Log.warn(String.format("Variable '%s' is deprecated. Please use '%s' instead.", LEGACY_VAR_DATE, VAR_DATE));
			return DatatypeConverter.printDate(Calendar.getInstance());
		}

		return null;
	}

	private static final Pattern VAR = Pattern.compile("\\$\\{([^\\}]*)\\}");

	public static Map<String, String> expandVariables(Map<String, String> raw) {
		return expandVariables(raw, null);
	}

	public static Map<String, String> expandVariables(Map<String, String> raw, Map<String, String> provided) {
		return expandVariables(raw, provided, false);
	}

	public static Map<String, String> expandVariables(Map<String, String> raw, Map<String, String> provided,
			boolean allowMissingVars) {
		raw = new HashMap<>(raw); // don't mess with someone else's data structure
		Map<String, String> exp = new HashMap<>();

		int prevCount;
		do {
			prevCount = raw.size();
			Iterator<Map.Entry<String, String>> iter = raw.entrySet().iterator();
			entries: while (iter.hasNext()) {
				Map.Entry<String, String> e = iter.next();
				Matcher m = VAR.matcher(e.getValue());
				if (m.find()) {
					// this value has variables
					StringBuffer sb = new StringBuffer();
					do {
						String key = m.group(1);
						String val = exp.get(key);
						if (val == null && provided != null)
							val = provided.get(key);
						if (val == null)
							val = generatedValue(key);

						if (val != null) {
							// found substitution, replace and then look for more
							m.appendReplacement(sb, val);
						} else {
							// we don't yet have all the substitutions, skip this entry for now
							continue entries;
						}
					} while (m.find());
					// done substituting the variables
					m.appendTail(sb);
					exp.put(e.getKey(), sb.toString());
					iter.remove();
				} else {
					// no variables, move the entry
					exp.put(e.getKey(), e.getValue());
					iter.remove();
				}
			}
			// exit when nothing left to convert or no incremental improvement
		} while (0 < raw.size() && raw.size() < prevCount);

		if (!allowMissingVars && raw.size() > 0) {
			StringBuilder sb = new StringBuilder("Could not satisfy variable substitution:");
			for (Map.Entry<String, String> e : raw.entrySet()) {
				sb.append("\n");
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
			}
			throw new RuntimeException(sb.toString());
		}

		return exp;
	}

	/**
	 * <p>
	 * iniString2Map.
	 * </p>
	 * 
	 * @param iniString
	 *            a {@link java.lang.String} object.
	 * @return a {@link java.util.Map} object.
	 */
	public static Map<String, String> iniString2Map(String iniString) {
		try {
			Properties p = new Properties();
			p.load(new ByteArrayInputStream(iniString.getBytes()));
			return p.entrySet().stream()//
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
		} catch (IOException e1) {
			throw rethrow(e1);
		}
	}

	/**
	 * Convert what looks like a map of ini key-value pairs into a SortedSet of
	 * WorkflowRunParams
	 * 
	 * @param map
	 * @return
	 * @throws NumberFormatException
	 */
	public static SortedSet<WorkflowRunParam> createWorkflowRunParameters(HashMap<String, String> map)
			throws NumberFormatException {
		SortedSet<WorkflowRunParam> runParams = new TreeSet<>();
		for (String str : map.keySet()) {
			if (map.get(str) != null) {
				Log.info(str + " " + map.get(str));
				if (str.equals(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey())
						|| str.equals(ReservedIniKeys.PARENT_ACCESSION.getKey())
						|| str.equals(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey())) {
					String[] pAcc = map.get(str).split(",");
					for (String parent : pAcc) {
						WorkflowRunParam wrp = new WorkflowRunParam();
						wrp.setKey(str);
						wrp.setValue(parent);
						wrp.setParentProcessingAccession(Integer.parseInt(parent));
						wrp.setType("text");
						runParams.add(wrp);
					}
				} else {
					if (str.trim().isEmpty() && map.get(str).trim().isEmpty()) {
						continue;
					}
					WorkflowRunParam wrp = new WorkflowRunParam();
					wrp.setKey(str);
					wrp.setValue(map.get(str));
					wrp.setType("text");
					runParams.add(wrp);
				}
			} else {
				Log.info("Null: " + str + " " + map.get(str));
			}
		}
		return runParams;
	}
}
