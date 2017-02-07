/* Copyright © 2017 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package bot.verter.storage;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageFactory {
    private static final String PATH_TO_QA = "resources/qa/";

    private static StorageFactory storageFactory = new StorageFactory();

    private static final String DELIMITER = "_";

    /**
     * Key - theme, value - qa
     */
    Map<String, List<Index>> storage = new HashMap<>();

    private StorageFactory() {
    }

    public static StorageFactory getInstance() {
        return storageFactory;
    }

    public void init() throws IOException {
        Map<String, String> ans = answers();
        for (File f : filesByExtension("questions")) {
            int index = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine();
                while (line != null) {
                    String fileName = FilenameUtils.getBaseName(f.getName());
                    List<Index> ind = storage.get(fileName);
                    if (ind == null) {
                        ind = new ArrayList<>();
                    }
                    ind.add(new Index(line, ans.get(fileName + DELIMITER + index)));
                    storage.put(fileName, ind);
                    line = br.readLine();
                    index++;
                }
            }
        }
    }

    /**
     * @return key - theme + index, value - answer
     */
    private Map<String, String> answers() throws IOException {
        Map<String, String> result = new HashMap<>();
        for (File f : filesByExtension("answers")) {
            String themeName = fileName(f);
            int index = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line = br.readLine();
                while (line != null) {
                    result.put(themeName + DELIMITER + index, line);
                    line = br.readLine();
                    index++;
                }
            }
        }

        return result;
    }

    private File[] filesByExtension(String extension) {
        return new File(PATH_TO_QA).listFiles((dir, name) -> name.endsWith("." + extension));
    }

    private String fileName(File file) {
        return FilenameUtils.getBaseName(file.getName());
    }

    public Map<String, List<Index>> getStorage() {
        return storage;
    }
}
