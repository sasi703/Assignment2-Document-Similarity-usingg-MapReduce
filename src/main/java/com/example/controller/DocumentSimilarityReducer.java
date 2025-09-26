package com.example.controller;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    // Map: document → set of words
    private final Map<String, Set<String>> docWordMap = new HashMap<>();

    @Override
    public void reduce(Text word, Iterable<Text> docs, Context context)
            throws IOException, InterruptedException {
        // Collect all documents containing this word
        for (Text doc : docs) {
            docWordMap.putIfAbsent(doc.toString(), new HashSet<>());
            docWordMap.get(doc.toString()).add(word.toString());
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        // Generate all unique document pairs and compute Jaccard similarity
        List<String> docs = new ArrayList<>(docWordMap.keySet());

        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                String d1 = docs.get(i);
                String d2 = docs.get(j);

                Set<String> w1 = docWordMap.get(d1);
                Set<String> w2 = docWordMap.get(d2);

                if (w1 == null || w2 == null) continue;

                // Compute intersection
                Set<String> intersection = new HashSet<>(w1);
                intersection.retainAll(w2);

                // Compute union
                Set<String> union = new HashSet<>(w1);
                union.addAll(w2);

                double score = (union.isEmpty()) ? 0.0 :
                        (double) intersection.size() / union.size();

                // Write output
                context.write(
                        new Text(d1 + ", " + d2),
                        new Text("Similarity: " + String.format("%.2f", score))
                );
            }
        }
    }
}
