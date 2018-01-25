package net.sourceforge.seqware.common.util.jsontools;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;
import com.sdicons.json.validator.JSONValidator;
import com.sdicons.json.validator.ValidationException;
import com.sdicons.json.validator.Validator;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON Helper Class Contains methods related to parsing/validating of JSON which one may find useful
 * 
 * @author Raunaq Suri
 * 
 */
public class JSONHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONHelper.class);

    /**
     * Validates a json file against a schema to see if the file is valid
     * 
     * @param schemaJSON
     *            the json file which contains the schema
     * @param dataJSON
     *            the json file which you wish to validate
     * @return Whether or not the JSON is valid or not
     */
    public boolean isJSONValid(InputStream schemaJSON, InputStream dataJSON) {

        // Loads the schema and the data
        JSONParser schemaParser = new JSONParser(schemaJSON);
        JSONParser dataParser = new JSONParser(dataJSON);
        try {

            // Setup
            JSONObject validatorObj = (JSONObject) schemaParser.nextValue();

            Validator validator = new JSONValidator(validatorObj);

            JSONValue data = dataParser.nextValue();

            // Validates the data
            validator.validate(data);

        } catch (TokenStreamException ex) {
            LOGGER.error("JSONHelper.isJSONValid Token stream exception");
            return false;

        } catch (RecognitionException ex) {
            LOGGER.error("JSONHelper.isJSONValid Not recognized");
            return false;
        } catch (ValidationException ex) {
            LOGGER.error("JSONHelper.isJSONValid Not properly validated");
            return false;
        }

        // Returns true if there are no errors
        return true;
    }
}
