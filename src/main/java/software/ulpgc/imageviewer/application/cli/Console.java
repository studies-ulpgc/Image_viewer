package software.ulpgc.imageviewer.application.cli;

import software.ulpgc.imageviewer.application.FileImageStore;
import software.ulpgc.imageviewer.architecture.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class Console {
    private static final Command Null = () -> System.out.println("Command not found");
    private static final File folder = new File("images");

    private final Scanner scanner;
    private final ImageDisplay display;
    private final Map<String, Command> commands;

    private Console() {
        this.scanner = new Scanner(System.in);
        this.display = new ConsoleImageDisplay();
        this.commands = new HashMap<>();
    }

    public static Console create() {
        return new Console();
    }

    public void execute() {
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("exit")) break;
            commands.getOrDefault(command, Null).execute();
        }
    }

    public Console initDisplay() {
        display.show(firstImage());
        return this;
    }

    public Console initCommands() {
        commands.put("next", new NextCommand(display));
        commands.put("prev", new PrevCommand(display));
        return this;
    }

    private static Image firstImage() {
        return firsImageIn(images());
    }

    private static Stream<String> images() {
        return new FileImageStore(folder).images();
    }

    private static Image firsImageIn(Stream<String> images) {
        return ImageProvider.with(images).first(Console::loader);
    }

    private static byte[] loader(String id)  {
        try (InputStream is = new FileInputStream(new File(folder, id))) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
