package com.example.controller;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // Normalize input line: lowercase + remove punctuation
        String line = value.toString().toLowerCase().replaceAll("[^a-z0-9 ]", " ");
        String[] parts = line.split("\\s+", 2);

        // Skip malformed lines
        if (parts.length < 2) return;

        String docID = parts[0];  // e.g., Document1
        String[] words = parts[1].split("\\s+");

        // Collect unique words per document
        Set<String> uniqueWords = new HashSet<>();
        for (String w : words) {
            if (!w.isEmpty()) uniqueWords.add(w);
        }

        // Emit (word, docID)
        for (String word : uniqueWords) {
            context.write(new Text(word), new Text(docID));
        }
    }
}
