package software.ulpgc.imageviewer.application.cli;

public class Main {

    public static void main(String[] args) {
        Console.create()
                .initDisplay()
                .initCommands()
                .execute();
    }

}
