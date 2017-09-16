package com.denweisenseel.com.backend.tools;


import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.data.Node;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * Created by denwe on 26.07.2017.
 */

public class GraphBuilder {

    private static ArrayList<Node> graphRep = null;
    private static String graph = "{\n" +
            "  \"type\": \"Map\",\n" +
            "  \"nodes\": {\n" +
            "    \"type\": \"NodeCollection\",\n" +
            "    \"collection\": [\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 1,\n" +
            "        \"coordinates\": [\n" +
            "          7.000090777873993,\n" +
            "          49.23600880958809\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 2,\n" +
            "        \"coordinates\": [\n" +
            "          7.00039118528366,\n" +
            "          49.23607010624798\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 3,\n" +
            "        \"coordinates\": [\n" +
            "          7.00029194355011,\n" +
            "          49.23625399577125\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 4,\n" +
            "        \"coordinates\": [\n" +
            "          6.998722851276398,\n" +
            "          49.23636257783494\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 5,\n" +
            "        \"coordinates\": [\n" +
            "          6.998744308948517,\n" +
            "          49.23608411690241\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 6,\n" +
            "        \"coordinates\": [\n" +
            "          6.999084949493408,\n" +
            "          49.23494924101662\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 7,\n" +
            "        \"coordinates\": [\n" +
            "          6.999350488185883,\n" +
            "          49.23492647317804\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 8,\n" +
            "        \"coordinates\": [\n" +
            "          6.9994765520095825,\n" +
            "          49.23551142816355\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 9,\n" +
            "        \"coordinates\": [\n" +
            "          7.000192701816559,\n" +
            "          49.234842407221585\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 10,\n" +
            "        \"coordinates\": [\n" +
            "          7.000855207443237,\n" +
            "          49.23508759919465\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 11,\n" +
            "        \"coordinates\": [\n" +
            "          7.001946866512299,\n" +
            "          49.23540984965112\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 12,\n" +
            "        \"coordinates\": [\n" +
            "          7.00165718793869,\n" +
            "          49.23629252491794\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 13,\n" +
            "        \"coordinates\": [\n" +
            "          7.001579403877258,\n" +
            "          49.236669058178606\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 14,\n" +
            "        \"coordinates\": [\n" +
            "          6.998881101608276,\n" +
            "          49.23680566023336\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 15,\n" +
            "        \"coordinates\": [\n" +
            "          6.997926235198974,\n" +
            "          49.23703683208076\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 16,\n" +
            "        \"coordinates\": [\n" +
            "          6.997443437576294,\n" +
            "          49.236164678088294\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 17,\n" +
            "        \"coordinates\": [\n" +
            "          6.996984779834747,\n" +
            "          49.23531877908305\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 18,\n" +
            "        \"coordinates\": [\n" +
            "          6.999514102935791,\n" +
            "          49.234306483385964\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 19,\n" +
            "        \"coordinates\": [\n" +
            "          7.000117599964142,\n" +
            "          49.23436603076597\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 20,\n" +
            "        \"coordinates\": [\n" +
            "          7.000109553337097,\n" +
            "          49.23392292648283\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 21,\n" +
            "        \"coordinates\": [\n" +
            "          7.000506520271301,\n" +
            "          49.2338406102296\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 22,\n" +
            "        \"coordinates\": [\n" +
            "          7.000605762004852,\n" +
            "          49.233959706041\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 23,\n" +
            "        \"coordinates\": [\n" +
            "          7.000755965709686,\n" +
            "          49.234320494540654\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 24,\n" +
            "        \"coordinates\": [\n" +
            "          7.001335322856903,\n" +
            "          49.23386863279931\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 25,\n" +
            "        \"coordinates\": [\n" +
            "          7.001509666442871,\n" +
            "          49.234241681743846\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 26,\n" +
            "        \"coordinates\": [\n" +
            "          7.002078294754027,\n" +
            "          49.23407705016257\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 27,\n" +
            "        \"coordinates\": [\n" +
            "          7.003440856933594,\n" +
            "          49.23485466684914\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 28,\n" +
            "        \"coordinates\": [\n" +
            "          7.0030975341796875,\n" +
            "          49.2354921632893\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 29,\n" +
            "        \"coordinates\": [\n" +
            "          7.003057301044464,\n" +
            "          49.236887971545485\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 30,\n" +
            "        \"coordinates\": [\n" +
            "          7.001625001430511,\n" +
            "          49.23731178506628\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 31,\n" +
            "        \"coordinates\": [\n" +
            "          6.999267339706421,\n" +
            "          49.2376357722886\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 32,\n" +
            "        \"coordinates\": [\n" +
            "          6.998441219329833,\n" +
            "          49.237852930102044\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 33,\n" +
            "        \"coordinates\": [\n" +
            "          6.997647285461426,\n" +
            "          49.23812612728535\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 34,\n" +
            "        \"coordinates\": [\n" +
            "          6.997014284133911,\n" +
            "          49.237313536354264\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Node\",\n" +
            "        \"id\": 35,\n" +
            "        \"coordinates\": [\n" +
            "          6.996338367462158,\n" +
            "          49.23620670995922\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"relationen\": {\n" +
            "    \"type\": \"RelationCollection\",\n" +
            "    \"collection\": [\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 1,\n" +
            "        \"neighbours\": [\n" +
            "          2,\n" +
            "          5,\n" +
            "          8,\n" +
            "          10\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 2,\n" +
            "        \"neighbours\": [\n" +
            "          1,\n" +
            "          3,\n" +
            "          12\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 3,\n" +
            "        \"neighbours\": [\n" +
            "          2,\n" +
            "          4\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 4,\n" +
            "        \"neighbours\": [\n" +
            "          3,\n" +
            "          5,\n" +
            "          14\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 5,\n" +
            "        \"neighbours\": [\n" +
            "          1,\n" +
            "          4,\n" +
            "          6,\n" +
            "          16\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 6,\n" +
            "        \"neighbours\": [\n" +
            "          5,\n" +
            "          7,\n" +
            "          17\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 7,\n" +
            "        \"neighbours\": [\n" +
            "          6,\n" +
            "          8,\n" +
            "          18\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 8,\n" +
            "        \"neighbours\": [\n" +
            "          1,\n" +
            "          7,\n" +
            "          9\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 9,\n" +
            "        \"neighbours\": [\n" +
            "          8,\n" +
            "          10,\n" +
            "          19\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 10,\n" +
            "        \"neighbours\": [\n" +
            "          9,\n" +
            "          1,\n" +
            "          11\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 11,\n" +
            "        \"neighbours\": [\n" +
            "          10,\n" +
            "          12,\n" +
            "          25,\n" +
            "          28\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 12,\n" +
            "        \"neighbours\": [\n" +
            "          11,\n" +
            "          2,\n" +
            "          13,\n" +
            "          29\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 13,\n" +
            "        \"neighbours\": [\n" +
            "          12,\n" +
            "          30,\n" +
            "          14\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 14,\n" +
            "        \"neighbours\": [\n" +
            "          13,\n" +
            "          33,\n" +
            "          4,\n" +
            "          15\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 15,\n" +
            "        \"neighbours\": [\n" +
            "          14,\n" +
            "          16,\n" +
            "          32,\n" +
            "          34\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 16,\n" +
            "        \"neighbours\": [\n" +
            "          15,\n" +
            "          5,\n" +
            "          17,\n" +
            "          35\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 17,\n" +
            "        \"neighbours\": [\n" +
            "          16,\n" +
            "          35,\n" +
            "          6\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 18,\n" +
            "        \"neighbours\": [\n" +
            "          7,\n" +
            "          19\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 19,\n" +
            "        \"neighbours\": [\n" +
            "          18,\n" +
            "          20,\n" +
            "          23,\n" +
            "          9\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 20,\n" +
            "        \"neighbours\": [\n" +
            "          21,\n" +
            "          19\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 21,\n" +
            "        \"neighbours\": [\n" +
            "          20,\n" +
            "          22\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 22,\n" +
            "        \"neighbours\": [\n" +
            "          21,\n" +
            "          23,\n" +
            "          24\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 23,\n" +
            "        \"neighbours\": [\n" +
            "          25,\n" +
            "          22,\n" +
            "          19\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 24,\n" +
            "        \"neighbours\": [\n" +
            "          25,\n" +
            "          22\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 25,\n" +
            "        \"neighbours\": [\n" +
            "          24,\n" +
            "          23,\n" +
            "          26,\n" +
            "          11\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 26,\n" +
            "        \"neighbours\": [\n" +
            "          25,\n" +
            "          27\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 27,\n" +
            "        \"neighbours\": [\n" +
            "          26,\n" +
            "          28\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 28,\n" +
            "        \"neighbours\": [\n" +
            "          27,\n" +
            "          11,\n" +
            "          29\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 29,\n" +
            "        \"neighbours\": [\n" +
            "          28,\n" +
            "          12,\n" +
            "          30\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 30,\n" +
            "        \"neighbours\": [\n" +
            "          29,\n" +
            "          13,\n" +
            "          31\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 31,\n" +
            "        \"neighbours\": [\n" +
            "          30,\n" +
            "          14,\n" +
            "          32\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 32,\n" +
            "        \"neighbours\": [\n" +
            "          31,\n" +
            "          15,\n" +
            "          33\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 33,\n" +
            "        \"neighbours\": [\n" +
            "          32,\n" +
            "          34\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 34,\n" +
            "        \"neighbours\": [\n" +
            "          33,\n" +
            "          15,\n" +
            "          35\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"type\": \"Relation\",\n" +
            "        \"self\": 35,\n" +
            "        \"neighbours\": [\n" +
            "          34,\n" +
            "          16,\n" +
            "          17\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    public static ArrayList<Node> getGraph() {
        if (graphRep != null) return graphRep;
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(graph).getAsJsonObject();


        ArrayList<Node> nodes = new ArrayList();
        JsonArray nodeArray = o.getAsJsonObject("nodes").getAsJsonArray("collection");
        for (int i = 0; i < nodeArray.size(); i++) {
            JsonObject eq = nodeArray.get(i).getAsJsonObject();
            int id = eq.get("id").getAsInt();
            id = id - 1;
            JsonArray ar = eq.getAsJsonArray("coordinates");
            double latitude = ar.get(1).getAsDouble();
            double longitude = ar.get(0).getAsDouble();
            Geolocation geolocation = new Geolocation();
            geolocation.setLatitude(latitude);
            geolocation.setLongitude(longitude);
            Node n = new Node(id, geolocation);
            nodes.add(n);
        }


        JsonArray relationArray = o.getAsJsonObject("relationen").getAsJsonArray("collection");
        for (int i = 0; i < relationArray.size(); i++) {
            JsonObject eq = relationArray.get(i).getAsJsonObject();
            int id = eq.get("self").getAsInt();
            JsonArray ar = eq.getAsJsonArray("neighbours");
            for (int k = 0; k < ar.size(); k++) {
                int neightbourId = ar.get(k).getAsInt();
                nodes.get(id - 1).addNeighbour(nodes.get(neightbourId - 1));
            }
        }

        for (Node n : nodes) {
            System.out.print(n.getId() + ":");
            for (Node ne : n.getNeighbours()) {
                System.out.print(ne.getId() + " | ");
            }
            System.out.println();
        }

        graphRep = nodes;
        return nodes;
    }



}
