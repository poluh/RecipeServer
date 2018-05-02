package logic.json;

import logic.image.Geometry.Point;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JSON {

    private Map<Double, Integer> allResult;
    private String JSONString;
    private int mostLikelyResult;

    public JSON(Map<Double, Integer> allResult, int mostLikelyResult) {
        this.allResult = allResult;
        this.mostLikelyResult = mostLikelyResult;
    }

    public JSON(String JSONString) {
        this.JSONString = JSONString;
    }

    public List<Point> toPoints() throws ParseException {
        var parser = new JSONParser();
        var JSONObject = (JSONObject) parser.parse(JSONString);
        var JSONArray = (JSONArray) JSONObject.get("allPoints");
        var resultPoints = new ArrayList<Point>();
        for (var aJSONArray : (Iterable<JSONObject>) JSONArray) {
            var interiorParser = new JSONParser();
            var interiorJSONObject = (JSONObject) interiorParser.parse(String.valueOf(aJSONArray));
            var x = interiorJSONObject.get("x").toString();
            var y = interiorJSONObject.get("y").toString();
            resultPoints.add(new Point(Integer.valueOf(x), Integer.valueOf(y)));
        }
        return resultPoints;
    }

    private JSONObject toJSONFormat() {
        var mainJSON = new JSONObject();
        var allResultArray = new JSONArray();

        for (var entry : allResult.entrySet()) {
            var resultObj = new JSONObject();
            resultObj.put(entry.getValue(), entry.getKey()) ;
            allResultArray.add(resultObj);
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
