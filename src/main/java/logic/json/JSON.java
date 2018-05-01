package logic.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JSON {

    private Map<Double, Integer> allResult;
    private int mostLikelyResult;

    public JSON(Map<Double, Integer> allResult, int mostLikelyResult) {
        this.allResult = allResult;
        this.mostLikelyResult = mostLikelyResult;
    }

    private JSONObject toJSONFormat() {
        var mainJSON = new JSONObject();
        var allResultArray = new JSONArray();

        for (var entry : allResult.entrySet()) {
            allResultArray.put(new JSONObject().put(String.valueOf(entry.getValue()), entry.getKey()));
        }
        mainJSON.put("allResult", allResultArray);
        mainJSON.put("mostLikelyResult", mostLikelyResult);
        return mainJSON;
    }

    public void saveJSON(String path) throws IOException {
        var writer = new FileWriter(new File(path));
        writer.write(toJSONFormat().toString());
        writer.close();
    }

}
