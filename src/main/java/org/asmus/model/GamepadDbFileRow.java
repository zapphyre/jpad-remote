package org.asmus.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GamepadDbFileRow {
    String guid;
    String name;
    String mapping;
    String platform;

    public static GamepadDbFileRow parse(String input) {
        try {
            String[] parts = input.split(",", -1);

            String guid = parts[0];
            String name = "";
            name = parts[1];


            StringBuilder mappingBuilder = new StringBuilder();
            for (int i = 2; i < parts.length - 2; i++) {
                if (i > 2) mappingBuilder.append(",");
                mappingBuilder.append(parts[i]);
            }

            String mapping = mappingBuilder.toString();
            String platform = parts[parts.length - 2].replace("platform:", "");

            return new GamepadDbFileRow(guid, name, mapping, platform);

        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error reading resource file: " + e.getMessage());
        }

        return null;
    }
}
