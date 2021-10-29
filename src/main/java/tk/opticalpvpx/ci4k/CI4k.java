package tk.opticalpvpx.ci4k;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CI4k {

    private static int previousDay = OffsetDateTime.now().getDayOfMonth();
    
    private static JsonObject days;
    private static JsonObject getDefault;
    private static String clientId;
    
    private static IPCClient client;
    
    static {
        updateConfig();
    }
    
    private static void updateConfig() {
        JsonObject config = null;
        try {
            config = JsonParser.parseReader(new FileReader(System.getProperty("user.dir") + "\\config.json")).getAsJsonObject();
        } catch (FileNotFoundException ignored) {
            System.out.println("Config not found. Please download the official config from github and push it into the program directory. Shutting down.");
            System.exit(1);
        }
        clientId = config.get("client_ID").getAsString();
        getDefault = config.get("default").getAsJsonObject();
        days = config.get("timings").getAsJsonObject();
    }

    public static void main(String[] args) {
        client = new IPCClient(Long.parseLong(clientId));
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                RichPresence.Builder Builder = new RichPresence.Builder();
                Builder.setState("Starting CI4k...")
                        .setDetails("Created By Optical and Magix/KingRainbow44")
                        .setStartTimestamp(OffsetDateTime.now());
                client.sendRichPresence(Builder.build());
            }
        });

        try {
            client.connect();
        } catch (NoDiscordClientException ignored) {
            System.out.print("There was no Discord client detected. Please open Discord and relaunch CI4k.");
            return;
        }
        System.out.print("Discord client successfully detected. Creating your rich presence.");

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                OffsetDateTime currentTime = OffsetDateTime.now();
                if(currentTime.getDayOfMonth() != previousDay) {
                    previousDay = currentTime.getDayOfMonth();
                    updateConfig();
                }
                
                JsonObject dayData = getPartsOfDay(currentTime.getDayOfWeek());
                for(Map.Entry<String, JsonElement> entry : dayData.entrySet()) {
                    String[] time = entry.getKey().split(":");
                    //System.out.print("Time: " + currentTime.getHour() + ":" + currentTime.getMinute());
                    if(time[0].matches(String.valueOf(currentTime.getHour())) && 
                            time[1].matches(String.valueOf(currentTime.getMinute()))) {
                        JsonObject data = entry.getValue().getAsJsonObject();
                        if(getValueNotNull(data, "state").matches("close")) {
                            client.close();
                            System.exit(1);
                        }
                        RichPresence.Builder builder = new RichPresence.Builder();
                        builder.setState(getValueNotNull(data, "state"));
                        builder.setDetails(getValueNotNull(data, "details"));
                        builder.setLargeImage(getValueNotNull(data, "large_image"), getValueNotNull(data, "large_image_text"));
                        //builder.setSmallImage(getValueNotNull(data, "small_image"), getValueNotNull(data, "small_image_text"));
                        builder.setStartTimestamp(currentTime);
                        client.sendRichPresence(builder.build());
                        //System.out.print("Time: " + currentTime.getHour() + ":" + currentTime.getMinute());
                    }
                }
            }
        }, 0L, 20000L);
    }

    private static JsonObject getPartsOfDay(DayOfWeek day) {
        return days.get(day.name().toLowerCase()).getAsJsonObject();
    }

    /**
     * Refers to the config's defaults when something is not present.
     * @return String
     */
    private static String getValueNotNull(JsonObject object, String valueToGrab) {
        return getObjectNotNull(object,
                valueToGrab,
                getObjectNotNull(getDefault, valueToGrab, null)
        ).toString().replaceAll("\"", "");
    }

    private static Object getObjectNotNull(JsonObject object, String valueToGrab, Object fallBack) {
        if(object.get(valueToGrab) == null)
            return fallBack;
        else
            return object.get(valueToGrab);
    }
}