package org.javawebstack.framework.command;

import org.javawebstack.command.Command;
import org.javawebstack.command.CommandResult;
import org.javawebstack.command.CommandSystem;
import org.javawebstack.framework.util.Crypt;
import org.javawebstack.framework.util.IO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GenerateKeyCommand implements Command {

    public CommandResult execute(CommandSystem commandSystem, List<String> args, Map<String, List<String>> params) {
        String[] lines;
        try {
            lines = IO.readTextFile(".env").replace("\r", "").split("\\n");
        } catch (IOException e) {
            lines = new String[0];
        }
        boolean found = false;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("CRYPT_KEY=")) {
                if (lines[i].length() > 10 && !params.containsKey("f"))
                    return CommandResult.error("You already have a key. You might loose all your encrypted data if you regenerate it! Use -f to do it anyway.");
                lines[i] = "CRYPT_KEY=" + Crypt.generateKey();
                found = true;
                break;
            }
        }
        if (!found) {
            String[] newLines = new String[lines.length + 3];
            System.arraycopy(lines, 0, newLines, 0, lines.length);
            newLines[newLines.length - 3] = "";
            newLines[newLines.length - 2] = "# Encryption Key";
            newLines[newLines.length - 1] = "CRYPT_KEY=" + Crypt.generateKey();
            lines = newLines;
        }
        try {
            IO.writeFile(".env", String.join("\n", lines));
            System.out.println("Key generated!");
            return CommandResult.success();
        } catch (IOException e) {
            return CommandResult.error(e);
        }
    }

}
