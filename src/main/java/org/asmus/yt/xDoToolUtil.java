package org.asmus.yt;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class xDoToolUtil {

    @SneakyThrows
    public static void ffFwd() {
        String[] args = new String[]{"xdotool", "key", "Alt+Right"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void ffBack() {
        String[] args = new String[]{"xdotool", "key", "Alt+Left"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void click() {
        String[] args = new String[]{"xdotool", "click", "1"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void pressCtrlT() {
        String[] args = new String[]{"xdotool", "key", "Ctrl+t"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void pressCtrlW() {
        String[] args = new String[]{"xdotool", "key", "Ctrl+w"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void pressF() {
        String[] args = new String[]{"xdotool", "key", "f"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void pressSpace() {
        String[] args = new String[]{"xdotool", "key", "Space"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void keyDown() {
        String[] args = new String[]{"xdotool", "key", "Down"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void keyUp() {
        String[] args = new String[]{"xdotool", "key", "Up"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void keyRight() {
        String[] args = new String[]{"xdotool", "key", "Right"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void keyLeft() {
        String[] args = new String[]{"xdotool", "key", "Left"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void tabSwitchOff() {
        String[] ctrlCmd = new String[]{"xdotool", "keyup", "ctrl"};
        new ProcessBuilder(ctrlCmd).start();
    }

    @SneakyThrows
    public static void tabSwitchOn() {
//        String[] ctrlCmd = new String[]{"xdotool", "keydown", "ctrl"};
        String[] ctrlCmd = new String[]{"xdotool", "keydown", "ctrl", "&&", "xdotool", "key", "Tab"};
        Process ctrl = new ProcessBuilder(ctrlCmd).start();
//        ctrl.wait(120);

//        String[] tabCmd = new String[]{"xdotool", "key", "Tab"};
//        new ProcessBuilder(tabCmd).start();
    }

    @SneakyThrows
    public static void pageDown() {
        String[] args = new String[]{"xdotool", "key", "Page_Down"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void pageUp() {
        String[] args = new String[]{"xdotool", "key", "Page_Up"};
        Process proc = new ProcessBuilder(args).start();
    }

    @SneakyThrows
    public static void setPointerLocation(Dimension dimension) {
        log.info("setting pointer location to " + dimension);
        String[] args = new String[]{"xdotool", "mousemove", String.valueOf(dimension.getWidth()), String.valueOf(dimension.getHeight())};
        Process proc = new ProcessBuilder(args).start();
    }

    public static Integer[] getScreenDimensions() {
        String[] args = new String[]{"cat", "/sys/class/graphics/*/virtual_size"};
        Process proc;
        try {
            proc = new ProcessBuilder(args).start();
            String line = readLine(proc.getInputStream());
            String[] d = line.split(",");

            return new Integer[] {Integer.valueOf(d[0]), Integer.valueOf(d[1])};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void togglePlayYoutube() {
        Integer winId = getYoutubeWindowIDs().getLast();
        log.info("Youtube window ID: {}", winId);

        String[] args = new String[]{"xdotool", "key", "--window", winId.toString(), "k"};
        try {
            Process proc = new ProcessBuilder(args).inheritIO().start();
//            proc.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Integer> getYoutubeWindowIDs() {
        String[] args = new String[]{"xdotool", "search", "--name", "youtube"};
        Process proc;
        try {
            proc = new ProcessBuilder(args).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return read(proc.getInputStream());
    }

    public static String readLine(InputStream inputStream) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.readLine();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static List<Integer> read(InputStream inputStream) {
        List<Integer> winIds = new LinkedList<>();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                winIds.add(Integer.valueOf(line));
            }
            reader.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return winIds;
    }
}
