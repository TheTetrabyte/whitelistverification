package dev.tetrabyte.whitelistverification;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class Events implements Listener {
    private final Plugin plugin;

    // Class to set the plugin
    public Events(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Variables
        Player player = event.getPlayer();
        Boolean whitelisted = player.isWhitelisted();
        Server server = Bukkit.getServer();

        // Log about the user joining
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', player.getDisplayName() + " has triggered the join event"));

        // Build http clients
        HttpClient client = HttpClients.custom().build();
        HttpClient checkClient = HttpClients.custom().build();

        // Check if the user is not whitelisted and that the system is enabled
        if (!whitelisted && plugin.getConfig().getBoolean("whitelist")) {
            try {
                // Create http get request
                HttpGet get = new HttpGet(plugin.getConfig().getString("api") + "/verification/" + player.getUniqueId());
                // Set the authorization header
                get.setHeader("authorization", plugin.getConfig().getString("token"));

                // Execute the http request
                HttpResponse checkResult = checkClient.execute(get);
                // Get the response string
                String checkJson = EntityUtils.toString(checkResult.getEntity(), "UTF-8");
                // Use gson to turn body into json data
                Gson checkGson = new Gson();
                Response checkResponse = checkGson.fromJson(checkJson, Response.class);

                // Check the response code is 5000 (User is allowed to be whitelisted)
                if (checkResponse.getCode() == 5000) {
                    // Log that they're being whitelisted
                    Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', player.getDisplayName() + " has joined and is verified, adding user to the whitelist and connecting them"));
                    // Whitelist the user
                    player.setWhitelisted(true);
                } else {
                    try {
                        // Stop their join message
                        event.setJoinMessage("");

                        // Create http post request
                        HttpPost post = new HttpPost(plugin.getConfig().getString("api") + "/verification");
                        // Send the server their user details
                        StringEntity params = new StringEntity("{\n\t\"name\": \"" + player.getDisplayName() + "\",\n\t\"uuid\": \"" + player.getUniqueId() + "\"\n}");
                        // Set the content type header and the authorization header
                        post.addHeader("content-type", "application/json");
                        post.addHeader("authorization", plugin.getConfig().getString("token"));
                        // Set the json data as the params of the body
                        post.setEntity(params);

                        // Execute the http request
                        HttpResponse result = client.execute(post);
                        // Get the response string
                        String json = EntityUtils.toString(result.getEntity(), "UTF-8");
                        // Use gson to turn body into json data
                        Gson gson = new Gson();
                        Response response = gson.fromJson(json, Response.class);

                        // Check the response code is 5002 (Successful code creation)
                        if (response.getCode() == 5002) {
                            // Kick the player and give them the code and instructions
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&b&lYou're not whitelisted on this server yet\n\n&6&l&nAccount link started&r\n\n&6&lLink code: &b&l&n" + response.getData().getCode().toString() + "\n&r&6&lRun !whitelist <CODE> on the #pinecraft chat to link your account.\n\n&a&lCode Expires in: &n&l" + response.getData().getExpiresIn() + ""));

                            // Check if the response code is 5003 (If account is already linked and is not in whitelist for some reason)
                        } else if (response.getCode() == 5003) {
                            // Kick the player and give them an error message
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lYour account is not whitelisted and we already have a link record please contact a lil admin in the Discord"));

                            // Catch all other error codes and fail
                        } else if (response.getCode() == 5001 || response.getCode() == 5000 || response.getCode() == 5004 || response.getCode() == 5005 || response.getCode() == 5006 || response.getCode() == 5007) {
                            // Kick the player and give them an error message
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lThe plugin failed to send the correct details to the server, report this with this error code (" + response.getCode().toString() + ")."));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lThe plugin failed to send the correct details to the server, report this with this error code (6000)."));
                    } finally {
                        try {
                            ((CloseableHttpClient) client).close();
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lThe plugin failed to send the correct details to the server, report this with this error code (6001)."));
                        } catch (IOException e) {
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lThe plugin failed to send the correct details to the server, report this with this error code (6002)."));
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "&c&lThe plugin failed to send the correct details to the server, report this with this error code (6007)."));
            } finally {
                try {
                    ((CloseableHttpClient) checkClient).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            new APIJoinLeave().playerJoin(player, plugin, server);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Variables
        Player player = event.getPlayer();
        Boolean whitelisted = player.isWhitelisted();
        Server server = Bukkit.getServer();

        // Check if not whitelisted and if system is enabled
        if (!whitelisted && plugin.getConfig().getBoolean("whitelist")) {
            // Set quit message to nothing if the user is not on the whitelist
            event.setQuitMessage("");
        } else {
            new APIJoinLeave().plaverLeave(player, plugin, server);
        }
    }

    // GSON response structure for verification and whitelist
    public class Response {
        private Data data;
        private Integer code;
        private String success;

        public Data getData () {
            return data;
        }

        public void setData (Data data) {
            this.data = data;
        }

        public Integer getCode () {
            return code;
        }

        public void setCode (Integer code) {
            this.code = code;
        }

        public String getSuccess () {
            return success;
        }

        public void setSuccess (String success) {
            this.success = success;
        }

        @Override
        public String toString() {
            return "ClassPojo [data = "+data+", code = "+code+", success = "+success+"]";
        }
    }


}
