package dev.tetrabyte.whitelistverification;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class APIJoinLeave {
    public void playerJoin(Player player, Plugin plugin, Server server) {
           HttpClient client = HttpClients.custom().build();

           try {
               HttpPost post = new HttpPost(plugin.getConfig().getString("api") + "/stats/join/" + player.getUniqueId());
               StringEntity params = new StringEntity("{\n\t\"name\": \"" + player.getDisplayName() + "\",\n\t\"uuid\": \"" + player.getUniqueId() + "\",\n\t\"players\": \"" + server.getOnlinePlayers().size() + "\",\n\t\"max\": \"" + server.getMaxPlayers() + "\"\n}");
               post.addHeader("content-type", "application/json");
               post.addHeader("authorization", plugin.getConfig().getString("token"));
               post.setEntity(params);

               HttpResponse result = client.execute(post);
           } catch (Exception ex) {
                ex.printStackTrace();
           } finally {
               try {
                   ((CloseableHttpClient) client).close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
    }

    public void plaverLeave(Player player, Plugin plugin, Server server) {
        HttpClient client = HttpClients.custom().build();

        try {
            HttpPost post = new HttpPost(plugin.getConfig().getString("api") + "/stats/leave/" + player.getUniqueId());
            StringEntity params = new StringEntity("{\n\t\"name\": \"" + player.getDisplayName() + "\",\n\t\"uuid\": \"" + player.getUniqueId() + "\",\n\t\"players\": \"" + server.getOnlinePlayers().size() + "\",\n\t\"max\": \"" + server.getMaxPlayers() + "\"\n}");
            post.addHeader("content-type", "application/json");
            post.addHeader("authorization", plugin.getConfig().getString("token"));
            post.setEntity(params);

            HttpResponse result = client.execute(post);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                ((CloseableHttpClient) client).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
