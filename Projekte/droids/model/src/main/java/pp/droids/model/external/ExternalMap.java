package pp.droids.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import pp.droids.model.DroidsMap;
import pp.droids.model.DroidsModel;
import pp.droids.model.Item;
import pp.droids.model.VoidVisitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * External representation of a game map. It is used for writing or reading JSON files.
 */
public class ExternalMap {

    /**
     * The width of the map.
     */
    @JsonProperty
    int width;

    /**
     * The height of the map.
     */
    @JsonProperty
    int height;

    /**
     * A list of all external items contained in the map.
     */
    @SuppressWarnings("CanBeFinal")
    @JsonProperty
    List<ExternalItem> items = new ArrayList<>();

    /**
     * Default constructor necessary for Jackson use.
     */
    private ExternalMap() { /* empty */ }

    /**
     * Creates an externalizable map from the specified game map.
     *
     * @param map a game map
     */
    public ExternalMap(DroidsMap map) {
        width = map.getWidth();
        height = map.getHeight();
        final VoidVisitor visitor = new ExternalizerVisitor(map, items);
        map.getDroid().accept(visitor);
        for (Item it : map.getItems())
            if (it != map.getDroid())
                it.accept(visitor);
    }

    /**
     * Writes this externalizable map into a JSON file.
     *
     * @param file a JSON file
     * @throws IOException if any IO error occurs.
     */
    public void writeToFile(File file) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(file, this);
        }
        catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Reads a JSON file and returns the externalizable map stored in the file.
     *
     * @param file a JSON file
     * @return the externalizable map stored in the file
     * @throws IOException if any IO error occurs or if the file doesn't contain an externalizable map.
     */
    public static ExternalMap readFromJsonFile(File file) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(file, ExternalMap.class);
        }
        catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Reads a JSON stream and returns the externalizable map.
     *
     * @param stream a JSON stream
     * @return the externalizable map in the stream
     * @throws IOException if any IO error occurs or if the file doesn't contain an externalizable map.
     */
    public static ExternalMap readFromJsonStream(InputStream stream) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(stream, ExternalMap.class);
        }
        catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Reads a JSON String and returns the externalizable map.
     *
     * @param string a JSON String
     * @return the externalizable map in the String
     * @throws IOException if any IO error occurs or if the file doesn't contain an externalizable map.
     */
    public static ExternalMap readFromJsonString(String string) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(string, ExternalMap.class);
        }
        catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Creates a game map from this externalizable map.
     *
     * @param model the game model that will be used for creating the map and all items.
     * @return a game map.
     */
    public DroidsMap toMap(DroidsModel model) throws IOException {
        final ToModelVisitor visitor = new ToModelVisitor(this, model);
        items.forEach(visitor::accept);
        visitor.connectItems();
        if (visitor.getErrors() != null)
            throw new IOException(visitor.getErrors());
        final DroidsMap map = visitor.getMap();
        // update map to add all registered items
        map.addRegisteredItems();
        return map;
    }

    /**
     * Creates code for generating a game map from this ExternalMap.
     *
     * @return code that creates a game map.
     */
    public String toCode() {
        final ToCodeVisitor visitor = new ToCodeVisitor(this);
        items.forEach(visitor::accept);
        visitor.connectItems();
        return visitor.getCode();
    }
}
